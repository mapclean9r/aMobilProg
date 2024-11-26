package com.example.mobprog.gui.event

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mobprog.createEvent.EventData
import com.example.mobprog.data.EventService
import com.example.mobprog.data.UserService
import com.example.mobprog.gui.components.BottomNavBar
import com.example.mobprog.maps.EventLocationMapView
import com.google.firebase.auth.FirebaseAuth
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import com.example.mobprog.R

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EventView(navController: NavController, eventData: EventData?, currentEvent: EventData?) {
    val scrollState = rememberScrollState()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    val eventService = EventService()
    val userService = UserService()
    val currentUserID = FirebaseAuth.getInstance().currentUser?.uid.toString()

    var isAttending by remember { mutableStateOf(false) }
    var username by remember { mutableStateOf("") }

    fun attenderStarterValue(): Int {
        return currentEvent?.attending?.size ?: 0
    }

    var numberOfPeopleAttending by remember { mutableIntStateOf(attenderStarterValue()) }

    LaunchedEffect(currentUserID) {
        UserService().getAttendingEvents { attendingEvents ->
            if (currentEvent != null) {
                isAttending = attendingEvents?.any { it.id == currentEvent.id } == true
            }
        }
    }

    fun updateAttendingPeople(isJoining: Boolean) {
        if (currentEvent != null) {
            if (isJoining) {
                isAttending = true
                numberOfPeopleAttending++
            } else {
                isAttending = false
                numberOfPeopleAttending--
            }
        }
    }

    if (currentEvent != null) {
        userService.getUsernameWithDocID(currentEvent.creatorId) { creatorId ->
            username = creatorId ?: "username not found..."
        }
    }

    if (!isLandscape) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = currentEvent?.name ?: "Event",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = Color.White
                    )
                )
            },
            content = { paddingValues ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    item {
                        currentEvent?.let { event ->
                            // Event Image
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(250.dp),
                                color = MaterialTheme.colorScheme.surface
                            ) {
                                AsyncImage(
                                    model = event.image,
                                    contentDescription = "Event Image",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            // Event Details Card
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .fillMaxWidth()
                                ) {
                                    // Date
                                    val dateFormatter = DateTimeFormatter.ofPattern("d/M/yyyy", Locale.getDefault())
                                    val parsedDate = LocalDate.parse(event.startDate, dateFormatter)
                                    val formattedDate = parsedDate.format(DateTimeFormatter.ofPattern("EEEE d MMMM yyyy"))

                                    ListItem(
                                        headlineContent = { Text(formattedDate) },
                                        leadingContent = {
                                            Icon(
                                                Icons.Default.DateRange,
                                                contentDescription = "Date",
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    )

                                    // Host
                                    ListItem(
                                        headlineContent = { Text("Hosted by $username") },
                                        leadingContent = {
                                            Icon(
                                                Icons.Default.Person,
                                                contentDescription = "Host",
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    )

                                    // Attendance
                                    ListItem(
                                        headlineContent = { 
                                            Text("$numberOfPeopleAttending attending")
                                        },
                                        supportingContent = {
                                            Text("Maximum capacity: ${event.maxAttendance}")
                                        },
                                        leadingContent = {
                                            Icon(
                                                painter = painterResource(R.drawable.baseline_people_24),
                                                contentDescription = "Attendance",
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    )

                                    // Description
                                    Text(
                                        text = "About this event",
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                    )
                                    Text(
                                        text = event.description,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                    )

                                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                                    // Location
                                    ListItem(
                                        headlineContent = { Text(event.location) },
                                        leadingContent = {
                                            Icon(
                                                Icons.Default.LocationOn,
                                                contentDescription = "Location",
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    )

                                    // Map
                                    val coordinatesParts = event.coordinates.split(",")
                                    if (coordinatesParts.size == 2) {
                                        val latitude = coordinatesParts[0].toDoubleOrNull()
                                        val longitude = coordinatesParts[1].toDoubleOrNull()
                                        if (latitude != null && longitude != null) {
                                            Surface(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(200.dp)
                                                    .padding(16.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                            ) {
                                                EventLocationMapView(latitude, longitude)
                                            }
                                        }
                                    }
                                }
                            }

                            // Action Buttons
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                if (event.creatorId != currentUserID) {
                                    FilledTonalButton(
                                        onClick = {
                                            if (isAttending) {
                                                eventService.leaveEvent(currentUserID, event.id)
                                                updateAttendingPeople(false)
                                                UserService().removeEventFromAttend(
                                                    currentUserID,
                                                    event.id,
                                                    onSuccess = { println("Event removed from user") },
                                                    onFailure = { exception ->
                                                        println("Failed to remove event from user: ${exception.message}")
                                                    }
                                                )
                                            } else {
                                                eventService.joinEvent(currentUserID, event.id)
                                                updateAttendingPeople(true)
                                                UserService().addEventToAttend(
                                                    currentUserID,
                                                    event.id,
                                                    onSuccess = { println("Event added to user") },
                                                    onFailure = { exception ->
                                                        println("Failed to add event to user: ${exception.message}")
                                                    }
                                                )
                                            }
                                        }
                                    ) {
                                        Text(if (isAttending) "Leave Event" else "Join Event")
                                    }
                                }

                                if (event.creatorId == currentUserID) {
                                    Button(
                                        onClick = {
                                            eventService.deleteEvent(event.id)
                                            navController.navigate("homeScreen") {
                                                navController.popBackStack()
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.error
                                        )
                                    ) {
                                        Text("Delete Event")
                                    }
                                }
                            }
                        }
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
                TopAppBar(
                    title = {
                        Text(
                            text = currentEvent?.name ?: "Event",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = Color.White
                    )
                )
            },
            content = { paddingValues ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    item {
                        currentEvent?.let { event ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                // Left Column - Image and Map
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(end = 8.dp)
                                ) {
                                    // Event Image
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(250.dp)
                                            .clip(RoundedCornerShape(8.dp)),
                                        color = MaterialTheme.colorScheme.surface,
                                        shadowElevation = 4.dp
                                    ) {
                                        AsyncImage(
                                            model = event.image,
                                            contentDescription = "Event Image",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Map
                                    val coordinatesParts = event.coordinates.split(",")
                                    if (coordinatesParts.size == 2) {
                                        val latitude = coordinatesParts[0].toDoubleOrNull()
                                        val longitude = coordinatesParts[1].toDoubleOrNull()
                                        if (latitude != null && longitude != null) {
                                            Card(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(200.dp),
                                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                                            ) {
                                                EventLocationMapView(latitude, longitude)
                                            }
                                        }
                                    }
                                }

                                // Right Column - Event Details
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(start = 8.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .padding(16.dp)
                                            .fillMaxWidth()
                                    ) {
                                        // Date
                                        val dateFormatter = DateTimeFormatter.ofPattern("d/M/yyyy", Locale.getDefault())
                                        val parsedDate = LocalDate.parse(event.startDate, dateFormatter)
                                        val formattedDate = parsedDate.format(DateTimeFormatter.ofPattern("EEEE d MMMM yyyy"))

                                        ListItem(
                                            headlineContent = { Text(formattedDate) },
                                            leadingContent = {
                                                Icon(
                                                    Icons.Default.DateRange,
                                                    contentDescription = "Date",
                                                    tint = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        )

                                        // Host
                                        ListItem(
                                            headlineContent = { Text("Hosted by $username") },
                                            leadingContent = {
                                                Icon(
                                                    Icons.Default.Person,
                                                    contentDescription = "Host",
                                                    tint = MaterialTheme.colorScheme.primary
        )
    }
                                        )

                                        // Attendance
                                        ListItem(
                                            headlineContent = { 
                                                Text("$numberOfPeopleAttending attending")
                                            },
                                            supportingContent = {
                                                Text("Maximum capacity: ${event.maxAttendance}")
                                            },
                                            leadingContent = {
                                                Icon(
                                                    painter = painterResource(R.drawable.baseline_people_24),
                                                    contentDescription = "Attendance",
                                                    tint = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        )

                                        // Location
                                        ListItem(
                                            headlineContent = { Text(event.location) },
                                            leadingContent = {
                                                Icon(
                                                    Icons.Default.LocationOn,
                                                    contentDescription = "Location",
                                                    tint = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        )

                                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                                        // Description
                                        Text(
                                            text = "About this event",
                                            style = MaterialTheme.typography.titleMedium,
                                            modifier = Modifier.padding(bottom = 8.dp)
                                        )
                                        Text(
                                            text = event.description,
                                            style = MaterialTheme.typography.bodyMedium
                                        )

                                        Spacer(modifier = Modifier.height(16.dp))

                                        // Action Buttons
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceEvenly
                                        ) {
                                            if (event.creatorId != currentUserID) {
                                                FilledTonalButton(
                                                    onClick = {
                                                        if (isAttending) {
                                                            eventService.leaveEvent(currentUserID, event.id)
                                                            updateAttendingPeople(false)
                                                            UserService().removeEventFromAttend(
                                                                currentUserID,
                                                                event.id,
                                                                onSuccess = { println("Event removed from user") },
                                                                onFailure = { exception ->
                                                                    println("Failed to remove event from user: ${exception.message}")
                                                                }
                                                            )
                                                        } else {
                                                            eventService.joinEvent(currentUserID, event.id)
                                                            updateAttendingPeople(true)
                                                            UserService().addEventToAttend(
                                                                currentUserID,
                                                                event.id,
                                                                onSuccess = { println("Event added to user") },
                                                                onFailure = { exception ->
                                                                    println("Failed to add event to user: ${exception.message}")
                                                                }
                                                            )
                                                        }
                                                    }
                                                ) {
                                                    Text(if (isAttending) "Leave Event" else "Join Event")
                                                }
                                            }

                                            if (event.creatorId == currentUserID) {
                                                Button(
                                                    onClick = {
                                                        eventService.deleteEvent(event.id)
                                                        navController.navigate("homeScreen") {
                                                            navController.popBackStack()
                                                        }
                                                    },
                                                    colors = ButtonDefaults.buttonColors(
                                                        containerColor = MaterialTheme.colorScheme.error
                                                    )
                                                ) {
                                                    Text("Delete Event")
                                                }
                                            }
                                        }
                                    }
                                }
                            }
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

@Composable
fun ShowDeleteButton(
    userIFromDb: String,
    hostID: String,
    navController: NavController,
    eventService: EventService,
    thisCurrentEvent: EventData?
) {
    if (userIFromDb == hostID) {
        Button(
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            onClick = {
                thisCurrentEvent?.let {
                    eventService.deleteEvent(it.id)
                    navController.navigate("homeScreen") {
                        navController.popBackStack()
                    }
                }
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Delete")
        }
    }
}

@Composable
fun CoverImageAPIEvent(url: String) {
    AsyncImage(
        model = url,
        contentDescription = "Cover Image",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
    )
}
