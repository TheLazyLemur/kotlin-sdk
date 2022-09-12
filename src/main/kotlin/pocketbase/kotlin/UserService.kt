package pocketbase.kotlin

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import com.fasterxml.jackson.databind.ObjectMapper

class UserService(
    private val client: PocketBase
) {

    fun listAuthMethods(): Map<Any, Any>? {
        val httpClient = HttpClient.newBuilder().build()

        val requestBuilder = HttpRequest.newBuilder()
            .uri(URI.create("${client.baseUrl}/api/users/auth-methods"))

        val request = requestBuilder
            .GET()
            .build()

        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())

        return ObjectMapper().readValue(response.body(), object: TypeReference<Map<Any, Any>>() {})
    }

    fun authViaEmail(email: String, password: String): UserAuth {
        val enrichedBody = emptyMap<String, Any>().toMutableMap()
        enrichedBody["email"] = email
        enrichedBody["password"] = password

        val enrichedHeaders = emptyMap<String, String>().toMutableMap()
        enrichedHeaders["Content-Type"] = "application/json"

        val objectMapper = ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

        val requestBody: String = objectMapper.writeValueAsString(enrichedBody)

        val httpClient = HttpClient.newBuilder().build()

        val requestBuilder = HttpRequest.newBuilder()
            .uri(URI.create("${client.baseUrl}/api/users/auth-via-email"))

        enrichedHeaders.forEach{(header, value) ->
            requestBuilder.header(header, value)
        }

        val request = requestBuilder
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build()

        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())

        val userObject = objectMapper.readValue(response.body(), UserModel::class.java)

        return UserAuth(userObject.token, userObject, emptyMap())
    }

}
