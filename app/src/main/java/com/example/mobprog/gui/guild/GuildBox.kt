package com.example.mobprog.gui.guild

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mobprog.data.UserService
import com.example.mobprog.gui.components.DynamicImageSelector
import com.example.mobprog.guild.GuildData

@Composable
fun GuildBox(guildData: GuildData, userService: UserService, navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, Color.LightGray, shape = RoundedCornerShape(10.dp))
            .padding(12.dp)
    ) {
        DynamicImageSelector(imageName = "guild")
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Guild name: ${guildData.name}",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "Guildleader: ${guildData.leader}",
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