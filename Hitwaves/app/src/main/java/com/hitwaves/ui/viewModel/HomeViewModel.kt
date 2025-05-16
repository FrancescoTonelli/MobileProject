package com.hitwaves.ui.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hitwaves.api.ApiResult
import com.hitwaves.api.NearestConcert
import com.hitwaves.api.PositionRequest
import com.hitwaves.api.TokenResponse
import com.hitwaves.api.apiGetNearestConcerts
import kotlinx.coroutines.launch

class HomeViewModel: ViewModel() {
    private val _nearestState = mutableStateOf<ApiResult<List<NearestConcert>>>(ApiResult<List<NearestConcert>>(false, null, null))
    val nearestState: State<ApiResult<List<NearestConcert>>> = _nearestState
    private val _isLoadingNearest = mutableStateOf(false)
    val isLoadingNearest : State<Boolean> = _isLoadingNearest

    private val _popularState = mutableStateOf<ApiResult<TokenResponse>>(ApiResult<TokenResponse>(false, null, null))
    val popularState: State<ApiResult<TokenResponse>> = _popularState
    private val _isLoadingPopular = mutableStateOf(false)
    val isLoadingPopular : State<Boolean> = _isLoadingPopular

    fun getNearest() {
        viewModelScope.launch {


            try {

                _isLoadingNearest.value = true

                val response = apiGetNearestConcerts(
                    PositionRequest(
                        0.0, 0.0
                    )
                )

                _isLoadingNearest.value = false

                if (!response.success) {
                    _nearestState.value = ApiResult<List<NearestConcert>>(false, null, response.errorMessage)
                }
                else {
                    _nearestState.value = ApiResult<List<NearestConcert>>(true, response.data, null)
                }

            } catch (e: Exception) {
                _nearestState.value = ApiResult<List<NearestConcert>>(false, null, e.message.toString())
            }
        }
    }
}