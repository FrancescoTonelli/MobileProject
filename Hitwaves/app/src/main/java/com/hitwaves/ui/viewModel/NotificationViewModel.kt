package com.hitwaves.ui.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hitwaves.api.ApiResult
import com.hitwaves.api.MessageResponse
import com.hitwaves.api.NotificationResponse
import com.hitwaves.api.apiDeleteNotification
import com.hitwaves.api.apiGetAllNotifications
import com.hitwaves.api.apiReadNotification
import com.hitwaves.utils.UnreadBadge
import kotlinx.coroutines.launch

class NotificationViewModel : ViewModel(){
    private val _notificationState = mutableStateOf(ApiResult<List<NotificationResponse>>(false, null, null))
    val notificationState: State<ApiResult<List<NotificationResponse>>> = _notificationState
    private val _readState = mutableStateOf(ApiResult<MessageResponse>(false, null, null))
    val readState: State<ApiResult<MessageResponse>> = _readState
    private val _deleteState = mutableStateOf(ApiResult<MessageResponse>(false, null, null))
    val deleteState: State<ApiResult<MessageResponse>> = _deleteState
    private val _isNotificationLoading = mutableStateOf(false)
    val isNotificationLoading : State<Boolean> = _isNotificationLoading


    fun getNotifications() {

        viewModelScope.launch {

            _isNotificationLoading.value = true

            try {

                val response = apiGetAllNotifications()

                _isNotificationLoading.value = false

                if (!response.success) {
                    _notificationState.value = ApiResult(false, null, response.errorMessage)
                }
                else {
                    _notificationState.value = ApiResult(true, response.data, null)
                    UnreadBadge.update()
                }

            } catch (e: Exception) {
                _notificationState.value = ApiResult(false, null, e.message.toString())
            }


        }
    }

    fun readNotification(id: Int) {

        viewModelScope.launch {

            _isNotificationLoading.value = true

            try {

                val response = apiReadNotification(id)

                _isNotificationLoading.value = false

                if (!response.success) {
                    _readState.value = ApiResult(false, null, response.errorMessage)
                }
                else {
                    _readState.value = ApiResult(true, response.data, null)
                    UnreadBadge.update()
                }

            } catch (e: Exception) {
                _readState.value = ApiResult(false, null, e.message.toString())
            }


        }
    }

    fun deleteNotification(id: Int) {

        viewModelScope.launch {

            _isNotificationLoading.value = true

            try {

                val response = apiDeleteNotification(id)

                if (!response.success) {
                    _deleteState.value = ApiResult(false, null, response.errorMessage)
                } else {
                    _deleteState.value = ApiResult(true, response.data, null)
                    val notificationResponse = apiGetAllNotifications()
                    if (!notificationResponse.success) {
                        _notificationState.value = ApiResult(false, null, notificationResponse.errorMessage)
                    }
                    else {
                        _notificationState.value = ApiResult(true, notificationResponse.data, null)
                        UnreadBadge.update()
                    }
                }

            } catch (e: Exception) {
                _deleteState.value = ApiResult(false, null, e.message.toString())
            } finally {
                _isNotificationLoading.value = false
            }
        }
    }
}