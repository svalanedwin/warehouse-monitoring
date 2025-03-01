package com.warehousemonitoring.kafka
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import java.time.Duration
import java.util.*

class KafkaProducerServiceTest {

    @Test
    @Timeout(15) // Increase timeout to 15 seconds
    fun `test sendToKafka sends message to Kafka`() {
        // Kafka configuration
        val kafkaBootstrapServers = "localhost:9093"
        val kafkaTopic = "sensor_data_test"

        // Message to send
        val message = "sensor_id=123;value=40"

        // Consumer properties
        val consumerProps = Properties().apply {
            put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers)
            put(ConsumerConfig.GROUP_ID_CONFIG, "test-group-" + UUID.randomUUID().toString()) // Unique group ID for isolation
            put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java)
            put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java)
            put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest") // Read from beginning
        }

        val consumer = KafkaConsumer<String, String>(consumerProps)
        consumer.subscribe(listOf(kafkaTopic))

        // Send message to Kafka
        KafkaProducerService.sendToKafka(kafkaTopic, message)

        // Poll multiple times to wait for Kafka to process the message
        val maxRetries = 10
        var retryCount = 0
        var receivedMessage: String? = null

        while (retryCount < maxRetries) {
            val records = consumer.poll(Duration.ofSeconds(2))
            if (records.count() > 0) {
                val record = records.iterator().next()
                receivedMessage = record.value()
                break
            }
            retryCount++
            Thread.sleep(500) // Wait before retrying
        }

        consumer.close()

        // Assert the received message
        assertEquals(message, receivedMessage, "Message should match")
    }
}
