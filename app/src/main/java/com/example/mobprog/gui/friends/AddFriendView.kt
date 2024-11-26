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
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mobprog.api.GameData
import com.example.mobprog.api.GamingApi
import com.example.mobprog.data.FriendService
import com.example.mobprog.data.UserService
import com.example.mobprog.gui.components.BottomNavBar
import com.example.mobprog.gui.components.FriendBox
import com.example.mobprog.gui.components.GameBox
import com.google.firebase.auth.FirebaseAuth


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AddFriendView(navController: NavController) {

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    val uid by remember { mutableStateOf("") }
    var showSearch by remember { mutableStateOf(true) }
    var searchFriendText by remember { mutableStateOf("") }
    var selectedFriend by remember { mutableStateOf<FriendData?>(null) }
    var friends by remember { mutableStateOf(emptyList<FriendData>()) }
    var currentUserFriends by remember { mutableStateOf(emptyList<String>()) }

    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    UserService().getAllUsers { userList ->
        userList?.let {
            friends = userList
        } ?: run {
            println("No Users found")
        }
    }

    FriendService().getAcceptedFriends { friendData ->
        friendData?.let {
            currentUserFriends = it.map { friend -> friend.id }
        }
    }

    if (!isLandscape) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Add Friend",
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.navigate("friendsScreen") {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
        ,
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
                Box(
                    modifier = Modifier

                        .background(MaterialTheme.colorScheme.background)
                        .padding()
                        .clickable {
                            showSearch = false
                        }
                )
                Column {
                    TextField(
                        value = searchFriendText,
                        onValueChange = { newText ->
                            searchFriendText = newText
                        },
                        placeholder = { Text("Search...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxSize(fraction = 0.09f)
                            .background(MaterialTheme.colorScheme.primary)
                            .focusRequester(focusRequester),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                keyboardController?.hide()
                            }
                        ),
                        singleLine = true
                    )
                    Button(
                        onClick = {
                            searchFriendText = ""
                            focusManager.clearFocus()
                            keyboardController?.hide()
                        },
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.Gray
                        )
                    ) {
                        Text("Cancel Search")
                    }

                    val filteredFriends = friends.filter { friend ->
                        !currentUserFriends.contains(friend.id) && friend.name.contains(searchFriendText, ignoreCase = true)
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        if (filteredFriends.isEmpty()) {
                            item {
                                Text(
                                    text = "No users found matching \"$searchFriendText\"",
                                    modifier = Modifier.padding(16.dp),
                                    color = Color.Gray
                                )
                            }
                        } else {
                            items(filteredFriends) { friend ->
                                Spacer(modifier = Modifier.height(8.dp))
                                if (friend.id != uid) {
                                    FriendBox(friendData = friend, navController) {
                                        selectedFriend = friend
                                        showSearch = false
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        )
    }
    else {
        Scaffold(
            topBar = {
                Row(
                    modifier = Modifier
                        .systemBarsPadding()
                        .fillMaxWidth()
                        .height(30.dp)
                        .background(MaterialTheme.colorScheme.primary)
                    ,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
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
                        .systemBarsPadding()
                        .fillMaxWidth()
                        .padding(3.dp)
                        .padding(horizontal = 2.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.background)
                            .padding()
                            .clickable {
                                showSearch = false
                            }
                    )
                    Column{
                        TextField(
                            value = searchFriendText,
                            onValueChange = { newText ->
                                searchFriendText = newText
                            },
                            placeholder = { Text("Search...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxSize(fraction = 0.22f)
                                .background(MaterialTheme.colorScheme.primary)
                                .focusRequester(focusRequester),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                    keyboardController?.hide()
                                }
                            ),
                            singleLine = true
                        )
                        Button(
                            onClick = {
                                searchFriendText = ""
                                focusManager.clearFocus()
                                keyboardController?.hide()
                            },
                            modifier = Modifier
                                .fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = Color.Gray
                            )
                        ) {
                            Text("Cancel Search")
                        }

                        val filteredFriends = friends.filter { friend ->
                            !currentUserFriends.contains(friend.id) && friend.name.contains(searchFriendText, ignoreCase = true)
                        }

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            if (filteredFriends.isEmpty()) {
                                item {
                                    Text(
                                        text = "No users found matching \"$searchFriendText\"",
                                        modifier = Modifier.padding(16.dp),
                                        color = Color.Gray
                                    )
                                }
                            } else {
                                items(filteredFriends) { friend ->
                                    Spacer(modifier = Modifier.height(4.dp))
                                    if (friend.id != uid) {
                                        FriendBox(friendData = friend, navController) {
                                            selectedFriend = friend
                                            showSearch = false
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            },
        )
    }


}

fun onAddFriend(uid: String) {
    FriendService().addFriend(uid) { callback ->
        if(callback) {
            Log.w("UserData", "Succesfully added friend")
        } else {
            Log.w("UserData",  "User Not Found")

        }
    }
}

