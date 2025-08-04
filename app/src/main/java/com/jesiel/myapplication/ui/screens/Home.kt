package com.jesiel.myapplication.ui.screens

import Week
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.jesiel.myapplication.ui.components.Card

import com.jesiel.myapplication.ui.theme.Grey80
import com.jesiel.myapplication.ui.theme.MyApplicationTheme
import com.jesiel.myapplication.ui.theme.White

@Composable
fun HomeScreen(navController: NavHostController){

    MyApplicationTheme {
        Column(
            Modifier
                .padding(12.dp, 48.dp)
                .fillMaxWidth()
                .fillMaxHeight()
                .background(White)
            ,

//            verticalArrangement = Arrangement.Center
        ) {
            Column(
//                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Ago 5, 2025",
                    color = Grey80
                )
                Text(
                    text = "Today",
                    fontWeight = FontWeight.Bold,
                    fontSize = 42.sp,
                )

                Week()

                Card()



            }

        }
    }

}


@Preview(showBackground = false)
@Composable
fun AppNavigationPreview() {
    MyApplicationTheme {
        Column(
            Modifier
                .padding(12.dp, 48.dp)
                .fillMaxWidth()
                .fillMaxHeight(),
//            verticalArrangement = Arrangement.Center
        ) {
            Column(
//                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Ago 5, 2025",
                    color = Grey80
                )
                Text(
                    text = "Today",
                    fontWeight = FontWeight.Bold,
                    fontSize = 42.sp,
                    )

                Week()
                Spacer(modifier = Modifier.height(16.dp))
                Card()



            }

        }
    }
}