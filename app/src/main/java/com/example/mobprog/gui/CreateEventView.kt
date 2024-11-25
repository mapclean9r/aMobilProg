package com.example.mobprog.gui

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mobprog.api.GameData
import com.example.mobprog.api.GamingApi
import com.example.mobprog.createEvent.EventData
import com.example.mobprog.data.EventService
import com.example.mobprog.data.UserService
import com.example.mobprog.gui.components.BottomNavBar
import com.example.mobprog.gui.components.GameBox
import com.google.firebase.auth.FirebaseAuth
import java.util.Calendar
import com.example.mobprog.util.Validation
import com.example.mobprog.util.ValidationResult

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CreateEventView(navController: NavController, eventService: EventService) {
    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    var isLoading by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val configuration = LocalConfiguration.current
    val isLandscape =
        configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    var name by remember { mutableStateOf(savedStateHandle?.get<String>("name") ?: "") }
    var location by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf(savedStateHandle?.get<String>("startDate") ?: "") }
    var description by remember {
        mutableStateOf(
            savedStateHandle?.get<String>("description") ?: ""
        )
    }
    var gameCoverImage by remember {
        mutableStateOf(
            savedStateHandle?.get<String>("gameCoverImage") ?: ""
        )
    }
    var maxAttendance by remember {
        mutableIntStateOf(
            savedStateHandle?.get<Int>("maxAttendance") ?: 0
        )
    }
    var maxAttendanceString by remember {
        mutableStateOf(
            savedStateHandle?.get<String>("maxAttendanceString") ?: ""
        )
    }

    var showSearch by remember { mutableStateOf(false) }
    var searchGameText by remember { mutableStateOf("") }
    var games by remember { mutableStateOf(emptyList<GameData>()) }
    var filteredGames by remember { mutableStateOf(emptyList<GameData>()) }
    var selectedGame by remember { mutableStateOf(savedStateHandle?.get<GameData>("selectedGame")) }

    var nameError by remember { mutableStateOf<String?>(null) }
    var descriptionError by remember { mutableStateOf<String?>(null) }
    var locationError by remember { mutableStateOf<String?>(null) }
    var dateError by remember { mutableStateOf<String?>(null) }
    var maxAttendanceError by remember { mutableStateOf<String?>(null) }

    val calendar = Calendar.getInstance()
    val context = LocalContext.current
    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, pickedYear: Int, pickedMonth: Int, pickedDay: Int ->
            val newDate = "$pickedDay/${pickedMonth + 1}/$pickedYear"
            when (val result = Validation.validateEventDate(newDate)) {
                is ValidationResult.Success -> {
                    startDate = newDate
                    dateError = null
                }

                is ValidationResult.Error -> {
                    dateError = result.message
                }
            }
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    var latitude by remember { mutableStateOf<Double?>(null) }
    var longitude by remember { mutableStateOf<Double?>(null) }

    val selectedLocation =
        savedStateHandle?.getLiveData<Triple<Double, Double, String>>("selected_location")
    selectedLocation?.observe(LocalLifecycleOwner.current) { locationTriple ->
        val (selectedLatitude, selectedLongitude, selectedLocationName) = locationTriple
        latitude = selectedLatitude
        longitude = selectedLongitude
        location = selectedLocationName
    }

    GamingApi().fetchAllGames { gameList ->
        gameList?.let { games = it }
    }

    LaunchedEffect(isLoading) {
        if (isLoading) {
            kotlinx.coroutines.delay(2000)
            isLoading = false
            navController.navigate("homeScreen") {
                while (navController.popBackStack()) {
                    navController.popBackStack()
                }
            }
        }
    }

    LaunchedEffect(searchGameText) {
        filteredGames = games.filter { game ->
            game.title.contains(searchGameText, ignoreCase = true)
        }
    }

    fun validateFields(): Boolean {
        // Reset all errors
        nameError = null
        descriptionError = null
        locationError = null
        dateError = null
        maxAttendanceError = null

        val validations = Validation.validateEventFields(
            name = name,
            description = description,
            location = location,
            date = startDate,
            maxAttendance = maxAttendance
        )

        var isValid = true
        validations.forEach { result ->
            when (result) {
                is ValidationResult.Error -> {
                    isValid = false
                    when {
                        result.message.contains("name", ignoreCase = true) -> nameError =
                            result.message

                        result.message.contains(
                            "description",
                            ignoreCase = true
                        ) -> descriptionError = result.message

                        result.message.contains("location", ignoreCase = true) -> locationError =
                            result.message

                        result.message.contains("date", ignoreCase = true) -> dateError =
                            result.message

                        result.message.contains(
                            "attendance",
                            ignoreCase = true
                        ) -> maxAttendanceError = result.message
                    }
                }

                is ValidationResult.Success -> { /* Field is valid */
                }
            }
        }
        return isValid
    }

    if (isLoading) {
        LoadingScreen()
    } else {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "Create Event",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White
                        )
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            },
            bottomBar = {
                BottomNavBar(navController = navController, userService = UserService())
            }
        ) { paddingValues ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                color = MaterialTheme.colorScheme.background
            ) {
                if (isLandscape) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            OutlinedTextField(
                                value = name,
                                onValueChange = {
                                    name = it
                                    nameError = null // Clear error when user types
                                },
                                label = { Text("Event Title") },
                                isError = nameError != null,
                                supportingText = nameError?.let {
                                    { Text(it, color = MaterialTheme.colorScheme.error) }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                    errorBorderColor = MaterialTheme.colorScheme.error
                                )
                            )
                            OutlinedTextField(
                                value = description,
                                onValueChange = {
                                    description = it
                                    descriptionError = null // Clear error when user types
                                },
                                label = { Text("Description") },
                                isError = descriptionError != null,
                                supportingText = descriptionError?.let {
                                    { Text(it, color = MaterialTheme.colorScheme.error) }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 3,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                    errorBorderColor = MaterialTheme.colorScheme.error
                                )
                            )
                            OutlinedTextField(
                                value = location,
                                onValueChange = {
                                    location = it
                                    locationError = null // Clear error when user types
                                },
                                label = { Text("Location") },
                                isError = locationError != null,
                                supportingText = locationError?.let {
                                    { Text(it, color = MaterialTheme.colorScheme.error) }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                    errorBorderColor = MaterialTheme.colorScheme.error
                                )
                            )

                            // Location Button remains unchanged
                            ElevatedButton(
                                onClick = {
                                    savedStateHandle?.apply {
                                        set("name", name)
                                        set("startDate", startDate)
                                        set("description", description)
                                        set("gameCoverImage", gameCoverImage)
                                        set("maxAttendance", maxAttendance)
                                        set("maxAttendanceString", maxAttendanceString)
                                        set("selectedGame", selectedGame)
                                    }
                                    navController.navigate("locationPickerScreen")
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = "Select location",
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Text("Select Location on Map")
                            }
                        }
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                ),
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = 2.dp
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Text(
                                        text = "Event Details",
                                        style = MaterialTheme.typography.titleLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        ElevatedButton(
                                            onClick = { datePickerDialog.show() },
                                            modifier = Modifier.wrapContentWidth()
                                        ) {
                                            Icon(
                                                Icons.Default.DateRange,
                                                contentDescription = "Select date",
                                                modifier = Modifier.padding(end = 8.dp)
                                            )
                                            Text("Select Date")
                                        }
                                        if (startDate.isNotEmpty()) {
                                            Surface(
                                                modifier = Modifier.wrapContentWidth(),
                                                shape = MaterialTheme.shapes.medium,
                                                color = MaterialTheme.colorScheme.surface,
                                                tonalElevation = 1.dp
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(
                                                        horizontal = 12.dp,
                                                        vertical = 8.dp
                                                    ),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    Icon(
                                                        Icons.Default.DateRange,
                                                        contentDescription = null,
                                                        tint = MaterialTheme.colorScheme.primary,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                    Text(
                                                        text = startDate,
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                }
                                            }
                                        }
                                        if (dateError != null) {
                                            Text(
                                                text = dateError!!,
                                                color = MaterialTheme.colorScheme.error,
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                    }
                                    OutlinedTextField(
                                        value = maxAttendanceString,
                                        onValueChange = {
                                            maxAttendanceString = it
                                            maxAttendance = it.toIntOrNull() ?: 0
                                            maxAttendanceError = null // Clear error when user types
                                        },
                                        label = { Text("Max Attendees") },
                                        isError = maxAttendanceError != null,
                                        supportingText = maxAttendanceError?.let {
                                            { Text(it, color = MaterialTheme.colorScheme.error) }
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                            errorBorderColor = MaterialTheme.colorScheme.error
                                        )
                                    )
                                }
                            }
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                ),
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = 2.dp
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .height(IntrinsicSize.Min),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text(
                                        text = "Game Selection",
                                        style = MaterialTheme.typography.titleLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(56.dp)
                                    ) {
                                        if (selectedGame != null) {
                                            Surface(
                                                modifier = Modifier.fillMaxWidth(),
                                                color = MaterialTheme.colorScheme.surface,
                                                tonalElevation = 1.dp,
                                                shape = MaterialTheme.shapes.medium
                                            ) {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(
                                                            horizontal = 12.dp,
                                                            vertical = 8.dp
                                                        ),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = selectedGame!!.title,
                                                        style = MaterialTheme.typography.titleMedium,
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                    IconButton(
                                                        onClick = { selectedGame = null }
                                                    ) {
                                                        Icon(
                                                            Icons.Default.Close,
                                                            contentDescription = "Clear selection",
                                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                    }
                                                }
                                            }
                                        } else {
                                            Text(
                                                text = "No game selected",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.padding(vertical = 8.dp)
                                            )
                                        }
                                    }

                                    ElevatedButton(
                                        onClick = { showSearch = true },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(
                                            Icons.Default.Search,
                                            contentDescription = "Select game",
                                            modifier = Modifier.padding(end = 8.dp)
                                        )
                                        Text(if (selectedGame == null) "Select Game" else "Change Game")
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.weight(1f))
                            Button(
                                onClick = {
                                    if (validateFields()) {
                                        isLoading = true
                                        onSubmit(
                                            name,
                                            maxAttendance,
                                            location,
                                            startDate,
                                            description,
                                            gameCoverImage,
                                            eventService,
                                            FirebaseAuth.getInstance().currentUser?.uid.toString(),
                                            selectedLocation?.value?.let { "${it.first},${it.second}" }
                                                ?: ""
                                        )
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text(
                                    "Create Event",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Title Section
                        OutlinedTextField(
                            value = name,
                            onValueChange = {
                                name = it
                                nameError = null // Clear error when user types
                            },
                            label = { Text("Event Title") },
                            isError = nameError != null,
                            supportingText = nameError?.let {
                                { Text(it, color = MaterialTheme.colorScheme.error) }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                errorBorderColor = MaterialTheme.colorScheme.error
                            )
                        )
                        OutlinedTextField(
                            value = description,
                            onValueChange = {
                                description = it
                                descriptionError = null // Clear error when user types
                            },
                            label = { Text("Description") },
                            isError = descriptionError != null,
                            supportingText = descriptionError?.let {
                                { Text(it, color = MaterialTheme.colorScheme.error) }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                errorBorderColor = MaterialTheme.colorScheme.error
                            )
                        )
                        OutlinedTextField(
                            value = location,
                            onValueChange = {
                                location = it
                                locationError = null // Clear error when user types
                            },
                            label = { Text("Location") },
                            isError = locationError != null,
                            supportingText = locationError?.let {
                                { Text(it, color = MaterialTheme.colorScheme.error) }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                errorBorderColor = MaterialTheme.colorScheme.error
                            )
                        )

                        // Location Button remains unchanged
                        ElevatedButton(
                            onClick = {
                                savedStateHandle?.apply {
                                    set("name", name)
                                    set("startDate", startDate)
                                    set("description", description)
                                    set("gameCoverImage", gameCoverImage)
                                    set("maxAttendance", maxAttendance)
                                    set("maxAttendanceString", maxAttendanceString)
                                    set("selectedGame", selectedGame)
                                }
                                navController.navigate("locationPickerScreen")
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Select location",
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text("Select Location on Map")
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                horizontalAlignment = Alignment.Start,
                                modifier = Modifier.weight(1f)
                            ) {
                                ElevatedButton(
                                    onClick = { datePickerDialog.show() },
                                    modifier = Modifier.wrapContentWidth()
                                ) {
                                    Icon(
                                        Icons.Default.DateRange,
                                        contentDescription = "Select date",
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                    Text("Select Date")
                                }
                                if (startDate.isNotEmpty()) {
                                    Surface(
                                        modifier = Modifier
                                            .padding(top = 8.dp)
                                            .wrapContentWidth(),
                                        shape = MaterialTheme.shapes.medium,
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        tonalElevation = 1.dp
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(
                                                horizontal = 12.dp,
                                                vertical = 8.dp
                                            ),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.DateRange,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Text(
                                                text = startDate,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                                if (dateError != null) {
                                    Text(
                                        text = dateError!!,
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }
                            OutlinedTextField(
                                value = maxAttendanceString,
                                onValueChange = {
                                    maxAttendanceString = it
                                    maxAttendance = it.toIntOrNull() ?: 0
                                    maxAttendanceError = null // Clear error when user types
                                },
                                label = { Text("Max Attendees") },
                                isError = maxAttendanceError != null,
                                supportingText = maxAttendanceError?.let {
                                    { Text(it, color = MaterialTheme.colorScheme.error) }
                                },
                                modifier = Modifier.width(120.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                    errorBorderColor = MaterialTheme.colorScheme.error
                                )
                            )
                        }
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 2.dp
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .height(IntrinsicSize.Min),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "Game Selection",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp)
                                ) {
                                    if (selectedGame != null) {
                                        Surface(
                                            modifier = Modifier.fillMaxWidth(),
                                            color = MaterialTheme.colorScheme.surface,
                                            tonalElevation = 1.dp,
                                            shape = MaterialTheme.shapes.medium
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = selectedGame!!.title,
                                                    style = MaterialTheme.typography.titleMedium,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                IconButton(
                                                    onClick = { selectedGame = null }
                                                ) {
                                                    Icon(
                                                        Icons.Default.Close,
                                                        contentDescription = "Clear selection",
                                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }
                                        }
                                    } else {
                                        Text(
                                            text = "No game selected",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(vertical = 8.dp)
                                        )
                                    }
                                }

                                ElevatedButton(
                                    onClick = { showSearch = true },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        Icons.Default.Search,
                                        contentDescription = "Select game",
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                    Text(if (selectedGame == null) "Select Game" else "Change Game")
                                }
                            }
                        }

                        // Create Button remains unchanged
                        Button(
                            onClick = {
                                if (validateFields()) {
                                    isLoading = true
                                    onSubmit(
                                        name,
                                        maxAttendance,
                                        location,
                                        startDate,
                                        description,
                                        gameCoverImage,
                                        eventService,
                                        FirebaseAuth.getInstance().currentUser?.uid.toString(),
                                        selectedLocation?.value?.let { "${it.first},${it.second}" }
                                            ?: ""
                                    )
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text(
                                "Create Event",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }
        }
        if (showSearch) {
            if (!isLandscape) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = searchGameText,
                            onValueChange = { searchGameText = it },
                            placeholder = { Text("Search games...") },
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp, top = 10.dp),
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = "Search")
                            },
                            singleLine = true
                        )
                        IconButton(onClick = { showSearch = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close search")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(if (filteredGames.isEmpty()) games else filteredGames) { game ->
                            GameBox(
                                gameData = game,
                                onClick = {
                                    selectedGame = game
                                    gameCoverImage = game.thumbnail
                                    showSearch = false
                                }
                            )
                        }
                    }
                }
            }
                } else {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = searchGameText,
                                onValueChange = { searchGameText = it },
                                placeholder = { Text("Search games...") },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 8.dp, top = 10.dp),
                                leadingIcon = {
                                    Icon(Icons.Default.Search, contentDescription = "Search")
                                },
                                singleLine = true
                            )
                            IconButton(onClick = { showSearch = false }, modifier = Modifier.padding(end = 40.dp)) {
                                Icon(Icons.Default.Close, contentDescription = "Close search")
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(if (filteredGames.isEmpty()) games else filteredGames) { game ->
                                GameBox(
                                    gameData = game,
                                    onClick = {
                                        selectedGame = game
                                        gameCoverImage = game.thumbnail
                                        showSearch = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun LoadingScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(64.dp)
                    .padding(bottom = 16.dp),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Creating your event...",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

fun onSubmit(
    name: String,
    maxAttendance: Int,
    location: String,
    startDate: String,
    description: String,
    gameCoverImage: String,
    eventService: EventService,
    creatorId: String,
    locationCoordinates: String
) {
    eventService.createEvent(
        EventData(
            name = name,
            image = gameCoverImage,
            maxAttendance = maxAttendance,
            location = location,
            description = description,
            startDate = startDate,
            creatorId = creatorId,
            coordinates = locationCoordinates
        )
    )
}

@Preview(showBackground = true)
@Composable
fun CreateEventViewPreview() {
    CreateEventView(navController = rememberNavController(), eventService = EventService())
}