package com.warehousemonitoring.db

import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class SensorReadingsTest {

    @Test
    fun `test SensorReadings table schema`() {
        try {
            // Initialize the database connection
            DatabaseConfig.init()

            transaction {
                // Drop the table if it exists
                SchemaUtils.drop(SensorReadings)

                // Create the table
                SchemaUtils.create(SensorReadings)

                // Verify the columns
                val columns = SensorReadings.columns
                assertEquals(4, columns.size, "SensorReadings table should have 4 columns")
                assertEquals("id", columns[0].name, "First column should be 'id'")
                assertEquals("sensor_id", columns[1].name, "Second column should be 'sensor_id'")
                assertEquals("value", columns[2].name, "Third column should be 'value'")
                assertEquals("timestamp", columns[3].name, "Fourth column should be 'timestamp'")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }
}