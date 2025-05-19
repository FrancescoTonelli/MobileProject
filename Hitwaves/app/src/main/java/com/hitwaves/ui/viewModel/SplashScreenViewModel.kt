package com.hitwaves.ui.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hitwaves.api.ApiResult
import com.hitwaves.api.LoginRequest
import com.hitwaves.api.TokenResponse
import com.hitwaves.api.apiAutomaticLogin
import com.hitwaves.api.apiLoginUser
import kotlinx.coroutines.launch

class SplashScreenViewModel : ViewModel(){
    private val _autoLoginState = mutableStateOf<ApiResult<TokenResponse>>(ApiResult<TokenResponse>(false, null, null))
    val autoLoginState: State<ApiResult<TokenResponse>> = _autoLoginState
    private val _isAutoLoading = mutableStateOf(false)
    val isAutoLoading : State<Boolean> = _isAutoLoading

    fun handleSplash() {

        viewModelScope.launch {

            _isAutoLoading.value = true

            try {

                val response = apiAutomaticLogin()

                _isAutoLoading.value = false

                if (!response.success) {
                    _autoLoginState.value = ApiResult<TokenResponse>(false, null, response.errorMessage)
                }
                else {
                    _autoLoginState.value = ApiResult<TokenResponse>(true, response.data, null)
                }

            } catch (e: Exception) {
                _autoLoginState.value = ApiResult<TokenResponse>(false, null, e.message.toString())
            }


        }
    }
}