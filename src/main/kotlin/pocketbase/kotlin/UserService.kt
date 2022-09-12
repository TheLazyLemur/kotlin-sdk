package pocketbase.kotlin

import com.fasterxml.jackson.databind.DeserializationFeature
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import com.fasterxml.jackson.databind.ObjectMapper

class UserService(
    private val client: PocketBase
) {
    fun authViaEmail(email: String, password: String): UserAuth {
        val enrichedBody = emptyMap<String, Any>().toMutableMap()
        enrichedBody["email"] = email
        enrichedBody["password"] = password

        val enrichedHeaders = emptyMap<String, String>().toMutableMap()
        enrichedHeaders["Authorization"] = ""
        enrichedHeaders["Content-Type"] = "application/json"

        val requestBody: String = ObjectMapper()
            .writeValueAsString(enrichedBody)

        val httpClient = HttpClient.newBuilder().build();

        val requestBuilder = HttpRequest.newBuilder()
            .uri(URI.create("${client.baseUrl}/api/users/auth-via-email"))

        enrichedHeaders.forEach{(header, value) ->
            requestBuilder.header(header, value)
        }

        val request = requestBuilder
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build()

        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        val userObject = ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .readValue(response.body(), UserModel::class.java)

        return UserAuth(userObject.token, userObject, emptyMap())
    }

}
