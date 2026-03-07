package nr.dev.esemkasport

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import nr.dev.esemkasport.ui.theme.EsemkaSportTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EsemkaSportTheme {
                val controller = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val mod = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(innerPadding)
                        .background(Color.White)
                    NavHost(navController = controller, startDestination = Route.START) {
                        composable(route = Route.START) {
                            StartScreen(controller)
                        }
                        composable(route = Route.LOGIN) {
                            LoginScreen(mod, controller)
                        }
                        composable(route = Route.SIGNUP) {
                            SignUpScreen(mod, controller)
                        }
                        composable(route = Route.HOME) {
                            HomeScreen(controller, mod)
                        }
                        composable(
                            route = Route.TEAM_DETAIL_FULL,
                            arguments = listOf(navArgument("id") {
                                type = NavType.IntType
                            })
                        ) { backStackTrace ->
                            val teamId = backStackTrace.arguments?.getInt("id") ?: 1
                            TeamDetailScreen(controller, mod, teamId)
                        }
                        composable(
                            route = Route.PLAYER_DETAIL_FULL,
                            arguments = listOf(navArgument("id") {
                                type = NavType.IntType
                            })
                        ) { backStackTrace ->
                            val playerId = backStackTrace.arguments?.getInt("id") ?: 1
                            PlayerDetailScreen(controller, mod, playerId)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StartScreen(controller: NavHostController) {
    Box(Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.primary)) {
        Image(
            painterResource(R.drawable.esemka_esport_logo_large),
            contentDescription = "Logo",
            modifier = Modifier.align(Alignment.Center)
        )
        Column(Modifier
            .align(Alignment.BottomStart)
            .fillMaxWidth()
            .padding(36.dp)) {
            Button(
                onClick = { controller.navigate(Route.LOGIN) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text("Sign In")
            }
            TextButton(
                onClick = { controller.navigate(Route.SIGNUP) },
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

@Composable
fun BackHeader(
    controller: NavHostController,
    text: String,
    textAlign: TextAlign = TextAlign.Center
) {
    Row(
        Modifier
            .fillMaxWidth()
            .dropShadow(
                shape = RectangleShape, shadow = Shadow(
                    radius = 4.dp,
                    color = Color(0x40000000),
                )
            )
            .background(Color.White)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painterResource(R.drawable.left),
            tint = Color.Black,
            contentDescription = "Back",
            modifier = Modifier.clickable(onClick = { controller.popBackStack() })
        )
        Text(
            text,
            Modifier.weight(1f),
            fontWeight = FontWeight.Bold,
            fontSize = MaterialTheme.typography.headlineSmall.fontSize,
            textAlign = textAlign
        )
    }
}