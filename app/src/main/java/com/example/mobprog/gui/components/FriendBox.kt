package com.example.mobprog.gui.components

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mobprog.R
import com.example.mobprog.data.FriendService
import com.example.mobprog.data.UserService
import com.example.mobprog.gui.friends.FriendData
import com.example.mobprog.util.titleLengthCheck


@Composable
fun FriendBox (friendData: FriendData, navController: NavController, onClick: (FriendData) -> Unit) {


    var isUpdating by remember { mutableStateOf(false) }
    var updateStatus by remember { mutableStateOf<String?>(null) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                FriendService().addFriend(friendData.id) { callback ->
                    if (callback) {
                        isUpdating = false
                        updateStatus = "Succesfully sent friend request" + friendData.name

                        Handler(Looper.getMainLooper()).postDelayed({
                            navController.navigate("friendsScreen") {
                                while (navController.popBackStack() == true) {
                                    navController.popBackStack()
                                }
                            }
                        }, 2000)
                    } else {
                        updateStatus = "User not found: " + friendData.name

                    }
                }
            }
            .border(2.dp, MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(10.dp))
            .padding(12.dp)
        ,
        verticalAlignment = Alignment.CenterVertically,

        ) {

        GetUserProfileImageCircle(userID = friendData.id, size = 56)

        Column(
            modifier = Modifier.padding(start = 16.dp)
        ) {
            Text(
                text = friendData.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            )
            Row(
                modifier = Modifier.padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp) // Space between buttons
            ) {
                Button(
                    onClick = {
                        FriendService().removeFriend(friendData.id, callback = {
                            navController.navigate("friendsScreen")
                        })
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF5CDE5C)),
                    shape = CircleShape
                ) {
                    Text(text = "Add Friend", color = Color.White)
                }

            }
        }


        /*
    Row {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, Color.Black, shape = RoundedCornerShape(10.dp))
            .padding(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding()
                .clickable {
                    UserService().addFriend(friendData.id) { callback ->
                        if (callback) {
                            isUpdating = false
                            updateStatus = "Succesfully sent friend request" + friendData.name

                            Handler(Looper.getMainLooper()).postDelayed({
                                navController.navigate("friendsScreen") {
                                    while (navController.popBackStack() == true) {
                                        navController.popBackStack()
                                    }
                                }
                            }, 2000)
                        } else {
                            updateStatus = "User not found: " + friendData.name

                        }
                    }
                }
        ) {
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
                    text = friendData.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
                )
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp) // Space between buttons
                ) {
                    Button(
                        onClick = { UserService().removeFriend(friendData.id, callback = {
                            navController.navigate("friendsScreen")
                        })},
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
                        shape = CircleShape
                    ) {
                        Text(text = "Add Friend", color = Color.White)
                    }

                }
            }
        }
        updateStatus?.let { statusMessage ->
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = statusMessage,
                color = if (statusMessage.contains("Succesfully", true)) Color.Green else Color.Red,
                fontWeight = FontWeight.Bold
            )
        }
    }
    }

 */

    }
}