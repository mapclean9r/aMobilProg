package com.example.mobprog.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoginActivity(modifier: Modifier = Modifier) {

    var usernameTextController by remember {
        mutableStateOf("")
    }

    var passwordTextController by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Welcome to Arena", fontSize = 28.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(28.dp))

        Text(text = "Enter username and password", fontSize = 16.sp)

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(value = usernameTextController, onValueChange = {
            usernameTextController = it
        }, label = {
            Text(text = "Username")
        })

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(value = passwordTextController, onValueChange = {
            passwordTextController = it
        }, label = {
            Text(text = "Password")
        }, visualTransformation = PasswordVisualTransformation())

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = {  }) {
            Text(text = "Login")
        }

        Spacer(modifier = Modifier.height(10.dp))

        TextButton(onClick = {  }) {
            Text(text = "Don't have an account? Click to register")
        }
    }

}

@Preview(showBackground = true)
@Composable
fun LoginActivityPreview() {
    LoginActivity()
}
