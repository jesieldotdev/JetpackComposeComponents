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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

import androidx.compose.ui.unit.dp
import com.jesiel.myapplication.ui.components.Card
import com.jesiel.myapplication.ui.components.ExampleBottomSheet
import com.jesiel.myapplication.ui.components.Header

import com.jesiel.myapplication.ui.theme.MyApplicationTheme

import com.jesiel.myapplication.viewmodel.UserViewModel

@Composable
fun HomeScreen(
//    navController: NavHostController,
    showSheet: Boolean,
    onDismissSheet: () -> Unit
) {

    HomeContent(
        showSheet,
        onDismissSheet
    )

}


data class Task(
    val title: String,
    val text: String,
    val done: Boolean = false,
    val createdAt: String
)




@Composable
fun HomeContent(
    showSheet: Boolean,
    onDismissSheet: () -> Unit
) {



    val tasks by remember { mutableStateOf(
        listOf<Task>(
            Task(
                title = "Wakeup",
                text = "Early from bed and fresh",
                done = false,
                createdAt = "7:00 AM"
            ),
            Task(
                title = "Morning exercises",
                text = "4 types of exercise",
                done = true,
                createdAt = "8:00 PM"
            ),
        )
    )}
    MyApplicationTheme(dynamicColor = false) {
        Column(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),

//            verticalArrangement = Arrangement.Center
        ) {
            Column(
//                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Header()
                Week()
                Spacer(modifier = Modifier.height(16.dp))
//                Card(isActive = true)
//                Card(isActive = false)
                LazyColumn (
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ){
                    items(tasks) {
                        task -> Card(task)
                    }
                }



                ExampleBottomSheet(
                    showSheet,
                    onDismissSheet
                )

            }
        }
    }
}

//
//@Preview(showBackground = true)
//@Composable
//fun AppNavigationPreview() {
//   HomeContent(showSheet)
//}