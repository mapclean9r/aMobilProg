package com.example.mobprog.gui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.trimmedLength
import com.example.mobprog.R
import com.example.mobprog.api.GameData
import com.example.mobprog.createEvent.EventData
import com.example.mobprog.util.titleLengthCheck

@Composable
fun GameBox (gameData: GameData) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, Color.Blue, shape = RoundedCornerShape(10.dp))
            .padding(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding()
                .clickable {

                }
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = titleLengthCheck(gameData.title),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
                , color = Color.Black
            )
            Text(
                text = gameData.genre,
                fontSize = 16.sp,
                fontWeight = FontWeight.W400,
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
    }

}
