package pocketbase.kotlin

class PocketBase(
    val baseUrl: String
) {
    val users: UserService = UserService(this)
}
