package com.example.mobprog.gui

import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.example.mobprog.createEvent.EventData
import com.example.mobprog.data.EventService
import com.example.mobprog.data.UserService
import com.example.mobprog.gui.components.BottomNavBar
import com.example.mobprog.gui.components.EventBox
import com.example.mobprog.gui.components.GetSelfProfileImageCircle
import com.example.mobprog.gui.components.ProfileEventBox
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase



private fun logout(navController: NavController) {
    val auth = Firebase.auth
    auth.signOut()
    navController.navigate("loginScreen") {
        popUpTo("homeScreen") {
            inclusive = true
        }
        navController.popBackStack()
    }
}

@Composable
fun ProfileView(navController: NavController, userService: UserService) {

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var createdAt by remember { mutableStateOf("") }
    var userID by remember { mutableStateOf("")}

    var myEvents by remember { mutableStateOf<List<EventData>?>(null) }


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
                Box(modifier = Modifier.fillMaxWidth()){
                    Text(
                        text = "Profile",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    IconButton(onClick = { navController.navigate("settingsScreen") },
                        modifier = Modifier.align(Alignment.CenterEnd)) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Profile Icon",
                            tint = Color.White,
                        )
                    }
                }
            }
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
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


                Column (modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)) {
                    Text(text = username,
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

                    Text(text = createdAt,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W400,
                        modifier = Modifier
                            .padding(2.dp, bottom = 15.dp)
                            .align(Alignment.CenterHorizontally)
                    )

                    Divider(color = Color.Black, thickness = 1.dp)

                    Spacer(modifier = Modifier.height(20.dp))

                    println(myEvents)
                    myEvents?.forEach { event ->
                        ProfileEventBox(navController = navController, eventData = event) {
                            println("Event stuff -> $event")
                        }}

                    Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Red // Bare endrer bakgrunnsfargen til mørkegrønn
                            ),
                            onClick = {
                           logout(navController) }, ) {
                            Text(text = "Logout")
                        }
                }
            }
        },
        bottomBar = {
            // inspirert av link under for å lage navbar.
            // https://www.youtube.com/watch?v=O9csfKW3dZ4
            BottomNavBar(navController = navController, userService = UserService())
        }
    )
}

@Composable
fun UsernameWithIcon(username: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(top = 12.dp)
    ) {
        Text(
            text = username,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.width(8.dp)) // Optional spacing between text and icon

        Icon(
            imageVector = Icons.Default.Star, // Replace with your desired icon
            contentDescription = "User Icon",
            tint = Color.Yellow,
            modifier = Modifier.size(24.dp) // Adjust icon size as needed
        )
    }
}



@Preview(showBackground = true)
@Composable
fun ProfileViewPreview() {
    ProfileView(navController = rememberNavController(), userService = UserService())
}
