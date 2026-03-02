package nr.dev.esemkasport

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import nr.dev.esemkasport.ui.theme.EsemkaSportTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EsemkaSportTheme {
                val controller = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(navController = controller, startDestination = Route.START) {
                        composable(route = Route.START) {
                            StartScreen(controller)
                        }
                        composable(route = Route.LOGIN) {
                            LoginScreen(Modifier.padding(innerPadding), controller)
                        }
                        composable(route = Route.SIGNUP) {
                            SignUpScreen(Modifier.padding(innerPadding), controller)
                        }
                        composable(route = Route.HOME) {
                            HomeScreen(controller, innerPadding)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StartScreen(controller: NavHostController) {
    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primary)) {
        Image(painterResource(R.drawable.esemka_esport_logo_large), contentDescription = "Logo", modifier = Modifier.align(Alignment.Center))
        Column(Modifier.align(Alignment.BottomStart).fillMaxWidth().padding(36.dp)) {
            Button(
                onClick = {controller.navigate(Route.LOGIN)},
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text("Sign In")
            }
            TextButton(
                onClick = {controller.navigate(Route.SIGNUP)},
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.tertiary)
            ) {
                Text("Sign Up")
            }
        }
    }
}