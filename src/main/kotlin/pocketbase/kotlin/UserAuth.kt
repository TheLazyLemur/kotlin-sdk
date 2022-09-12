package pocketbase.kotlin

class UserAuth(
    val token: String = "",
    val user: UserModel?,
    val meta: Map<String, Any> = emptyMap()
) {

}
