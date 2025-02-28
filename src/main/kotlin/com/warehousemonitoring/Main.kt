package com.warehousemonitoring

import com.warehousemonitoring.udp.UdpListener
import com.warehousemonitoring.kafka.KafkaConsumerService
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory

/**
 * Main entry point for the Warehouse Monitoring Service.
 * Initializes the database, starts UDP listeners for sensor data,
 * and launches the Kafka consumer for processing incoming messages.
 */
fun main() {
    // Logger instance for logging application startup events
    val logger = LoggerFactory.getLogger("Main")

    logger.info("🚀 Starting Warehouse Monitoring Service...")

    // Initialize database connection and ensure necessary tables exist
    DatabaseConfig.init()

    // Use runBlocking to manage coroutine launching in a structured manner
    runBlocking {
        // Launch UDP listeners for different sensors
        launch { UdpListener(3344).startListening() } // Temperature Sensor
        launch { UdpListener(3355).startListening() } // Humidity Sensor

        // Start Kafka Consumer to process incoming sensor data from Kafka topic
        launch { KafkaConsumerService.startMonitoring() }
    }
}
