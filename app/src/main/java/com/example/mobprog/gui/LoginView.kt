package com.example.mobprog.gui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mobprog.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginView(navController: NavController, isDarkMode: Boolean) {

    val background = if (isDarkMode) {
        painterResource(id = R.drawable.letsgoo4)
    } else {
        painterResource(id = R.drawable.light)
    }

    var emailTextController by remember { mutableStateOf("") }
    var passwordTextController by remember { mutableStateOf("") }

    var showPasswordResetField by remember { mutableStateOf(false) }
    var resetEmailText by remember { mutableStateOf("") }
    var resetMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var loginError by remember { mutableStateOf<String?>(null) }


if (isDarkMode){
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = background,
            contentDescription = "Background Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Welcome to Arena",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Green,
                modifier = Modifier.padding(top = 20.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Enter email and password",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Green
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = emailTextController,
                onValueChange = { emailTextController = it },
                label = { Text(text = "Email", color = Color.Magenta) },
                textStyle = TextStyle(color = Color.Cyan),
                isError = loginError != null
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = passwordTextController,
                onValueChange = { passwordTextController = it },
                label = { Text(text = "Password", color = Color.Magenta) },
                textStyle = TextStyle(color = Color.Cyan),
                visualTransformation = PasswordVisualTransformation(),
                isError = loginError != null
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (loginError != null) {
                Text(text = loginError!!, color = Color.Red, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 10.dp))
                Spacer(modifier = Modifier.height(10.dp))
            }

            Button(
                onClick = {
                    if (emailTextController.isNotEmpty() && passwordTextController.isNotEmpty()) {
                        isLoading = true
                        loginError = null
                        login(navController, emailTextController, passwordTextController) { success, message ->
                            isLoading = false
                            if (!success) {
                                loginError = message
                            }
                        }
                    } else {
                        loginError = "Please fill in both email and password."
                    }
                },
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6200EA),
                    contentColor = Color.White
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp))
                } else {
                    Text(text = "Login")
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            TextButton(onClick = {
                navController.navigate("registerScreen") {
                    navController.popBackStack()
                }
            }) {
                Text(
                    text = "Don't have an account? Click to register",
                    color = Color.Green
                )
            }

            TextButton(onClick = {
                showPasswordResetField = !showPasswordResetField
            }) {
                Text(
                    text = "Forgot your password? Click to reset",
                    color = Color.Green
                )
            }

            if (showPasswordResetField) {
                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = resetEmailText,
                    onValueChange = { resetEmailText = it },
                    label = { Text(text = "Enter your email", color = Color.Magenta) },
                    textStyle = TextStyle(color = Color.Cyan),
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        if (resetEmailText.isNotEmpty()) {
                            isLoading = true
                            sendPasswordResetEmail(resetEmailText) { success, message ->
                                isLoading = false
                                resetMessage = "Password reset e-mail sent to $resetEmailText"
                            }
                        } else {
                            resetMessage = "Please enter your email."
                        }
                    },
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6200EA),
                        contentColor = Color.White
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp))
                    } else {
                        Text(text = "Send Reset Email")
                    }
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
    } else {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = background,
            contentDescription = "Background Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Welcome to Arena",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(top = 20.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Enter email and password",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = emailTextController,
                onValueChange = { emailTextController = it },
                label = { Text(text = "Email", color = Color.Gray) },
                textStyle = TextStyle(color = Color.Black),
                isError = loginError != null
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = passwordTextController,
                onValueChange = { passwordTextController = it },
                label = { Text(text = "Password", color = Color.Gray) },
                textStyle = TextStyle(color = Color.Black),
                visualTransformation = PasswordVisualTransformation(),
                isError = loginError != null
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (loginError != null) {
                Text(
                    text = loginError!!,
                    color = Color.Red,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 10.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            Button(
                onClick = {
                    if (emailTextController.isNotEmpty() && passwordTextController.isNotEmpty()) {
                        isLoading = true
                        loginError = null
                        login(
                            navController,
                            emailTextController,
                            passwordTextController
                        ) { success, message ->
                            isLoading = false
                            if (!success) {
                                loginError = message
                            }
                        }
                    } else {
                        loginError = "Please fill in both email and password."
                    }
                },
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6200EA),
                    contentColor = Color.White
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp))
                } else {
                    Text(text = "Login")
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            TextButton(onClick = {
                navController.navigate("registerScreen") {
                    navController.popBackStack()
                }
            }) {
                Text(
                    text = "Don't have an account? Click to register",
                    color = Color.Blue
                )
            }

            TextButton(onClick = {
                showPasswordResetField = !showPasswordResetField
            }) {
                Text(
                    text = "Forgot your password? Click to reset",
                    color = Color.Blue
                )
            }

            if (showPasswordResetField) {
                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = resetEmailText,
                    onValueChange = { resetEmailText = it },
                    label = { Text(text = "Enter your email", color = Color.Gray) },
                    textStyle = TextStyle(color = Color.Black),
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        if (resetEmailText.isNotEmpty()) {
                            isLoading = true
                            sendPasswordResetEmail(resetEmailText) { success, message ->
                                isLoading = false
                                resetMessage = "Password reset e-mail sent to $resetEmailText"
                            }
                        } else {
                            resetMessage = "Please enter your email."
                        }
                    },
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6200EA),
                        contentColor = Color.White
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    } else {
                        Text(text = "Send Reset Email")
                    }
                }

                if (!resetMessage.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = resetMessage ?: "",
                        color = if (resetMessage!!.contains(
                                "success",
                                true
                            )
                        ) Color.Green else Color.Red
                    )
                }
            }
        }
    }
    }
}

private fun login(navController: NavController, email: String, password: String, onResult: (Boolean, String?) -> Unit) {
    val auth = Firebase.auth
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                navController.navigate("homeScreen") {
                    popUpTo("loginScreen") { inclusive = true }
                }
                onResult(true, null)
            } else {
                onResult(false, task.exception?.message ?: "Login failed.")
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

/*
@Preview(showBackground = true)
    @Composable
    fun LoginViewPreview() {
        LoginView(navController = rememberNavController())
    }
*/
