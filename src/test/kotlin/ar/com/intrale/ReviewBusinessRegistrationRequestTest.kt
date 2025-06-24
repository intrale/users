package ar.com.intrale

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ReviewBusinessRegistrationRequestTest {
    @Test
    fun `valores se asignan correctamente`() {
        val req = ReviewBusinessRegistrationRequest("Biz", "approved", "123456")
        assertEquals("Biz", req.name)
        assertEquals("approved", req.decision)
        assertEquals("123456", req.twoFactorCode)
    }

    @Test
    fun `objetos iguales tienen igualdad estructural`() {
        val req1 = ReviewBusinessRegistrationRequest("Biz", "approved", "123456")
        val req2 = ReviewBusinessRegistrationRequest("Biz", "approved", "123456")
        assertEquals(req1, req2)
    }

    @Test
    fun `copy permite modificar campos`() {
        val req = ReviewBusinessRegistrationRequest("Biz", "approved", "123456")
        val copy = req.copy(decision = "rejected")
        assertNotEquals(req.decision, copy.decision)
        assertEquals("rejected", copy.decision)
        assertEquals(req.name, copy.name)
        assertEquals(req.twoFactorCode, copy.twoFactorCode)
    }

    @Test
    fun `objetos con diferente nombre no son iguales`() {
        val req1 = ReviewBusinessRegistrationRequest("Biz1", "approved", "123456")
        val req2 = ReviewBusinessRegistrationRequest("Biz2", "approved", "123456")
        assertNotEquals(req1, req2)
    }

    @Test
    fun `toString refleja los valores`() {
        val req = ReviewBusinessRegistrationRequest("Biz", "approved", "123456")
        val esperado = "ReviewBusinessRegistrationRequest(name=Biz, decision=approved, twoFactorCode=123456)"
        assertEquals(esperado, req.toString())
    }
}
