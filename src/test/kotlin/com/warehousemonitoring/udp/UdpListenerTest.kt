package com.warehousemonitoring.udp

import com.warehousemonitoring.kafka.KafkaProducerService
import io.mockk.*
import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import java.net.DatagramPacket
import java.net.DatagramSocket

class UdpListenerTest {

    @Test
    @Timeout(5) // Prevent infinite loops from hanging the test
    fun `test start listening and forward to Kafka`() = runBlocking {
        // Mock KafkaProducerService
        val mockKafkaProducerService = mockk<KafkaProducerService>(relaxed = true)

        // Use a test-specific port to avoid conflicts
        val testPort = 5555
        val udpListener = UdpListener(testPort, mockKafkaProducerService)

        // Start the UDP listener in a background coroutine
        val job = launch { udpListener.startListening() }

        delay(500) // Wait for the UDP listener to start

        // Send a test UDP packet
        val socket = DatagramSocket()
        val testMessage = "sensor_id=t1;value=36"
        val packet = DatagramPacket(testMessage.toByteArray(), testMessage.length, java.net.InetAddress.getByName("localhost"), testPort)
        socket.send(packet)
        socket.close()

        delay(1000) // Wait for the listener to process the message

        // Verify that the message was forwarded to Kafka
        verify(exactly = 1) {
            mockKafkaProducerService.sendToKafka("sensor_data", testMessage)
        }

        // Clean up
        job.cancel()
    }
}
