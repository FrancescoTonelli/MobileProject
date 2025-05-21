package com.hitwaves.ui.viewModel

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class LocationViewModel: ViewModel() {

    private val _locationState = mutableStateOf(Pair<Double?, Double?>(null, null))
    val locationState: State<Pair<Double?, Double?>> = _locationState
    private val _isLoadingLocation = mutableStateOf(false)
    val isLoadingLocation : State<Boolean> = _isLoadingLocation

    private val _isGpsEnabled = MutableStateFlow<Boolean>(true)
    val isGpsEnabled: StateFlow<Boolean> = _isGpsEnabled

    private var gpsReceiver: BroadcastReceiver? = null

    fun registerGpsStatusReceiver(context: Context) {
        if (gpsReceiver != null) return

        val filter = IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
        gpsReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
                val isEnabled = locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true
                _isGpsEnabled.value = isEnabled
            }
        }
        context.registerReceiver(gpsReceiver, filter)
    }

    fun unregisterGpsStatusReceiver(context: Context) {
        gpsReceiver?.let {
            context.unregisterReceiver(it)
            gpsReceiver = null
        }
    }

    fun getUserLocation(context: Context) {
        viewModelScope.launch {
            _isLoadingLocation.value = true
            val location = getUserLocationInternal(context)
            _locationState.value = location
            _isLoadingLocation.value = false
        }
    }

    private suspend fun getUserLocationInternal(context: Context): Pair<Double?, Double?> {
        val fineLocationGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseLocationGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!fineLocationGranted && !coarseLocationGranted) {
            return Pair(null, null)
        }

        return try {
            val fusedLocationClient: FusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(context)

            val location = suspendCancellableCoroutine<Location?> { continuation ->
                fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    CancellationTokenSource().token
                ).addOnSuccessListener { loc ->
                    continuation.resume(loc)
                }.addOnFailureListener { e ->
                    continuation.resumeWithException(e)
                }
            }

            if (location != null) {
                Pair(location.latitude, location.longitude)
            } else {
                Pair(null, null)
            }
        } catch (e: Exception) {
            Pair(null, null)
        }
    }

    fun setIsGpsEnabled(enabled: Boolean) {
        _isGpsEnabled.value = enabled
    }
}