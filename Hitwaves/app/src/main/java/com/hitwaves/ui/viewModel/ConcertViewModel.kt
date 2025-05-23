package com.hitwaves.ui.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hitwaves.api.ApiResult
import com.hitwaves.api.ConcertDetailsResponse
import com.hitwaves.api.apiGetConcertDetails
import kotlinx.coroutines.launch

class ConcertViewModel: ViewModel() {
    private val _concertState = mutableStateOf(ApiResult<ConcertDetailsResponse>(false, null, null))
    val concertState: State<ApiResult<ConcertDetailsResponse>> = _concertState

    private val _isLoadingConcert = mutableStateOf(false)
    val isLoadingConcert : State<Boolean> = _isLoadingConcert

    fun getConcertInfo(concertId: Int){
        viewModelScope.launch {
            try {
                _isLoadingConcert.value = true

                val response = apiGetConcertDetails(concertId)

                _isLoadingConcert.value = false

                if (!response.success) {
                    _concertState.value = ApiResult(false, null, response.errorMessage)
                } else {
                    _concertState.value = ApiResult(true, response.data, "${System.currentTimeMillis()}")
                }

            } catch (e: Exception) {
                _concertState.value = ApiResult(false, null, e.message.toString())
            }
        }
    }
}