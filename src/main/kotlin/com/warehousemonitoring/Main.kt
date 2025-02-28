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
        // Read UDP ports from environment variables or use default values
        val udpPort1 = System.getenv("UDP_PORT_1")?.toIntOrNull() ?: 3344  // Default: 3344
        val udpPort2 = System.getenv("UDP_PORT_2")?.toIntOrNull() ?: 3355  // Default: 3355

        logger.info("Using UDP ports: $udpPort1 (Temperature Sensor), $udpPort2 (Humidity Sensor)")

        // Launch UDP listeners for different sensors
        launch { UdpListener(udpPort1).startListening() } // Temperature Sensor
        launch { UdpListener(udpPort2).startListening() } // Humidity Sensor

        // Start Kafka Consumer to process incoming sensor data from Kafka topic
        launch { KafkaConsumerService.startMonitoring() }
    }
}