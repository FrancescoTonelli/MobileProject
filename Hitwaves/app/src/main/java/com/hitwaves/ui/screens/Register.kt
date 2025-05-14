package com.hitwaves.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hitwaves.R
import com.hitwaves.component.LoginButton
import com.hitwaves.component.LoginDateField
import com.hitwaves.component.LoginInputField
import com.hitwaves.component.LoginPasswordField
import com.hitwaves.component.SecondaryLoginButton
import com.hitwaves.ui.theme.*

@Composable
fun Register(navController: NavHostController) {
    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var birthdate by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.logo),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxSize(),
            tint = Primary.copy(alpha = 0.8f)
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            LoginInputField(
                value = name,
                onValueChange = { name = it },
                label = "Name"
            )

            Spacer(modifier = Modifier.height(16.dp))

            LoginInputField(
                value = surname,
                onValueChange = { surname = it },
                label = "Surname"
            )

            Spacer(modifier = Modifier.height(16.dp))

            LoginDateField(
                value = birthdate,
                onValueChange = { birthdate = it },
                label = "Birthdate"
            )

            Spacer(modifier = Modifier.height(16.dp))

            LoginInputField(
                value = username,
                onValueChange = { username = it },
                label = "Username"
            )

            Spacer(modifier = Modifier.height(16.dp))

            LoginInputField(
                value = email,
                onValueChange = { email = it },
                label = "Email"
            )

            Spacer(modifier = Modifier.height(16.dp))

            LoginPasswordField(
                value = password,
                onValueChange = { password = it },
                label = "Password"
            )

            Spacer(modifier = Modifier.height(16.dp))

            LoginPasswordField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = "Confirm Password"
            )

            Spacer(modifier = Modifier.height(32.dp))

            LoginButton (
                textBtn = "Sign up",
                onClickAction = {
                    // Handle login action
                }
            )

            SecondaryLoginButton(
                textBtn = "Sign in",
                onClickAction = {
                    navController.navigate("login"){
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}