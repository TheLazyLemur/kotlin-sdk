package pocketbase.kotlin.services

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import com.fasterxml.jackson.databind.ObjectMapper
import pocketbase.kotlin.PocketBase
import pocketbase.kotlin.models.UserAuth
import pocketbase.kotlin.models.UserModel
import java.lang.IllegalArgumentException
import java.net.ConnectException

class UserService(
    private val client: PocketBase
) {
    fun creatUserViaEmail(email: String?, password: String?, passwordConfirm: String?){
        if(email.isNullOrEmpty()){
            throw IllegalArgumentException("Email cannot be empty or null")
        }

        if (password.isNullOrEmpty()){
            throw IllegalArgumentException("Password cannot be empty or null")
        }

        if (passwordConfirm.isNullOrEmpty()){
            throw IllegalArgumentException("Password confirm cannot be empty or null")
        }

        val enrichedBody = emptyMap<String, Any>().toMutableMap()
        enrichedBody["email"] = email
        enrichedBody["password"] = password
        enrichedBody["passwordConfirm"] = passwordConfirm

        val enrichedHeaders = emptyMap<String, String>().toMutableMap()
        enrichedHeaders["Content-Type"] = "application/json"

        val objectMapper = ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

        val requestBody: String = objectMapper.writeValueAsString(enrichedBody)

        val httpClient = HttpClient.newBuilder().build()

        val requestBuilder = HttpRequest.newBuilder()
            .uri(URI.create("${client.baseUrl}/api/users"))

        enrichedHeaders.forEach{(header, value) ->
            requestBuilder.header(header, value)
        }

        val request = requestBuilder
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build()

        try{
            val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
            println(response.statusCode())
            if(response.statusCode() > 200){
                throw Exception("Something went wrong while trying to create a new user via email")
            }

            val userObject = objectMapper.readValue(response.body(), UserModel::class.java)
            UserAuth(userObject.token, userObject, emptyMap())
        }catch (e: ConnectException){
            println("Was not able to connect to the PocketBase server")
        }catch (e: Exception){
            println(e.message)
        }
    }

    fun listAuthMethods(): Map<Any, Any>? {
        val httpClient = HttpClient.newBuilder().build()

        val requestBuilder = HttpRequest.newBuilder()
            .uri(URI.create("${client.baseUrl}/api/users/auth-methods"))

        val request = requestBuilder
            .GET()
            .build()

        return try {
            val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
            if(response.statusCode() > 200){
                throw Exception("Something went wrong while trying to list auth methods")
            }

            ObjectMapper().readValue(response.body(), object: TypeReference<Map<Any, Any>>() {})
        }catch (e: ConnectException){
            println("Was not able to connect to the PocketBase server")
            null
        }catch (e: Exception){
            println(e.message)
            null
        }
    }

    fun authViaEmail(email: String?, password: String?): UserAuth? {
        if(email.isNullOrEmpty()){
           throw IllegalArgumentException("Email cannot be empty or null")
        }

        if (password.isNullOrEmpty()){
            throw IllegalArgumentException("Password cannot be empty or null")
        }

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

        return try{
            val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
            if(response.statusCode() > 200){
                throw Exception("Something went wrong while trying to authenticate via email")
            }

            val userObject = objectMapper.readValue(response.body(), UserModel::class.java)
            UserAuth(userObject.token, userObject, emptyMap())
        }catch (e: ConnectException){
            println("Was not able to connect to the PocketBase server")
            null
        }catch (e: Exception){
            println(e.message)
            null
        }
    }
}
