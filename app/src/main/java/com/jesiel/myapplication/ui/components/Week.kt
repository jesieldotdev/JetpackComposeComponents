import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jesiel.myapplication.ui.theme.MyApplicationTheme

@Composable
fun Week() {
    val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val numbers = listOf("4", "5", "6", "7", "8", "9", "10")
    var selectedIndex by remember { mutableStateOf(5) } // Saturday initially

    Row(
        modifier = Modifier
            .padding(horizontal = 0.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        daysOfWeek.forEachIndexed { i, day ->
            Day(
                day = day,
                number = numbers[i],
                isSelected = i == selectedIndex,
                onClick = { selectedIndex = i }
            )
        }
    }
}

@Composable
fun Day(
    day: String,
    number: String,
    isSelected: Boolean = false,
    onClick: () -> Unit = {}
) {

    val pallete = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .width(44.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = day,
            color = if (isSelected) pallete.primary else pallete.onSecondary,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = number,
            color = if (isSelected) pallete.primary else pallete.onSecondary,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 18.sp
        )
        if (isSelected) {
            Spacer(modifier = Modifier.height(4.dp))
            Canvas(modifier = Modifier.size(6.dp)) {
                drawCircle(color = pallete.primary)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun previewWeek() {
    MyApplicationTheme (dynamicColor = false){
        Week()
    }
}
