package ar.com.intrale

import kotlin.test.Test
import kotlin.test.assertEquals

class ProfileTest {
    @Test
    fun `profile name stored`() {
        val p = Profile("Client")
        assertEquals("Client", p.name)
    }
}
