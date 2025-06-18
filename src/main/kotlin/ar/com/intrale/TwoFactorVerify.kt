package ar.com.intrale

import aws.sdk.kotlin.services.cognitoidentityprovider.CognitoIdentityProviderClient
import aws.sdk.kotlin.services.cognitoidentityprovider.getUser
import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator
import com.google.gson.Gson
import io.konform.validation.Validation
import io.konform.validation.ValidationResult
import org.apache.commons.codec.binary.Base32
import org.slf4j.Logger
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable
import java.security.SecureRandom
import java.time.Instant
import javax.crypto.spec.SecretKeySpec

class TwoFactorVerify (override val config: UsersConfig, override val logger: Logger, val cognito: CognitoIdentityProviderClient, val tableUsers: DynamoDbTable<User>) :
    SecuredFunction(config=config, logger=logger ) {


    fun requestValidation(body:TwoFactorVerifyRequest): Response? {
        val validation = Validation<TwoFactorVerifyRequest> {
            TwoFactorVerifyRequest::code required {}
        }
        val validationResult: ValidationResult<Any> = try {
            validation(body)
        } catch (e: Exception) {
            e.printStackTrace()
            return RequestValidationException(e.message ?: "Unknown error")
        }
        if (!validationResult.isValid) {
            val errorsMessage = validationResult.errors.joinToString(" ") {
                "${it.dataPath.substring(1)} ${it.message}"
            }
            return RequestValidationException(errorsMessage)
        }
        return null
    }


        override suspend fun securedExecute(
        business: String,
        function: String,
        headers: Map<String, String>,
        textBody: String
    ): Response {
            logger.debug("starting two factor verify $function")

            // Validacion del request
            if (textBody.isEmpty()) return RequestValidationException("Request body not found")
            val body = Gson().fromJson(textBody, TwoFactorVerifyRequest::class.java)
            val response = requestValidation(body)
            if (response!=null) return response


            logger.debug("checking accessToken")
            cognito.use { identityProviderClient ->
                val response = identityProviderClient.getUser {
                    this.accessToken = headers["Authorization"]
                }

                logger.debug("trying to get user $response")
                val email = response.userAttributes?.firstOrNull { it.name == "email" }?.value
                val username = response.username

                if (email != null) {
                   var user = User(
                        email = email,
                    )
                    logger.debug("getting user $user")
                    user = tableUsers.getItem(user)

                    val generator = TimeBasedOneTimePasswordGenerator()
                    val key = SecretKeySpec(Base32().decode(user.secret), "HmacSHA1")

                    val now = Instant.now()
                    val expected = generator.generateOneTimePassword(key, now)

                    logger.debug("body.code=" + body.code + ", expected=" + expected)
                    if (body.code == expected.toString()){
                        return Response()
                    }

                    return ExceptionResponse("Invalid Two Factor Code")
                } else {
                    logger.error("failed to get user")
                    return ExceptionResponse("Email not found")
                }
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