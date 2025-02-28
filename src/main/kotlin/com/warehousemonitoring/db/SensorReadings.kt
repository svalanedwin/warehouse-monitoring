package com.warehousemonitoring.db

import org.jetbrains.exposed.sql.Table  // Importing Exposed's Table class for defining the schema
import org.jetbrains.exposed.sql.javatime.datetime  // Importing support for Java 8+ date-time handling
import java.time.LocalDateTime  // Importing LocalDateTime for timestamp management

/**
 * Object representing the SensorReadings table in the database.
 */
object SensorReadings : Table("\"SensorReadings\"") {  // Explicitly setting table name with quotes to handle case sensitivity
    // Auto-incrementing primary key for unique identification of each record
    val id = integer("id").autoIncrement()

    // Column for storing sensor ID (max length: 10 characters)
    val sensorId = varchar("sensor_id", 10)

    // Column for storing sensor readings (assumed to be an integer value)
    val value = integer("value")

    // Column for storing the timestamp of the reading, with a default value of the current time
    val timestamp = datetime("timestamp").default(LocalDateTime.now())

    // Defining the primary key constraint for the table
    override val primaryKey = PrimaryKey(id)
}
