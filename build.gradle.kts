plugins {
    kotlin("jvm") version "1.9.0"  // Using Kotlin JVM plugin with version 1.9.0
    application  // Plugin for creating a runnable application
}

group = "com.warehousemonitoring"  // Defines the package group for the project
version = "1.0-SNAPSHOT"  // Version of the application

application {
    mainClass.set("com.warehousemonitoring.MainKt")  // Sets the main entry point of the application
}

repositories {
    mavenCentral()  // Use Maven Central as the primary repository for dependencies
}

dependencies {
    // Core Kotlin and Coroutines libraries
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinx.coroutines.core)

    // Logging dependencies
    implementation(libs.slf4j.api)  // SLF4J logging API
    implementation(libs.logback.classic)  // Logback for logging implementation

    // Kafka dependencies
    implementation(libs.kafka.clients)

    // Exposed ORM (for database interaction)
    implementation(libs.exposed.core)  // Core Exposed library
    implementation(libs.exposed.dao)  // DAO support
    implementation(libs.exposed.jdbc)  // JDBC support
    implementation(libs.exposed.java.time)  // Java Time support for Exposed

    // PostgreSQL driver
    implementation(libs.postgresql)

    // Testing dependencies
    testImplementation(libs.kotest.runner.junit5)  // Kotest runner for JUnit 5
    testImplementation(libs.kotest.assertions.core)  // Kotest assertion library
    testImplementation(libs.mockk)  // Mocking library for Kotlin

    // Testcontainers for integration testing with Kafka and PostgreSQL
    testImplementation(libs.testcontainers)
    testImplementation(libs.testcontainers.junit.jupiter)
    testImplementation(libs.testcontainers.kafka)
    testImplementation(libs.testcontainers.postgresql)

    // REST API testing
    testImplementation(libs.rest.assured)

    // Configuration file support
    implementation(libs.typesafe.config)
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1") // Dotenv library for Kotlinimplementation("io.github.cdimascio:dotenv-kotlin:6.4.1") // Dotenv library for Kotlin
}

tasks.test {
    useJUnitPlatform()  // Configure tests to use JUnit 5
}

tasks.jar {
    manifest {
        attributes("Main-Class" to "com.warehousemonitoring.MainKt")
    }
    archiveFileName.set("warehouse-monitoring.jar")
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}


kotlin {
    jvmToolchain(17)  // Set JVM toolchain to Java 17
}
