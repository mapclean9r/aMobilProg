import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun MessageBubble(message: String, timestamp: Timestamp?, isSender: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = if (isSender) Arrangement.End else Arrangement.Start
    ) {

        fun formatTimestamp(timestamp: Timestamp?): String {
            return if (timestamp != null) {
                val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
                sdf.format(timestamp.toDate())
            } else {
                ""
            }
        }

        Box(
            modifier = Modifier
                .background(
                    color = if (isSender) Color(0xFF007AFF) else Color(0xFFEBEBEB),
                    shape = MaterialTheme.shapes.medium
                )
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                // Display the message text
                Text(
                    text = message,
                    color = if (isSender) Color.White else Color.Black,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal
                )
                Spacer(modifier = Modifier.width(8.dp))

                // Display the formatted timestamp
                Text(
                    text = formatTimestamp(timestamp),
                    color = if (isSender) Color.White else Color.Gray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Light
                )
            }
        }
    }

}
