package ar.com.intrale

import kotlin.test.Test
import kotlin.test.assertEquals

class TwoFactorSetupResponseTest {
    @Test
    fun `otpUri stored`() {
        val resp = TwoFactorSetupResponse("uri")
        assertEquals("uri", resp.otpAuthUri)
    }
}
