package com.hitwaves.ui.viewModel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import com.hitwaves.model.Artist
import com.hitwaves.model.EventForCards

class FilterViewModel : ViewModel() {
    private var _allArtists: List<Artist> = emptyList()
    private var allEvents: List<EventForCards> = emptyList()

    fun getAllEvent(){}

    fun getAllArtist(){}

}
