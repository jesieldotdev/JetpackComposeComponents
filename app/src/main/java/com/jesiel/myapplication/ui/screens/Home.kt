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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.jesiel.myapplication.ui.components.Card
import com.jesiel.myapplication.ui.components.ExampleBottomSheet
import com.jesiel.myapplication.ui.components.Header

import com.jesiel.myapplication.ui.theme.Grey80
import com.jesiel.myapplication.ui.theme.MyApplicationTheme

@Composable
fun HomeScreen(
//    navController: NavHostController,
    showSheet: Boolean,
    onDismissSheet: () -> Unit
){

 HomeContent(showSheet,
     onDismissSheet)

}

@Composable
fun HomeContent(
    showSheet: Boolean,
                onDismissSheet: () -> Unit
){
    MyApplicationTheme(dynamicColor = false) {
    Column(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
        ,

//            verticalArrangement = Arrangement.Center
    ) {
        Column(
//                horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Header()
            Week()
            Spacer(modifier = Modifier.height(16.dp))
            Card(isActive = true)
            Card(isActive = false)
            ExampleBottomSheet(
                showSheet,
                onDismissSheet
            )

        }
    }
    }
}


//@Preview(showBackground = true)
//@Composable
//fun AppNavigationPreview() {
//   HomeContent(showSheet)
//}