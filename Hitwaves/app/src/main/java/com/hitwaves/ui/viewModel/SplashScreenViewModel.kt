package com.hitwaves.ui.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hitwaves.api.ApiResult
import com.hitwaves.api.TokenResponse
import com.hitwaves.api.apiAutomaticLogin
import kotlinx.coroutines.launch

class SplashScreenViewModel : ViewModel(){
    private val _autoLoginState = mutableStateOf(ApiResult<TokenResponse>(false, null, null))
    val autoLoginState: State<ApiResult<TokenResponse>> = _autoLoginState

    fun handleSplash() {

        viewModelScope.launch {

            try {

                val response = apiAutomaticLogin()

                if (!response.success) {
                    _autoLoginState.value = ApiResult(false, null, response.errorMessage)
                }
                else {
                    _autoLoginState.value = ApiResult(true, response.data, null)
                }

            } catch (e: Exception) {
                _autoLoginState.value = ApiResult(false, null, e.message.toString())
            }


        }
    }
}