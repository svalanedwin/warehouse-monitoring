# Use an OpenJDK base image to run Kotlin
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the compiled JAR file into the container
COPY build/libs/warehouse-monitoring-1.0-SNAPSHOT.jar /app/warehouse-monitoring.jar

# Expose the port your application will run on
EXPOSE 8080

# Optional: Health check (if your application has a health endpoint)
# HEALTHCHECK --interval=30s --timeout=10s --retries=3 \
#   CMD curl --fail http://localhost:8080/health || exit 1

# Command to run the application
ENTRYPOINT ["java", "-jar", "warehouse-monitoring.jar"]
