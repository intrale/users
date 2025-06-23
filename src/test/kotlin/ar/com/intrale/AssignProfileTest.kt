package ar.com.intrale

import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.runBlocking
import org.slf4j.helpers.NOPLogger
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClientExtension
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import kotlin.test.Test
import kotlin.test.assertEquals

class DummyProfileTable : DynamoDbTable<UserBusinessProfile> {
    val items = mutableListOf<UserBusinessProfile>()
    override fun mapperExtension(): DynamoDbEnhancedClientExtension? = null
    override fun tableSchema(): TableSchema<UserBusinessProfile> = TableSchema.fromBean(UserBusinessProfile::class.java)
    override fun tableName(): String = "profiles"
    override fun keyFrom(item: UserBusinessProfile): Key = Key.builder().partitionValue(item.compositeKey).build()
    override fun index(indexName: String) = throw UnsupportedOperationException()
    override fun putItem(item: UserBusinessProfile) { items.add(item) }
}

class AssignProfileTest {
    private val logger = NOPLogger.NOP_LOGGER
    private val config = UsersConfig(setOf("biz"), "us-east-1", "key", "secret", "pool", "client")
    private val table = DummyProfileTable()
    private val assign = AssignProfile(config, logger, table)

    @Test
    fun validRequestPassesValidation() {
        val req = AssignProfileRequest("test@intrale.com", "ADMIN")
        val resp = assign.requestValidation(req)
        assertEquals(null, resp)
    }

    @Test
    fun invalidRequestReturnsError() {
        val req = AssignProfileRequest("", "ADMIN")
        val resp = assign.requestValidation(req)
        assertEquals(HttpStatusCode.BadRequest, (resp as RequestValidationException).statusCode)
    }

    @Test
    fun securedExecuteStoresProfile() = runBlocking {
        val body = "{\"email\":\"user@test.com\",\"profile\":\"ADMIN\"}"
        val token = "eyJhbGciOiAibm9uZSJ9.eyJwcm9maWxlIjogIlBMQVRGT1JNX0FETUlOIn0."
        val headers = mapOf("Authorization" to token)
        val resp = assign.securedExecute("biz", "assignProfile", headers, body)
        assertEquals(HttpStatusCode.OK, resp.statusCode)
        assertEquals(1, table.items.size)
    }
}
