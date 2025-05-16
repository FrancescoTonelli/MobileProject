package com.hitwaves.ui.screens

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.hitwaves.ui.component.LoginButton
import com.hitwaves.ui.component.LoginDateField
import com.hitwaves.ui.component.LoginInputField
import com.hitwaves.ui.component.LoginPasswordField
import com.hitwaves.ui.component.SecondaryLoginButton
import com.hitwaves.ui.component.loadingIndicator
import com.hitwaves.ui.theme.*
import com.hitwaves.ui.viewModel.RegisterViewModel

private fun init() : RegisterViewModel {
    return RegisterViewModel()
}

@Composable
fun Register(navController: NavHostController) {
    var name = remember { mutableStateOf("") }
    var surname  = remember { mutableStateOf("") }
    var birthdate = remember { mutableStateOf("") }
    var username = remember { mutableStateOf("") }
    var email = remember { mutableStateOf("") }
    var password = remember { mutableStateOf("") }
    var confirmPassword = remember { mutableStateOf("") }

    val registerViewModel = remember { init() }
    val result: ApiResult<TokenResponse> by registerViewModel.registerState
    val isLoading by registerViewModel.isLoading
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
            confirmPassword.value = ""
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
                    value = name.value,
                    onValueChange = { name.value = it },
                    label = "Name"
                )

                Spacer(modifier = Modifier.height(16.dp))

                LoginInputField(
                    value = surname.value,
                    onValueChange = { surname.value = it },
                    label = "Surname"
                )

                Spacer(modifier = Modifier.height(16.dp))

                LoginDateField(
                    value = birthdate.value,
                    onValueChange = { birthdate.value = it },
                    label = "Birthdate"
                )

                Spacer(modifier = Modifier.height(16.dp))

                LoginInputField(
                    value = username.value,
                    onValueChange = { username.value = it },
                    label = "Username"
                )

                Spacer(modifier = Modifier.height(16.dp))

                LoginInputField(
                    value = email.value,
                    onValueChange = { email.value = it },
                    label = "Email"
                )

                Spacer(modifier = Modifier.height(16.dp))

                LoginPasswordField(
                    value = password.value,
                    onValueChange = { password.value = it },
                    label = "Password"
                )

                Spacer(modifier = Modifier.height(16.dp))

                LoginPasswordField(
                    value = confirmPassword.value,
                    onValueChange = { confirmPassword.value = it },
                    label = "Confirm Password"
                )

                Spacer(modifier = Modifier.height(32.dp))

                LoginButton (
                    textBtn = "Sign up",
                    onClickAction = {
                        registerViewModel.handleRegister(
                            name = name.value,
                            surname = surname.value,
                            birthdate = birthdate.value,
                            username = username.value,
                            email = email.value,
                            password = password.value,
                            confirmPassword = confirmPassword.value
                        )
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

    CustomSnackbar(snackbarHostState)

    if (isLoading) {
        loadingIndicator()
    }
}