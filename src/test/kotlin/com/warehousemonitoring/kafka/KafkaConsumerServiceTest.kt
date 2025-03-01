package com.warehousemonitoring.kafka
import com.warehousemonitoring.db.SensorReadings
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import java.util.Properties
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.selectAll
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class KafkaConsumerServiceTest {

    private val dbUrl = "jdbc:postgresql://localhost:5433/warehouse"
    private val dbUser = "user"
    private val dbPassword = "password"
    private val kafkaBootstrapServers = "localhost:9093"
    private val kafkaTopic = "sensor_data_test"

    private fun connectToDatabase() {
        Database.connect(
            url = dbUrl,
            driver = "org.postgresql.Driver",
            user = dbUser,
            password = dbPassword
        )
    }

    @Test
    @Timeout(60)
    fun `test KafkaConsumerService processes messages from Kafka`() {
        connectToDatabase()

        // Clear previous data
        transaction {
            SensorReadings.deleteAll()
        }

        // Setup Kafka Producer
        val producerProps = Properties().apply {
            put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers)
            put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java)
            put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java)
        }
        val producer = KafkaProducer<String, String>(producerProps)

        // Send test message to Kafka
        val testMessage = "sensor_id=t123;value=40"
        println("Sending test message to Kafka: $testMessage")
        producer.send(ProducerRecord(kafkaTopic, testMessage)).get()
        producer.close()
        println("Test message sent to Kafka")

        // Start Kafka consumer in a separate thread
        println("Starting Kafka consumer thread...")
        val consumerThread = Thread {
            KafkaConsumerService.startMonitoring(shouldStopAfterProcessing = true)
        }
        consumerThread.start()

        // Wait for the message to be processed
        println("Waiting for message to be processed...")
        var lastProcessedMessage: String? = null
        val startTime = System.currentTimeMillis()
        while (System.currentTimeMillis() - startTime < 30000) { // 30 seconds timeout
            lastProcessedMessage = KafkaConsumerService.getLastProcessedMessage()
            if (lastProcessedMessage != null) {
                break
            }
            Thread.sleep(1000) // Sleep for 1 second
        }

        // Verify Kafka message processing
        assertNotNull(lastProcessedMessage, "KafkaConsumerService should process a message")
        assertEquals(testMessage, lastProcessedMessage, "Processed message should match the test input")

        // Verify database record insertion
        transaction {
            val reading = SensorReadings.selectAll().lastOrNull()
            assertNotNull(reading, "Database should have a record")
            assertEquals("t123", reading?.get(SensorReadings.sensorId), "Sensor ID should match")
            assertEquals(40, reading?.get(SensorReadings.value), "Sensor value should match")
        }
    }
}
