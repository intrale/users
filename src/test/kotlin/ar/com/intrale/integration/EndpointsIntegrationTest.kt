package ar.com.intrale.integration

import ar.com.intrale.Config
import ar.com.intrale.Function
import ar.com.intrale.Response
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
import org.kodein.di.ktor.closestDI
import org.kodein.di.ktor.di
import org.kodein.di.singleton
import org.slf4j.Logger
import org.slf4j.helpers.NOPLogger
import kotlin.test.Test
import kotlin.test.assertEquals

private class DummyFunction(private val status: HttpStatusCode = HttpStatusCode.Created) : Function {
    override suspend fun execute(business: String, function: String, headers: Map<String, String>, textBody: String): Response {
        return Response(status)
    }
}

class EndpointsIntegrationTest {

    private val logger: Logger = NOPLogger.NOP_LOGGER
    private val config = Config(setOf("intrale"), "us-east-1", "pool", "client")

    private fun DI.MainBuilder.bindDummyFunctions() {
        val dummy = DummyFunction()
        bind<Logger>() with singleton { logger }
        bind<Config>() with singleton { config }
        bind<Function>(tag = "signup") with singleton { dummy }
        bind<Function>(tag = "signupPlatformAdmin") with singleton { dummy }
        bind<Function>(tag = "signupDelivery") with singleton { dummy }
        bind<Function>(tag = "signupSaler") with singleton { dummy }
        bind<Function>(tag = "signin") with singleton { dummy }
        bind<Function>(tag = "validate") with singleton { dummy }
        bind<Function>(tag = "recovery") with singleton { dummy }
        bind<Function>(tag = "confirm") with singleton { dummy }
        bind<Function>(tag = "changePassword") with singleton { dummy }
        bind<Function>(tag = "profiles") with singleton { dummy }
        bind<Function>(tag = "2fasetup") with singleton { dummy }
        bind<Function>(tag = "2faverify") with singleton { dummy }
        bind<Function>(tag = "registerBusiness") with singleton { dummy }
        bind<Function>(tag = "reviewBusiness") with singleton { dummy }
        bind<Function>(tag = "assignProfile") with singleton { dummy }
    }

    @Test
    fun allEndpointsReturnCreated() = testApplication {
        application {
            di { bindDummyFunctions() }
            routing {
                post("/{business}/{function}") {
                    val di = closestDI()
                    val business = call.parameters["business"] ?: ""
                    val functionName = call.parameters["function"] ?: ""
                    val function = di.direct.instance<Function>(tag = functionName)
                    val headers = call.request.headers.entries().associate { it.key to it.value.joinToString(",") }
                    val response = function.execute(business, functionName, headers, call.receiveText())
                    call.respondText("", status = response.statusCode)
                }
            }
        }
        val functions = listOf(
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
        for (fn in functions) {
            val response = client.post("/intrale/$fn") {
                setBody("{}")
                header(HttpHeaders.ContentType, "application/json")
            }
            assertEquals(HttpStatusCode.Created, response.status, fn)
        }
    }
}
