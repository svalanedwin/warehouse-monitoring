package com.warehousemonitoring.kafka

import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.LoggerFactory
import java.util.*

object KafkaProducerService {
    private val logger = LoggerFactory.getLogger(KafkaProducerService::class.java)

    private val properties = Properties().apply {
        put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9093")
        put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
        put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
    }

    private val producer = KafkaProducer<String, String>(properties)

    fun sendToKafka(topic: String, message: String) {
        producer.send(ProducerRecord(topic, message)) { metadata, exception ->
            if (exception == null) {
                logger.info("✅ Message sent to Kafka: $message (Partition: ${metadata.partition()})")
            } else {
                logger.error("❌ Error sending message to Kafka", exception)
            }
        }
    }
}