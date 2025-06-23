package ar.com.intrale

import kotlin.test.Test
import kotlin.test.assertEquals

class ConfirmPasswordRecoveryRequestTest {
    @Test
    fun `constructor sets values`() {
        val req = ConfirmPasswordRecoveryRequest("user@test.com", "code", "pass")
        assertEquals("user@test.com", req.email)
        assertEquals("code", req.code)
        assertEquals("pass", req.password)
    }
}
