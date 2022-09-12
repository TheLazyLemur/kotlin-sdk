package pocketbase.kotlin

class PocketBase(baseUrl: String) {
    val users: UserService = UserService()
}
