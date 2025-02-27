import com.warehousemonitoring.db.SensorReadings
import com.warehousemonitoring.kafka.KafkaProducerService
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import java.util.*
object DatabaseConfig {
    private val logger = LoggerFactory.getLogger(KafkaProducerService::class.java)
    fun init() {
        val dbUrl = "jdbc:postgresql://localhost:5433/warehouse"
        val dbUser = "user"
        val dbPassword = "password"

        logger.info("Connecting to database: $dbUrl")
        Database.connect(dbUrl, driver = "org.postgresql.Driver", user = dbUser, password = dbPassword)

        transaction {
            logger.info("Creating or updating table SensorReadings")
            SchemaUtils.createMissingTablesAndColumns(SensorReadings)
        }
    }
}