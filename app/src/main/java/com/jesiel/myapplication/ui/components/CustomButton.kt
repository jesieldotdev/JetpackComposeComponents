package com.jesiel.myapplication.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jesiel.myapplication.ui.theme.Pink40
import com.jesiel.myapplication.ui.theme.Purple40


data class CustomButtonProps(
    val handleClick: () -> Unit,
    val text: String,
    val modifier: Modifier = Modifier
)

@Composable
fun CustomButton(props: CustomButtonProps){

    Button(
        onClick = {props.handleClick},
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(24.dp, 16.dp),
        modifier = Modifier.fillMaxWidth()
//        colors = ButtonDefaults.buttonColors(
//            containerColor = Green
//        )
    ) {
        Text(
            text = props.text,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp

        )
    }
}


@Preview
@Composable
fun ButtonPreview(showBackground: Boolean =true){
    CustomButton(
        CustomButtonProps(
            handleClick = {println("")},
            text = "Teste"
        )
    )
}