package com.example.mobprog.data;

import com.example.mobprog.createEvent.EventBase
import com.example.mobprog.createEvent.EventData
import com.google.firebase.firestore.FirebaseFirestore

class EventService {
    val db = FirebaseFirestore.getInstance()

    //Fungerer 100% @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

    fun createEvent(event: EventData){
        db.collection("events").add(event)
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
        return try {
            EventData(
                name = data["name"] as? String ?: "",
                startDate = data["startDate"] as? String ?: "",
                creatorId = data["creatorId"] as? String ?: "",
                description = data["description"] as? String ?: "",
                price = data["price"] as? String ?: "Free",
                location = data["location"] as? String ?: "N/A",
                picture = data["picture"] as? String ?: "",
                host = data["host"] as? String ?: "N/A",
                date = data["date"] as? String ?: "N/A",
                comments = data["comments"] as? List<String> ?: emptyList(),
                attendance = data["attendance"] as? Int ?: 0,
                members = data["members"] as? List<String> ?: emptyList()
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    //In Progress @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

}
