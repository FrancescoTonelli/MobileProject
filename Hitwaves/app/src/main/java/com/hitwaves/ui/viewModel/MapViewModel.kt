package com.hitwaves.ui.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hitwaves.api.ApiResult
import com.hitwaves.api.MapConcertResponse
import com.hitwaves.api.UserDetailsResponse
import com.hitwaves.api.apiGetMapConcerts
import com.hitwaves.api.apiGetUserDetails
import kotlinx.coroutines.launch

class MapViewModel: ViewModel() {
    private val _mapEventState = mutableStateOf(ApiResult<List<MapConcertResponse>>(false, null, null))
    val mapEventState: State<ApiResult<List<MapConcertResponse>>> = _mapEventState
    private val _isLoadingPOIs = mutableStateOf(false)
    val isLoadingPOIs: State<Boolean> = _isLoadingPOIs

    fun getPOIs() {
        viewModelScope.launch {

            try {

                _isLoadingPOIs.value = true

                val response = apiGetMapConcerts()

                _isLoadingPOIs.value = false

                if (!response.success) {
                    _mapEventState.value = ApiResult(false, null, response.errorMessage)
                } else {
                    _mapEventState.value = ApiResult(true, response.data, null)
                }

            } catch (e: Exception) {
                _mapEventState.value = ApiResult(false, null, e.message.toString())
            }
        }
    }
}