package com.example.mobprog.gui.friends

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mobprog.data.UserService
import com.example.mobprog.gui.components.BottomNavBar
import com.google.firebase.auth.FirebaseAuth


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AddFriendView(navController: NavController) {

    var uid by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(88.dp)
                    .padding(bottom = 10.dp, top = 24.dp)
                    .background(MaterialTheme.colorScheme.primary),
                verticalAlignment = Alignment.CenterVertically,
                //horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {

                    IconButton(
                        onClick = {
                            navController.navigate("friendsScreen")
                        },
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Search Icon",
                            tint = Color.White
                        )
                    }
                    Text(
                        text = "Add Friend",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        },
        bottomBar = {
            // inspirert av link under for å lage navbar.
            // https://www.youtube.com/watch?v=O9csfKW3dZ4
            BottomNavBar(navController = navController, userService = UserService())
        },
        content = { paddingValues ->

            val currentUser = FirebaseAuth.getInstance().currentUser ?: return@Scaffold

            val uidUser = currentUser.uid

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "User Id",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W400,
                    modifier = Modifier
                        .padding(6.dp)
                        .align(Alignment.Start)
                )
                TextField(
                    value = uid,
                    onValueChange = { newText ->
                        uid = newText
                    },
                    label = { Text("Enter User ID") },
                    placeholder = { Text("userid") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(22.dp))
                Button(
                    onClick = {
                        onAddFriend(
                            uid
                        )
                        uid = ""
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Add Friend")
                }

            }
        },

        )
}

fun onAddFriend(uid: String) {
    UserService().addFriend(uid) { callback ->
        if(callback) {
            Log.w("UserData", "Succesfully added friend")
        } else {
            Log.w("UserData",  "User Not Found")

        }
    }
}

