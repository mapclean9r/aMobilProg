package com.example.mobprog.gui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mobprog.createEvent.EventData
import com.example.mobprog.data.EventService
import com.example.mobprog.data.UserService
import com.example.mobprog.gui.components.BottomNavBar
import com.example.mobprog.gui.components.GetSelfProfileImageCircle
import com.example.mobprog.gui.components.ProfileEventBox

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileView(navController: NavController, userService: UserService, onEventClick: (EventData) -> Unit) {

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var createdAt by remember { mutableStateOf("") }
    var userID by remember { mutableStateOf("")}

    var selectedMenu by remember { mutableStateOf("My Events") }

    var myEvents by remember { mutableStateOf<List<EventData>?>(null) }
    var attendingEvents by remember { mutableStateOf<List<EventData>?>(null) }
    var currentView by remember { mutableStateOf("My Events") }


    LaunchedEffect(Unit) {
        userService.getCurrentUserData { userData ->
            println(userData)
            if (userData != null) {
                val fetchedName = userData["name"] as? String
                val fetchedEmail = userData["email"] as? String
                val fetchedDate = userData["dateCreated"] as? String
                val fetchedID = userData["id"] as? String
                username = fetchedName ?: ""
                email = fetchedEmail ?: ""
                createdAt = fetchedDate ?: ""
                userID = fetchedID ?: ""
            } else {
                Log.w("UserData", "No user data found for current user")
            }
        }
    }
    LaunchedEffect(userID) {
        EventService().getEventsByCreatorId(userID) { result ->
            if (result != null) {
                myEvents = result
                println("launch $result")
            } else {
                println("Failed to load events.")
            }
        }
    }

    LaunchedEffect(userID) {
        userService.getAttendingEvents() { result ->
            if (result != null) {
                attendingEvents = result
                println("attend $result")
            } else {
                println("Failed to retrieve attending events.")
            }
        }
    }
if (!isLandscape) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Profile",
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                actions = {
                    IconButton(
                        onClick = { navController.navigate("settingsScreen") }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings Icon",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(152.dp)
                        .background(
                            MaterialTheme.colorScheme.secondary,
                            shape = CircleShape
                        )
                        .padding(1.dp)
                ) {
                    GetSelfProfileImageCircle(200)
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Text(
                        text = username,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(top = 12.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Profile Icon",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    Text(
                        text = createdAt,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W400,
                        modifier = Modifier
                            .padding(2.dp, bottom = 15.dp)
                            .align(Alignment.CenterHorizontally)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "My Events",
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    selectedMenu = "My Events"; currentView = "My Events"
                                }
                                .padding(1.dp)
                                .background(
                                    color = if (selectedMenu == "My Events") MaterialTheme.colorScheme.primary.copy(
                                        alpha = 0.1f
                                    ) else Color.Transparent,
                                    shape = MaterialTheme.shapes.small
                                )
                                .padding(vertical = 8.dp),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 18.sp,
                                fontWeight = if (selectedMenu == "My Events") {
                                    FontWeight.Bold
                                } else FontWeight.Normal,
                                color = if (selectedMenu == "My Events") {
                                    MaterialTheme.colorScheme.primary
                                } else Color.Gray,
                                textAlign = TextAlign.Center
                            )
                        )

                        Text(
                            text = "Attending",
                            modifier = Modifier
                                .weight(1f)
                                .padding(1.dp)
                                .clickable {
                                    selectedMenu = "Attending"; currentView = "Attending Events"
                                }
                                .background(
                                    color = if (selectedMenu == "Attending") MaterialTheme.colorScheme.primary.copy(
                                        alpha = 0.1f
                                    ) else Color.Transparent,
                                    shape = MaterialTheme.shapes.small
                                )
                                .padding(vertical = 8.dp),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 18.sp,
                                fontWeight = if (selectedMenu == "Attending") {
                                    FontWeight.Bold
                                } else FontWeight.Normal,
                                color = if (selectedMenu == "Attending") {
                                    MaterialTheme.colorScheme.primary
                                } else Color.Gray,
                                textAlign = TextAlign.Center
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(5.dp))
                    LazyColumn(
                        modifier = Modifier.weight(1f)
                    ) {
                        when (currentView) {
                            "My Events" -> {
                                if (myEvents?.isEmpty() == true) {
                                    item {
                                        Text(
                                            text = "You have no events created",
                                            modifier = Modifier
                                                .align(Alignment.CenterHorizontally)
                                                .padding(top = 20.dp)
                                        )
                                    }
                                } else {
                                    items(myEvents ?: emptyList()) { event ->
                                        ProfileEventBox(
                                            navController = navController,
                                            eventData = event,
                                            eventClick = { event ->
                                                onEventClick(event)
                                            })
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }
                                }
                            }

                            "Attending Events" -> {
                                if (attendingEvents?.isEmpty() == true) {
                                    item {
                                        Text(
                                            text = "You are not attending any events",
                                            modifier = Modifier
                                                .align(Alignment.CenterHorizontally)
                                                .padding(top = 20.dp)
                                        )
                                    }
                                } else {
                                    items(attendingEvents ?: emptyList()) { event ->
                                        ProfileEventBox(
                                            navController = navController,
                                            eventData = event,
                                            eventClick = { event ->
                                                onEventClick(event)
                                            })
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        ,

                bottomBar = {
            // inspirert av link under for å lage navbar.
            // https://www.youtube.com/watch?v=O9csfKW3dZ4
            BottomNavBar(navController = navController, userService = UserService())
        }
    )
} else {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Profile",
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                actions = {
                    IconButton(
                        onClick = { navController.navigate("settingsScreen") }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings Icon",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .width(200.dp)
                            .align(Alignment.Top)
                            .padding(start = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(102.dp)
                                .background(
                                    MaterialTheme.colorScheme.secondary,
                                    shape = CircleShape
                                )
                                .padding(1.dp)
                        ) {
                            GetSelfProfileImageCircle(100)
                        }

                        Text(
                            text = username,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .padding(top = 22.dp)
                                .align(Alignment.Start)
                        )

                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Profile Icon",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.align(Alignment.Start)
                        )

                        Text(
                            text = createdAt,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W400,
                            modifier = Modifier
                                .padding(2.dp, bottom = 15.dp)
                                .align(Alignment.Start)
                        )
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "My Events",
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        selectedMenu = "My Events"; currentView = "My Events"
                                    }
                                    .padding(1.dp)
                                    .background(
                                        color = if (selectedMenu == "My Events") MaterialTheme.colorScheme.primary.copy(
                                            alpha = 0.1f
                                        ) else Color.Transparent,
                                        shape = MaterialTheme.shapes.small
                                    )
                                    .padding(vertical = 8.dp),
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = 18.sp,
                                    fontWeight = if (selectedMenu == "My Events") {
                                        FontWeight.Bold
                                    } else FontWeight.Normal,
                                    color = if (selectedMenu == "My Events") {
                                        MaterialTheme.colorScheme.primary
                                    } else Color.Gray,
                                    textAlign = TextAlign.Center
                                )
                            )

                            Text(
                                text = "Attending",
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(1.dp)
                                    .clickable {
                                        selectedMenu = "Attending"; currentView = "Attending Events"
                                    }
                                    .background(
                                        color = if (selectedMenu == "Attending") MaterialTheme.colorScheme.primary.copy(
                                            alpha = 0.1f
                                        ) else Color.Transparent,
                                        shape = MaterialTheme.shapes.small
                                    )
                                    .padding(vertical = 8.dp),
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = 18.sp,
                                    fontWeight = if (selectedMenu == "Attending") {
                                        FontWeight.Bold
                                    } else FontWeight.Normal,
                                    color = if (selectedMenu == "Attending") {
                                        MaterialTheme.colorScheme.primary
                                    } else Color.Gray,
                                    textAlign = TextAlign.Center
                                )
                            )
                        }
                        when (currentView) {
                            "My Events" -> {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    if (myEvents?.isEmpty() == true) {
                                        item {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Text(
                                                    text = "You have no events created",
                                                    modifier = Modifier
                                                        .align(Alignment.CenterHorizontally)
                                                        .padding(top = 20.dp)
                                                )
                                            }
                                        }
                                    } else {
                                        items(myEvents ?: emptyList()) { event ->
                                            ProfileEventBox(
                                                navController = navController,
                                                eventData = event,
                                                eventClick = { event ->
                                                    onEventClick(event)
                                                })
                                            Spacer(modifier = Modifier.height(8.dp))
                                        }
                                    }
                                }
                            }

                            "Attending Events" -> {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    if (attendingEvents?.isEmpty() == true) {
                                        item {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Text(
                                                    text = "You are not attending any events",
                                                    modifier = Modifier
                                                        .align(Alignment.CenterHorizontally)
                                                        .padding(top = 20.dp)
                                                )
                                            }
                                        }
                                    } else {
                                        items(attendingEvents ?: emptyList()) { event ->
                                            ProfileEventBox(
                                                navController = navController,
                                                eventData = event,
                                                eventClick = { event ->
                                                    onEventClick(event)
                                                })
                                            Spacer(modifier = Modifier.height(8.dp))
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

/*

@Preview(showBackground = true)
@Composable
fun ProfileViewPreview() {
    ProfileView(navController = rememberNavController(), userService = UserService())
}
*/