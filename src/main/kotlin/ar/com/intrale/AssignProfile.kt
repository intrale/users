package ar.com.intrale

import com.google.gson.Gson
import com.auth0.jwt.JWT
import io.konform.validation.Validation
import io.konform.validation.ValidationResult
import io.konform.validation.jsonschema.pattern
import org.slf4j.Logger
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable

// Uso constante definida en ReviewBusinessRegistration.kt

data class AssignProfileRequest(val email: String, val profile: String)

class AssignProfile(
    override val config: UsersConfig,
    override val logger: Logger,
    private val tableProfiles: DynamoDbTable<UserBusinessProfile>
) : SecuredFunction(config = config, logger = logger) {

    fun requestValidation(body: AssignProfileRequest): Response? {
        val validation = Validation<AssignProfileRequest> {
            AssignProfileRequest::email required {
                pattern(".+@.+\\..+") hint "El campo email debe tener formato de email. Valor actual: '{value}'"
            }
            AssignProfileRequest::profile required {}
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
        logger.debug("starting assign profile $function")
        if (textBody.isEmpty()) return RequestValidationException("Request body not found")
        val body = Gson().fromJson(textBody, AssignProfileRequest::class.java)
        val validation = requestValidation(body)
        if (validation != null) return validation

        val token = headers["Authorization"]
        if (token != null) {
            val decoded = JWT.decode(token)
            val profile = decoded.getClaim(PROFILE_ATT_NAME).asString()
            if (PLATFORM_ADMIN_PROFILE != profile) {
                return UnauthorizeExeption()
            }
        } else {
            return UnauthorizeExeption()
        }

        val item = UserBusinessProfile().apply {
            email = body.email
            this.business = business
            this.profile = body.profile
        }
        tableProfiles.putItem(item)
        logger.debug("profile assigned $item")
        return Response()
    }
}
