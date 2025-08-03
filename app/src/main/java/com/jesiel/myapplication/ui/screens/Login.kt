package com.jesiel.myapplication.ui.screens

import SvgButtonWithUrl
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.jesiel.myapplication.ui.components.CustomButton
import com.jesiel.myapplication.ui.components.CustomButtonProps
import com.jesiel.myapplication.ui.components.CustomCheckbox
import com.jesiel.myapplication.ui.components.CustomTextInput
import com.jesiel.myapplication.ui.components.StyledText
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.jesiel.myapplication.R

@Composable
fun LoginScreen(navController: NavHostController){
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    fun handleLogin(){
        Log.d("LoginScreen", "Login button pressed")
        return navController.navigate("/home")
    }


    Column(
        Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Image(
                painter = painterResource(id= R.drawable.ic_launcher_foreground),
                contentDescription = "Icone do app"
            )
            StyledText(text = "Primeiro faça seu login")
            CustomTextInput(email, label = "Email")
            CustomTextInput(password, label = "Senha")
            Row(

                modifier= Modifier
                    .fillMaxWidth(),
//                    .background(color = Purple40),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(0.dp)


            ) {
                CustomCheckbox(label="Permanecer conectado")


            }
            CustomButton(
                CustomButtonProps(
                    handleClick = { handleLogin() },
                    text = "Entrar"
                )

            )

            Text(text="Ou logar com")

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
//                CustomIconButton()
                SvgButtonWithUrl(
                    onClick = { /* ação do botão */ },
                    svgUrl = "https://www.svgrepo.com/show/303108/google-icon-logo.svg"
                )
                SvgButtonWithUrl(
                    onClick = { /* ação do botão */ },
                    svgUrl = "https://www.svgrepo.com/show/512317/github-142.svg"
                )
            }

        }

    }
}