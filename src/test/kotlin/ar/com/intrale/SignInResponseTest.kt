package ar.com.intrale

import kotlin.test.Test
import kotlin.test.assertEquals

class SignInResponseTest {
    @Test
    fun `values stored`() {
        val resp = SignInResponse("id", "access", "refresh")
        assertEquals("id", resp.idToken)
        assertEquals("access", resp.accessToken)
        assertEquals("refresh", resp.refreshToken)
    }
}
