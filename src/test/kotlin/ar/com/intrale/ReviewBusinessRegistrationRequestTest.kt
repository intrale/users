package ar.com.intrale

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ReviewBusinessRegistrationRequestTest {
    @Test
    fun `valores se asignan correctamente`() {
        val req = ReviewBusinessRegistrationRequest("Biz", "APPROVED", "123456")
        assertEquals("Biz", req.name)
        assertEquals("APPROVED", req.decision)
        assertEquals("123456", req.twoFactorCode)
    }

    @Test
    fun `objetos iguales tienen igualdad estructural`() {
        val r1 = ReviewBusinessRegistrationRequest("Biz", "APPROVED", "123456")
        val r2 = ReviewBusinessRegistrationRequest("Biz", "APPROVED", "123456")
        assertEquals(r1, r2)
    }

    @Test
    fun `copy permite modificar campos`() {
        val req = ReviewBusinessRegistrationRequest("Biz", "APPROVED", "123456")
        val copy = req.copy(decision = "REJECTED")
        assertNotEquals(req.decision, copy.decision)
        assertEquals("REJECTED", copy.decision)
    }

    @Test
    fun `toString refleja los valores`() {
        val req = ReviewBusinessRegistrationRequest("Biz", "APPROVED", "123456")
        val esperado = "ReviewBusinessRegistrationRequest(name=Biz, decision=APPROVED, twoFactorCode=123456)"
        assertEquals(esperado, req.toString())
    }
}
