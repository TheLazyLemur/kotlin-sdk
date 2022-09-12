import pocketbase.kotlin.PocketBase

fun main(){
    val client = PocketBase("http://127.0.0.1:8090")

    val userData = client.users.authViaEmail("test@example.com", "123456");
}