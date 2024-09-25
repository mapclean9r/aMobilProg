package com.example.mobprog.profile

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun NotificationActivity(navController: NavController) {

}

@Preview(showBackground = true)
@Composable
fun NotificationActivityPreview() {
    NotificationActivity(navController = rememberNavController())
}
