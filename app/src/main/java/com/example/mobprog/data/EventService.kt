package com.example.mobprog.data

import android.content.Context
import com.example.mobprog.createEvent.EventData
import com.example.mobprog.notifications.sendEventNotification
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EventService {
    val db = FirebaseFirestore.getInstance()
    private var context: Context? = null

    fun setContext(context: Context) {
        this.context = context
    }

    fun checkTodayEvents() {
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        val tomorrow = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            add(Calendar.DAY_OF_MONTH, 1)
        }.time

        val currentUser = FirebaseAuth.getInstance().currentUser ?: return

        db.collection("events")
            .whereEqualTo("startDate", SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(today))
            .whereArrayContains("attending", currentUser.uid)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val eventData = document.toObject(EventData::class.java)
                    context?.let { ctx ->
                        sendEventNotification(ctx, eventData.name, eventData.startDate)
                    }
                }
            }
    }

    fun createEvent(event: EventData){
        val newDocumentRef = db.collection("events").document()
        newDocumentRef.set(event)
            .addOnSuccessListener {
                val documentId = newDocumentRef.id
                newDocumentRef.update("id", documentId)
            }
    }

    fun printAllEvents() {
        val allEventsRef = db.collection("events")

        allEventsRef
            .get()
            .addOnSuccessListener { events ->
                for (event in events) {
                    println(
                        "Userid: ${event.id},\n" +
                                "Name: ${event.get("name")},\n" +
                                "StartDate: ${event.get("startDate")},\n" +
                                "Location: ${event.get("location")},\n" +
                                "Attendance: ${event.get("attendance")},\n" +
                                "Price: ${event.get("price")},\n" +
                                "Picture: ${event.get("picture")},\n" +
                                "Participants: ${event.get("participants")},\n" +
                                "Comments: ${event.get("comments")},\n\n"
                    )
                }
            }
    }

    fun getAllEvents(callback: (List<Map<String, Any>>?) -> Unit) {
        val allEventsRef = db.collection("events")

        allEventsRef
            .get()
            .addOnCompleteListener { events ->
                if (events.isSuccessful) {
                    val doc = events.result
                    if (doc != null && !doc.isEmpty) {
                        val allDocuments = mutableListOf<Map<String, Any>>()

                        for (field in doc) {
                            field.data.let {
                                allDocuments.add(it)
                            }
                        }
                        callback(allDocuments)
                    }
                }
            }
    }

    fun getEventById(id: String, callback: (Map<String, Any>?) -> Unit) {
        val eventRef = db.collection("events").document(id)
        eventRef.get()
            .addOnCompleteListener { doc ->
                if (doc.isSuccessful) {
                    val document = doc.result
                    if (document != null && document.exists()) {
                        val hashmap = HashMap<String, Any>()
                        document.data?.forEach { (key, value) ->
                            hashmap[key] = value
                        }
                        callback(hashmap)
                    }
                }
            }
    }

    fun getEventsByCreatorId(creatorID: String, callback: (List<EventData>?) -> Unit) {
        val eventsRef = db.collection("events")
        eventsRef.whereEqualTo("creatorId", creatorID)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val eventsList = task.result?.mapNotNull { document ->
                        document.toObject(EventData::class.java).copy(id = document.id)
                    }
                    callback(eventsList)
                } else {
                    callback(null)
                }
            }
    }

    fun deleteEvent(documentId: String) {
        val docRef = db.collection("events").document(documentId)
        docRef.delete()
    }

    fun parseToEventData(data: Map<String, Any>): EventData? {
        val currentUserID = FirebaseAuth.getInstance().currentUser?.uid.toString()
        return try {
            EventData(
                name = data["name"] as? String ?: "",
                startDate = data["startDate"] as? String ?: "N/A",
                endDate = data["date"] as? String ?: "N/A",
                description = data["description"] as? String ?: "",
                location = data["location"] as? String ?: "N/A",
                creatorId = data["creatorId"] as? String ?: currentUserID,
                comments = data["comments"] as? List<String> ?: emptyList(),
                maxAttendance = (data["maxAttendance"] as? Number)?.toInt() ?: 0,
                attending = data["attending"] as? List<String> ?: emptyList(),
                image = data["image"] as? String ?: "",
                coordinates = data["coordinates"] as? String ?: "",
                id = data["id"] as? String ?: ""
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun joinEvent(userID: String, eventID: String) {
        print(userID)
        print(eventID)
        val eventToBeUpdated = db.collection("events").document(eventID)
        eventToBeUpdated.update("attending", FieldValue.arrayUnion(userID))
    }

    fun leaveEvent(userID: String, eventID: String) {
        print(userID)
        print(eventID)
        val eventToBeUpdated = db.collection("events").document(eventID)
        eventToBeUpdated.update("attending", FieldValue.arrayRemove(userID))
    }
}
