package ar.com.intrale

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class AssignProfileRequestTest {
    @Test
    fun `valores se asignan correctamente`() {
        val req = AssignProfileRequest("user@test.com", "CLIENT")
        assertEquals("user@test.com", req.email)
        assertEquals("CLIENT", req.profile)
    }

    @Test
    fun `objetos iguales tienen igualdad estructural`() {
        val req1 = AssignProfileRequest("user@test.com", "CLIENT")
        val req2 = AssignProfileRequest("user@test.com", "CLIENT")
        assertEquals(req1, req2)
    }

    @Test
    fun `copy permite modificar campos`() {
        val req = AssignProfileRequest("user@test.com", "CLIENT")
        val copy = req.copy(profile = "ADMIN")
        assertNotEquals(req.profile, copy.profile)
        assertEquals("ADMIN", copy.profile)
        assertEquals(req.email, copy.email)
    }
}
