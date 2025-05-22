package com.hitwaves.ui.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hitwaves.api.ApiResult
import com.hitwaves.api.TicketDetailsResponse
import com.hitwaves.api.TicketResponse
import com.hitwaves.api.apiGetUserTicketDetails
import com.hitwaves.api.apiGetUserTickets
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TicketViewModel: ViewModel() {
    private val _ticketsState = mutableStateOf(ApiResult<List<TicketResponse>>(false, null, null))
    val ticketsState: State<ApiResult<List<TicketResponse>>> = _ticketsState
    private val _isLoadingTickets = mutableStateOf(false)
    val isLoadingTickets: State<Boolean> = _isLoadingTickets
    private val _displayIndex = mutableIntStateOf(0)
    val displayIndex: State<Int> = _displayIndex
    private val _detailsState = mutableStateOf(ApiResult<TicketDetailsResponse>(false, null, null))
    val detailsState: State<ApiResult<TicketDetailsResponse>> = _detailsState
    private val _isLoadingDetails = mutableStateOf(false)
    val isLoadingDetails: State<Boolean> = _isLoadingDetails

    fun getTickets() {
        viewModelScope.launch {

            try {

                _isLoadingTickets.value = true

                val response = apiGetUserTickets()

                _isLoadingTickets.value = false

                if (!response.success) {
                    _ticketsState.value = ApiResult(false, null, response.errorMessage)
                } else {
                    _ticketsState.value = ApiResult(true, response.data, null)
                }

            } catch (e: Exception) {
                _ticketsState.value = ApiResult(false, null, e.message.toString())
            }
        }
    }

    fun setDisplayIndex(index: Int) {
        _displayIndex.intValue = index
    }

    fun getTicketDetails(ticketId: Int) {
        viewModelScope.launch {

            try {

                _isLoadingDetails.value = true

                val response = apiGetUserTicketDetails(ticketId)

                _isLoadingDetails.value = false

                if (!response.success) {
                    _detailsState.value = ApiResult(false, null, response.errorMessage)
                } else {
                    _detailsState.value = ApiResult(true, response.data, null)
                }

            } catch (e: Exception) {
                _detailsState.value = ApiResult(false, null, e.message.toString())
            }
        }
    }

    fun isFutureOrToday(dateString: String): Boolean {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val inputDate = LocalDate.parse(dateString, formatter)
        val today = LocalDate.now()

        return !inputDate.isBefore(today)
    }
}