package com.example.mobprog.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

sealed class ValidationResult {
    object Success : ValidationResult()
    data class Error(val message: String) : ValidationResult()
}

object Validation {
    // Event Validations
    fun validateEventName(name: String): ValidationResult {
        return when {
            name.isBlank() -> ValidationResult.Error("Event name cannot be empty")
            name.length < 3 -> ValidationResult.Error("Event name must be at least 3 characters long")
            name.length > 50 -> ValidationResult.Error("Event name cannot exceed 50 characters")
            else -> ValidationResult.Success
        }
    }

    fun validateEventDescription(description: String): ValidationResult {
        return when {
            description.isBlank() -> ValidationResult.Error("Description cannot be empty")
            description.length < 10 -> ValidationResult.Error("Description must be at least 10 characters long")
            description.length > 500 -> ValidationResult.Error("Description cannot exceed 500 characters")
            else -> ValidationResult.Success
        }
    }

    fun validateEventDate(dateStr: String): ValidationResult {
        if (dateStr.isBlank()) {
            return ValidationResult.Error("Date cannot be empty")
        }

        try {
            val sdf = SimpleDateFormat("d/M/yyyy", Locale.getDefault())
            val selectedDate = sdf.parse(dateStr)
            val currentDate = Calendar.getInstance().time

            // Remove time component for date comparison
            val calendar = Calendar.getInstance()
            calendar.time = currentDate
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val today = calendar.time

            return if (selectedDate != null && selectedDate.before(today)) {
                ValidationResult.Error("Event date cannot be in the past")
            } else {
                ValidationResult.Success
            }
        } catch (e: Exception) {
            return ValidationResult.Error("Invalid date format. Use DD/MM/YYYY")
        }
    }

    fun validateEventLocation(location: String): ValidationResult {
        return when {
            location.isBlank() -> ValidationResult.Error("Location cannot be empty")
            location.length < 3 -> ValidationResult.Error("Location must be at least 3 characters long")
            location.length > 100 -> ValidationResult.Error("Location cannot exceed 100 characters")
            else -> ValidationResult.Success
        }
    }

    fun validateEventMaxAttendance(maxAttendance: Int): ValidationResult {
        return when {
            maxAttendance <= 0 -> ValidationResult.Error("Maximum attendance must be greater than 0")
            maxAttendance > 1000 -> ValidationResult.Error("Maximum attendance cannot exceed 1000")
            else -> ValidationResult.Success
        }
    }

    // Guild Validations
    fun validateGuildName(name: String): ValidationResult {
        return when {
            name.isBlank() -> ValidationResult.Error("Guild name cannot be empty")
            name.length < 3 -> ValidationResult.Error("Guild name must be at least 3 characters long")
            name.length > 30 -> ValidationResult.Error("Guild name cannot exceed 30 characters")
            !name.matches(Regex("^[a-zA-Z0-9 ]*$")) -> ValidationResult.Error("Guild name can only contain letters, numbers and spaces")
            else -> ValidationResult.Success
        }
    }

    fun validateGuildDescription(description: String): ValidationResult {
        return when {
            description.isBlank() -> ValidationResult.Error("Guild description cannot be empty")
            description.length < 10 -> ValidationResult.Error("Guild description must be at least 10 characters long")
            description.length > 300 -> ValidationResult.Error("Guild description cannot exceed 300 characters")
            else -> ValidationResult.Success
        }
    }

    // Helper function to validate all event fields at once
    fun validateEventFields(
        name: String,
        description: String,
        location: String,
        date: String,
        maxAttendance: Int
    ): List<ValidationResult> {
        return listOf(
            validateEventName(name),
            validateEventDescription(description),
            validateEventLocation(location),
            validateEventDate(date),
            validateEventMaxAttendance(maxAttendance)
        )
    }

    // Helper function to validate all guild fields at once
    fun validateGuildFields(
        name: String,
        description: String
    ): List<ValidationResult> {
        return listOf(
            validateGuildName(name),
            validateGuildDescription(description)
        )
    }
}
