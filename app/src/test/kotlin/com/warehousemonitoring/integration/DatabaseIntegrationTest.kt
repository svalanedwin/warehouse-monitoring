package com.warehousemonitoring.integration

import com.typesafe.config.ConfigFactory
import com.warehousemonitoring.db.SensorReadings
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
class DatabaseIntegrationTest : StringSpec({

    val testConfig = ConfigFactory.load("application-test.conf")

    "database inserts sensor reading" {
        Database.connect(
            url = postgres.jdbcUrl, // Use container's JDBC URL
            user = postgres.username,
            password = postgres.password,
            driver = "org.postgresql.Driver"
        )

        transaction {
            SchemaUtils.create(SensorReadings)
        }

        transaction {
            SensorReadings.insert {
                it[sensorId] = "t1"
                it[value] = 36
                it[timestamp] = java.time.LocalDateTime.now()
            }
        }

        val count = transaction {
            SensorReadings.selectAll().count()
        }
        count shouldBe 1
    }
}) {
    companion object {
        @JvmStatic
        @Container
        val postgres = PostgreSQLContainer("postgres:latest").apply {
            start() // Explicitly start the container
        }
    }
}