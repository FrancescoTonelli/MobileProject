package com.hitwaves.ui.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hitwaves.api.ApiResult
import com.hitwaves.api.ArtistDetailsResponse
import com.hitwaves.api.apiGetArtistDetails
import kotlinx.coroutines.launch

class ArtistViewModel: ViewModel() {
    private val _artistState = mutableStateOf(ApiResult<ArtistDetailsResponse>(false, null, null))
    val artistState: State<ApiResult<ArtistDetailsResponse>> = _artistState

    private val _isLoadingArtist = mutableStateOf(false)
    val isLoadingArtist : State<Boolean> = _isLoadingArtist

    fun getArtistInfo(artistId: Int){
        viewModelScope.launch {
            try {
                _isLoadingArtist.value = true

                val response = apiGetArtistDetails(artistId)

                _isLoadingArtist.value = false

                if (!response.success) {
                    _artistState.value = ApiResult(false, null, response.errorMessage)
                } else {
                    _artistState.value = ApiResult(true, response.data, "${System.currentTimeMillis()}")
                }

            } catch (e: Exception) {
                _artistState.value = ApiResult(false, null, e.message.toString())
            }
        }
    }
}