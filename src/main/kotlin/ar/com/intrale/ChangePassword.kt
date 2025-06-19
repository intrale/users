package ar.com.intrale

import aws.sdk.kotlin.services.cognitoidentityprovider.CognitoIdentityProviderClient
import aws.sdk.kotlin.services.cognitoidentityprovider.model.ChangePasswordRequest as CognitoChangePasswordRequest
import com.google.gson.Gson
import io.konform.validation.Validation
import io.konform.validation.ValidationResult
import org.slf4j.Logger

class ChangePassword(
    override val config: UsersConfig,
    override val logger: Logger,
    private val cognito: CognitoIdentityProviderClient
) : SecuredFunction(config = config, logger = logger) {

    private fun requestValidation(body: ChangePasswordRequest): Response? {
        val validation = Validation<ChangePasswordRequest> {
            ChangePasswordRequest::previousPassword required {}
            ChangePasswordRequest::proposedPassword required {}
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
        if (textBody.isEmpty()) return RequestValidationException("Request body not found")
        val body = Gson().fromJson(textBody, ChangePasswordRequest::class.java)
        val validationResponse = requestValidation(body)
        if (validationResponse != null) return validationResponse

        try {
            cognito.use { client ->
                client.changePassword(
                    CognitoChangePasswordRequest {
                        accessToken = headers["Authorization"]
                        previousPassword = body.previousPassword
                        proposedPassword = body.proposedPassword
                    }
                )
                return Response()
            }
        } catch (e: Exception) {
            logger.error("Error al cambiar la contraseña: ${e.message}", e)
            return ExceptionResponse(e.message ?: "Internal Server Error")
        }
        return ExceptionResponse("Unknown error")
    }
}
