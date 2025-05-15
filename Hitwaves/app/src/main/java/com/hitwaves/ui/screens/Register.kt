package com.hitwaves.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import com.hitwaves.api.RegisterRequest
import com.hitwaves.api.TokenManager
import com.hitwaves.api.TokenResponse
import com.hitwaves.api.apiRegisterUser
import com.hitwaves.component.CustomSnackbar
import com.hitwaves.component.LoginButton
import com.hitwaves.component.LoginDateField
import com.hitwaves.component.LoginInputField
import com.hitwaves.component.LoginPasswordField
import com.hitwaves.component.SecondaryLoginButton
import com.hitwaves.ui.theme.*
import kotlinx.coroutines.launch

suspend fun handleRegister(
    name: String,
    surname: String,
    birthdate: String,
    username: String,
    email: String,
    password: String
): ApiResult<TokenResponse> {
    val registerRequest = RegisterRequest(
        name = name,
        surname = surname,
        birthdate = birthdate,
        username = username,
        email = email,
        password = password
    )
    return apiRegisterUser(registerRequest)
}

@Composable
fun Register(navController: NavHostController) {
    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var birthdate by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

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

        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.logo),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxSize(),
            tint = Primary.copy(alpha = 0.8f)
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth()
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            item {
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
                        coroutineScope.launch {
                            isLoading = true
                            try {
                                if (password != confirmPassword) {
                                    throw Exception("Passwords do not match")
                                }
                                val response = handleRegister(
                                    name = name,
                                    surname = surname,
                                    birthdate = birthdate,
                                    username = username,
                                    email = email,
                                    password = password
                                )
                                if (!response.success) {
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
                                isLoading = false
                                snackbarHostState.showSnackbar(e.message.toString())
                            }
                        }
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