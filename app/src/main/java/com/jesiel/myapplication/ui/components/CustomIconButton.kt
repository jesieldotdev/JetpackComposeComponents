package com.jesiel.myapplication.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.filled.Favorite

@Composable
fun CustomIconButton() {
    Button(
        onClick = {}) {
        Icon(
            imageVector = Icons.Filled.Favorite,
            contentDescription = ""
        )

    }

}
//
//@Composable
//fun Icon(imageVector: Favorite, contentDescription: String, tint: Red, modifier: size) {
//    TODO("Not yet implemented")
//}