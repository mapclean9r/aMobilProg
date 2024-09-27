package com.example.mobprog.gui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun GuildActivity(navController: NavController) {

}

@Preview(showBackground = true)
@Composable
fun GuildActivityPreview() {
    GuildActivity(navController = rememberNavController())
}