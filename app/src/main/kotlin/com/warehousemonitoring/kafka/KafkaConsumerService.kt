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
        put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9093")
        put(ConsumerConfig.GROUP_ID_CONFIG, "monitoring_service")
        put(
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
            "org.apache.kafka.common.serialization.StringDeserializer"
        )
        put(
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
            "org.apache.kafka.common.serialization.StringDeserializer"
        )
    }

    private val consumer = KafkaConsumer<String, String>(properties).apply {
        subscribe(listOf("sensor_data"))
    }

    private var lastProcessedMessage: String? = null

    fun startMonitoring() {
        while (true) {
            val records = consumer.poll(Duration.ofMillis(100))
            for (record in records) {
                val data = record.value()
                lastProcessedMessage = data // Store the last processed message
                logger.info("🔎 Processing sensor data init: $data")
                checkThresholds(data)
            }
        }
    }

    private fun checkThresholds(data: String) {
        logger.info("🔎 Processing sensor data: $data")

        val parts = data.split(";")
        val sensorId = parts[0].split("=")[1]
        val value = parts[1].split("=")[1].toInt()

        val isTemperature = sensorId.startsWith("t")
        val threshold = if (isTemperature) 35 else 50

        // Insert data into the database
        transaction {
            logger.info("Inserting sensor reading: sensor_id=$sensorId, value=$value")
            SensorReadings.insert {
                it[this.sensorId] = sensorId
                it[this.value] = value
                it[this.timestamp] = java.time.LocalDateTime.now()
            }
        }

        // Check thresholds and raise alerts
        if (value > threshold) {
            logger.warn("🚨 ALERT! Sensor $sensorId exceeded threshold with value $value")
        }
    }

    // Function to retrieve the last processed message
    fun getLastProcessedMessage(): String? = lastProcessedMessage
}