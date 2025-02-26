import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class WarehouseService(private val udpPort: Int) {
    fun startListening() {
        val socket = DatagramSocket(udpPort)
        println("Listening for sensor data on port $udpPort...")

        while (true) {
            val buffer = ByteArray(1024)
            val packet = DatagramPacket(buffer, buffer.size)
            socket.receive(packet)

            val message = String(packet.data, 0, packet.length)
            println("Received: $message")
            KafkaProducerService.sendToKafka("sensor_data", message)
        }
    }
}
