package com.hitwaves.ui.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hitwaves.api.ApiResult
import com.hitwaves.api.LikedArtistResponse
import com.hitwaves.api.MessageResponse
import com.hitwaves.api.UpdateUserImageResponse
import com.hitwaves.api.UserReviewResponses
import com.hitwaves.api.apiDeleteUserReview
import com.hitwaves.api.apiGetLikedArtists
import com.hitwaves.api.apiGetUserReviews
import kotlinx.coroutines.launch

class AccountReviewsViewModel : ViewModel() {
    private val _reviewsState = mutableStateOf(ApiResult<List<UserReviewResponses>>(false, null, null))
    val reviewsState: State<ApiResult<List<UserReviewResponses>>> = _reviewsState
    private val _deleteState = mutableStateOf(ApiResult<MessageResponse>(false, null, null))
    val deleteState: State<ApiResult<MessageResponse>> = _deleteState
    private val _isLoadingReviews = mutableStateOf(false)
    val isLoadingReviews: State<Boolean> = _isLoadingReviews

    fun getReviews() {
        viewModelScope.launch {

            _isLoadingReviews.value = true

            try {

                val response = apiGetUserReviews()

                _isLoadingReviews.value = false

                if (!response.success) {
                    _reviewsState.value = ApiResult<List<UserReviewResponses>>(false, null, response.errorMessage)
                }
                else {
                    _reviewsState.value = ApiResult<List<UserReviewResponses>>(true, response.data, null)
                }

            } catch (e: Exception) {
                _reviewsState.value = ApiResult<List<UserReviewResponses>>(false, null, e.message.toString())
            }


        }
    }

    fun deleteReview(reviewId: Int) {
        viewModelScope.launch {

            _isLoadingReviews.value = true

            try {

                val response = apiDeleteUserReview(reviewId)

                _isLoadingReviews.value = false

                if (!response.success) {
                    _deleteState.value = ApiResult<MessageResponse>(false, null, response.errorMessage)
                }
                else {
                    _deleteState.value = ApiResult<MessageResponse>(true, response.data, null)
                }

            } catch (e: Exception) {
                _deleteState.value = ApiResult<MessageResponse>(false, null, e.message.toString())
            }


        }
    }
}