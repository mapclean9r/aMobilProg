package com.example.mobprog.gui

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mobprog.data.UserService
import com.example.mobprog.user.UserData
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth


@Composable
fun RegisterView(navController: NavController) {
    val userService = UserService()
    val context = LocalContext.current

    var emailTextController by remember { mutableStateOf("") }
    var usernameTextController by remember { mutableStateOf("") }
    var passwordTextController by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp), // Optional padding for better UI
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Register User", fontSize = 28.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(28.dp))

        Text(text = "Enter email, username, and password", fontSize = 16.sp)

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = emailTextController,
            onValueChange = { emailTextController = it },
            label = { Text(text = "Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = usernameTextController,
            onValueChange = { usernameTextController = it },
            label = { Text(text = "Username") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = passwordTextController,
            onValueChange = { passwordTextController = it },
            label = { Text(text = "Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (emailTextController.isBlank() || usernameTextController.isBlank() || passwordTextController.isBlank()) {
                    Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                } else {
                    userService.createUser(
                        emailTextController,
                        usernameTextController,
                        passwordTextController
                    ) { success, exception ->
                        if (success) {
                            Toast.makeText(context, "Registration successful", Toast.LENGTH_SHORT).show()
                            emailTextController = ""
                            usernameTextController = ""
                            passwordTextController = ""
                            navController.navigate("loginScreen") {
                                popUpTo("registerScreen") { inclusive = true }
                            }
                        } else {
                            Toast.makeText(
                                context,
                                "Registration failed: ${exception?.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Register")
        }

        Spacer(modifier = Modifier.height(10.dp))

        TextButton(onClick = {
            navController.navigate("loginScreen") {
                popUpTo("registerScreen") { inclusive = true }
            }
        }) {
            Text(text = "Go to login page")
        }
    }
}




@Preview(showBackground = true)
@Composable
fun RegisterViewPreview() {
    RegisterView(navController = rememberNavController())
}
