package com.example.mobprog
import android.net.Uri
import androidx.navigation.NavController
import com.example.mobprog.data.GuildService
import com.example.mobprog.data.UserService
import com.example.mobprog.data.handlers.ImageHandler
import com.example.mobprog.gui.guild.onSubmit
import com.example.mobprog.guild.GuildData
import org.junit.Test
import org.mockito.kotlin.*

class GuildUnitTest {

    @Test
    fun `test om onSubmit bruker createGuild med riktig data`() {
        val mockGuildService: GuildService = mock()
        val mockUserService: UserService = mock()
        val mockNavController: NavController = mock()
        val guildURI: Uri? = null

        val name = "TestGuild"
        val description = "testGuild."
        val picture = "test.jpg"
        val leader = "testLeader"

        onSubmit(
            name = name,
            description = description,
            picture = picture,
            guildService = mockGuildService,
            leader = leader,
            userService = mockUserService,
            navController = mockNavController,
            guildURI = guildURI
        )

        val data = argumentCaptor<GuildData>()
        verify(mockGuildService).createGuild(data.capture(), any())

        val capturedGuildData = data.firstValue

        assert(capturedGuildData.name == name)
        assert(capturedGuildData.description == description)
        assert(capturedGuildData.leader == leader)
        assert(capturedGuildData.picture == picture)

        assert(capturedGuildData.guildId.isNotEmpty())
        assert(capturedGuildData.dateCreated.isNotEmpty())
    }


}