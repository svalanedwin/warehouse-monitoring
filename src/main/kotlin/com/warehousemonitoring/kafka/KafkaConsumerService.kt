package com.warehousemonitoring.kafka

import com.warehousemonitoring.db.SensorReadings
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.*

object KafkaConsumerService {
    private val logger = LoggerFactory.getLogger(KafkaConsumerService::class.java)

    private val properties = Properties().apply {
        put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, System.getenv("KAFKA_BOOTSTRAP_SERVERS") ?: "localhost:9093")
        put(ConsumerConfig.GROUP_ID_CONFIG, "monitoring_service")
        put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
        put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
    }

    var consumer = KafkaConsumer<String, String>(properties).apply {
        subscribe(listOf("sensor_data"))
    }

    private var lastProcessedMessage: String? = null

    fun startMonitoring(shouldStopAfterProcessing: Boolean = false) {
        while (true) {
            val records = consumer.poll(Duration.ofMillis(100))
            if (records.isEmpty) {
                logger.info("No records received from Kafka")
            } else {
                for (record in records) {
                    logger.info("Processing Kafka record: ${record.value()}")
                    lastProcessedMessage = record.value()
                    checkThresholds(record.value())
                }
                if (shouldStopAfterProcessing) break // Stop after processing test messages
            }
        }
    }

    fun checkThresholds(data: String) {
        logger.info("🔎 Processing sensor data: $data")

        val parts = data.split(";")
        val sensorId = parts[0].split("=")[1]
        val value = parts[1].split("=")[1].toInt()

        transaction {
            logger.info("Inserting sensor reading: sensor_id=$sensorId, value=$value")
            SensorReadings.insert {
                it[this.sensorId] = sensorId
                it[this.value] = value
                it[this.timestamp] = java.time.LocalDateTime.now()
            }
        }

        val threshold = if (sensorId.startsWith("t")) 35 else 50
        if (value > threshold) {
            logger.warn("🚨 ALERT! Sensor $sensorId exceeded threshold with value $value")
        }
    }

    fun getLastProcessedMessage(): String? = lastProcessedMessage
}
