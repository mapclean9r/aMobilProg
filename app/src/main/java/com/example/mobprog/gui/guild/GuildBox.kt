package com.example.mobprog.gui.guild

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobprog.gui.components.DynamicImageSelector
import com.example.mobprog.guild.GuildData

@Composable
fun GuildBox(guildData: GuildData) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, Color.LightGray, shape = RoundedCornerShape(10.dp))
            .padding(12.dp)
    ) {
        DynamicImageSelector(imageName = guildData.picture)
        /*Image(
            painter = painterResource(R.drawable.lol),
            contentDescription = eventData.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )*/
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Guild name: $guildData.name",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "Guildleader: $guildData.leader",
            fontSize = 16.sp,
            fontWeight = FontWeight.W400
        )
        Text(
            text = "Member: " + guildData.members,
            fontSize = 12.sp,
            fontWeight = FontWeight.W300
        )
    }
}