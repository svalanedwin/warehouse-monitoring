package com.warehousemonitoring.kafka

import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.LoggerFactory
import java.util.*

/**
 * KafkaProducerService is responsible for producing messages to Kafka topics.
 */
object KafkaProducerService {
    // Logger instance for logging producer events
    private val logger = LoggerFactory.getLogger(KafkaProducerService::class.java)

    /**
     * Kafka producer configuration properties.
     */
    private val properties = Properties().apply {
        put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9093") // Kafka broker address
        put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer") // Key serializer
        put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer") // Value serializer
    }

    /**
     * KafkaProducer instance responsible for sending messages.
     */
    private val producer = KafkaProducer<String, String>(properties)

    /**
     * Sends a message to the specified Kafka topic.
     *
     * @param topic The Kafka topic to send the message to.
     * @param message The message content to send.
     */
    fun sendToKafka(topic: String, message: String) {
        // Create a ProducerRecord with the topic and message
        producer.send(ProducerRecord(topic, message)) { metadata, exception ->
            if (exception == null) {
                // Log successful message send
                logger.info("✅ Message sent to Kafka: $message (Partition: ${metadata.partition()})")
            } else {
                // Log error if message fails to send
                logger.error("❌ Error sending message to Kafka", exception)
            }
        }
    }
}
