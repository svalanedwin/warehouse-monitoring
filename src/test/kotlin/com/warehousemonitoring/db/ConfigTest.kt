package com.warehousemonitoring.db

import com.typesafe.config.ConfigFactory
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ConfigTest {

    @Test
    fun `test loading configuration`() {
        // Load the configuration file
        val config = ConfigFactory.load("application-test.conf")

        // Access configuration values
        val dbUrl = config.getString("database.url")
        val kafkaTopic = config.getString("kafka.topic")
        val udpTemperaturePort = config.getInt("udp.temperature.port")

        // Assert values
        assertEquals("jdbc:postgresql://localhost:5433/warehouse_test", dbUrl)
        assertEquals("sensor_data_test", kafkaTopic)
        assertEquals(3344, udpTemperaturePort)
    }
}