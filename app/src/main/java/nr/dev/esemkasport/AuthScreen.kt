package nr.dev.esemkasport

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicSecureTextField
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch

@Composable
fun PasswordField(state: TextFieldState, modifier: Modifier, placeholder: String = "Password") {
    BasicSecureTextField(
        modifier = modifier,
        state = state,
        decorator = {tField ->
            Row(Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.primary,
                RoundedCornerShape(12.dp)
            ).padding(12.dp)) {
                if(state.text.isEmpty()) {
                    Text(placeholder, color = Color.Gray)
                } else {
                    tField()
                }
            }
        }
    )
}

@Composable
fun LoginScreen(modifier: Modifier, controller: NavHostController) {
    var username by remember { mutableStateOf("") }
    val passwordState = remember { TextFieldState() }
    var loading by remember { mutableStateOf(false) }
    var errMsg by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    Column(modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(painterResource(R.drawable.left), tint = Color.Black, contentDescription = "Back", modifier = Modifier.clickable(onClick = {controller.popBackStack()}))
            Text("Sign In", Modifier.weight(1f), fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
        }
        Column(Modifier.fillMaxWidth().padding(12.dp)) {
            Text("Haloo, Selamat datang kembali!", fontWeight = FontWeight.Medium)
            Text("Kami sangat senang bertemu dengan kamu lagi")
            if(errMsg.isNotEmpty()) {
                Text(modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, text = errMsg, color = Color.Red)
            }
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = username,
                onValueChange = {str: String -> username = str},
                label = {Text("Username atau Email")},
                modifier = Modifier.fillMaxWidth(),
                shape = corner(12.dp)
            )
            Spacer(Modifier.height(12.dp))
            PasswordField(
                state = passwordState,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = {
                    if(username.isEmpty()) {
                        errMsg = "Username/Email tidak boleh kosong!"
                        return@Button
                    }
                    if(passwordState.text.isEmpty()) {
                        errMsg = "Password tidak boleh kosong!"
                        return@Button
                    }
                    scope.launch {
                        loading = true
                        when(HttpClient.login(username, passwordState.text.toString())) {
                            200 -> {
                                controller.navigate(Route.HOME) {
                                    popUpTo(controller.graph.id) {
                                        inclusive = true
                                    }
                                }
                            }
                            404 -> {
                                loading = false
                                errMsg = "User tidak ditemukan"
                            }
                            else -> {
                                loading = false
                                errMsg = "Error tidak diketahui"
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                if(loading) {
                    CircularProgressIndicator(Modifier.size(48.dp))
                    return@Button
                }
                Text("Sign In")
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Text("Tidak memiliki akun?")
                TextButton(
                    onClick = {controller.navigate(Route.SIGNUP)}
                ) {
                    Text("Sign Up Sekarang!")
                }
            }
        }
        Image(painterResource(R.drawable.wave), contentDescription = "wave", modifier = Modifier.fillMaxWidth())
    }
}

@Composable
fun SignUpScreen(modifier: Modifier, controller: NavHostController) {
    var username by remember { mutableStateOf("") }
    var fullname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNum by remember { mutableStateOf("") }
    val passwordState = remember { TextFieldState() }
    val passwordState2 = remember { TextFieldState() }

    var loading by remember { mutableStateOf(false) }
    var errMsg by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    Column(modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(painterResource(R.drawable.left), tint = Color.Black, contentDescription = "Back", modifier = Modifier.clickable(onClick = {controller.popBackStack()}))
            Text("Sign Up", Modifier.weight(1f), fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
        }
        Column(Modifier.fillMaxWidth().padding(12.dp)) {
            Text("Halo!", fontWeight = FontWeight.Medium)
            Text("Buat akun baru!")
            if(errMsg.isNotEmpty()) {
                Text(modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, text = errMsg, color = Color.Red)
            }
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = fullname,
                onValueChange = {str: String -> fullname = str},
                label = {Text("Full Name")},
                modifier = Modifier.fillMaxWidth(),
                shape = corner(12.dp)
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = username,
                onValueChange = {str: String -> username = str},
                label = {Text("Username")},
                modifier = Modifier.fillMaxWidth(),
                shape = corner(12.dp)
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = email,
                onValueChange = {str: String -> email = str},
                label = {Text("Email")},
                modifier = Modifier.fillMaxWidth(),
                shape = corner(12.dp)
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = phoneNum,
                onValueChange = {str: String -> phoneNum = str},
                label = {Text("Phone Number")},
                modifier = Modifier.fillMaxWidth(),
                shape = corner(12.dp)
            )
            Spacer(Modifier.height(12.dp))
            PasswordField(
                state = passwordState,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(12.dp))
            PasswordField(
                state = passwordState2,
                modifier = Modifier.fillMaxWidth(),
                placeholder = "Confirm Password"
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = {
                    if(fullname.isEmpty()) {
                        errMsg = "Full Name tidak boleh kosong!"
                        return@Button
                    }
                    if(username.length < 6) {
                        errMsg = "Username harus berjumlah 6 karakter atau lebih!"
                        return@Button
                    }
                    if(!email.contains('@')) {
                        errMsg = "Email tidak valid!"
                        return@Button
                    }
                    if(!phoneNum.isDigitsOnly()) {
                        errMsg = "No. Telepon tidak valid!"
                        return@Button
                    }
                    if(passwordState.text.length < 6) {
                        errMsg = "Password harus berjumlah 6 karakter atau lebih!"
                        return@Button
                    }
                    if(passwordState.text != passwordState2.text) {
                        errMsg = "Password tidak sama!"
                        return@Button
                    }
                    errMsg = ""
                    scope.launch {
                        loading = true
                        when(HttpClient.signup(username, fullname, email, phoneNum, passwordState.text.toString())) {
                            200, 201 -> controller.navigate(Route.LOGIN)
                            409 -> {
                                errMsg = "Username/Email sudah dipakai, tolong ganti yang lain!"
                                loading = false
                            }
                            else -> {
                                errMsg = "Error tidak diketahui"
                                loading = false
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                if(loading) {
                    CircularProgressIndicator(Modifier.size(48.dp))
                    return@Button
                }
                Text("Sign In")
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Text("Sudah memiliki akun?")
                TextButton(
                    onClick = {controller.navigate(Route.LOGIN)}
                ) {
                    Text("Sign In")
                }
            }
        }
        Image(painterResource(R.drawable.wave), contentDescription = "wave", modifier = Modifier.fillMaxWidth())
    }
}