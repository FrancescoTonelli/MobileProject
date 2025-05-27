package com.hitwaves.ui.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hitwaves.api.ApiResult
import com.hitwaves.api.MessageResponse
import com.hitwaves.api.TicketPurchaseRequest
import com.hitwaves.api.apiGetConcertDetails
import com.hitwaves.api.apiGetUserDetails
import com.hitwaves.api.apiPurchaseTicket
import kotlinx.coroutines.launch

data class CheckoutData(
    val image: String,
    val title: String,
    val tourTitle: String?,
    val refunds: Double
)

class CheckoutViewModel: ViewModel() {
    private val _checkoutDisplay = mutableStateOf(ApiResult<CheckoutData>(false, null, null))
    val checkoutDisplay: State<ApiResult<CheckoutData>> = _checkoutDisplay
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _purchaseState = mutableStateOf(ApiResult<MessageResponse>(false, null, null))
    val purchaseState: State<ApiResult<MessageResponse>> = _purchaseState

    fun getCheckoutDisplay(concertId: Int){
        viewModelScope.launch {
            try {
                _isLoading.value = true

                val concertData = apiGetConcertDetails(concertId)

                if (concertData.success && concertData.data != null) {

                    val accountData = apiGetUserDetails()

                    _isLoading.value = false

                    if (accountData.success && accountData.data != null) {
                        _checkoutDisplay.value = ApiResult(
                            true,
                            CheckoutData(
                                image = concertData.data.concertInfo.effectiveImage,
                                title = concertData.data.concertInfo.concertTitle,
                                tourTitle = concertData.data.concertInfo.tourTitle,
                                refunds = accountData.data.refunds
                            ),
                            null
                        )
                    } else {
                        _checkoutDisplay.value = ApiResult(false, null, concertData.errorMessage)
                    }


                } else {
                    _isLoading.value = false
                    _checkoutDisplay.value = ApiResult(false, null, concertData.errorMessage)
                }

            } catch (e: Exception) {
                _isLoading.value = false
                _checkoutDisplay.value = ApiResult(false, null, e.message.toString())
            }
        }
    }

    fun buyTickets(ticketIds: TicketPurchaseRequest) {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                val result = apiPurchaseTicket(ticketIds)

                _isLoading.value = false

                if (result.success && result.data != null) {
                    _purchaseState.value = ApiResult(true, result.data, null)
                } else {
                    _purchaseState.value = ApiResult(false, null, result.errorMessage)
                }

            } catch (e: Exception) {
                _isLoading.value = false
                _checkoutDisplay.value = ApiResult(false, null, e.message.toString())
            }
        }
    }
}