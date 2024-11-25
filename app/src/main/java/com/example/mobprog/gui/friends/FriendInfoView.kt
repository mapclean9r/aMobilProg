package com.example.mobprog.gui.friends

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.mobprog.data.UserService
import com.example.mobprog.gui.components.BottomNavBar
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun FriendInfoView(navController: NavController, friend: String) {

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .systemBarsPadding()
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
                            contentDescription = "Back Icon",
                            tint = Color.White
                        )
                    }
                    Text(
                        text = "Edit Friend",
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
                    .systemBarsPadding()
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
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
                        text = friend,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
                    )
                }

                Button(
                    onClick = {

                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Remove Friend")
                }
            }
        },

        )
}