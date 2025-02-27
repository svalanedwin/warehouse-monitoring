package com.warehousemonitoring.udp

import com.warehousemonitoring.kafka.KafkaProducerService
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import java.net.DatagramPacket
import java.net.DatagramSocket

class UdpListener(private val port: Int) {
    private val logger = LoggerFactory.getLogger(UdpListener::class.java)
    private var lastReceivedMessage: String? = null

    fun startListening() {
        logger.info("🔄 Starting UDP listener on port $port...")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                DatagramSocket(port).use { socket ->
                    logger.info("✅ Successfully bound to UDP port $port")
                    val buffer = ByteArray(1024)

                    while (true) {
                        val packet = DatagramPacket(buffer, buffer.size)
                        socket.receive(packet)
                        lastReceivedMessage = String(packet.data, 0, packet.length).trim()
                        logger.info("📥 Received UDP message: $lastReceivedMessage")
                        KafkaProducerService.sendToKafka("sensor_data", lastReceivedMessage!!)
                    }
                }
            } catch (e: Exception) {
                logger.error("❌ Error receiving UDP message", e)
            }
        }
    }

    // Function to retrieve the last received message
    fun getLastReceivedMessage(): String? = lastReceivedMessage
}