package com.example.mobprog.createEvent

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun CreateEventActivity(navController: NavController) {

}

@Preview(showBackground = true)
@Composable
fun CreateEventActivityPreview() {
    CreateEventActivity(navController = rememberNavController())
}
