package com.example.mobprog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mobprog.auth.LoginActivity
import com.example.mobprog.auth.RegisterActivity
import com.example.mobprog.home.HomeActivity

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
    NavHost(navController = navController, startDestination = "loginScreen") {
        composable("loginScreen") {
            LoginActivity(navController = navController)
        }
        composable("registerScreen") { 
            RegisterActivity(navController = navController)
        }
        composable("homeScreen") {
            HomeActivity(navController = navController)
        }
    }
}