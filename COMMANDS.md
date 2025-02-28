# Warehouse Monitoring System - Command Summary

This file provides a quick reference for all commands required to set up, run, and monitor the Warehouse Monitoring System.

---

## **1. Manual Setup (Using `.env`)**

| **Step**                          | **Command**                                                                 |
|------------------------------------|-----------------------------------------------------------------------------|
| **Start Kafka & PostgreSQL**       | `docker-compose up -d zookeeper kafka postgres`                             |
| **Build the Application**          | `./gradlew build`                                                           |
| **Run the Application**            | `java -jar build/libs/warehouse-monitoring.jar`                             |
| **Simulate Sensor Data (UDP)**     |                                                                             |
| - Temperature Sensor               | `echo "sensor_id=t1;value=30" | nc -u -w1 localhost 3344`                   |
| - Humidity Sensor                  | `echo "sensor_id=h1;value=40" | nc -u -w1 localhost 3355`                   |
| **Check Application Logs**         | View logs in the console where the application is running.                  |
| **Access PostgreSQL**              | `psql -h localhost -p 5432 -U user -d warehouse`                           |
| **Query Sensor Data**              | `SELECT * FROM "SensorReadings";`                                           |
| **Stop Kafka & PostgreSQL**        | `docker-compose down`                                                       |

---

## **2. Docker Setup (Using `.env.docker`)**

| **Step**                          | **Command**                                                                 |
|------------------------------------|-----------------------------------------------------------------------------|
| **Build and Start Containers**     | `docker-compose up --build`                                                 |
| **Simulate Sensor Data (UDP)**     |                                                                             |
| - Temperature Sensor               | `echo "sensor_id=t1;value=30" | nc -u -w1 localhost 3345`                   |
| - Humidity Sensor                  | `echo "sensor_id=h1;value=40" | nc -u -w1 localhost 3356`                   |
| **Check Application Logs**         | `docker-compose logs -f warehouse-monitoring`                               |
| **Access PostgreSQL**              | `docker-compose exec postgres psql -U user -d warehouse`                    |
| **Query Sensor Data**              | `SELECT * FROM "SensorReadings";`                                           |
| **Consume Kafka Messages**         | `docker-compose exec kafka kafka-console-consumer --bootstrap-server kafka:9093 --topic sensor_data --from-beginning` |
| **Stop Containers**                | `docker-compose down`                                                       |

---

## **3. General Commands**

| **Step**                          | **Command**                                                                 |
|------------------------------------|-----------------------------------------------------------------------------|
| **Check Container Status**         | `docker-compose ps`                                                         |
| **Run Unit Tests**                 | `./gradlew test`                                                            |
| **Run Integration Tests**          | `./gradlew integrationTest`                                                 |
| **Rebuild and Restart Containers** | `docker-compose up --build --force-recreate`                                |
| **View Kafka Logs**                | `docker-compose logs kafka`                                                 |
| **View PostgreSQL Logs**           | `docker-compose logs postgres`                                              |

---

## **4. Troubleshooting Commands**

| **Step**                          | **Command**                                                                 |
|------------------------------------|-----------------------------------------------------------------------------|
| **Check Port Conflicts**           | `lsof -i :<port>` (e.g., `lsof -i :3344`)                                  |
| **Remove All Docker Containers**   | `docker-compose down --remove-orphans`                                      |
| **Remove Docker Volumes**          | `docker-compose down -v`                                                    |
| **Rebuild Docker Images**          | `docker-compose build --no-cache`                                           |

---

## **5. Docker Compose Quick Reference**

| **Command**                        | **Description**                                                             |
|------------------------------------|-----------------------------------------------------------------------------|
| `docker-compose up -d`             | Start all services in detached mode.                                        |
| `docker-compose up --build`        | Build and start all services.                                               |
| `docker-compose down`              | Stop and remove all containers.                                             |
| `docker-compose logs -f <service>` | View logs for a specific service (e.g., `warehouse-monitoring`).            |
| `docker-compose ps`                | List all running containers.                                                |
| `docker-compose exec <service>`    | Execute a command in a running container (e.g., `postgres` or `kafka`).     |

---

## **6. Simulating Sensor Data**

| **Sensor Type**                    | **Command**                                                                 |
|------------------------------------|-----------------------------------------------------------------------------|
| **Temperature Sensor**             | `echo "sensor_id=t1;value=30" | nc -u -w1 localhost 3344`                   |
| **Humidity Sensor**                | `echo "sensor_id=h1;value=40" | nc -u -w1 localhost 3355`                   |

---

## **7. Database Commands**

| **Step**                          | **Command**                                                                 |
|------------------------------------|-----------------------------------------------------------------------------|
| **Access PostgreSQL**              | `psql -h localhost -p 5432 -U user -d warehouse` (manual)                   |
|                                    | `docker-compose exec postgres psql -U user -d warehouse` (Docker)            |
| **List All Tables**                | `\dt`                                                                       |
| **Query Sensor Data**              | `SELECT * FROM "SensorReadings";`                                           |

---

## **8. Kafka Commands**

| **Step**                          | **Command**                                                                 |
|------------------------------------|-----------------------------------------------------------------------------|
| **Consume Kafka Messages**         | `docker-compose exec kafka kafka-console-consumer --bootstrap-server kafka:9093 --topic sensor_data --from-beginning` |
| **View Kafka Logs**                | `docker-compose logs kafka`                                                 |

---

This file serves as a quick reference for all commands required to set up, run, and monitor the Warehouse Monitoring System.