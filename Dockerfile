# Use an OpenJDK base image to run Kotlin
FROM openjdk:17-jdk-slim AS build

# Set the working directory inside the container
WORKDIR /workspace

# Copy the Gradle wrapper files
COPY gradlew .
COPY gradle ./gradle

# Copy the Gradle build files
COPY build.gradle.kts settings.gradle.kts ./

# Copy the source code
COPY src ./src

# Make the gradlew script executable
RUN chmod +x gradlew

# Build the JAR file
RUN ./gradlew build

# Use a smaller runtime image
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /workspace/build/libs/warehouse-monitoring.jar /app/warehouse-monitoring.jar

# Expose the port your application will run on
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "warehouse-monitoring.jar"]