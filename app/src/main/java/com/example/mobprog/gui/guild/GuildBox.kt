package com.example.mobprog.gui.guild

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mobprog.data.UserService
import com.example.mobprog.data.handlers.ImageHandler
import com.example.mobprog.gui.components.GetGuildProfileImageView
import com.example.mobprog.guild.GuildData

@Composable
fun GuildBox(guildData: GuildData, userService: UserService, navController: NavController) {

    var imageUrl by remember { mutableStateOf<String?>(null) }
    var guildLeaderName by remember { mutableStateOf<String?>(null) }

    userService.getUsernameWithDocID(guildData.leader) { guildLeader ->
        if (guildData.leader != null) {
            guildLeaderName =  guildLeader
        }
        else {
            println("Guild leader name not found")
        }
    }

    LaunchedEffect(guildData.guildId) {
        ImageHandler().getGuildImageUrl(
            guildID = guildData.guildId,

            onSuccess =
            { url -> imageUrl = url },

            onFailure =
            { exception -> println(exception.message) }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        GetGuildProfileImageView(guildID = guildData.guildId, size = 500)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = guildData.name,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            modifier = Modifier.padding( top = 5.dp),
            text = guildData.description,
            fontSize = 18.sp,
            fontWeight = FontWeight.W400
        )
        Text(
            text = "Guildleader: $guildLeaderName",
            fontSize = 16.sp,
            fontWeight = FontWeight.W400
        )
        Button(
            /* TODO: Lage logikk som hindrer en bruker å joine guild ved visse tilfeller (full guild) */
            onClick = { userService.updateUserGuild(guildData.guildId){success, exception ->
                if (success) {
                    /* TODO: Lage notification for joined guild */
                    navController.navigate("guildScreen")  {
                        while (navController.popBackStack() == true) {
                            navController.popBackStack()
                        }
                    }
                }
                else {
                    /* TODO: Lage feilmelding til bruker hvis det ikke gikk å joine guild */
                    exception?.printStackTrace()
                }
            } },
            shape = ButtonDefaults.textShape
        ) {
            Text(text = "Join Guild")
        }
    }
}