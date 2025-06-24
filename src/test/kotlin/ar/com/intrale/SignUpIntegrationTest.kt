package ar.com.intrale

import aws.sdk.kotlin.services.cognitoidentityprovider.CognitoIdentityProviderClient
import aws.sdk.kotlin.services.cognitoidentityprovider.model.AdminCreateUserResponse
import aws.sdk.kotlin.services.cognitoidentityprovider.model.AdminGetUserResponse
import aws.sdk.kotlin.services.cognitoidentityprovider.model.AdminUpdateUserAttributesResponse
import aws.sdk.kotlin.services.cognitoidentityprovider.model.AttributeType
import aws.sdk.kotlin.services.cognitoidentityprovider.model.UsernameExistsException
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import io.mockk.coEvery
import io.mockk.justRun
import io.mockk.mockk
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.direct
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI
import org.kodein.di.ktor.di
import org.kodein.di.singleton
import org.slf4j.LoggerFactory
import kotlin.test.Test
import kotlin.test.assertEquals

class SignUpIntegrationTest {
    private val config = UsersConfig(setOf("biz"), "us-east-1", "key", "secret", "pool", "client")

    private fun Application.testModule(cognito: CognitoIdentityProviderClient) {
        di {
            import(appModule, allowOverride = true)
            bind<UsersConfig>(overrides = true) { singleton { config } }
            bind<org.slf4j.Logger>(overrides = true) { singleton { LoggerFactory.getLogger("test") } }
            bind<CognitoIdentityProviderClient>(overrides = true) { singleton { cognito } }
        }
        routing {
            post("/{business}/{function}") {
                val di = closestDI()
                val logger: org.slf4j.Logger by di.instance()
                val businessName = call.parameters["business"]
                val functionName = call.parameters["function"]
                val functionResponse: Response = if (businessName == null) {
                    RequestValidationException("No business defined on path")
                } else {
                    val cfg = di.direct.instance<Config>()
                    logger.info("config.businesses: ${'$'}{cfg.businesses}")
                    if (!cfg.businesses.contains(businessName)) {
                        ExceptionResponse("Business not avaiable with name ${'$'}businessName")
                    } else {
                        if (functionName == null) {
                            RequestValidationException("No function defined on path")
                        } else {
                            try {
                                val function = di.direct.instance<Function>(tag = functionName)
                                val headers: Map<String, String> = call.request.headers.entries().associate { it.key to it.value.joinToString(",") }
                                function.execute(businessName, functionName, headers, call.receiveText())
                            } catch (e: DI.NotFoundException) {
                                ExceptionResponse("No function with name ${'$'}functionName found")
                            }
                        }
                    }
                }
                call.respondText(
                    text = com.google.gson.Gson().toJson(functionResponse),
                    contentType = ContentType.Application.Json,
                    status = functionResponse.statusCode
                )
            }
        }
    }

    @Test
    fun `registro exitoso de un nuevo usuario`() = testApplication {
        val cognito = mockk<CognitoIdentityProviderClient>()
        coEvery { cognito.adminCreateUser(any()) } returns AdminCreateUserResponse {}
        justRun { cognito.close() }
        application { testModule(cognito) }
        val response = client.post("/biz/signup") {
            header(HttpHeaders.ContentType, "application/json")
            setBody("{\"email\":\"new@test.com\"}")
        }
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `usuario existente retorna error`() = testApplication {
        val cognito = mockk<CognitoIdentityProviderClient>()
        coEvery { cognito.adminCreateUser(any()) } throws UsernameExistsException { message = "exists" }
        coEvery { cognito.adminGetUser(any()) } returns AdminGetUserResponse {
            username = "exist@test.com"
            userAttributes = listOf(AttributeType { name = BUSINESS_ATT_NAME; value = "biz" })
        }
        coEvery { cognito.adminUpdateUserAttributes(any()) } returns AdminUpdateUserAttributesResponse {}
        justRun { cognito.close() }
        application { testModule(cognito) }
        val response = client.post("/biz/signup") {
            header(HttpHeaders.ContentType, "application/json")
            setBody("{\"email\":\"exist@test.com\"}")
        }
        assertEquals(HttpStatusCode.InternalServerError, response.status)
    }
}

