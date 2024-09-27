package com.example.mobprog.gui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun ProfileActivity(navController: NavController) {

}

@Preview(showBackground = true)
@Composable
fun ProfileActivityPreview() {
    ProfileActivity(navController = rememberNavController())
}