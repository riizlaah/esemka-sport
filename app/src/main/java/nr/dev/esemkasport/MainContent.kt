package nr.dev.esemkasport

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen(controller: NavHostController, modifier: Modifier) {
    var datetime by remember { mutableStateOf("") }
    var search by remember { mutableStateOf("") }
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
            filteredPlayers = players.filter {
                it.fullName.contains(search, true) || it.ign.contains(
                    search,
                    true
                )
            }
        }
    }

    Column(modifier) {
        Row(
            Modifier
                .fillMaxWidth()
                .dropShadow(
                    RoundedCornerShape(bottomStart = 6.dp, bottomEnd = 6.dp),
                    Shadow(radius = 4.dp, color = Color(0x40000000))
                )
                .clip(RoundedCornerShape(bottomStart = 6.dp, bottomEnd = 6.dp))
                .background(Color.White)
                .padding(16.dp),
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
                        Spacer(Modifier.width(4.dp))
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
                            if (search.isEmpty()) {
                                Text("Cari nama $selectedTab...", color = Color.Gray)
                            }
                            tField()
                        }
                    }
                }
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item(span = { GridItemSpan(2) }) {
                    Spacer(Modifier.height(8.dp))
                }
                if (selectedTab == tabs[0]) {
                    items(filteredTeams) { team ->
                        Column(
                            Modifier
                                .dropShadow(
                                    shape = corner(16.dp), shadow = Shadow(
                                        radius = 4.dp,
                                        color = Color(0x40000000),
                                    )
                                )
                                .clip(corner(16.dp))
                                .background(Color.White)
                                .padding(16.dp)
                                .clickable(onClick = {
                                    controller.navigate(Route.TEAM_DETAIL + "/${team.id}")
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
                        PlayerCard(player, controller)
                    }
                }
                item(span = { GridItemSpan(2) }) {
                    Spacer(Modifier.height(8.dp))
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
                        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                    ) {
                        Text(name)
                    }
                }
            }
        }
    }
}

@Composable
fun TeamDetailScreen(controller: NavHostController, modifier: Modifier, teamId: Int) {
    val tabs = listOf("Tentang", "Prestasi", "Statistik", "Pemain")
    var selectedTab by remember { mutableStateOf(tabs[0]) }
    var team by remember { mutableStateOf<Team?>(null) }
    val achievements = remember { mutableStateListOf<String>() }
    val players = remember { mutableStateListOf<Player>() }

    LaunchedEffect(Unit) {
        if (team == null) {
            team = HttpClient.getTeamById(teamId)
            achievements.addAll(HttpClient.getTeamAchievements(teamId))
            players.addAll(HttpClient.getPlayersInTeam(teamId))
        }
    }

    Column(modifier) {
        BackHeader(controller, "Detail Tim", TextAlign.Left)
        Column(Modifier.weight(1f)) {
            if (team == null) return@Column
            when (selectedTab) {
                tabs[0] -> {
                    LazyColumn(
                        Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            NetworkImage(
                                HttpClient.address + "logos/${team!!.logo500}",
                                modifier = Modifier.fillMaxWidth(),
                                contentScale = ContentScale.Fit,
                                contentDescription = team!!.name
                            )
                        }
                        item {
                            Text(
                                team!!.name,
                                fontWeight = FontWeight.Black,
                                fontSize = MaterialTheme.typography.displayMedium.fontSize
                            )
                        }
                        item {
                            Text(team!!.about, color = Color.Gray)
                        }
                    }
                }

                tabs[1] -> {
                    LazyColumn(
                        Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(achievements) { name ->
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
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
                                    .padding(16.dp),
                                text = name,
                                color = Color.Gray
                            )
                        }
                    }
                }

                tabs[2] -> {
                    LazyColumn(
                        Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            MaxWidthRow {
                                StatCard(
                                    painterResource(R.drawable.deaths),
                                    "${team!!.deaths} Deaths",
                                    Modifier.weight(1f)
                                )
                                StatCard(
                                    painterResource(R.drawable.kills),
                                    "${team!!.kills} Kills",
                                    Modifier.weight(1f)
                                )
                            }
                        }
                        item {
                            MaxWidthRow {
                                StatCard(
                                    painterResource(R.drawable.assists),
                                    "${team!!.assists} Assists",
                                    Modifier.weight(1f)
                                )
                                StatCard(
                                    painterResource(R.drawable.gold),
                                    "${team!!.gold} Gold",
                                    Modifier.weight(1f)
                                )
                            }
                        }
                        item {
                            MaxWidthRow {
                                StatCard(
                                    painterResource(R.drawable.damage),
                                    "${team!!.damage} Damage",
                                    Modifier.weight(1f)
                                )
                                StatCard(
                                    painterResource(R.drawable.lord_kills),
                                    "${team!!.lordKills} Lord Kills",
                                    Modifier.weight(1f)
                                )
                            }
                        }
                        item {
                            MaxWidthRow {
                                StatCard(
                                    painterResource(R.drawable.tortoise_kills),
                                    "${team!!.deaths} Tortoise Kills",
                                    Modifier.weight(1f)
                                )
                                StatCard(
                                    painterResource(R.drawable.towe_destroy),
                                    "${team!!.towerDestroy} Tower Destroy",
                                    Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                tabs[3] -> {
                    LazyVerticalGrid(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        columns = GridCells.Fixed(2),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(players) { player ->
                            PlayerCard(player, controller)
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
                        },
                        shape = RectangleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = MaterialTheme.colorScheme.primary
                        ),
                        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                    ) {
                        Text(name)
                    }
                }
            }
        }
    }
}

@Composable
fun PlayerCard(player: Player, controller: NavHostController) {
    Column(
        Modifier
            .dropShadow(
                shape = corner(16.dp), shadow = Shadow(
                    radius = 4.dp,
                    color = Color(0x40000000),
                )
            )
            .clip(corner(16.dp))
            .background(Color.White)
            .padding(16.dp)
            .clickable(onClick = {
                controller.navigate(Route.PLAYER_DETAIL + "/${player.id}")
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
        Text(
            "${player.ign} (${player.team.name})",
            fontWeight = FontWeight.Black,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(6.dp))
        Text(
            player.playerRole.name,
            color = Color.Gray,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun PlayerDetailScreen(controller: NavHostController, modifier: Modifier, playerId: Int) {
    var player by remember { mutableStateOf<Player?>(null) }

    LaunchedEffect(Unit) {
        if (player == null) {
            player = HttpClient.getPlayerById(playerId)
        }
    }

    Column(modifier) {
        BackHeader(controller, "Detail Pemain", TextAlign.Left)
        if (player == null) return@Column
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                NetworkImage(
                    url = HttpClient.address + "players/${player!!.image}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp),
                    contentScale = ContentScale.Fit
                )
            }
            item {
                val text = player!!.fullName + "\n(${player!!.team.name} ${player!!.ign})"
                Text(
                    text,
                    fontWeight = FontWeight.Bold,
                    fontSize = MaterialTheme.typography.displaySmall.fontSize,
                    textAlign = TextAlign.Center,
                    lineHeight = MaterialTheme.typography.displaySmall.lineHeight
                )
                Text(
                    player!!.playerRole.name,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray,
                    fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun MaxWidthRow(content: @Composable () -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        content()
    }
}

@Composable
fun StatCard(painter: Painter, text: String, modifier: Modifier = Modifier) {
    Column(
        modifier
            .dropShadow(
                shape = corner(16.dp), shadow = Shadow(
                    radius = 4.dp,
                    color = Color(0x40000000),
                )
            )
            .clip(corner(16.dp))
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter,
            contentDescription = text,
            modifier = Modifier.padding(horizontal = 48.dp, vertical = 16.dp)
        )
        Text(
            text,
            fontWeight = FontWeight.Medium,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
