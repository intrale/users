import ar.com.intrale.Business
import ar.com.intrale.BusinessState
import kotlin.test.Test
import kotlin.test.assertEquals

class BusinessTest {
    @Test
    fun defaultStateIsPending() {
        val business = Business()
        assertEquals(BusinessState.PENDING, business.state)
    }

    @Test
    fun propertiesCanBeAssigned() {
        val business = Business()
        business.name = "Biz"
        business.emailAdmin = "admin@biz.com"
        business.description = "desc"
        business.state = BusinessState.APPROVED

        assertEquals("Biz", business.name)
        assertEquals("admin@biz.com", business.emailAdmin)
        assertEquals("desc", business.description)
        assertEquals(BusinessState.APPROVED, business.state)
    }
}
