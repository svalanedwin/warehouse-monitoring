package com.warehousemonitoring.unit.kafka

import com.warehousemonitoring.kafka.KafkaConsumerService
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.TopicPartition
import java.time.Duration

class KafkaConsumerServiceTest : StringSpec({

    "kafka consumer processes sensor data" {
        val kafkaConsumer = mockk<KafkaConsumer<String, String>>(relaxed = true)

        // Use reflection to set the mock KafkaConsumer in KafkaConsumerService
        mockkObject(KafkaConsumerService) // Mock the singleton
        every { KafkaConsumerService::class.java.getDeclaredField("consumer").apply { isAccessible = true }.get(KafkaConsumerService) } returns kafkaConsumer

        val topic = "sensor_data"
        val partition = 0
        val message = "sensor_id=t1;value=36"
        val record = ConsumerRecord(topic, partition, 0L, "key", message)

        val topicPartition = TopicPartition(topic, partition)
        val records = ConsumerRecords(mapOf(topicPartition to listOf(record)))

        // Mock Kafka consumer behavior
        every { kafkaConsumer.poll(any<Duration>()) } returns records

        // Start monitoring (assumes startMonitoring() processes messages)
        KafkaConsumerService.startMonitoring()

        // Verify the message was processed
        KafkaConsumerService.getLastProcessedMessage() shouldBe message
    }
})