package com.warehousemonitoring.unit.udp

import com.warehousemonitoring.udp.UdpListener
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class UdpListenerTest : StringSpec({
    val port = 3344
    val udpListener = UdpListener(port)
    val message = "sensor_id=t1;value=36"

    "udp listener receives message" {
        // Start the UDP listener in a separate thread
        Thread {
            udpListener.startListening()
        }.start()

        // Send a UDP message to the listener
        val socket = DatagramSocket()
        val buffer = message.toByteArray()
        val packet = DatagramPacket(buffer, buffer.size, InetAddress.getLocalHost(), port)
        socket.send(packet)
        socket.close()

        // Wait for the listener to process the message
        Thread.sleep(1000)

        // Verify the message was received
        udpListener.getLastReceivedMessage() shouldBe message
    }
})