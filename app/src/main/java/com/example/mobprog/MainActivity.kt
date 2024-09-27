package com.example.mobprog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mobprog.gui.LoginView
import com.example.mobprog.gui.RegisterView
import com.example.mobprog.gui.CreateEventView
import com.example.mobprog.gui.FriendsView
import com.example.mobprog.gui.guild.GuildView
import com.example.mobprog.gui.HomeView
import com.example.mobprog.gui.NotificationView
import com.example.mobprog.gui.ProfileView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Arena()
        }
    }
}

@Composable
fun Arena() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "homeScreen") {
        composable("loginScreen") {
            LoginView(navController = navController)
        }
        composable("registerScreen") { 
            RegisterView(navController = navController)
        }
        composable("homeScreen") {
            HomeView(navController = navController)
        }
        composable("createEventScreen") {
            CreateEventView(navController = navController)
        }
        composable("friendsScreen") {
            FriendsView(navController = navController)
        }
        composable("guildScreen") {
            GuildView(navController = navController)
        }
        composable("profileScreen") {
            ProfileView(navController = navController)
        }
        composable("notificationScreen") {
            NotificationView(navController = navController)
        }
    }
}