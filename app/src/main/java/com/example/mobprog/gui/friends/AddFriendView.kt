package com.example.mobprog.gui.friends

import android.annotation.SuppressLint
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.example.mobprog.api.GameData
import com.example.mobprog.api.GamingApi
import com.example.mobprog.data.UserService
import com.example.mobprog.gui.components.BottomNavBar
import com.example.mobprog.gui.components.FriendBox
import com.example.mobprog.gui.components.GameBox
import com.google.firebase.auth.FirebaseAuth


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AddFriendView(navController: NavController) {

    var uid by remember { mutableStateOf("") }
    var showSearch by remember { mutableStateOf(false) }
    var searchFriendText by remember { mutableStateOf("") }
    var selectedFriend by remember { mutableStateOf<FriendData?>(null) }
    var friends by remember { mutableStateOf(emptyList<FriendData>()) }


    UserService().getAllUsers { userList ->
        userList?.let {
            friends = userList
        } ?: run {
            println("No Users found")
        }
    }

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
                Spacer(modifier = Modifier.height(22.dp))

                Button(
                    onClick = {
                        showSearch = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = Color.White
                    )
                ) {
                    Text("Add Friend From User List", color = MaterialTheme.colorScheme.primary)
                }
            }
        },

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
            value = searchFriendText,
            onValueChange = { searchFriendText = it },
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

            items(friends) { friend ->
                Spacer(modifier = Modifier.height(8.dp))

                if(friend.id != uid) {
                    FriendBox(friendData = friend, navController) {
                        selectedFriend = friend
                        showSearch = false

                    }
                }

            }
        }
    }
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

