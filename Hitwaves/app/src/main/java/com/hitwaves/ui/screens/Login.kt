package com.hitwaves.ui.screens

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hitwaves.AppActivity
import com.hitwaves.R
import com.hitwaves.api.ApiResult
import com.hitwaves.api.TokenManager
import com.hitwaves.api.TokenResponse
import com.hitwaves.ui.component.CustomSnackbar
import com.hitwaves.ui.component.IconData
import com.hitwaves.ui.component.LoginButton
import com.hitwaves.ui.component.LoginInputField
import com.hitwaves.ui.component.LoginPasswordField
import com.hitwaves.ui.component.SecondaryLoginButton
import com.hitwaves.ui.component.LoadingIndicator
import com.hitwaves.ui.theme.*
import com.hitwaves.ui.viewModel.LoginViewModel

private fun init() : LoginViewModel {
    return LoginViewModel()
}

@Composable
fun Login(navController: NavHostController) {
    val emailUsername = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    val loginViewModel = remember { init() }
    val result: ApiResult<TokenResponse> by loginViewModel.loginState
    val isLoading by loginViewModel.isLoading
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(result) {
        if (result.success && result.data != null) {
            TokenManager.saveToken(result.data!!.token)
            val intent = Intent(context, AppActivity::class.java)
            context.startActivity(intent)
            (context as? Activity)?.finish()
        } else if (!result.success && result.errorMessage != null) {
            password.value = ""
            snackbarHostState.showSnackbar(result.errorMessage!!)
        }
    }


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
                value = emailUsername.value,
                onValueChange = { emailUsername.value = it },
                label = "Email or Username"
            )

            Spacer(modifier = Modifier.height(16.dp))

            LoginPasswordField(
                value = password.value,
                onValueChange = { password.value = it },
                label = "Password"
            )

            Spacer(modifier = Modifier.height(100.dp))

            LoginButton(
                textBtn = "Sign in",
                onClickAction = {
                    loginViewModel.handleLogin(emailUsername.value, password.value)
                }
            )


            SecondaryLoginButton(
                textBtn = "Sign up",
                onClickAction = {
                    navController.navigate("register") {
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

    CustomSnackbar(snackbarHostState)

    if (isLoading) {
        LoadingIndicator()
    }

}
