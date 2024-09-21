package com.example.mobprog.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.mobprog.ui.theme.MobProgTheme


@Composable
fun LoginScreen(modifier: Modifier = Modifier) {
    Text(
        text = "Login Page",
        modifier = modifier
    )
    Column {
        TextField(
            value = "null",
            onValueChange = {},
            label = { Text("Skriv noe her") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    MobProgTheme {
        LoginScreen()
    }
}
