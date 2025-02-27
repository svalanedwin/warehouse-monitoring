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
    // Logger instance for logging database initialization steps
    private val logger = LoggerFactory.getLogger(KafkaProducerService::class.java)

    /**
     * Initializes the database connection and ensures the necessary table exists.
     */
    fun init() {
        // Database connection details (Consider using environment variables or configuration files for security)
        val dbUrl = "jdbc:postgresql://localhost:5433/warehouse"
        val dbUser = "user"
        val dbPassword = "password"

        // Logging the database connection attempt
        logger.info("Connecting to database: $dbUrl")

        // Establishing connection to the PostgreSQL database
        Database.connect(dbUrl, driver = "org.postgresql.Driver", user = dbUser, password = dbPassword)

        // Executing a transaction to ensure table existence
        transaction {
            // Logging schema creation/update process
            logger.info("Creating or updating table SensorReadings")

            // Automatically creates missing tables and columns for the SensorReadings entity
            SchemaUtils.createMissingTablesAndColumns(SensorReadings)
        }
    }
}
