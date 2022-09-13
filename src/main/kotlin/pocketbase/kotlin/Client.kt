package pocketbase.kotlin

import pocketbase.kotlin.services.UserService

class PocketBase(
    val baseUrl: String
) {
    val users: UserService = UserService(this)
}
