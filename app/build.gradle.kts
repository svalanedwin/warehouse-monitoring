plugins {
    kotlin("jvm") version "1.9.0"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

application {
    // Update with the correct package and main class
    mainClass.set("com.warehousemonitoring.MainKt")
}

repositories {
    mavenCentral()
}

dependencies {
    // Kotlin Standard Library
    implementation("org.jetbrains.kotlin:kotlin-stdlib")

    // Ktor: Netty engine and core functionality
    implementation("io.ktor:ktor-server-netty:2.3.4")
    implementation("io.ktor:ktor-server-core:2.3.4")

    // Logging with Logback
    implementation("ch.qos.logback:logback-classic:1.4.7")

    // Apache Kafka Client for producing/consuming messages
    implementation("org.apache.kafka:kafka-clients:3.5.1")

    // Database: Exposed ORM (core, DAO, JDBC) and PostgreSQL Driver
    implementation("org.jetbrains.exposed:exposed-core:0.43.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.43.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.43.0")
    implementation("org.postgresql:postgresql:42.5.1")

    // Testing: Kotlin test framework + JUnit Jupiter engine
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
}

tasks.test {
    useJUnitPlatform()
}

// Use Java toolchain for consistent Java version (Java 17 is recommended for stability and features)
kotlin {
    jvmToolchain(17)
}
