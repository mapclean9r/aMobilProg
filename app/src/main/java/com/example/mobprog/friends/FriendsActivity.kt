package com.example.mobprog.friends

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun FriendsActivity(navController: NavController) {

}

@Preview(showBackground = true)
@Composable
fun FriendsActivityPreview() {
    FriendsActivity(navController = rememberNavController())
}
