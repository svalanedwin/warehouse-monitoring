package com.warehousemonitoring

import com.warehousemonitoring.udp.UdpListener
import com.warehousemonitoring.kafka.KafkaConsumerService

import kotlinx.coroutines.*
import org.slf4j.LoggerFactory

fun main() {
    val logger = LoggerFactory.getLogger("Main")

    logger.info("🚀 Starting Warehouse Monitoring Service...")

    DatabaseConfig.init()

    runBlocking {
        launch { UdpListener(3344).startListening() } // Temperature Sensor
        launch { UdpListener(3355).startListening() } // Humidity Sensor
        launch { KafkaConsumerService.startMonitoring() } // Start Kafka Consumer
    }
}