package ar.com.intrale

import kotlin.test.Test
import kotlin.test.assertEquals

class UserTest {
    @Test
    fun `default values and setters work`() {
        val user = User()
        user.email = "mail@test.com"
        user.name = "Name"
        user.familyName = "Last"
        user.secret = "secret"
        user.enabled = true

        assertEquals("mail@test.com", user.email)
        assertEquals("Name", user.name)
        assertEquals("Last", user.familyName)
        assertEquals("secret", user.secret)
        assertEquals(true, user.enabled)
    }
}
