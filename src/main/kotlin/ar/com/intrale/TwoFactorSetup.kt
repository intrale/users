package ar.com.intrale

import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
import aws.sdk.kotlin.services.cognitoidentityprovider.CognitoIdentityProviderClient
import aws.sdk.kotlin.services.cognitoidentityprovider.getUser
import aws.smithy.kotlin.runtime.auth.awscredentials.Credentials
import net.datafaker.Faker
import org.apache.commons.codec.binary.Base32
import org.slf4j.Logger
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import java.security.SecureRandom
import kotlin.math.log

class TwoFactorSetup (override val config: UsersConfig, val faker: Faker, override val logger: Logger) :
    SecuredFunction(config=config, logger=logger ) {


        override suspend fun securedExecute(
        business: String,
        function: String,
        headers: Map<String, String>,
        textBody: String
    ): Response {
            logger.debug("starting two factor setup $function")

            val dynamoDbClient: DynamoDbClient = DynamoDbClient.builder()
                .region(Region.of(config.region))
                .credentialsProvider(software.amazon.awssdk.auth.credentials.StaticCredentialsProvider.create(AwsBasicCredentials.create(config.accessKeyId, config.secretAccessKey)))
                .build()

            logger.debug("dynamoDbClient ok")
            val enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build()

            logger.debug("enhancedClient ok")
            val table = enhancedClient.table("users", TableSchema.fromBean(User::class.java))

            logger.debug("checking accessToken")
            CognitoIdentityProviderClient {
                region = config.region
                credentialsProvider = StaticCredentialsProvider(Credentials(
                    accessKeyId = config.accessKeyId,
                    secretAccessKey = config.secretAccessKey
                ))
            }.use { identityProviderClient ->
                val response = identityProviderClient.getUser {
                    this.accessToken = headers["Authorization"]
                }

                logger.debug("trying to get user $response")
                val email = response.userAttributes?.firstOrNull { it.name == "email" }?.value
                val username = response.username
                val secret = generateSecret()

                if (email != null) {
                   val user = User(
                        email = email,
                        secret = secret
                    )
                    logger.debug("persisting user $user")
                    table.putItem(user)
                } else {
                    logger.error("failed to get user")
                    return ExceptionResponse("Email not found")
                }
                logger.debug ("return two factor setup $function")
                return TwoFactorSetupResponse(buildOtpAuthUri(secret, email))
            }
        logger.error("failed to get two factor setup $function")
        return ExceptionResponse()
    }


    fun generateSecret(): String {
        val random = SecureRandom()
        val buffer = ByteArray(20)
        random.nextBytes(buffer)
        return Base32().encodeToString(buffer)
    }

    fun buildOtpAuthUri(secret: String, email: String?, issuer: String = "intrale"): String {
        return "otpauth://totp/${issuer}:${email}?secret=$secret&issuer=$issuer&algorithm=SHA1&digits=6&period=30"
    }

}