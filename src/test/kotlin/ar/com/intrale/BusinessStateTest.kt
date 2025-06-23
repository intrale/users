package ar.com.intrale

import kotlin.test.Test
import kotlin.test.assertEquals

class BusinessStateTest {
    @Test
    fun `enum has expected values`() {
        val states = BusinessState.values().map { it.name }
        assertEquals(listOf("PENDING","APPROVED","REJECTED"), states)
    }
}
