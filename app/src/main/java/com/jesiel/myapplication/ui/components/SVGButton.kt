import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialogDefaults.shape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import com.jesiel.myapplication.ui.theme.Purple40
import com.jesiel.myapplication.ui.theme.White

@Composable
fun SvgButtonWithUrl(onClick: () -> Unit, svgUrl: String) {
    Button(
        onClick = onClick,

        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = White // ou Color.White
        ),
        modifier = Modifier.
        shadow(8.dp, shape = RoundedCornerShape(16.dp))

    ) {

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(svgUrl)
                .decoderFactory(coil.decode.SvgDecoder.Factory())
                .build(),
            contentDescription = "SVG Icon",
            modifier = Modifier.size(24.dp)
        )

    }
}