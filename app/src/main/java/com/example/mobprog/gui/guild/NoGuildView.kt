package com.example.mobprog.gui.guild

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mobprog.data.GuildService
import com.example.mobprog.data.UserService
import com.example.mobprog.gui.components.BottomNavBar
import com.example.mobprog.guild.GuildData

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NoGuildView(navController: NavController, guildService: GuildService, modifier: Modifier = Modifier) {

    var guilds by remember { mutableStateOf(emptyList<GuildData>()) }

    var showSearch by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }

    var filteredGuildData by remember { mutableStateOf(emptyList<GuildData>()) }


    fun fetchGuilds(){
        guildService.getAllGuilds { guildList ->
            guildList?.let { documents ->
                val guildDataList = documents.mapNotNull { document ->
                    guildService.parseGuildData(document)
                }
                guilds = guildDataList
            } ?: run {
                println("No events found")
            }
        }
    }

    LaunchedEffect(searchText) {
        fetchGuilds()
        filteredGuildData = guilds.filter { event ->
            event.name.contains(searchText, ignoreCase = true)
        }
    }


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(88.dp)
                    .padding(bottom = 10.dp, top = 24.dp)
                    .background(MaterialTheme.colorScheme.primary),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    IconButton(onClick = { showSearch = true })
                    {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search Icon",
                            tint = Color.White
                        )
                    }
                    Text(
                        text = "Guilds",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                containerColor = Color(0xFFb57bb5),
                onClick = { navController.navigate("createGuildScreen") },
                modifier = Modifier.padding(16.dp),
                content = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "add",
                        tint = Color.Black
                    )
                }
            )
        },
        floatingActionButtonPosition = FabPosition.End,
        content = { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                if (filteredGuildData.isEmpty()) {
                    items(guilds) { guild ->
                        GuildBox(guildData = guild, userService = UserService(), navController)
                        Spacer(modifier = Modifier.height(16.dp))

                    }
                }
                items(filteredGuildData) { guild ->
                    GuildBox(guildData = guild, userService =  UserService(), navController)
                    Spacer(modifier = Modifier.height(16.dp))

                }
            }
        },
        bottomBar = {
            // inspirert av link under for å lage navbar.
            // https://www.youtube.com/watch?v=O9csfKW3dZ4
            BottomNavBar(navController = navController, userService = UserService())
        }
    )

    if (showSearch) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .padding()
                .clickable {
                    showSearch = false
                }
        )
        TextField(
            value = searchText,
            onValueChange = { searchText = it },
            placeholder = { Text("Search...") },
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxSize(fraction = 0.09f)
                .padding(top = 24.dp),
            singleLine = true
        )
    }


}

@Preview(showBackground = true)
@Composable
fun NoGuildViewPreview() {
    NoGuildView(navController = rememberNavController(), guildService = GuildService())
}