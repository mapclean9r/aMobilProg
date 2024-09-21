package com.example.mobprog.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.mobprog.ui.theme.MobProgTheme


@Composable
fun LoginActivity(modifier: Modifier = Modifier) {
    var textState by remember { mutableStateOf(" ") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Hjem-skjerm") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* Handling */ }) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        },
        content = { textState ->
            Text(
                text = "Login Page",
                modifier = modifier
            )
            Column {
                TextField(
                    value = textState,
                    onValueChange = { textState = it },
                    label = { Text("Username:") }
                )
            }
        }
    )


}

@Preview(showBackground = true)
@Composable
fun LoginActivityPreview() {
    MobProgTheme {
        LoginActivity()
    }
}
