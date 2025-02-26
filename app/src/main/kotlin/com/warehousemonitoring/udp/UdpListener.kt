package com.warehousemonitoring.udp

import com.warehousemonitoring.kafka.KafkaProducerService
import org.slf4j.LoggerFactory
import java.net.DatagramPacket
import java.net.DatagramSocket

class UdpListener(private val port: Int) {
    private val logger = LoggerFactory.getLogger(UdpListener::class.java)

    fun startListening() {
        val socket = DatagramSocket(port)
        val buffer = ByteArray(1024)

        logger.info("Listening for sensor data on port $port...")

        while (true) {
            val packet = DatagramPacket(buffer, buffer.size)
            socket.receive(packet)
            val receivedData = String(packet.data, 0, packet.length)
            logger.info("Received: $receivedData")

            KafkaProducerService.sendToKafka("sensor_data", receivedData)
        }
    }
}
