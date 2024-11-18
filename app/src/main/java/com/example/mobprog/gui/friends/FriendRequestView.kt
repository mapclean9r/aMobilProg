package com.example.mobprog.gui.friends

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mobprog.R
import com.example.mobprog.data.FriendService
import com.example.mobprog.data.UserService
import com.example.mobprog.gui.components.BottomNavBar
import com.example.mobprog.gui.components.GetUserProfileImageCircle
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "MutableCollectionMutableState")
@Composable
fun FriendRequestView(navController: NavController) {

    val friendRequests = remember { mutableStateOf<ArrayList<FriendData>?>(null) }
    val isLoading = remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        FriendService().getUserFriends { friendData ->
            if (friendData != null) {
                val pendingRequests = friendData.filter { !it.accepted }
                friendRequests.value = ArrayList(pendingRequests)
            } else {
                friendRequests.value = ArrayList()
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
                    navController.navigate("friendsScreen")
                    {
                        popUpTo(0) { inclusive = true }
                    }
                },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Text(
                    text = "Friend Requests",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }, content = {
        if (!isLoading.value) {
            if (friendRequests.value.isNullOrEmpty()) {
                NoFriendRequestsMessage()
            } else {
                FriendRequestList(navController, friendRequests.value!!)
            }
        }
    }, bottomBar = {
        // inspirert av link under for å lage navbar.
        // https://www.youtube.com/watch?v=O9csfKW3dZ4
        BottomNavBar(navController = navController, userService = UserService())
    })
}

@Composable
fun FriendRequestList(navController: NavController, friends: ArrayList<FriendData>) {

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
                    if(!friend.accepted) {
                        FriendRequestItem(navController, friend) {
                        }
                    }
                }
            }
        }

    }
}



@Composable
fun FriendRequestItem(navController: NavController, friend: FriendData, onClick: () -> Unit) {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = {
                navController.navigate("anyProfileView/${friend.id}")
            })
            .padding(8.dp)
        //.border(2.dp, Color.LightGray, shape = RoundedCornerShape(10.dp))
        ,
        verticalAlignment = Alignment.CenterVertically,

        ){

        GetUserProfileImageCircle(userID = friend.id, size = 56)
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
                    onClick = { FriendService().acceptFriendRequest(friend.id, callback = {
                        navController.navigate("friendRequestScreen")
                    }) },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Green),
                    shape = CircleShape
                ) {
                    Text(text = "Accept", color = Color.White)
                }

                Button(
                    onClick = { FriendService().removeFriend(friend.id, callback = {
                        navController.navigate("friendRequestScreen")
                    }) },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
                    shape = CircleShape
                ) {
                    Text(text = "Deny", color = Color.White)
                }
            }
        }

    }
}

@Composable
fun NoFriendRequestsMessage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No pending friend requests",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FriendRequestViewPreview() {
    FriendRequestView(navController = rememberNavController())
}
