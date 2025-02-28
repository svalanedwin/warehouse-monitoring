import com.warehousemonitoring.db.SensorReadings  // Importing the SensorReadings table schema
import com.warehousemonitoring.kafka.KafkaProducerService  // Importing KafkaProducerService for logging reference
import org.jetbrains.exposed.sql.Database  // Importing Exposed library for database connection
import org.jetbrains.exposed.sql.SchemaUtils  // Importing utilities for schema creation
import org.jetbrains.exposed.sql.transactions.transaction  // Importing transaction management for safe DB operations
import org.slf4j.LoggerFactory  // Importing SLF4J for logging
import java.util.*

/**
 * Object responsible for database configuration and initialization.
 */
object DatabaseConfig {
    private val logger = LoggerFactory.getLogger(KafkaProducerService::class.java)

    fun init() {
        val dbUrl = System.getenv("POSTGRES_URL") ?: "jdbc:postgresql://localhost:5432/warehouse"
        val dbUser = System.getenv("POSTGRES_USER") ?: "user"
        val dbPassword = System.getenv("POSTGRES_PASSWORD") ?: "password"

        logger.info("Connecting to database: $dbUrl")

        Database.connect(dbUrl, driver = "org.postgresql.Driver", user = dbUser, password = dbPassword)

        transaction {
            logger.info("Creating or updating table SensorReadings")
            SchemaUtils.createMissingTablesAndColumns(SensorReadings)
        }
    }
}
