package ar.com.intrale

import com.google.gson.Gson
import kotlin.test.Test
import kotlin.test.assertEquals

class AssignProfileRequestTest {

    @Test
    fun deserializaDesdeJson() {
        val json = "{\"email\":\"test@example.com\",\"profile\":\"CLIENT\"}"
        val req = Gson().fromJson(json, AssignProfileRequest::class.java)
        assertEquals("test@example.com", req.email)
        assertEquals("CLIENT", req.profile)
    }

    @Test
    fun serializaAJson() {
        val req = AssignProfileRequest("user@test.com", "ADMIN")
        val json = Gson().toJson(req)
        val parsed = Gson().fromJson(json, Map::class.java)
        assertEquals("user@test.com", parsed["email"])
        assertEquals("ADMIN", parsed["profile"])
    }
}
