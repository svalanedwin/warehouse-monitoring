package com.warehousemonitoring.db

import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue
import org.jetbrains.exposed.sql.exists

class DatabaseConfigTest {

    @Test
    fun `test database connection and table creation`() {
        // Initialize the database
        DatabaseConfig.init()

        // Verify that the SensorReadings table is created
        transaction {
            SchemaUtils.createMissingTablesAndColumns(SensorReadings)

            // Check if the table exists
            val tableExists = SensorReadings.exists()
            assertTrue(tableExists, "SensorReadings table should exist in the database")
        }
    }
}