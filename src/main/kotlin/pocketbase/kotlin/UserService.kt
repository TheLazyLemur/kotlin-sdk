package pocketbase.kotlin

class UserService {
    fun authViaEmail(email: String, password: String): UserAuth {
        return UserAuth("", UserModel(), emptyMap())
    }
}
