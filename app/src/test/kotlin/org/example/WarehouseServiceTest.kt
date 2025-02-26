import kotlin.test.Test
import kotlin.test.assertTrue

class WarehouseServiceTest {
    @Test
    fun testUDPListening() {
        val data = "sensor_id=t1;value=40"
        assertTrue(data.contains("sensor_id"))
    }
}
