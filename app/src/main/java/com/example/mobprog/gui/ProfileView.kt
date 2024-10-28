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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
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
import com.example.mobprog.data.UserService
import com.example.mobprog.gui.components.BottomNavBar
import com.example.mobprog.gui.components.GetProfileImageCircle
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

    LaunchedEffect(Unit) {
        userService.getCurrentUserData { userData ->
            println(userData)
            if (userData != null) {
                val fetchedName = userData["name"] as? String
                val fetchedEmail = userData["email"] as? String
                val fetchedDate = userData["dateCreated"] as? String
                username = fetchedName ?: ""
                email = fetchedEmail ?: ""
                createdAt = fetchedDate ?: ""
            } else {
                Log.w("UserData", "No user data found for current user")
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
                    GetProfileImageCircle()
                }
                Column (modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)) {
                    Text(text = "Username: $username",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.W400,
                        modifier = Modifier
                            .padding(12.dp)
                            .align(Alignment.Start)
                    )
                    Text(text = "Email: $email",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.W400,
                        modifier = Modifier
                            .padding(12.dp)
                            .align(Alignment.Start)
                    )
                    Text(text = "User created: $createdAt",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.W400,
                        modifier = Modifier
                            .padding(12.dp)
                            .align(Alignment.Start)
                    )
                    Spacer(
                        modifier = Modifier.height(20.dp)
                    )
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

@Preview(showBackground = true)
@Composable
fun ProfileViewPreview() {
    ProfileView(navController = rememberNavController(), userService = UserService())
}
