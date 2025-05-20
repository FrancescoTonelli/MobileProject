package com.hitwaves.ui.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hitwaves.api.ApiResult
import com.hitwaves.model.Artist
import com.hitwaves.model.EventForCards
import com.hitwaves.api.ArtistResponse
import com.hitwaves.api.ConcertNoTourResponse
import com.hitwaves.api.TourResponse
import com.hitwaves.api.apiGetArtists
import com.hitwaves.api.apiGetConcertsNoTour
import com.hitwaves.api.apiGetTours
import kotlinx.coroutines.launch


class FilterViewModel : ViewModel() {
    private val _allArtistState = mutableStateOf(ApiResult<List<ArtistResponse>>(false, null, null))
    val allArtistState: State<ApiResult<List<ArtistResponse>>> = _allArtistState

    private val _allConcertState = mutableStateOf(ApiResult<List<ConcertNoTourResponse>>(false, null, null))
    val allConcertState: State<ApiResult<List<ConcertNoTourResponse>>> = _allConcertState

    private val _allTourState = mutableStateOf(ApiResult<List<TourResponse>>(false, null, null))
    val allTourState: State<ApiResult<List<TourResponse>>> = _allTourState

    private val _isLoadingFilter = mutableStateOf(false)
    val isLoadingFilter : State<Boolean> = _isLoadingFilter

    fun getAllArtist() {
        viewModelScope.launch {
            try {

                _isLoadingFilter.value = true

                val response = apiGetArtists()

                _isLoadingFilter.value = false

                if (!response.success) {
                    _allArtistState.value = ApiResult(false, null, response.errorMessage)
                } else {
                    _allArtistState.value = ApiResult(true, response.data, null)
                }

            } catch (e: Exception) {
                _allArtistState.value = ApiResult(false, null, e.message.toString())
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
                    _allConcertState.value = ApiResult(false, null, response.errorMessage)
                } else {
                    _allConcertState.value = ApiResult(true, response.data, null)
                }

            } catch (e: Exception) {
                _allConcertState.value = ApiResult(false, null, e.message.toString())
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
                    _allTourState.value = ApiResult(false, null, response.errorMessage)
                } else {
                    _allTourState.value = ApiResult(true, response.data, null)
                }

            } catch (e: Exception) {
                _allTourState.value = ApiResult(false, null, e.message.toString())
            }
        }
    }

}

