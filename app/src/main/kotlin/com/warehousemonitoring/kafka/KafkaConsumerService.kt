package com.warehousemonitoring.kafka

import com.warehousemonitoring.db.SensorReadings
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.*

/**
 * KafkaConsumerService listens to Kafka topic "sensor_data" and processes sensor readings.
 */
object KafkaConsumerService {
    // Logger instance for logging messages
    private val logger = LoggerFactory.getLogger(KafkaConsumerService::class.java)

    /**
     * Kafka consumer configuration properties.
     */
    private val properties = Properties().apply {
        put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9093")  // Kafka broker address
        put(ConsumerConfig.GROUP_ID_CONFIG, "monitoring_service")  // Consumer group ID
        put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer") // Key deserialization
        put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer") // Value deserialization
    }

    /**
     * KafkaConsumer instance for reading messages from "sensor_data" topic.
     */
    private val consumer = KafkaConsumer<String, String>(properties).apply {
        subscribe(listOf("sensor_data"))  // Subscribing to the topic
    }

    /**
     * Stores the last processed message for debugging or monitoring purposes.
     */
    private var lastProcessedMessage: String? = null

    /**
     * Starts listening for Kafka messages and processes them continuously.
     */
    fun startMonitoring() {
        while (true) {
            val records = consumer.poll(Duration.ofMillis(100))  // Poll for new messages
            for (record in records) {
                val data = record.value()
                lastProcessedMessage = data  // Store the last processed message
                logger.info("🔎 Processing sensor data init: $data")
                checkThresholds(data)
            }
        }
    }

    /**
     * Parses the sensor data, stores it in the database, and checks for threshold violations.
     *
     * @param data Sensor reading in the format "sensor_id=value;value=value".
     */
    private fun checkThresholds(data: String) {
        logger.info("🔎 Processing sensor data: $data")

        // Splitting input data into key-value pairs
        val parts = data.split(";")
        val sensorId = parts[0].split("=")[1]  // Extracting sensor ID
        val value = parts[1].split("=")[1].toInt()  // Extracting sensor value

        // Determine if the sensor is a temperature sensor (sensor IDs starting with 't')
        val isTemperature = sensorId.startsWith("t")
        val threshold = if (isTemperature) 35 else 50  // Set threshold based on sensor type

        // Insert data into the database
        transaction {
            logger.info("Inserting sensor reading: sensor_id=$sensorId, value=$value")
            SensorReadings.insert {
                it[this.sensorId] = sensorId
                it[this.value] = value
                it[this.timestamp] = java.time.LocalDateTime.now()
            }
        }

        // Check if sensor value exceeds the threshold and raise an alert if necessary
        if (value > threshold) {
            logger.warn("🚨 ALERT! Sensor $sensorId exceeded threshold with value $value")
        }
    }

    /**
     * Retrieves the last processed message for monitoring or debugging.
     *
     * @return Last processed Kafka message or null if no messages have been processed.
     */
    fun getLastProcessedMessage(): String? = lastProcessedMessage
}
