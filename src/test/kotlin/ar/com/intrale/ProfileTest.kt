package ar.com.intrale

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ProfileTest {
    @Test
    fun `valores se asignan correctamente`() {
        val profile = Profile("ADMIN")
        assertEquals("ADMIN", profile.name)
    }

    @Test
    fun `objetos iguales tienen igualdad estructural`() {
        val p1 = Profile("ADMIN")
        val p2 = Profile("ADMIN")
        assertEquals(p1, p2)
    }

    @Test
    fun `copy permite modificar campos`() {
        val profile = Profile("ADMIN")
        val copy = profile.copy(name = "CLIENT")
        assertNotEquals(profile.name, copy.name)
        assertEquals("CLIENT", copy.name)
    }

    @Test
    fun `toString refleja los valores`() {
        val profile = Profile("ADMIN")
        val esperado = "Profile(name=ADMIN)"
        assertEquals(esperado, profile.toString())
    }
}
