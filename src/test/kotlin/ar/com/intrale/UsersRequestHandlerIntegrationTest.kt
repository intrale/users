package ar.com.intrale

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import io.ktor.http.HttpStatusCode
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton
import org.slf4j.helpers.NOPLogger
import java.util.Base64
import kotlin.test.Test
import kotlin.test.assertEquals

class UsersRequestHandlerIntegrationTest {

    private class DummyFunction : Function {
        override suspend fun execute(business: String, function: String, headers: Map<String, String>, textBody: String): Response {
            return Response(HttpStatusCode.OK)
        }
    }

    private val module = DI.Module(name = "testModule") {
        bind<org.slf4j.Logger>() with singleton { NOPLogger.NOP_LOGGER }
        bind<UsersConfig>() with singleton { UsersConfig(setOf("biz"), "us-east-1", "key", "secret", "pool", "client") }
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

    private val handler = object : LambdaRequestHandler() {
        override fun handleRequest(requestEvent: APIGatewayProxyRequestEvent?, context: Context?): com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent {
            return handle(module, requestEvent, context)
        }
    }

    private fun request(function: String) = APIGatewayProxyRequestEvent().apply {
        httpMethod = "POST"
        pathParameters = mapOf("business" to "biz", "function" to function)
        headers = emptyMap()
        body = Base64.getEncoder().encodeToString("{}".toByteArray())
    }

    @Test fun signup() { assertEquals(200, handler.handle(module, request("signup"), null).statusCode) }
    @Test fun signupPlatformAdmin() { assertEquals(200, handler.handle(module, request("signupPlatformAdmin"), null).statusCode) }
    @Test fun signupDelivery() { assertEquals(200, handler.handle(module, request("signupDelivery"), null).statusCode) }
    @Test fun signupSaler() { assertEquals(200, handler.handle(module, request("signupSaler"), null).statusCode) }
    @Test fun signin() { assertEquals(200, handler.handle(module, request("signin"), null).statusCode) }
    @Test fun validate() { assertEquals(200, handler.handle(module, request("validate"), null).statusCode) }
    @Test fun recovery() { assertEquals(200, handler.handle(module, request("recovery"), null).statusCode) }
    @Test fun confirm() { assertEquals(200, handler.handle(module, request("confirm"), null).statusCode) }
    @Test fun changePassword() { assertEquals(200, handler.handle(module, request("changePassword"), null).statusCode) }
    @Test fun profiles() { assertEquals(200, handler.handle(module, request("profiles"), null).statusCode) }
    @Test fun twoFactorSetup() { assertEquals(200, handler.handle(module, request("2fasetup"), null).statusCode) }
    @Test fun twoFactorVerify() { assertEquals(200, handler.handle(module, request("2faverify"), null).statusCode) }
    @Test fun registerBusiness() { assertEquals(200, handler.handle(module, request("registerBusiness"), null).statusCode) }
    @Test fun reviewBusiness() { assertEquals(200, handler.handle(module, request("reviewBusiness"), null).statusCode) }
    @Test fun assignProfile() { assertEquals(200, handler.handle(module, request("assignProfile"), null).statusCode) }
}
