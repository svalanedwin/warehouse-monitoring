package com.warehousemonitoring.db

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object SensorReadings : Table("\"SensorReadings\"") {
    val id = integer("id").autoIncrement()
    val sensorId = varchar("sensor_id", 10)
    val value = integer("value")
    val timestamp = datetime("timestamp")

    override val primaryKey = PrimaryKey(id)
}