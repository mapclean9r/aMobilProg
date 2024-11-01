package com.example.mobprog.gui.friends.message

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mobprog.data.FriendService

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MessageInput(friendId: String) {
    var message by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        TextField(
            value = message,
            onValueChange = { message = it },
            placeholder = { Text("Type a message") },
            modifier = Modifier
                .weight(1f)
                .background(Color.White, shape = MaterialTheme.shapes.small)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Button(
            onClick = {
                if (message.isNotBlank()) {
                    FriendService().sendMessage(friendId, message, callback = {

                    })
                    message = ""  // Clear input
                }
            }
        ) {
            Text("Send")
        }
    }
}