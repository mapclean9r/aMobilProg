package com.example.mobprog.data;

import com.example.mobprog.createEvent.EventData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class EventService {
    val db = FirebaseFirestore.getInstance()

    //Fungerer 100% @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

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


    fun parseToEventData(data: Map<String, Any>): EventData? {
        val currentUserID = FirebaseAuth.getInstance().currentUser?.uid.toString()
        return try {
            EventData(
                name = data["name"] as? String ?: "",
                startDate = data["startDate"] as? String ?: "N/A",
                endDate = data["date"] as? String ?: "N/A",
                description = data["description"] as? String ?: "",
                location = data["location"] as? String ?: "N/A",
                creatorId = data["host"] as? String ?: currentUserID,
                comments = data["comments"] as? List<String> ?: emptyList(),
                maxAttendance = data["maxAttendance"] as? Number ?: 0,
                attending = data["attending"] as? List<String> ?: emptyList(),
                image = data["image"] as? String ?: "",
                id = data["id"] as? String ?: ""
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    //In Progress @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

    fun joinEvent(userID: String, eventID: String) {
        print(userID)
        print(eventID)
        val allEventsRef = db.collection("events").document(eventID)
        allEventsRef.update("attending", FieldValue.arrayUnion(userID))
    }
}
