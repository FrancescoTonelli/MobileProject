package com.hitwaves.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.hitwaves.api.UserUpdateRequest
import com.hitwaves.ui.component.CustomSnackBar
import com.hitwaves.ui.component.GoBack
import com.hitwaves.ui.component.LoginButton
import com.hitwaves.ui.component.LoginDateField
import com.hitwaves.ui.component.LoginInputField
import com.hitwaves.ui.component.LoginPasswordField
import com.hitwaves.ui.component.LoadingIndicator
import com.hitwaves.ui.theme.*
import com.hitwaves.ui.viewModel.AccountViewModel
import kotlinx.coroutines.launch

private fun init() : AccountViewModel {
    return AccountViewModel()
}

@Composable
fun AccountUpdate(navController: NavHostController) {

    val name = remember { mutableStateOf("") }
    val surname  = remember { mutableStateOf("") }
    val birthdate = remember { mutableStateOf("") }
    val username = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val confirmPassword = remember { mutableStateOf("") }

    val accountViewModel = remember { init() }
    val accountState by accountViewModel.accountState
    val updateState by accountViewModel.updateState
    val isLoading by accountViewModel.isLoadingAccount
    val snackBarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        accountViewModel.getAccount()
    }

    LaunchedEffect(accountState) {
        if (accountState.success && accountState.data != null) {
            name.value = accountState.data!!.name
            surname.value = accountState.data!!.surname
            birthdate.value = accountState.data!!.birthdate
            username.value = accountState.data!!.username
            email.value = accountState.data!!.email
            password.value = ""
            confirmPassword.value = ""
        } else if (!accountState.success && accountState.errorMessage != null) {
            snackBarHostState.showSnackbar(accountState.errorMessage!!)
        }
    }

    LaunchedEffect(updateState) {
        if (updateState.success && updateState.data != null) {
            snackBarHostState.showSnackbar("Account updated successfully")
            accountViewModel.getAccount()
        } else if (!updateState.success && updateState.errorMessage != null) {
            password.value = ""
            confirmPassword.value = ""
            snackBarHostState.showSnackbar(updateState.errorMessage!!)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark),
        contentAlignment = Alignment.Center
    ) {



        LazyColumn(
            modifier = Modifier.fillMaxWidth()
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(top = 4.dp, bottom = 24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    GoBack(navController)

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "Edit your data",
                        style = Typography.titleLarge.copy(
                            fontSize = 24.sp,
                            color = Secondary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

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
                    textBtn = "Save",
                    onClickAction = {
                        if(password.value != confirmPassword.value) {
                            coroutineScope.launch {
                                snackBarHostState.showSnackbar("Passwords do not match")
                            }
                        }
                        else {
                            accountViewModel.updateDetails(
                                UserUpdateRequest(
                                    name = name.value,
                                    surname = surname.value,
                                    birthdate = birthdate.value,
                                    username = username.value,
                                    email = email.value,
                                    password = password.value.ifEmpty { null }
                                )
                            )
                        }
                    }
                )
            }
        }
    }

    CustomSnackBar(snackBarHostState)

    if (isLoading) {
        LoadingIndicator()
    }
}