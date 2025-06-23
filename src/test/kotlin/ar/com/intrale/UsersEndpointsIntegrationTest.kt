package ar.com.intrale

import com.google.gson.Gson
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.direct
import org.kodein.di.instance
import org.kodein.di.ktor.di
import org.kodein.di.ktor.closestDI
import org.kodein.di.singleton
import org.slf4j.helpers.NOPLogger
import kotlin.test.Test
import kotlin.test.assertEquals

class UsersEndpointsIntegrationTest {

    private class DummyFunction : Function {
        override suspend fun execute(business: String, function: String, headers: Map<String, String>, textBody: String): Response {
            return Response(HttpStatusCode.OK)
        }
    }

    private val module = DI.Module(name = "testModule") {
        bind<org.slf4j.Logger>() with singleton { NOPLogger.NOP_LOGGER }
        bind<UsersConfig>() with singleton {
            UsersConfig(setOf("intrale"), "us-east-1", "key", "secret", "pool", "client")
        }
        val names = listOf(
            "signup",
            "signupPlatformAdmin",
            "signupDelivery",
            "signupSaler",
            "signin",
            "validate",
            "recovery",
            "confirm",
            "changePassword",
            "profiles",
            "2fasetup",
            "2faverify",
            "registerBusiness",
            "reviewBusiness",
            "assignProfile"
        )
        names.forEach { name ->
            bind<Function>(tag = name) with singleton { DummyFunction() }
        }
    }

    private fun Application.testApp() {
        di { import(module) }
        routing {
            post("/{business}/{function}") {
                val di = closestDI()
                val logger: org.slf4j.Logger by di.instance()

                val businessName = call.parameters["business"]
                val functionName = call.parameters["function"]

                val functionResponse: Response = if (businessName == null) {
                    RequestValidationException("No business defined on path")
                } else {
                    val config = di.direct.instance<Config>()
                    logger.info("config.businesses: ${'$'}{config.businesses}")
                    if (!config.businesses.contains(businessName)) {
                        ExceptionResponse("Business not avaiable with name ${'$'}businessName")
                    } else {
                        if (functionName == null) {
                            RequestValidationException("No function defined on path")
                        } else {
                            try {
                                logger.info("Injecting Function ${'$'}functionName")
                                val function = di.direct.instance<Function>(tag = functionName)
                                val headers: Map<String, String> = call.request.headers.entries().associate { it.key to it.value.joinToString(",") }
                                function.execute(businessName, functionName, headers, call.receiveText())
                            } catch (_: DI.NotFoundException) {
                                ExceptionResponse("No function with name ${'$'}functionName found")
                            }
                        }
                    }
                }

                call.respondText(
                    text = Gson().toJson(functionResponse),
                    contentType = ContentType.Application.Json,
                    status = functionResponse.statusCode
                )
            }
            options {
                call.response.headers.append("Access-Control-Allow-Origin", "*")
                call.response.headers.append("Access-Control-Allow-Methods", "GET, OPTIONS, HEAD, PUT, POST")
                call.response.headers.append(
                    "Access-Control-Allow-Headers",
                    "Content-Type,Accept,Referer,User-Agent,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token,Access-Control-Allow-Origin,Access-Control-Allow-Headers,function,idToken,businessName,filename"
                )
                call.respond(HttpStatusCode.OK)
            }
        }
    }

    @Test
    fun allEndpointsReturnOk() = withTestApplication({ testApp() }) {
        val names = listOf(
            "signup",
            "signupPlatformAdmin",
            "signupDelivery",
            "signupSaler",
            "signin",
            "validate",
            "recovery",
            "confirm",
            "changePassword",
            "profiles",
            "2fasetup",
            "2faverify",
            "registerBusiness",
            "reviewBusiness",
            "assignProfile"
        )
        names.forEach { name ->
            val call = handleRequest(HttpMethod.Post, "/intrale/$name") {
                setBody("{}")
            }
            assertEquals(HttpStatusCode.OK, call.response.status())
        }
    }
}

