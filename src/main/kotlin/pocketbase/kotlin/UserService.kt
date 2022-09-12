package pocketbase.kotlin

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

        val objectMapper = ObjectMapper()
        val requestBody: String = objectMapper
            .writeValueAsString(enrichedBody)

        println(requestBody)

        val c = HttpClient.newBuilder().build();

        val request = HttpRequest.newBuilder()
            .uri(URI.create("${client.baseUrl}/api/users/auth-via-email"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build()

        val response = c.send(request, HttpResponse.BodyHandlers.ofString());

        println(response.body())

        return UserAuth("", UserModel(), emptyMap())
    }
}
