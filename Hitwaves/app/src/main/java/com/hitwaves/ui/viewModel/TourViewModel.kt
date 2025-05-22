package com.hitwaves.ui.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hitwaves.api.ApiResult
import com.hitwaves.api.TourDetailsResponse
import com.hitwaves.api.apiGetTourDetails
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TourViewModel : ViewModel(){
    private val _tourArtistState = mutableStateOf(ApiResult<TourDetailsResponse>(false, null, null))
    val tourArtistState: State<ApiResult<TourDetailsResponse>> = _tourArtistState

    private val _tourConcertState = mutableStateOf(ApiResult<TourDetailsResponse>(false, null, null))
    val tourConcertState: State<ApiResult<TourDetailsResponse>> = _tourConcertState

    private val _isLoadingTour = mutableStateOf(false)
    val isLoadingTour : State<Boolean> = _isLoadingTour

    fun getTourArtist(tourId : Int){
        viewModelScope.launch {
            try {

                _isLoadingTour.value = true

                val response = apiGetTourDetails(tourId)

                _isLoadingTour.value = false

                if (!response.success) {
                    _tourArtistState.value = ApiResult(false, null, response.errorMessage)
                } else {
                    _tourArtistState.value = ApiResult(true, response.data, "${System.currentTimeMillis()}")
                }

            } catch (e: Exception) {
                _tourArtistState.value = ApiResult(false, null, e.message.toString())
            }
        }
    }

    fun getTourConcert(tourId : Int){
        viewModelScope.launch {
            try {

                _isLoadingTour.value = true

                val response = apiGetTourDetails(tourId)

                _isLoadingTour.value = false

                if (!response.success) {
                    _tourConcertState.value = ApiResult(false, null, response.errorMessage)
                } else {
                    _tourConcertState.value = ApiResult(true, response.data, "${System.currentTimeMillis()}")
                }

            } catch (e: Exception) {
                _tourConcertState.value = ApiResult(false, null, e.message.toString())
            }
        }
    }
}