package com.hitwaves.ui.screens

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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.hitwaves.AppActivity
import com.hitwaves.R
import com.hitwaves.api.ApiResult
import com.hitwaves.api.LoginRequest
import com.hitwaves.api.TokenManager
import com.hitwaves.api.TokenResponse
import com.hitwaves.api.apiLoginUser
import com.hitwaves.component.CustomSnackbar
import com.hitwaves.component.IconData
import com.hitwaves.component.LoginButton
import com.hitwaves.component.LoginInputField
import com.hitwaves.component.LoginPasswordField
import com.hitwaves.component.SecondaryLoginButton
import com.hitwaves.ui.theme.*
import kotlinx.coroutines.launch

suspend fun handleLogin(emailUsername: String, password: String): ApiResult<TokenResponse> {
    val loginRequest = if (emailUsername.contains("@")) {
        LoginRequest(email = emailUsername, password = password)
    } else {
        LoginRequest(username = emailUsername, password = password)
    }
    return apiLoginUser(loginRequest)
}

@Composable
fun Login(navController: NavHostController) {
    var emailUsername by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var errorMsg by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

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

            LoginButton(
                textBtn = "Sign in",
                onClickAction = {
                    coroutineScope.launch {
                        isLoading = true
                        try {
                            val response = handleLogin(emailUsername, password)
                            if (!response.success) {
                                password = ""
                                isLoading = false
                                snackbarHostState.showSnackbar(response.errorMessage.toString())
                            }
                            else {
                                isLoading = false
                                TokenManager.saveToken(response.data?.token.toString())
                                val intent = Intent(context, AppActivity::class.java)
                                context.startActivity(intent)
                            }

                        } catch (e: Exception) {
                            password = ""
                            isLoading = false
                            snackbarHostState.showSnackbar(e.message.toString())
                        }
                    }
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

    Box(
        modifier = Modifier.fillMaxSize()
            .padding(bottom = 40.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        SnackbarHost(
            hostState = snackbarHostState,
            snackbar = { snackbarData ->
                CustomSnackbar(snackbarData)
            }
        )
    }

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color.White)
        }
    }

}
