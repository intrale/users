package ar.com.intrale

import kotlin.test.Test
import kotlin.test.assertEquals

class SignUpRequestTest {
    @Test
    fun testEquality() {
        val req1 = SignUpRequest("test@example.com")
        val req2 = SignUpRequest("test@example.com")
        assertEquals(req1, req2)
    }
}
