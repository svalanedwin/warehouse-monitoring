package com.warehousemonitoring.integration

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import java.time.Duration
import java.util.*

@Testcontainers
class KafkaIntegrationTest : StringSpec({

    val topic = "sensor_data_test"
    val message = "sensor_id=t1;value=36"

    "kafka producer sends message and consumer receives it" {
        val producer = KafkaProducer<String, String>(kafkaProducerProperties(kafka.bootstrapServers))
        producer.send(ProducerRecord(topic, message)).get()

        val consumer = KafkaConsumer<String, String>(kafkaConsumerProperties(kafka.bootstrapServers))
        consumer.subscribe(listOf(topic))

        val records: ConsumerRecords<String, String> = consumer.poll(Duration.ofMillis(1000))

        records.count() shouldBe 1
        records.iterator().next().value() shouldBe message
    }

}) {
    companion object {
        @JvmStatic
        @Container
        val kafka = KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.0.0")).apply {
            start() // Ensure the container starts before running tests
        }

        fun kafkaProducerProperties(bootstrapServers: String): Properties {
            return Properties().apply {
                put("bootstrap.servers", bootstrapServers)
                put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
                put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
            }
        }

        fun kafkaConsumerProperties(bootstrapServers: String): Properties {
            return Properties().apply {
                put("bootstrap.servers", bootstrapServers)
                put("group.id", "test-group")
                put("auto.offset.reset", "earliest") // Ensure we get messages from the beginning
                put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
                put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
            }
        }
    }
}