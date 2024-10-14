package com.example.mobprog.gui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

private fun login(navController: NavController, email: String, password: String) {

    val auth = Firebase.auth
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                navController.navigate("homeScreen") {
                    popUpTo("loginScreen") {
                        inclusive = true
                    }
                }
            }
        }
}

private fun sendPasswordResetEmail(email: String, onResult: (Boolean, String?) -> Unit) {
    val auth = Firebase.auth
    auth.sendPasswordResetEmail(email)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onResult(true, "Password reset email sent.")
            } else {
                onResult(false, task.exception?.message ?: "Failed to send password reset email.")
            }
        }
}

    @Composable
    fun LoginView(navController: NavController) {

        var emailTextController by remember { mutableStateOf("") }
        var passwordTextController by remember { mutableStateOf("") }

        var showPasswordResetField by remember { mutableStateOf(false) }
        var resetEmailText by remember { mutableStateOf("") }
        var resetMessage by remember { mutableStateOf<String?>(null) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Welcome to Arena",
                fontSize = 28.sp, fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary)

            Spacer(modifier = Modifier.height(28.dp))

            Text(text = "Enter email and password", fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary)

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(value = emailTextController, onValueChange = {
                emailTextController = it
            }, label = {
                Text(text = "Email")
            })

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(value = passwordTextController, onValueChange = {
                passwordTextController = it
            }, label = {
                Text(text = "Password")
            }, visualTransformation = PasswordVisualTransformation())

            Spacer(modifier = Modifier.height(20.dp))

            Button(onClick = {
                if(emailTextController != "" && passwordTextController != "") {
                    login(navController, emailTextController, passwordTextController)
                }
            }) {
                Text(text = "Login")
            }

            Spacer(modifier = Modifier.height(10.dp))

            TextButton(onClick = {
                navController.navigate("registerScreen") {
                    navController.popBackStack()
                }
            }) {
                Text(text = "Don't have an account? Click to register",
                    color = MaterialTheme.colorScheme.primary)
            }
            TextButton(onClick = {
                showPasswordResetField = !showPasswordResetField
            }) {
                Text(
                    text = "Forgot your password? Click to reset",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally) {
            if (showPasswordResetField) {
                Spacer(modifier = Modifier.height(670.dp))

                OutlinedTextField(
                    value = resetEmailText,
                    onValueChange = { resetEmailText = it },
                    label = { Text(text = "Enter your email") }
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(onClick = {
                    if (resetEmailText.isNotEmpty()) {
                        sendPasswordResetEmail(resetEmailText) { success, message ->
                            resetMessage = message
                        }
                    }
                }) {
                    Text(text = "Send Reset Email")
                }

                if (!resetMessage.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = resetMessage ?: "",
                        color = if (resetMessage!!.contains("success", true)) Color.Green else Color.Red
                    )
                }
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun LoginViewPreview() {
        LoginView(navController = rememberNavController())
    }

