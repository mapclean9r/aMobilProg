package com.example.mobprog.gui.friends

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.dataStore
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mobprog.R
import com.example.mobprog.data.FriendService
import com.example.mobprog.data.UserService
import com.example.mobprog.gui.components.BottomNavBar

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "MutableCollectionMutableState")
@Composable
fun FriendsView(navController: NavController) {

    val friends = remember { mutableStateOf<ArrayList<FriendData>?>(ArrayList()) }
    val isLoading = remember { mutableStateOf(true) }
    val hasRequests = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        FriendService().getAllFriends { friendData ->
            if(friendData != null) {
                friends.value = friendData

                val notifs = friendData.stream().filter{!it.accepted}.count().toString();
                Log.w("UserData", notifs)
                if(friendData.stream().filter{!it.accepted}.count() >= 1) {
                    hasRequests.value = true;
                }
            }
            isLoading.value = false
        }
    }

    Scaffold(topBar = {
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

                IconButton(onClick = {
                    navController.navigate("friendRequestScreen")
                },
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = Color.White,
                    )
                    if(hasRequests.value) {
                        Box(
                            modifier = Modifier
                                .size(12.dp) // Size of the red dot
                                .background(Color.Red, shape = CircleShape) // Red circle shape
                                //.align(Alignment.TopEnd) // Aligns to the top right of the notification icon
                                .absoluteOffset(x = 40.dp, y = 20.dp)
                                .align(BiasAlignment(0.3f, -0.7f))

                        )
                    }
                }
                Text(
                    text = "Friends",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }, content = {
        if (!isLoading.value) {
            if (friends.value?.isEmpty() != true) {
                if(friends.value?.stream()?.filter{it.accepted}?.count()!! >= 1) {
                    friends.value?.let { FriendsList(navController, it) }
                    return@Scaffold
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.Center, // Center content vertically
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Text when there are no friends
                Text(
                    text = "No Friends Yet",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = TextStyle(
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary // Text color
                    )
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.Center, // Center content vertically
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Text when there are no friends
                Text(
                    text = "Loading...",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = TextStyle(
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary // Text color
                    )
                )
            }
        }
    }, bottomBar = {
            // inspirert av link under for å lage navbar.
            // https://www.youtube.com/watch?v=O9csfKW3dZ4
            BottomNavBar(navController = navController, userService = UserService())
        },
        floatingActionButton = {
            FloatingActionButton(
                containerColor = Color.Magenta,
                onClick = { navController.navigate("addFriendScreen") },
                modifier = Modifier.padding(16.dp),  // Padding from the bottom and right
                content = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "add",
                        tint = Color.Black
                    )
                }
            )
        })
}

@Composable
fun FriendsList(navController: NavController, friends: ArrayList<FriendData>) {
    Column(
        modifier = Modifier.fillMaxSize().padding(28.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(50.dp))

        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            LazyColumn {
                items(friends) { friend ->
                    FriendItem(navController, friend) {
                    }
                }
            }
        }

    }
}



@Composable
fun FriendItem(navController: NavController, friend: FriendData, onClick: () -> Unit) {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(8.dp)
            //.border(2.dp, Color.LightGray, shape = RoundedCornerShape(10.dp))
        ,
        verticalAlignment = Alignment.CenterVertically,

    ){

            Image(
                modifier = Modifier
                    .clip(shape = CircleShape)
                    .size(56.dp),
                painter = painterResource(id = R.drawable.profile),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier.padding(start = 16.dp)
            ) {
                Text(
                    text = friend.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
                )
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp) // Space between buttons
                ) {
                    Button(
                        onClick = { FriendService().removeFriend(friend.id, callback = {
                            navController.navigate("friendsScreen")
                        })},
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
                        shape = CircleShape
                    ) {
                        Text(text = "Remove Friend", color = Color.White)
                    }

                    Button(
                        onClick = { },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Blue),
                        shape = CircleShape
                    ) {
                        Text(text = "Message", color = Color.White)
                    }
                }
            }
    }
}

@Preview(showBackground = true)
@Composable
fun FriendsViewPreview() {
    FriendsView(navController = rememberNavController())
}
