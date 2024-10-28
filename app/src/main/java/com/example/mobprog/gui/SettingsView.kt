package com.example.mobprog.gui

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.mobprog.data.UserController
import com.example.mobprog.data.UserService
import com.example.mobprog.data.handlers.ImageHandler
import com.example.mobprog.gui.components.BottomNavBar
import com.example.mobprog.ui.theme.MobProgTheme


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SettingsView(navController: NavController, onDarkModeToggle: (Boolean) -> Unit, currentSettingsDarkMode: Boolean){

    var isDarkTheme by remember { mutableStateOf(false) }
    var isPreChecked by remember { mutableStateOf(isDarkTheme) }

    var newName by remember { mutableStateOf("") }
    var isUpdating by remember { mutableStateOf(false) }
    var updateStatus by remember { mutableStateOf<String?>(null) }

    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isPasswordUpdating by remember { mutableStateOf(false) }
    var passwordUpdateStatus by remember { mutableStateOf<String?>(null) }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri

        if (uri != null) {
            selectedImageUri?.let {
                ImageHandler().uploadImageToFirebase(uri,
                    onSuccess = {
                        println("Image uploaded and URL saved to Firestore successfully!")
                    },
                    onFailure = { exception ->
                        println("Failed to upload image or save URL to Firestore: ${exception.message}")
                    }
                )
            }
        }
    }

    fun updateUserName(newName: String, callback: (Boolean, String) -> Unit) {
        isUpdating = true
        val userService = UserService()
        userService.updateUserProfile(name = newName, picture = "") { success, exception ->
            if (success) {
                callback(true, "Name updated successfully!")
            } else {
                val errorMessage = exception?.message ?: "An error occurred."
                callback(false, errorMessage)
            }
        }
    }

    LaunchedEffect(isDarkTheme) {
        isPreChecked = currentSettingsDarkMode
    }

    Scaffold(topBar = {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(88.dp)
                .padding(bottom = 10.dp, top = 24.dp)
                .background(MaterialTheme.colorScheme.primary),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {

                IconButton(onClick = {
                    UserService().getCurrentUserData { docFields ->
                        println("$docFields")
                    }
                },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    IconButton(onClick = {
                        navController.navigate("profileScreen") {
                            while (navController.popBackStack() == true) {
                                navController.popBackStack()
                            }
                        } },
                        modifier = Modifier.align(Alignment.CenterEnd)) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back Button",
                        tint = Color.White
                    )
                }
                }
                Text(
                    text = "Settings",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }, content = { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            contentAlignment = Alignment.TopStart
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Dark Mode")
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(
                        checked = isPreChecked,
                        onCheckedChange = { isChecked ->
                            isDarkTheme = isChecked
                            isPreChecked = isChecked
                            onDarkModeToggle(isChecked)
                        }
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isPreChecked) "ON" else "OFF",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("New Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        updateUserName(newName) { success, message ->
                            isUpdating = false
                            updateStatus = message
                            if (success) {
                                newName = ""
                            }
                        }
                    },
                    enabled = newName.isNotBlank() && !isUpdating,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isUpdating) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text("Update Name")
                    }
                }

                updateStatus?.let { statusMessage ->
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = statusMessage,
                        color = if (statusMessage.contains("success", true)) Color.Green else Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // New Password Input
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("New Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (newPassword == confirmPassword) {
                            isPasswordUpdating = true
                            UserService().updatePassword(newPassword) { success, message ->
                                isPasswordUpdating = false
                                passwordUpdateStatus = message
                            }
                        } else {
                            passwordUpdateStatus = "Passwords do not match."
                        }
                    },
                    enabled = newPassword.isNotBlank() && confirmPassword.isNotBlank() && !isPasswordUpdating,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isPasswordUpdating) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text("Update Password")
                    }
                }


                    Button(
                        onClick = { launcher.launch("image/*") }
                    ) {
                        Text("Select image")
                    }

                selectedImageUri?.let { uri ->
                    Image(
                        painter = rememberAsyncImagePainter(uri),
                        contentDescription = null,
                        modifier = Modifier.size(128.dp)
                    )
                } ?: run {
                    Text("No image selected", color = Color.Gray)
                }


                passwordUpdateStatus?.let { statusMessage ->
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = statusMessage,
                        color = if (statusMessage.contains("success", true)) Color.Green else Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }, bottomBar = {
        // inspirert av link under for å lage navbar.
        // https://www.youtube.com/watch?v=O9csfKW3dZ4
        BottomNavBar(navController = navController, userService = UserService())
    })
}




@Preview(showBackground = true)
@Composable
fun SettingsViewPreview() {
    var isDarkMode by remember { mutableStateOf(false) }
    MobProgTheme(darkTheme = isDarkMode) {
        SettingsView(
            navController = rememberNavController(),
            onDarkModeToggle = { isDarkMode = it },
            currentSettingsDarkMode = isDarkMode
        )
    }
}
