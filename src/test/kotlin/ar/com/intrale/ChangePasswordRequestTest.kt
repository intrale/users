package ar.com.intrale

import kotlin.test.Test
import kotlin.test.assertEquals

class ChangePasswordRequestTest {
    @Test
    fun `constructor sets fields correctly`() {
        val req = ChangePasswordRequest("old", "new")
        assertEquals("old", req.oldPassword)
        assertEquals("new", req.newPassword)
    }
}
