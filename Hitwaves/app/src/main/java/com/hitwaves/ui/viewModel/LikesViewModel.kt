package com.hitwaves.ui.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hitwaves.api.ApiResult
import com.hitwaves.api.LikedArtistResponse
import com.hitwaves.api.apiGetLikedArtists
import com.hitwaves.api.apiLikeOrUnlikeArtist
import kotlinx.coroutines.launch

class LikesViewModel : ViewModel(){
    private val _likedArtistsState = mutableStateOf<ApiResult<List<LikedArtistResponse>>>(ApiResult<List<LikedArtistResponse>>(false, null, null))
    val likedArtistsState: State<ApiResult<List<LikedArtistResponse>>> = _likedArtistsState
    private val _toggleState = mutableStateOf<ApiResult<String>>(ApiResult<String>(false, null, null))
    val toggleState: State<ApiResult<String>> = _toggleState
    private val _isLikesLoading = mutableStateOf(false)
    val isLikesLoading : State<Boolean> = _isLikesLoading

    fun getLikedArtists() {

        viewModelScope.launch {

            _isLikesLoading.value = true

            try {

                val response = apiGetLikedArtists()

                _isLikesLoading.value = false

                if (!response.success) {
                    _likedArtistsState.value = ApiResult<List<LikedArtistResponse>>(false, null, response.errorMessage)
                }
                else {
                    _likedArtistsState.value = ApiResult<List<LikedArtistResponse>>(true, response.data, null)
                }

            } catch (e: Exception) {
                _likedArtistsState.value = ApiResult<List<LikedArtistResponse>>(false, null, e.message.toString())
            }


        }
    }

    fun toggleLike(artistId: Int) {
        viewModelScope.launch {
            try {
                _isLikesLoading.value = true

                val response = apiLikeOrUnlikeArtist(artistId)

                _isLikesLoading.value = false

                if (!response.success) {
                    _toggleState.value = ApiResult<String>(false, null, response.errorMessage)
                }
                else {
                    _toggleState.value = ApiResult<String>(true, response.data, null)
                }
            } catch (e: Exception) {
                _toggleState.value = ApiResult<String>(false, null, e.message.toString())
            }
        }
    }
}