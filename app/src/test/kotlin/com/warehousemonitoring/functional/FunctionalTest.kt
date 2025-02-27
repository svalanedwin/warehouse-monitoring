package com.warehousemonitoring.functional

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.restassured.RestAssured
import io.restassured.http.ContentType
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class FunctionalTest : StringSpec({

    "end-to-end test for sensor data flow" {
        val message = "sensor_id=t1;value=36"
        val port = 3344

        // Send a UDP message to the server
        val socket = DatagramSocket()
        val buffer = message.toByteArray()
        val packet = DatagramPacket(buffer, buffer.size, InetAddress.getLocalHost(), port)
        socket.send(packet)
        socket.close()

        // Wait for the server to process the message
        Thread.sleep(1000) // Adjust the delay as needed

        // Verify the sensor data via the REST API
        val response = RestAssured.given()
            .contentType(ContentType.JSON)
            .get("/sensor-readings")

        // Assert the response
        response.statusCode shouldBe 200
        response.body().jsonPath().getString("sensor_id") shouldBe "t1"
        response.body().jsonPath().getInt("value") shouldBe 36
    }
}) {
    companion object {
        init {
            // Set up RestAssured when the class is loaded
            RestAssured.baseURI = "http://localhost:8080"
        }
    }
}