package com.warehousemonitoring.unit

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class ThresholdTest : StringSpec({
    "temperature exceeds threshold" {
        checkThreshold("sensor_id=t1;value=36", 35) shouldBe true
    }

    "temperature below threshold" {
        checkThreshold("sensor_id=t1;value=34", 35) shouldBe false
    }

    "humidity exceeds threshold" {
        checkThreshold("sensor_id=h1;value=51", 50) shouldBe true
    }

    "humidity below threshold" {
        checkThreshold("sensor_id=h1;value=49", 50) shouldBe false
    }
})

private fun checkThreshold(data: String, threshold: Int): Boolean {
    val value = data.split(";")[1].split("=")[1].toInt()
    return value > threshold
}