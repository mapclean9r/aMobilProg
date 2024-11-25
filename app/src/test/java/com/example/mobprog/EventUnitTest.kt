package com.example.mobprog

import com.example.mobprog.createEvent.EventData
import com.example.mobprog.data.EventService
import com.example.mobprog.gui.onSubmit
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.eq

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class EventUnitTest {


    @Test
    fun `test om onSubmit bruker createEvent med riktig data`() {
        val mockEventService: EventService = mock()
        val name = "Test Event"
        val maxAttendance = 100
        val location = "Test Location"
        val startDate = "11-11-2027"
        val description = "Test event."
        val gameCoverImage = "test.jpg"
        val creatorId = "cIdTest"
        val locationCoordinates = "33.7349,-232.4194"

        val curEventData = EventData(
            name = name,
            image = gameCoverImage,
            maxAttendance = maxAttendance,
            location = location,
            description = description,
            startDate = startDate,
            creatorId = creatorId,
            coordinates = locationCoordinates
        )

        onSubmit(
            name = name,
            maxAttendance = maxAttendance,
            location = location,
            startDate = startDate,
            description = description,
            gameCoverImage = gameCoverImage,
            eventService = mockEventService,
            creatorId = creatorId,
            locationCoordinates = locationCoordinates
        )

        verify(mockEventService).createEvent(eq(curEventData))
    }

}