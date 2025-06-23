package ar.com.intrale

import kotlin.test.Test
import kotlin.test.assertEquals

class SignInRequestTest {
    @Test
    fun `properties assigned`() {
        val req = SignInRequest("email", "pass", "new", "name", "last")
        assertEquals("email", req.email)
        assertEquals("pass", req.password)
        assertEquals("new", req.newPassword)
        assertEquals("name", req.name)
        assertEquals("last", req.familyName)
    }
}
