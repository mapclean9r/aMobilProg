package com.example.mobprog.gui

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mobprog.api.GameData
import com.example.mobprog.api.GamingApi
import com.example.mobprog.createEvent.EventData
import com.example.mobprog.data.EventService
import com.example.mobprog.data.UserService
import com.example.mobprog.gui.components.BottomNavBar
import com.example.mobprog.gui.components.GameBox
import com.google.firebase.auth.FirebaseAuth
import java.util.Calendar
import androidx.compose.animation.core.RepeatMode
import androidx.compose.foundation.border
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.mobprog.ui.theme.BackgroundTheme40


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CreateEventView(navController: NavController, eventService: EventService) {
    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle

    var isLoading by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    var name by remember { mutableStateOf(savedStateHandle?.get<String>("name") ?: "") }
    var location by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf(savedStateHandle?.get<String>("startDate") ?: "") }
    var description by remember { mutableStateOf(savedStateHandle?.get<String>("description") ?: "") }
    var gameCoverImage by remember { mutableStateOf(savedStateHandle?.get<String>("gameCoverImage") ?: "") }
    var maxAttendance by remember { mutableIntStateOf(savedStateHandle?.get<Int>("maxAttendance") ?: 0) }
    var maxAttendanceString by remember { mutableStateOf(savedStateHandle?.get<String>("maxAttendanceString") ?: "") }

    var showSearch by remember { mutableStateOf(false) }
    var searchGameText by remember { mutableStateOf("") }
    var games by remember { mutableStateOf(emptyList<GameData>()) }
    var filteredGames by remember { mutableStateOf(emptyList<GameData>()) }

    var selectedGame by remember { mutableStateOf(savedStateHandle?.get<GameData>("selectedGame")) }

    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val context = LocalContext.current
    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, pickedYear: Int, pickedMonth: Int, pickedDay: Int ->
            startDate = "$pickedDay/${pickedMonth + 1}/$pickedYear"
        }, year, month, day
    )
    var latitude by remember { mutableStateOf<Double?>(null) }
    var longitude by remember { mutableStateOf<Double?>(null) }

    val selectedLocation = savedStateHandle?.getLiveData<Triple<Double, Double, String>>("selected_location")
    selectedLocation?.observe(LocalLifecycleOwner.current) { locationTriple ->
        val (selectedLatitude, selectedLongitude, selectedLocationName) = locationTriple
        latitude = selectedLatitude
        longitude = selectedLongitude
        location = selectedLocationName
    }



    GamingApi().fetchAllGames { gameList ->
        gameList?.let {
            games = gameList
        } ?: run {
            println("No games found")
        }
    }

    LaunchedEffect(isLoading) {
        if (isLoading) {
            kotlinx.coroutines.delay(2000)
            isLoading = false
            navController.navigate("homeScreen") {
                while (navController.popBackStack()) {
                    navController.popBackStack()
                }
            }
        }
    }


    LaunchedEffect(searchGameText) {
        filteredGames = games.filter { game ->
            game.title.contains(searchGameText, ignoreCase = true)
        }
    }
    if (isLoading) {
        LoadingScreen()
    } else {


    if (!isLandscape) {
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
                            .align(Alignment.Start),
                        color = MaterialTheme.colorScheme.primary
                    )
                    TextField(
                        value = name,
                        onValueChange = { newText ->
                            name = newText
                            /* TODO behandle input her */
                        },
                        label = { Text("Enter Title") },
                        placeholder = { Text("title") },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    // Location END
                    Text(
                        text = "Description",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W400,
                        modifier = Modifier
                            .padding(6.dp)
                            .align(Alignment.Start),
                        color = MaterialTheme.colorScheme.primary
                    )
                    TextField(
                        value = description,
                        onValueChange = { newText ->
                            description = newText
                        },
                        label = { Text("Enter Description") },
                        placeholder = { Text("description") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp)
                    )
                // Location START
                Text(
                    text = "Location",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W400,
                    modifier = Modifier
                        .padding(6.dp)
                        .align(Alignment.Start),
                    color = MaterialTheme.colorScheme.primary
                )
                TextField(
                    value = location,
                    onValueChange = { newText ->
                        location = newText
                    },
                    label = { Text("Enter Location") },
                    placeholder = { Text("location") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true
                )
                Button(
                    onClick = {
                        savedStateHandle?.set("name", name)
                        savedStateHandle?.set("startDate", startDate)
                        savedStateHandle?.set("description", description)
                        savedStateHandle?.set("gameCoverImage", gameCoverImage)
                        savedStateHandle?.set("maxAttendance", maxAttendance)
                        savedStateHandle?.set("maxAttendanceString", maxAttendanceString)
                        savedStateHandle?.set("selectedGame", selectedGame)
                        navController.navigate("locationPickerScreen")
                    },
                    modifier = Modifier
                        .width(275.dp)
                        .padding(bottom = 8.dp, top = 8.dp),

                ) {
                    Text("Select Location with Google Maps")
                }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Column(
                            modifier = Modifier.padding(end = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Button(
                                onClick = { datePickerDialog.show() },
                                modifier = Modifier
                                    .width(120.dp)
                                    .padding(bottom = 8.dp)

                            ) {
                                Text(text = "Velg dato")
                            }
                            Text(
                                text = "Valgt dato: $startDate",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Max Attendance",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.W400,
                                modifier = Modifier.padding(bottom = 6.dp),
                            )
                            TextField(
                                value = maxAttendanceString,
                                onValueChange = { newText ->
                                    maxAttendanceString = newText
                                    val convertToInt = maxAttendanceString.toIntOrNull() ?: 0
                                    maxAttendance = convertToInt
                                },
                                label = { Text("Enter Attendance", fontSize = 12.sp) },
                                placeholder = { Text("attendance") },
                                modifier = Modifier
                                    .width(140.dp)
                                    .padding(top = 8.dp)
                                    .height(50.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true
                            )
                        }
                    }

                    Text(
                        text = "Game: " + (selectedGame?.title ?: "None"),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W400,
                        modifier = Modifier
                            .padding(6.dp)
                            .align(Alignment.Start),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Button(
                        onClick = {
                            showSearch = true
                        },
                        modifier = Modifier
                        .width(275.dp),
                    ) {
                        Text("Select Game")
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Button(
                        onClick = {
                            isLoading = true
                            onSubmit(
                                name,
                                maxAttendance,
                                location,
                                startDate,
                                description,
                                gameCoverImage,
                                eventService = eventService,
                                creatorId = FirebaseAuth.getInstance().currentUser?.uid.toString(),
                                locationCoordinates = selectedLocation?.value?.let { "${it.first},${it.second}" } ?: ""
                            )


                        },
                        modifier = Modifier.fillMaxWidth()
                            .height(75.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.DarkGray,
                            contentColor = Color.White,
                            disabledContainerColor = Color.Gray,
                            disabledContentColor = Color.LightGray
                        )

                    ) {
                        Text("Create Event")
                    }

                }
            },
            bottomBar = {
                BottomNavBar(navController = navController, userService = UserService())
            }
        )
    } else {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
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
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .verticalScroll(scrollState),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Title",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.W400,
                            modifier = Modifier.align(Alignment.Start),
                            color = MaterialTheme.colorScheme.primary
                        )
                        TextField(
                            value = name,
                            onValueChange = { newText -> name = newText },
                            label = { Text("Enter Title") },
                            placeholder = { Text("title") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Location START
                        Text(
                            text = "Location",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.W400,
                            modifier = Modifier
                                .padding(6.dp)
                                .align(Alignment.Start),
                            color = MaterialTheme.colorScheme.primary
                        )
                        TextField(
                            value = location,
                            onValueChange = { newText ->
                                location = newText
                            },
                            label = { Text("Enter Location or use Map") },
                            placeholder = { Text("location") },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true
                        )
                        Button(
                            onClick = {
                                navController.navigate("locationPickerScreen")
                            },
                            modifier = Modifier.fillMaxWidth(),

                        ) {
                            Text("Select Location on Map")
                        }
                        // Location END

                        Text(
                            text = "Description",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.W400,
                            modifier = Modifier.align(Alignment.Start),
                            color = MaterialTheme.colorScheme.primary
                        )
                        TextField(
                            value = description,
                            onValueChange = { newText -> description = newText },
                            label = { Text("Enter Description") },
                            placeholder = { Text("description") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 6.dp)
                        )
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            onClick = { datePickerDialog.show() },
                            modifier = Modifier
                                .width(120.dp)
                                .padding(bottom = 8.dp)
                        ) {
                            Text(text = "Velg dato")
                        }
                        Text(
                            text = "Valgt dato: $startDate",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(4.dp)
                        )

                        Text(
                            text = "Max Attendance",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.W400,
                            color = MaterialTheme.colorScheme.primary
                        )
                        TextField(
                            value = maxAttendanceString,
                            onValueChange = { newText ->
                                maxAttendanceString = newText
                                val convertToInt = maxAttendanceString.toIntOrNull() ?: 0
                                maxAttendance = convertToInt
                            },
                            label = { Text("Enter Attendance", fontSize = 12.sp) },
                            placeholder = { Text("attendance") },
                            modifier = Modifier
                                .width(140.dp)
                                .padding(top = 8.dp)
                                .height(50.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )

                        Text(
                            text = "Game: " + (selectedGame?.title ?: "None"),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.W400,
                            modifier = Modifier.padding(6.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Button(
                            onClick = { showSearch = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Select Game")
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Button(
                            onClick = {
                                isLoading = true
                                onSubmit(
                                    name,
                                    maxAttendance,
                                    location,
                                    startDate,
                                    description,
                                    gameCoverImage,
                                    eventService = eventService,
                                    creatorId = FirebaseAuth.getInstance().currentUser?.uid.toString(),
                                    locationCoordinates = selectedLocation?.value?.let { "${it.first},${it.second}" } ?: ""
                                )


                            },
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Text("Create Event")
                        }
                    }
                }
            },
            bottomBar = {
                BottomNavBar(navController = navController, userService = UserService())
            }
        )
    }
    }

    if (showSearch) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.onPrimary)
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
                    GameBox(gameData = game) {
                        selectedGame = game
                        println("Game cover = " + game.thumbnail)
                        gameCoverImage = game.thumbnail
                        showSearch = false

                    }

                }
            }
            items(filteredGames) { game ->
                Spacer(modifier = Modifier.height(8.dp))
                GameBox(gameData = game) {
                    selectedGame = game
                    println("Game cover = " + game.thumbnail)
                    gameCoverImage = game.thumbnail
                    showSearch = false
                }
            }
            println("Selected Game: $selectedGame")
            }
        }

}

fun onSubmit(name: String,
             maxAttendance: Int,
             location: String,
             startDate: String,
             description: String,
             gameCoverImage: String,
             eventService: EventService,
             creatorId: String,
             locationCoordinates: String) {
    eventService.createEvent(EventData(
        name = name,
        image = gameCoverImage,
        maxAttendance = maxAttendance,
        location = location,
        description = description,
        startDate = startDate,
        creatorId = creatorId,
        coordinates = locationCoordinates))
}

@Composable
fun LoadingScreen() {
    val infiniteTransition = rememberInfiniteTransition()
    val animatedAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            progress = {
                animatedAlpha
            },
            modifier = Modifier
                .size(64.dp)
                .padding(bottom = 16.dp),
        )
        Text(
            text = "Your event is being created, please wait...",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CreateEventViewPreview() {
    CreateEventView(navController = rememberNavController(), eventService = EventService())
}

