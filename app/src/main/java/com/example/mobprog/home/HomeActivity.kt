package com.example.mobprog.home

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun HomeActivity(navController: NavController) {
    Text(text = "HomePage", fontSize = 28.sp, fontWeight = FontWeight.Bold)
}

@Preview(showBackground = true)
@Composable
fun HomeActivityPreview() {
    HomeActivity(navController = rememberNavController())
}
