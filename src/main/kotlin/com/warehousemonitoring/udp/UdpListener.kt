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
class UdpListener(private val port: Int) {
    // Logger instance for logging messages
    private val logger = LoggerFactory.getLogger(UdpListener::class.java)

    // Stores the last received UDP message for monitoring/debugging
    private var lastReceivedMessage: String? = null

    /**
     * Starts listening for UDP packets and forwards them to Kafka.
     */
    fun startListening() {
        logger.info("🔄 Starting UDP listener on port $port...")

        // Launch a coroutine for non-blocking UDP listening
        CoroutineScope(Dispatchers.IO).launch {
            try {
                DatagramSocket(port).use { socket ->  // Open a UDP socket on the specified port
                    logger.info("✅ Successfully bound to UDP port $port")
                    val buffer = ByteArray(1024)  // Buffer to store received UDP data

                    while (true) {
                        val packet = DatagramPacket(buffer, buffer.size)
                        socket.receive(packet)  // Receive incoming UDP packet

                        // Convert received bytes to a string and trim unnecessary spaces
                        lastReceivedMessage = String(packet.data, 0, packet.length).trim()
                        logger.info("📥 Received UDP message: $lastReceivedMessage")

                        // Send the received message to the Kafka topic "sensor_data"
                        lastReceivedMessage?.let {
                            KafkaProducerService.sendToKafka("sensor_data", it)
                        }
                    }
                }
            } catch (e: BindException) {
                logger.error("❌ Port $port is already in use. Please stop the conflicting process or choose a different port.")
            } catch (e: Exception) {
                logger.error("❌ Error receiving UDP message", e)
            }
        }
    }

    /**
     * Retrieves the last received UDP message.
     *
     * @return The last received UDP message, or null if no messages have been received.
     */
    fun getLastReceivedMessage(): String? = lastReceivedMessage
}