package com.warehousemonitoring.udp

import com.warehousemonitoring.kafka.KafkaProducerService
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.BindException

/**
 * UDP Listener that listens for incoming UDP packets on a specified port
 * and forwards the received messages to a Kafka topic.
 *
 * @param port The UDP port to listen on.
 */
class UdpListener(
    private val port: Int,
    private val kafkaProducerService: KafkaProducerService = KafkaProducerService
) {
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
                        lastReceivedMessage?.let {
                            kafkaProducerService.sendToKafka("sensor_data", it)
                        }
                    }
                }
            } catch (e: BindException) {
                logger.error("❌ Port $port is already in use.")
            } catch (e: Exception) {
                logger.error("❌ Error receiving UDP message", e)
            }
        }
    }

    fun getLastReceivedMessage(): String? = lastReceivedMessage
}