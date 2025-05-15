package com.hitwaves.ui.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hitwaves.api.ApiResult
import com.hitwaves.api.RegisterRequest
import com.hitwaves.api.TokenResponse
import com.hitwaves.api.apiRegisterUser
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel(){
    private val _registerState = mutableStateOf<ApiResult<TokenResponse>>(ApiResult<TokenResponse>(false, null, null))
    val registerState: State<ApiResult<TokenResponse>> = _registerState
    private val _isLoading = mutableStateOf(false)
    val isLoading : State<Boolean> = _isLoading

    fun handleRegister(
        name: String,
        surname: String,
        birthdate: String,
        username: String,
        email: String,
        password: String,
        confirmPassword: String
    ) {

        viewModelScope.launch {
            if (password != confirmPassword) {
                throw Exception("Passwords do not match")
            }
            _isLoading.value = true

            try {

                val registerRequest = RegisterRequest(
                    name = name,
                    surname = surname,
                    birthdate = birthdate,
                    username = username,
                    email = email,
                    password = password
                )

                val response = apiRegisterUser(registerRequest)

                _isLoading.value = false

                if (!response.success) {
                    _registerState.value = ApiResult<TokenResponse>(false, null, response.errorMessage)
                }
                else {
                    _registerState.value = ApiResult<TokenResponse>(true, response.data, null)
                }

            } catch (e: Exception) {
                _registerState.value = ApiResult<TokenResponse>(false, null, e.message.toString())
            }


        }
    }
}