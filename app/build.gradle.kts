plugins {
    kotlin("jvm") version "1.9.0"
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
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.slf4j:slf4j-api:2.0.7")
    implementation("ch.qos.logback:logback-classic:1.4.7")
    implementation("org.apache.kafka:kafka-clients:3.5.1")
    implementation("org.jetbrains.exposed:exposed-core:0.43.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.43.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.43.0")
    implementation("org.jetbrains.exposed:exposed-java-time:0.43.0")
    implementation("org.postgresql:postgresql:42.5.1")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
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