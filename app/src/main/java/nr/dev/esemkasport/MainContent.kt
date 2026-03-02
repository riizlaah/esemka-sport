package nr.dev.esemkasport

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen(controller: NavHostController, padVal: PaddingValues) {
    var datetime by remember { mutableStateOf("") }
    var search by remember { mutableStateOf("") }
    var placeholder by remember { mutableStateOf("Cari nama tim...") }
    val tabs = listOf("Tim", "Pemain")
    var selectedTab by remember { mutableStateOf(tabs[0]) }
    var teams by remember { mutableStateOf(listOf<Team>()) }
    var players by remember { mutableStateOf(listOf<Player>()) }
    var filteredTeams by remember { mutableStateOf(listOf<Team>()) }
    var filteredPlayers by remember { mutableStateOf(listOf<Player>()) }

    LaunchedEffect(Unit) {
        val formatter = DateTimeFormatter.ofPattern("EEEE, dd MMM yyyy HH:mm:ss")
        datetime = OffsetDateTime.now().format(formatter)
        if (teams.isEmpty()) {
            teams = HttpClient.getTeams()
            players = HttpClient.getPlayers()
            filteredPlayers = players
            filteredTeams = teams
        }

        while (true) {
            delay(1000)
            datetime = OffsetDateTime.now().format(formatter)
        }

    }

    LaunchedEffect(search) {
        if (selectedTab == tabs[0]) {
            filteredTeams = teams.filter { it.name.contains(search, true) }
        } else {
            filteredPlayers = players.filter { it.fullName.contains(search, true) || it.ign.contains(search, true) }
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
            .padding(padVal)
            .background(
                MaterialTheme.colorScheme.tertiary
            )
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    "Halo, ${HttpClient.user!!.fullName} \uD83D\uDC4B",
                    fontWeight = FontWeight.Medium
                )
                Text(datetime, color = Color.Gray)
            }
            Image(
                painterResource(R.drawable.esemka_esport_logo_small),
                contentDescription = "Logo Small",
                modifier = Modifier.height(64.dp)
            )
        }
        Column(
            Modifier
                .weight(1f)
                .padding(12.dp)
        ) {
            BasicTextField(
                value = search,
                onValueChange = { search = it },
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { tField ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color.LightGray, corner(12.dp))
                            .padding(12.dp), verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(painterResource(R.drawable.search), contentDescription = "Search")
                        Spacer(Modifier.width(8.dp))
                        if (search.isEmpty()) {
                            Text(placeholder, color = Color.Gray)
                        } else {
                            tField()
                        }
                    }
                }
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if(selectedTab == tabs[0]) {
                    items(filteredTeams) { team ->
                        Column(
                            Modifier
                                .clip(corner(16.dp))
                                .dropShadow(
                                    shape = corner(16.dp), shadow = Shadow(
                                        radius = 10.dp,
                                        spread = 6.dp,
                                        color = Color(0x40000000),
                                        offset = DpOffset(x = 4.dp, 4.dp)
                                    )
                                )
                                .background(Color.White)
                                .padding(16.dp)
                                .clickable(onClick = {

                                }),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            NetworkImage(
                                HttpClient.address + "logos/${team.logo256}",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                contentScale = ContentScale.Fit
                            )
                            Spacer(Modifier.height(12.dp))
                            Text(team.name, fontWeight = FontWeight.Black)
                        }
                    }
                } else {
                    items(filteredPlayers) { player ->
                        Column(
                            Modifier
                                .clip(corner(16.dp))
                                .dropShadow(
                                    shape = corner(16.dp), shadow = Shadow(
                                        radius = 10.dp,
                                        spread = 6.dp,
                                        color = Color(0x40000000),
                                        offset = DpOffset(x = 4.dp, 4.dp)
                                    )
                                )
                                .background(Color.White)
                                .padding(16.dp)
                                .clickable(onClick = {

                                }),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            NetworkImage(
                                HttpClient.address + "players/${player.image}",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                contentScale = ContentScale.Fit
                            )
                            Spacer(Modifier.height(12.dp))
                            Text("${player.ign} (${player.team.name})", fontWeight = FontWeight.Black)
                            Spacer(Modifier.height(6.dp))
                            Text(player.playerRole.name, color = Color.Gray)
                        }
                    }
                }
            }
        }
        Row(Modifier.fillMaxWidth()) {
            tabs.forEach { name ->
                if (name == selectedTab) {
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            selectedTab = name
                        },
                        shape = RectangleShape
                    ) {
                        Text(name)
                    }
                } else {
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            selectedTab = name
                            search = ""
                        },
                        shape = RectangleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = MaterialTheme.colorScheme.primary
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                    ) {
                        Text(name)
                    }
                }
            }
        }
    }
}