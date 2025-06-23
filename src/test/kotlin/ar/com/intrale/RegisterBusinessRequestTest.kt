package ar.com.intrale

import kotlin.test.Test
import kotlin.test.assertEquals

class RegisterBusinessRequestTest {
    @Test
    fun `values are assigned`() {
        val req = RegisterBusinessRequest("Biz", "admin@biz.com", "desc")
        assertEquals("Biz", req.name)
        assertEquals("admin@biz.com", req.emailAdmin)
        assertEquals("desc", req.description)
    }
}
