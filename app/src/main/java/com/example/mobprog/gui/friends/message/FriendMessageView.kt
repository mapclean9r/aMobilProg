package com.example.mobprog.gui.friends.message

import MessageBubble
import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.runtime.mutableStateListOf
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
import com.example.mobprog.data.FriendService
import com.example.mobprog.data.UserService
import com.example.mobprog.gui.components.BottomNavBar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun FriendMessageView(navController: NavController, friendId: String, friendService: FriendService) {
    val db = FirebaseFirestore.getInstance()
    val messages = remember { mutableStateListOf<Map<String, Any>>() }
    var friend by remember { mutableStateOf("Unknown") }

    // Listen to message updates
    LaunchedEffect(Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser;

        UserService().getUserDataByID(friendId) { user ->
            if (user != null) {
                friend = user.name  // Update `friend` when data is retrieved
            } else {
                println("User not found.")
            }
        }

        if (currentUser != null) {
            friendService.getFriendRelationshipId(friendId) { userRel ->
                if(userRel != null) {
                    db.collection("friends")
                        .document(userRel)
                        .collection("messages")
                        .orderBy("timestamp", Query.Direction.DESCENDING)
                        .addSnapshotListener { snapshot, e ->
                            if (e != null) {
                                e.printStackTrace()
                                return@addSnapshotListener
                            }
                            messages.clear()
                            snapshot?.documents?.forEach { doc ->
                                messages.add(doc.data!!)
                            }
                        }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = friend,
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
                            contentDescription = "Back Button",
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
        content = { padding ->
            val currentUser = FirebaseAuth.getInstance().currentUser ?: return@Scaffold

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(MaterialTheme.colorScheme.onPrimary)
            ) {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    reverseLayout = true
                ) {
                    items(messages) { message ->
                        MessageBubble(
                            message = message["content"].toString(),
                            timestamp = message["timestamp"] as? Timestamp,
                            isSender = message["senderId"] == currentUser.uid
                        )
                    }
                }
                MessageInput(friendId, friendService)
            }
        }
    )
}
