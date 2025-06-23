package ar.com.intrale

import kotlin.test.Test
import kotlin.test.assertEquals

class UserBusinessProfileTest {
    @Test
    fun `composite key updates`() {
        val ubp = UserBusinessProfile()
        ubp.email = "mail@test.com"
        ubp.business = "biz"
        ubp.profile = "CLIENT"
        assertEquals("mail@test.com#biz#CLIENT", ubp.compositeKey)
    }
}
