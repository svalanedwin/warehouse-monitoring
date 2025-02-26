import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import java.time.Duration
import java.util.*

object CentralMonitoringService {
    private val properties = Properties().apply {
        put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
        put(ConsumerConfig.GROUP_ID_CONFIG, "monitoring_service")
        put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
        put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
    }

    private val consumer = KafkaConsumer<String, String>(properties).apply {
        subscribe(listOf("sensor_data"))
    }

    fun startMonitoring() {
        while (true) {
            val records = consumer.poll(Duration.ofMillis(100))
            for (record in records) {
                val data = record.value()
                println("Processing sensor data: $data")
                checkThresholds(data)
            }
        }
    }

    private fun checkThresholds(data: String) {
        val parts = data.split(";")
        val sensorId = parts[0].split("=")[1]
        val value = parts[1].split("=")[1].toInt()

        val isTemperature = sensorId.startsWith("t")
        val threshold = if (isTemperature) 35 else 50

        if (value > threshold) {
            println("ALERT! Sensor $sensorId exceeded threshold with value $value")
        }
    }
}
