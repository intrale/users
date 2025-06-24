package ar.com.intrale

import aws.sdk.kotlin.services.cognitoidentityprovider.CognitoIdentityProviderClient
import io.mockk.mockk
import org.slf4j.helpers.NOPLogger
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable
import kotlin.test.Test
import kotlin.test.assertEquals

class TwoFactorSetupTest {
    private val config = UsersConfig(setOf("test"), "us-east-1", "key", "secret", "pool", "client")
    private val cognito = CognitoIdentityProviderClient { region = config.region }
    private val tableUsers = mockk<DynamoDbTable<User>>(relaxed = true)
    private val setup = TwoFactorSetup(config, NOPLogger.NOP_LOGGER, cognito, tableUsers)

    @Test
    fun `generateSecret produce cadena base32 de 32 caracteres`() {
        val secret = setup.generateSecret()
        assertEquals(32, secret.length)
    }

    @Test
    fun `buildOtpAuthUri genera uri con issuer por defecto`() {
        val uri = setup.buildOtpAuthUri("SECRET", "user@test.com")
        val esperado = "otpauth://totp/intrale:user@test.com?secret=SECRET&issuer=intrale&algorithm=SHA1&digits=6&period=30"
        assertEquals(esperado, uri)
    }
}
