package com.hitwaves.ui.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hitwaves.api.ApiResult
import com.hitwaves.api.TourDetailsResponse
import com.hitwaves.api.apiGetTourDetails
import kotlinx.coroutines.launch

class TourViewModel : ViewModel(){
    private val _tourState = mutableStateOf(ApiResult<TourDetailsResponse>(false, null, null))
    val tourState: State<ApiResult<TourDetailsResponse>> = _tourState

    private val _isLoadingTour = mutableStateOf(false)
    val isLoadingTour : State<Boolean> = _isLoadingTour

    fun getTourDetails(tourId : Int){
        viewModelScope.launch {
            try {

                _isLoadingTour.value = true

                val response = apiGetTourDetails(tourId)

                _isLoadingTour.value = false

                if (!response.success) {
                    _tourState.value = ApiResult(false, null, response.errorMessage)
                } else {
                    _tourState.value = ApiResult(true, response.data, "${System.currentTimeMillis()}")
                }

            } catch (e: Exception) {
                _tourState.value = ApiResult(false, null, e.message.toString())
            }
        }
    }
}