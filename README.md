#  Warehouse Monitoring System

## 📖 Overview
The **Warehouse Monitoring System** is designed to track environmental conditions, such as temperature and humidity, within a warehouse. Sensors transmit data via **UDP**, which is then processed and monitored for threshold violations. The system consists of:

- **Warehouse Service**: Collects sensor data and publishes it to **Kafka**.
- **Central Monitoring Service**: Consumes data from Kafka, checks thresholds, and logs alarms if necessary.

---

## 🏗️ System Architecture
### **Components**
1. **Warehouse Service**
    - Listens for UDP messages from sensors.
    - Publishes sensor data to **Kafka**.
2. **Central Monitoring Service**
    - Consumes sensor data from **Kafka**.
    - Checks for threshold violations:
        - 🚨 Temperature > **35°C**
        - 🚨 Humidity > **50%**
    - Logs alarms when thresholds are exceeded.

---

## 📋 Prerequisites
Ensure you have the following installed:
- **Java 17** or higher
- **Docker & Docker Compose** (for containerized setup)
- **Kafka & PostgreSQL** (included in Docker setup)
- **Gradle** (for manual setup)

---

## 🚀 Setup & Running

### **Option 1: Running Manually** (Using `.env`)

#### 🛠️ Step 1: Set Up Environment Variables
Create a `.env` file in the root directory with the following:
```plaintext
POSTGRES_URL=jdbc:postgresql://localhost:5432/warehouse
POSTGRES_USER=user
POSTGRES_PASSWORD=password
KAFKA_BOOTSTRAP_SERVERS=localhost:9092
UDP_PORT_1=3344
UDP_PORT_2=3355
```

#### 🛠️ Step 2: Start Kafka & PostgreSQL
```sh
docker-compose up -d zookeeper kafka postgres
```

#### 🛠️ Step 3: Build & Run Application
```sh
./gradlew build
java -jar build/libs/warehouse-monitoring.jar
```

#### 🛠️ Step 4: Simulate Sensor Data (via UDP)
```sh
# Temperature Sensor
echo "sensor_id=t1;value=30" | nc -u -w1 localhost 3344

# Humidity Sensor
echo "sensor_id=h1;value=40" | nc -u -w1 localhost 3355
```

#### 🛠️ Step 5: Check Logs
```sh
INFO  Processing Kafka record: sensor_id=t1;value=30
INFO  Inserting sensor reading: sensor_id=t1, value=30
WARN  ALERT! Sensor t1 exceeded threshold with value 36
```

---

### **Option 2: Running with Docker** (Using `.env.docker`)

#### 🛠️ Step 1: Set Up Environment Variables
Create `.env.docker` with:
```plaintext
POSTGRES_URL=jdbc:postgresql://postgres:5432/warehouse
POSTGRES_USER=user
POSTGRES_PASSWORD=password
KAFKA_BOOTSTRAP_SERVERS=kafka:9093
UDP_PORT_1=3345
UDP_PORT_2=3356
```

#### 🛠️ Step 2: Run with Docker Compose
```sh
docker-compose up --build
```

#### 🛠️ Step 3: Simulate Sensor Data (via UDP)
```sh
# Temperature Sensor
echo "sensor_id=t1;value=30" | nc -u -w1 localhost 3345

# Humidity Sensor
echo "sensor_id=h1;value=40" | nc -u -w1 localhost 3356
```

#### 🛠️ Step 4: Check Logs
```sh
docker-compose logs -f warehouse-monitoring
```

---

## 📡 Kafka Monitoring

### **View Kafka Logs**
```sh
docker ps
```

### **Consume Messages from Kafka**
```sh
kafka-console-consumer --bootstrap-server kafka:9093 --topic sensor_data --from-beginning
```
**Example Output:**
```plaintext
sensor_id=t1;value=30
sensor_id=h1;value=40
```

---

## 🗄️ PostgreSQL Database Monitoring

### **Access PostgreSQL**
For **manual setup**:
```sh
psql -h localhost -p 5432 -U user -d warehouse
```
For **Docker setup**:
```sh
psql -h localhost -p 5433 -U user -d warehouse
```

### **Query Sensor Data**
```sql
\dt  -- List all tables
SELECT * FROM "SensorReadings";
```

---

## ✅ Testing
Run unit tests with:
```sh
./gradlew test
```

---

## 📂 Project Structure
```plaintext
warehouse-monitoring/
├── src/                     # Source code
├── build.gradle.kts         # Gradle build configuration
├── docker-compose.yml       # Docker Compose configuration
├── Dockerfile               # Dockerfile for building the application
├── .env                     # Environment variables for manual setup
├── .env.docker              # Environment variables for Docker setup
└── README.md                # This file
```

---

## 🛠️ Troubleshooting

🔴 **Port Conflicts**: Ensure required ports (3344, 3355, 5432, 9092, etc.) are not in use.

🔴 **Kafka Issues**: If Kafka fails, check logs:
```sh
docker-compose logs kafka
```

🔴 **Database Issues**: Verify PostgreSQL credentials in `.env` or `.env.docker`.


