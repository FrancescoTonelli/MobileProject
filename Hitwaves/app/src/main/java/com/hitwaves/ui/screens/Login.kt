package com.hitwaves.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hitwaves.component.LoginInputField


@Composable
fun Login(navController: NavHostController) {
    var emailUsername by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            LoginInputField(
                value = emailUsername,
                onValueChange = { emailUsername = it },
                label = "Email or Username"
            )

            Spacer(modifier = Modifier.height(16.dp))

            LoginInputField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                isPassword = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = {
                // Gestisci login qui
            }) {
                Text("Login")
            }
        }
    }
}
