package com.example.mobprog.gui.event

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import com.example.mobprog.createEvent.EventData

val eventDataSaver = androidx.compose.runtime.saveable.Saver<EventData, Map<String, Any>>(
    save = { eventData ->
        mapOf(
            "name" to eventData.name,
            "startDate" to eventData.startDate,
            "endDate" to eventData.endDate,
            "creatorId" to eventData.creatorId,
            "description" to eventData.description,
            "location" to eventData.location,
            "comments" to eventData.comments,
            "maxAttendance" to eventData.maxAttendance,
            "attending" to eventData.attending,
            "image" to eventData.image,
            "coordinates" to eventData.coordinates,
            "id" to eventData.id
        )
    },
    restore = { savedData ->
        EventData(
            name = savedData["name"] as String,
            startDate = savedData["startDate"] as String,
            endDate = savedData["endDate"] as String,
            creatorId = savedData["creatorId"] as String,
            description = savedData["description"] as String,
            location = savedData["location"] as String,
            comments = savedData["comments"] as List<String>,
            maxAttendance = savedData["maxAttendance"] as Int,
            attending = savedData["attending"] as List<String>,
            image = savedData["image"] as String,
            coordinates = savedData["coordinates"] as String,
            id = savedData["id"] as String
        )
    }
)