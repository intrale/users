package ar.com.intrale

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ConfirmPasswordRecoveryRequestTest {
    @Test
    fun `valores se asignan correctamente`() {
        val req = ConfirmPasswordRecoveryRequest("user@test.com", "123456", "Pwd123!")
        assertEquals("user@test.com", req.email)
        assertEquals("123456", req.code)
        assertEquals("Pwd123!", req.password)
    }

    @Test
    fun `objetos iguales tienen igualdad estructural`() {
        val req1 = ConfirmPasswordRecoveryRequest("user@test.com", "123456", "Pwd123!")
        val req2 = ConfirmPasswordRecoveryRequest("user@test.com", "123456", "Pwd123!")
        assertEquals(req1, req2)
    }

    @Test
    fun `copy permite modificar campos`() {
        val req = ConfirmPasswordRecoveryRequest("user@test.com", "123456", "Pwd123!")
        val copy = req.copy(password = "NewPwd")
        assertNotEquals(req.password, copy.password)
        assertEquals("NewPwd", copy.password)
        assertEquals(req.email, copy.email)
        assertEquals(req.code, copy.code)
    }
}

