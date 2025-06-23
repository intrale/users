package ar.com.intrale

import kotlin.test.Test
import kotlin.test.assertEquals

class SignUpRequestTest {
    @Test
    fun `email stored`() {
        val req = SignUpRequest("user@test.com")
        assertEquals("user@test.com", req.email)
    }
}
