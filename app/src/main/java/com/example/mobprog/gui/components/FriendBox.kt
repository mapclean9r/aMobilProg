package com.example.mobprog.gui.components

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
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
import com.example.mobprog.gui.friends.FriendData
import com.example.mobprog.util.titleLengthCheck


@Composable
fun FriendBox (friendData: FriendData, navController: NavController, onClick: (FriendData) -> Unit) {


    var isUpdating by remember { mutableStateOf(false) }
    var updateStatus by remember { mutableStateOf<String?>(null) }


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
                            updateStatus = "Succesfully added " + friendData.name

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
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = titleLengthCheck(friendData.name),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
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