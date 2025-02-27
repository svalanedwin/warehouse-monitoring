plugins {
    kotlin("jvm") version( "1.9.0")
    application
}

group = "com.warehousemonitoring"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("com.warehousemonitoring.MainKt")
}

repositories {
    mavenCentral()
}

dependencies {

        implementation(libs.kotlin.stdlib)
        implementation(libs.kotlinx.coroutines.core)
        implementation(libs.slf4j.api)
        implementation(libs.logback.classic)
        implementation(libs.kafka.clients)
        implementation(libs.exposed.core)
        implementation(libs.exposed.dao)
        implementation(libs.exposed.jdbc)
        implementation(libs.exposed.java.time)
        implementation(libs.postgresql)
        testImplementation(libs.kotest.runner.junit5)
        testImplementation(libs.kotest.assertions.core)
        testImplementation(libs.mockk)
        testImplementation(libs.testcontainers)
        testImplementation(libs.testcontainers.junit.jupiter)
        testImplementation(libs.testcontainers.kafka)
        testImplementation(libs.testcontainers.postgresql)
        testImplementation(libs.rest.assured)
        implementation(libs.typesafe.config)

}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    manifest {
        attributes("Main-Class" to "com.warehousemonitoring.MainKt")
    }
}

kotlin {
    jvmToolchain(17)
}