package com.hitwaves.ui.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hitwaves.api.ApiResult
import com.hitwaves.api.NearestConcert
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import com.hitwaves.model.Artist
import com.hitwaves.model.EventForCards
import com.hitwaves.api.ArtistResponse
import com.hitwaves.api.ConcertNoTourResponse
import com.hitwaves.api.TourResponse
import com.hitwaves.api.apiGetUserDetails
import kotlinx.coroutines.launch


class FilterViewModel : ViewModel() {
    private val _allArtist = mutableStateOf(ApiResult<ArtistResponse>(false, null, null))
    val allArtist: State<ApiResult<ArtistResponse>> = _allArtist

    private val _allConcert = mutableStateOf(ApiResult<ConcertNoTourResponse>(false, null, null))
    val allConcert: State<ApiResult<ConcertNoTourResponse>> = _allConcert

    private val _allTour = mutableStateOf(ApiResult<TourResponse>(false, null, null))
    val allTour: State<ApiResult<TourResponse>> = _allTour

    private val _isLoadingFilter = mutableStateOf(false)
    val isLoadingFilter : State<Boolean> = _isLoadingFilter

    fun getAllArtist() {
        viewModelScope.launch {
            try {

                _isLoadingFilter.value = true

                val response = apiGetArtists()

                _isLoadingFilter.value = false

                if (!response.success) {
                    _allArtist.value = ApiResult(false, null, response.errorMessage)
                } else {
                    _allArtist.value = ApiResult(true, response.data, "${System.currentTimeMillis()}")
                }

            } catch (e: Exception) {
                _allArtist.value = ApiResult(false, null, e.message.toString())
            }
        }
    }


    fun getAllEvent(){
        viewModelScope.launch {
            try {

                _isLoadingFilter.value = true

                val response = apiGetConcertsNoTour()

                _isLoadingFilter.value = false

                if (!response.success) {
                    _allConcert.value = ApiResult(false, null, response.errorMessage)
                } else {
                    _allConcert.value = ApiResult(true, response.data, "${System.currentTimeMillis()}")
                }

            } catch (e: Exception) {
                _allConcert.value = ApiResult(false, null, e.message.toString())
            }
        }
    }

    fun getAllTour(){
        viewModelScope.launch {
            try {

                _isLoadingFilter.value = true

                val response = apiGetTours()

                _isLoadingFilter.value = false

                if (!response.success) {
                    _allTour.value = ApiResult(false, null, response.errorMessage)
                } else {
                    _allTour.value = ApiResult(true, response.data, "${System.currentTimeMillis()}")
                }

            } catch (e: Exception) {
                _allTour.value = ApiResult(false, null, e.message.toString())
            }
        }
    }

}
