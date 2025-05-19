package com.hitwaves.ui.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hitwaves.api.ApiResult
import com.hitwaves.api.LikedArtistResponse
import com.hitwaves.api.MessageResponse
import com.hitwaves.api.NotificationResponse
import com.hitwaves.api.apiGetAllNotifications
import com.hitwaves.api.apiGetLikedArtists
import com.hitwaves.api.apiLikeOrUnlikeArtist
import com.hitwaves.api.apiReadNotification
import kotlinx.coroutines.launch

class NotificationViewModel : ViewModel(){
    private val _notificationState = mutableStateOf<ApiResult<List<NotificationResponse>>>(ApiResult<List<NotificationResponse>>(false, null, null))
    val notificationState: State<ApiResult<List<NotificationResponse>>> = _notificationState
    private val _readState = mutableStateOf<ApiResult<MessageResponse>>(ApiResult<MessageResponse>(false, null, null))
    val readState: State<ApiResult<MessageResponse>> = _readState
    private val _isNotificationLoading = mutableStateOf(false)
    val isNotificationLoading : State<Boolean> = _isNotificationLoading

    fun getNotifications() {

        viewModelScope.launch {

            _isNotificationLoading.value = true

            try {

                val response = apiGetAllNotifications()

                _isNotificationLoading.value = false

                if (!response.success) {
                    _notificationState.value = ApiResult<List<NotificationResponse>>(false, null, response.errorMessage)
                }
                else {
                    _notificationState.value = ApiResult<List<NotificationResponse>>(true, response.data, null)
                }

            } catch (e: Exception) {
                _notificationState.value = ApiResult<List<NotificationResponse>>(false, null, e.message.toString())
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
                    _readState.value = ApiResult<MessageResponse>(false, null, response.errorMessage)
                }
                else {
                    _readState.value = ApiResult<MessageResponse>(true, response.data, null)
                }

            } catch (e: Exception) {
                _readState.value = ApiResult<MessageResponse>(false, null, e.message.toString())
            }


        }
    }
}