package ar.com.intrale

import kotlin.test.Test
import kotlin.test.assertEquals

class PasswordRecoveryRequestTest {
    @Test
    fun `email is stored`() {
        val req = PasswordRecoveryRequest("user@test.com")
        assertEquals("user@test.com", req.email)
    }
}
