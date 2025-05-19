package com.hitwaves.ui.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hitwaves.api.ApiResult
import com.hitwaves.api.MessageResponse
import com.hitwaves.api.UpdateUserImageResponse
import com.hitwaves.api.UserDetailsResponse
import com.hitwaves.api.UserUpdateRequest
import com.hitwaves.api.apiDeleteUser
import com.hitwaves.api.apiGetUserDetails
import com.hitwaves.api.apiLogoutUser
import com.hitwaves.api.apiUpdateUserDetails
import com.hitwaves.api.updateUserImageRequest
import kotlinx.coroutines.launch

class AccountViewModel : ViewModel() {
    private val _accountState = mutableStateOf(ApiResult<UserDetailsResponse>(false, null, null))
    val accountState: State<ApiResult<UserDetailsResponse>> = _accountState
    private val _logoutState = mutableStateOf(ApiResult<MessageResponse>(false, null, null))
    val logoutState: State<ApiResult<MessageResponse>> = _logoutState
    private val _deleteState = mutableStateOf(ApiResult<MessageResponse>(false, null, null))
    val deleteState: State<ApiResult<MessageResponse>> = _deleteState
    private val _updateState = mutableStateOf(ApiResult<MessageResponse>(false, null, null))
    val updateState: State<ApiResult<MessageResponse>> = _updateState
    private val _imageUpdateState = mutableStateOf(ApiResult<UpdateUserImageResponse>(false, null, null))
    val imageUpdateState: State<ApiResult<UpdateUserImageResponse>> = _imageUpdateState
    private val _isLoadingAccount = mutableStateOf(false)
    val isLoadingAccount: State<Boolean> = _isLoadingAccount

    fun getAccount() {
        viewModelScope.launch {

            try {

                _isLoadingAccount.value = true

                val response = apiGetUserDetails()

                _isLoadingAccount.value = false

                if (!response.success) {
                    _accountState.value = ApiResult(false, null, response.errorMessage)
                } else {
                    _accountState.value = ApiResult(true, response.data, "${System.currentTimeMillis()}")
                }

            } catch (e: Exception) {
                _accountState.value = ApiResult(false, null, e.message.toString())
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {

                _isLoadingAccount.value = true

                val response = apiLogoutUser()

                _isLoadingAccount.value = false

                if (!response.success) {
                    _logoutState.value = ApiResult(false, null, response.errorMessage)
                } else {
                    _logoutState.value = ApiResult(true, response.data, null)
                }
            } catch (e: Exception) {
                _logoutState.value = ApiResult(false, null, e.message.toString())
            }
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            try {

                _isLoadingAccount.value = true

                val response = apiDeleteUser()

                _isLoadingAccount.value = false

                if (!response.success) {
                    _deleteState.value = ApiResult(false, null, response.errorMessage)
                } else {
                    _deleteState.value = ApiResult(true, response.data, null)
                }
            } catch (e: Exception) {
                _deleteState.value = ApiResult(false, null, e.message.toString())
            }
        }
    }

    fun updateDetails(userUpdateRequest: UserUpdateRequest) {
        viewModelScope.launch {
            try {

                _isLoadingAccount.value = true

                val response = apiUpdateUserDetails(userUpdateRequest)

                _isLoadingAccount.value = false

                if (!response.success) {
                    _updateState.value = ApiResult(false, null, response.errorMessage)
                } else {
                    _updateState.value = ApiResult(true, response.data, null)
                }

            } catch (e: Exception) {
                _updateState.value = ApiResult(false, null, e.message.toString())
            }
        }
    }

    fun updateImage(imageBytes: ByteArray) {
        viewModelScope.launch {
            _isLoadingAccount.value = true

            val result = updateUserImageRequest(imageBytes)

            _isLoadingAccount.value = false
            _imageUpdateState.value = result
        }
    }
}
