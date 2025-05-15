package com.hitwaves.ui.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hitwaves.api.ApiResult
import com.hitwaves.api.LoginRequest
import com.hitwaves.api.TokenResponse
import com.hitwaves.api.apiLoginUser
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel(){
    private val _loginState = mutableStateOf<ApiResult<TokenResponse>>(ApiResult<TokenResponse>(false, null, null))
    val loginState: State<ApiResult<TokenResponse>> = _loginState
    private val _isLoading = mutableStateOf(false)
    val isLoading : State<Boolean> = _isLoading

    fun handleLogin(emailUsername: String, password: String) {

        viewModelScope.launch {

            _isLoading.value = true

            try {

                val loginRequest = if (emailUsername.contains("@")) {
                    LoginRequest(email = emailUsername, password = password)
                } else {
                    LoginRequest(username = emailUsername, password = password)
                }

                val response = apiLoginUser(loginRequest)

                _isLoading.value = false

                if (!response.success) {
                    _loginState.value = ApiResult<TokenResponse>(false, null, response.errorMessage)
                }
                else {
                    _loginState.value = ApiResult<TokenResponse>(true, response.data, null)
                }

            } catch (e: Exception) {
                _loginState.value = ApiResult<TokenResponse>(false, null, e.message.toString())
            }


        }
    }
}