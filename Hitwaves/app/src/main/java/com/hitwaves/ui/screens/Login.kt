package com.hitwaves.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hitwaves.R
import com.hitwaves.component.ButtonWithIcons
import com.hitwaves.component.IconData
import com.hitwaves.component.LoginButton
import com.hitwaves.component.LoginInputField
import com.hitwaves.component.LoginPasswordField
import com.hitwaves.component.SecondaryLoginButton
import com.hitwaves.ui.theme.*


@Composable
fun Login(navController: NavHostController) {
    var emailUsername by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val logo = IconData(
                route = "logo",
                label = "Logo",
                icon = ImageVector.vectorResource(id = R.drawable.logo)
            )

            Icon(
                imageVector = logo.icon,
                contentDescription = "Logo",
                modifier = Modifier.size(140.dp),
                tint = Color.White
            )

            Spacer(modifier = Modifier.height(35.dp))

            LoginInputField(
                value = emailUsername,
                onValueChange = { emailUsername = it },
                label = "Email or Username"
            )

            Spacer(modifier = Modifier.height(16.dp))

            LoginPasswordField(
                value = password,
                onValueChange = { password = it },
                label = "Password"
            )

            Spacer(modifier = Modifier.height(100.dp))

            LoginButton (
                textBtn = "Sign in",
                onClickAction = {
                    // Handle login action
                }
            )

            SecondaryLoginButton(
                textBtn = "Sign up",
                onClickAction = {
                    navController.navigate("register"){
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
