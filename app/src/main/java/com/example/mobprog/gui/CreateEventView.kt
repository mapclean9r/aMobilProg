package com.example.mobprog.gui

import android.annotation.SuppressLint
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mobprog.api.GameData
import com.example.mobprog.api.GamingApi
import com.example.mobprog.createEvent.EventData
import com.example.mobprog.data.EventService
import com.example.mobprog.data.UserService
import com.example.mobprog.gui.components.BottomNavBar
import com.example.mobprog.gui.components.GameBox
import com.example.mobprog.gui.guild.GuildBox


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CreateEventView(navController: NavController, eventService: EventService) {
    val scrollState = rememberScrollState()
    val gamingApi = GamingApi()

    /* TODO - legge til alle felter som trengs og endre tekst felter til å benytte disse */
    var name by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    var maxAttendance by remember { mutableIntStateOf(0) }
    var maxAttendanceString by remember { mutableStateOf("") }

    var showSearch by remember { mutableStateOf(false) }
    var searchGameText by remember { mutableStateOf("") }
    var games by remember { mutableStateOf(emptyList<GameData>()) }
    var filteredGames by remember { mutableStateOf(emptyList<GameData>()) }

    GamingApi().fetchAllGames { gameList ->
        gameList?.let {
            games = gameList
        } ?: run {
            println("No games found")
        }
    }


    LaunchedEffect(searchGameText) {
        filteredGames = games.filter { game ->
            game.title.contains(searchGameText, ignoreCase = true)
        } ?: emptyList()
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
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Create Event",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Title",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W400,
                    modifier = Modifier
                        .padding(6.dp)
                        .align(Alignment.Start)
                )
                TextField(
                    value = name,
                    onValueChange = { newText ->
                        name = newText
                        /* TODO behandle input her */
                    },
                    label = { Text("Enter Title") },
                    placeholder = { Text("title") },
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "Location",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W400,
                    modifier = Modifier
                        .padding(6.dp)
                        .align(Alignment.Start)
                )
                TextField(
                    value = location,
                    onValueChange = { newText ->
                        location = newText
                        /* TODO behandle input her */
                    },
                    label = { Text("Enter Location") },
                    placeholder = { Text("location") },
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "Price",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W400,
                    modifier = Modifier
                        .padding(6.dp)
                        .align(Alignment.Start)
                )
                TextField(
                    value = price,
                    onValueChange = { newText ->
                        price = newText
                    },
                    label = { Text("Enter Price") },
                    placeholder = { Text("price") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Max Attendance",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W400,
                    modifier = Modifier
                        .padding(6.dp)
                        .align(Alignment.Start)
                )
                TextField(
                    value = maxAttendanceString,
                    onValueChange = { newText ->
                        maxAttendanceString = newText
                        maxAttendance = maxAttendanceString.toInt()
                    },
                    label = { Text("Enter Attendance") },
                    placeholder = { Text("attendance") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)

                )

                Text(
                    text = "Date",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W400,
                    modifier = Modifier
                        .padding(6.dp)
                        .align(Alignment.Start)
                )
                TextField(
                    value = startDate,
                    onValueChange = { newText ->
                        startDate = newText
                    },
                    label = { Text("Enter Date") },
                    placeholder = { Text("date") },
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "Description",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W400,
                    modifier = Modifier
                        .padding(6.dp)
                        .align(Alignment.Start)
                )
                TextField(
                    value = description,
                    onValueChange = { newText ->
                        description = newText
                    },
                    label = { Text("Enter Description") },
                    placeholder = { Text("description") },
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "Game",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W400,
                    modifier = Modifier
                        .padding(6.dp)
                        .align(Alignment.Start)
                )
                Button(
                    onClick = {
                        showSearch = true
                        // TODO Make this button work
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Gray,
                        contentColor = Color.White
                    )
                ) {
                    Text("Select Game")
                }
                Spacer(modifier = Modifier.height(22.dp))
                Button(
                    onClick = {
                        onSubmit(
                            name,
                            maxAttendance,
                            price,
                            location,
                            startDate,
                            description,
                            eventService = eventService
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Create Event")
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
                .background(Color.White.copy(alpha = 1.0f))
                .padding()
                .clickable {
                    showSearch = false
                }
        )

        TextField(
            value = searchGameText,
            onValueChange = { searchGameText = it },
            placeholder = { Text("Search...") },
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxSize(fraction = 0.09f)
                .padding(top = 24.dp),
            singleLine = true
        )
        Button(onClick = {showSearch = false},
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 80.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color.Gray)

        ) {
            Text("Cancel Search")
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .padding(top = 110.dp)
        ) {
            if (filteredGames.isEmpty()) {
                items(games) { game ->
                    Spacer(modifier = Modifier.height(8.dp))
                    GameBox(gameData = game)

                }
            }
            items(filteredGames) { game ->
                Spacer(modifier = Modifier.height(8.dp))
                GameBox(gameData = game)

            }
        }


    }
}

fun onSubmit(name: String, maxAttendance: Int, price: String, location: String, startDate: String, description: String, eventService: EventService) {
    eventService.createEvent(EventData(name = name, maxAttendance = maxAttendance, location = location, description = description, startDate = startDate, price = price))
}



@Preview(showBackground = true)
@Composable
fun CreateEventViewPreview() {
    CreateEventView(navController = rememberNavController(), eventService = EventService())
}

