package nr.dev.esemkasport

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object Route {
    const val START = "start"
    const val LOGIN = "login"
    const val SIGNUP = "signup"
    const val HOME = "home"
    const val TEAM_DETAIL = "team_detail"
    const val PLAYER_DETAIL = "player_detail"
}

data class HttpRequest(
    val url: String,
    val method: String = "GET",
    val body: String? = null,
    val headers: Map<String, String> = emptyMap(),
    val timeout: Int = 10000
)

data class HttpResponse(
    val code: Int,
    val body: String? = null,
    val bytes: ByteArray? = null,
    val errors: String? = null,
    val headers: Map<String, List<String>> = emptyMap()
)

data class User(
    val id: Int,
    val fullName: String,
    val username: String,
    val email: String,

)

object HttpClient {
    val address = "http://10.0.2.2:5000/"

    var user: User? = null

    fun send(req: HttpRequest, getByte: Boolean = false): HttpResponse {
        val conn = URL(req.url).openConnection() as HttpURLConnection
        return try {
            conn.requestMethod = req.method
            conn.readTimeout = req.timeout
            conn.connectTimeout = req.timeout

            req.headers.forEach { (k, v) -> conn.setRequestProperty(k, v) }
            if(req.body != null && req.method in listOf("POST", "PUT", "PATCH")) {
                conn.doOutput = true
                conn.getOutputStream().buffered().use { it.write(req.body.toByteArray()) }
            }
            conn.connect()
            val code = conn.responseCode
            var body: String? = null
            var bytes: ByteArray? = null
            if(getByte) {
                bytes = if(code in 200..299) {
                    conn.getInputStream().buffered().use { it.readBytes() }
                } else {
                    conn.errorStream?.buffered()?.use {it.readBytes()}
                }
            } else {
                body = if(code in 200..299) {
                    conn.getInputStream().bufferedReader().use { it.readText() }
                } else {
                    conn.errorStream?.bufferedReader()?.use { it.readText() }
                }
            }
            HttpResponse(
                code = code,
                body = body,
                bytes = bytes,
                headers = conn.headerFields
            )
        } catch (e: Exception) {
            e.printStackTrace()
            HttpResponse(
                code = -1,
                errors = e.message ?: "Network Error"
            )
        } finally {
            conn.disconnect()
        }
    }

    suspend fun login(username: String, password: String): Int {
        val body = """{"usernameOrEmail": "$username", "password": "$password"}"""
        val res = withContext(Dispatchers.IO) {
            send(HttpRequest(
                url = address + "api/sign-in",
                method = "POST",
                body = body,
                headers = mapOf("content-type" to "application/json")
            ))
        }
        if(res.code == 200 && res.body != null) {
            val json = JSONObject(res.body)
            user = User(
                id = json.getInt("id"),
                fullName = json.getString("fullName"),
                username = json.getString("username"),
                email = json.getString("email"),
            )
        }
        return res.code
    }

    suspend fun signup(username: String, fullName: String, email: String, phoneNumber: String, password: String): Int {
        val body = """{"username": "$username", "fullName": "$fullName", "email": "$email", "phoneNumber": "$phoneNumber", "password": "$password"}"""
        val res = withContext(Dispatchers.IO) {
            send(HttpRequest(
                url = address + "api/sign-up",
                method = "POST",
                body = body,
                headers = mapOf("content-type" to "application/json")
            ))
        }
        if(res.code == 200 && res.body != null) {
            val json = JSONObject(res.body)
            user = User(
                id = json.getInt("id"),
                fullName = json.getString("fullName"),
                username = json.getString("username"),
                email = json.getString("email"),
            )
        }
        println(res)
        return res.code
    }
}


fun corner(size: Dp): RoundedCornerShape {
    return RoundedCornerShape(size)
}