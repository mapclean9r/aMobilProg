package com.example.mobprog.gui

import android.annotation.SuppressLint
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.mobprog.data.UserService
import com.example.mobprog.data.picture.ImageService
import com.example.mobprog.gui.components.BottomNavBar
import com.example.mobprog.gui.components.GetSelfProfileImageCircle
import com.example.mobprog.notifications.sendNotification
import com.example.mobprog.ui.theme.MobProgTheme
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


private fun logout(navController: NavController) {
    val auth = Firebase.auth
    auth.signOut()
    navController.navigate("loginScreen") {
        popUpTo("homeScreen") {
            inclusive = true
        }
        navController.popBackStack()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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
    val context = LocalContext.current

    
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri

        if (uri != null) {
            selectedImageUri?.let {
                ImageService().uploadProfileImageToFirebase(uri,
                    onSuccess = {
                        Toast.makeText(context, "Profile Picture Saved", Toast.LENGTH_SHORT).show()
                        println("Image uploaded and URL saved to Firestore successfully!")
                    },
                    onFailure = { exception ->
                        Toast.makeText(context, "Error: Unable to store image", Toast.LENGTH_SHORT).show()
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

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            UserService().getCurrentUserData { docFields ->
                                println("$docFields")
                            }
                            navController.navigate("profileScreen") {
                                while (navController.popBackStack()) {
                                    navController.popBackStack()
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back Button",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
            )
        }, content = { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            contentAlignment = Alignment.TopStart
        ) {
            LazyColumn (
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    Box(modifier = Modifier.size(145.dp)) {
                        selectedImageUri?.let { uri ->
                            Image(
                                painter = rememberAsyncImagePainter(uri),
                                contentDescription = "User Profile Image",
                                modifier = Modifier
                                    .size(140.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } ?: run {
                            GetSelfProfileImageCircle(140)
                        }
                    }
                    Spacer(modifier = Modifier.padding(5.dp))

                    Button(
                        onClick = { launcher.launch("image/*") }
                    ) {
                        Text("Update Profile Picture")
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
                            color = if (statusMessage.contains(
                                    "success",
                                    true
                                )
                            ) Color.Green else Color.Red,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

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
                                sendNotification(context = context)
                            }
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isPreChecked) "ON" else "OFF",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }



                    passwordUpdateStatus?.let { statusMessage ->
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = statusMessage,
                            color = if (statusMessage.contains(
                                    "success",
                                    true
                                )
                            ) Color.Green else Color.Red,
                            fontWeight = FontWeight.Bold
                        )
                    }


                    Spacer(modifier = Modifier.height(8.dp))
                    Button(

                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red
                        ),
                        onClick = {
                            logout(navController)
                        },
                    ) {
                        Text(text = "Logout")
                    }
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
