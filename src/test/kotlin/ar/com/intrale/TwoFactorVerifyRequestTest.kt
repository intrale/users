package ar.com.intrale

import kotlin.test.Test
import kotlin.test.assertEquals

class TwoFactorVerifyRequestTest {
    @Test
    fun `code stored`() {
        val req = TwoFactorVerifyRequest("123456")
        assertEquals("123456", req.code)
    }
}
