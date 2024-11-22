package com.example.mobprog.gui.friends

import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mobprog.data.FriendService
import com.example.mobprog.data.UserService
import com.example.mobprog.gui.components.BottomNavBar
import com.example.mobprog.gui.components.GetUserProfileImageCircle

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "MutableCollectionMutableState")
@Composable
fun FriendsView(navController: NavController) {

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    val friends = remember { mutableStateOf<ArrayList<FriendData>?>(ArrayList()) }
    val allFriends = remember { mutableStateOf<ArrayList<FriendData>?>(ArrayList()) }
    val isLoading = remember { mutableStateOf(true) }
    val hasRequests = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        FriendService().getAcceptedFriends { friendData ->
            if (friendData != null) {
                allFriends.value = friendData
            }
            else {
                println("getAllFriends Failed")
            }
        }
        FriendService().getUserFriends { friendData ->
            if(friendData != null) {
                friends.value = friendData

                if(friendData.stream().filter{!it.accepted}.count() >= 1) {
                    hasRequests.value = true;
                }
            }
            isLoading.value = false
        }
    }

    Scaffold(
        topBar = {
            if (!isLandscape) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(88.dp)
                        .padding(bottom = 10.dp, top = 24.dp)
                        .background(MaterialTheme.colorScheme.primary),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = { navController.navigate("friendRequestScreen") },

                        ) {
                        Box {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = Color.White
                            )
                            if (hasRequests.value) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .background(Color.Red, shape = CircleShape)
                                        .align(Alignment.TopEnd)
                                        .offset(x = (-4).dp, y = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(32.dp))
                    Text(
                        text = "Friends",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier
                            .weight(2f)
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.width(32.dp))

                    IconButton(
                        onClick = { navController.navigate("addFriendScreen") },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Friend",
                            tint = Color.White
                        )
                    }
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                        .padding(bottom = 10.dp, top = 24.dp)
                        .background(MaterialTheme.colorScheme.primary),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    IconButton(
                        onClick = { navController.navigate("friendRequestScreen") },
                        Modifier.padding(start = 25.dp)
                        ) {
                        Box {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = Color.White
                            )
                            if (hasRequests.value) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .background(Color.Red, shape = CircleShape)
                                        .align(Alignment.TopEnd)
                                        .offset(x = (-4).dp, y = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(32.dp))
                    Text(
                        text = "Friends",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier
                            .weight(2f)
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.width(32.dp))

                    IconButton(
                        onClick = { navController.navigate("addFriendScreen") },
                        Modifier.padding(end = 25.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Friend",
                            tint = Color.White
                        )
                    }
                }
            }

    }, content = {
        if (!isLoading.value) {
            if (allFriends.value?.isEmpty() != true) {
                if(allFriends.value?.stream()?.filter{it.accepted}?.count()!! >= 1) {
                    allFriends.value?.let { FriendsList(navController, it) }
                    return@Scaffold
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "No Friends Yet",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = TextStyle(
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Loading...",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = TextStyle(
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
    }, bottomBar = {
            // inspirert av link under for å lage navbar.
            // https://www.youtube.com/watch?v=O9csfKW3dZ4
            BottomNavBar(navController = navController, userService = UserService())
        })
}

@Composable
fun FriendsList(navController: NavController, friends: ArrayList<FriendData>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(28.dp),
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate("anyProfileView/${friend.id}")
            }
            .padding(8.dp),
        //.border(2.dp, Color.LightGray, shape = RoundedCornerShape(10.dp))
        verticalAlignment = Alignment.CenterVertically,

        ){

        GetUserProfileImageCircle(userID = friend.id, size = 60)
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
                        colors = ButtonDefaults.buttonColors(Color.Red),
                        shape = CircleShape
                    ) {
                        Text(text = "Remove Friend", color = Color.White)
                    }

                    Button(
                        onClick = { navController.navigate("friendMessageView/${friend.id}")
                        },
                        colors = ButtonDefaults.buttonColors(Color.Blue),
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
