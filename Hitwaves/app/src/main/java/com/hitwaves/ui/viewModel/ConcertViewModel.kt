package com.hitwaves.ui.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hitwaves.api.ApiResult
import com.hitwaves.api.CanvasResponse
import com.hitwaves.api.ConcertDetailsResponse
import com.hitwaves.api.apiGetConcertCanvas
import com.hitwaves.api.apiGetConcertDetails
import kotlinx.coroutines.launch

class ConcertViewModel: ViewModel() {
    private val _concertState = mutableStateOf(ApiResult<ConcertDetailsResponse>(false, null, null))
    val concertState: State<ApiResult<ConcertDetailsResponse>> = _concertState

    private val _isLoadingConcert = mutableStateOf(false)
    val isLoadingConcert : State<Boolean> = _isLoadingConcert

    private val _canvasState = mutableStateOf(ApiResult<CanvasResponse>(false, null, null))
    val canvasState: State<ApiResult<CanvasResponse>> = _canvasState

    private val _isLoadingCanvas = mutableStateOf(false)
    val isLoadingCanvas: State<Boolean> = _isLoadingCanvas

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

    fun getConcertCanvas(concertId: Int) {
        viewModelScope.launch {
            try {
                _isLoadingCanvas.value = true

                val response = apiGetConcertCanvas(concertId)

                _isLoadingCanvas.value = false

                if (!response.success) {
                    _canvasState.value = ApiResult(false, null, response.errorMessage)
                } else {
                    _canvasState.value = ApiResult(true, response.data, null)
                }

            } catch (e: Exception) {
                _canvasState.value = ApiResult(false, null, e.message.toString())
            }
        }
    }
}