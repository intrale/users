package ar.com.intrale

import kotlin.test.Test
import kotlin.test.assertEquals

class UsersConfigTest {
    @Test
    fun `valores se asignan correctamente`() {
        val config = UsersConfig(setOf("biz"), "us-east-1", "key", "secret", "pool", "client")
        assertEquals(setOf("biz"), config.businesses)
        assertEquals("us-east-1", config.region)
        assertEquals("key", config.accessKeyId)
        assertEquals("secret", config.secretAccessKey)
        assertEquals("pool", config.awsCognitoUserPoolId)
        assertEquals("client", config.awsCognitoClientId)
    }

    @Test
    fun `copy permite modificar campos`() {
        val config = UsersConfig(setOf("biz"), "us-east-1", "key", "secret", "pool", "client")
        val copy = config.copy(region = "us-west-2")
        assertEquals("us-west-2", copy.region)
        assertEquals(config.accessKeyId, copy.accessKeyId)
    }
}
