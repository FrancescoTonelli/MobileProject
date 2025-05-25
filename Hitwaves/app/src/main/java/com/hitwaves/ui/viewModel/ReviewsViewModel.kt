package com.hitwaves.ui.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hitwaves.api.ApiResult
import com.hitwaves.api.CheckReviewResponse
import com.hitwaves.api.MessageResponse
import com.hitwaves.api.ReviewRequest
import com.hitwaves.api.UserReviewResponses
import com.hitwaves.api.apiAddReview
import com.hitwaves.api.apiCheckUserReview
import com.hitwaves.api.apiGetUserReviews
import kotlinx.coroutines.launch

class ReviewsViewModel: ViewModel() {
    private val _checkReviewState = mutableStateOf(ApiResult<CheckReviewResponse>(false, null, null))
    val checkReviewState: State<ApiResult<CheckReviewResponse>> = _checkReviewState
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading
    private val _postReviewState = mutableStateOf(ApiResult<MessageResponse>(false, null, null))
    val postReviewState: State<ApiResult<MessageResponse>> = _postReviewState

    fun checkExistence(concertId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiCheckUserReview(concertId)
                _isLoading.value = false

                if (!response.success) {
                    _checkReviewState.value = ApiResult<CheckReviewResponse>(false, null, response.errorMessage)
                } else {
                    _checkReviewState.value = ApiResult<CheckReviewResponse>(true, response.data, null)
                }
            } catch (e: Exception) {
                _checkReviewState.value = ApiResult<CheckReviewResponse>(false, null, e.message.toString())
            }
        }
    }

    fun postReview(
        ticketId: Int,
        rating: Int,
        comment: String?
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiAddReview(ReviewRequest(
                    ticketId = ticketId,
                    rate = rating,
                    description = if (comment.isNullOrEmpty()) null else comment
                ))
                _isLoading.value = false

                if (!response.success) {
                    _postReviewState.value = ApiResult<MessageResponse>(false, null, response.errorMessage)
                } else {
                    _postReviewState.value = ApiResult<MessageResponse>(true, response.data, null)
                }
            } catch (e: Exception) {
                _postReviewState.value = ApiResult<MessageResponse>(false, null, e.message.toString())
            }
        }
    }

}