package com.warehousemonitoring

import com.warehousemonitoring.udp.UdpListener
import com.warehousemonitoring.kafka.KafkaProducerService
import org.slf4j.LoggerFactory
import kotlinx.coroutines.*

fun main() {
    val logger = LoggerFactory.getLogger("Main")

    logger.info("Starting Warehouse Monitoring Service...")

    // Start UDP listeners for Temperature & Humidity sensors
    runBlocking {
        launch { UdpListener(3344).startListening() } // Temperature Sensor
        launch { UdpListener(3355).startListening() } // Humidity Sensor
    }
}
