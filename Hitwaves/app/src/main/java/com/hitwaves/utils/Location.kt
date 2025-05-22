package com.hitwaves.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices.getFusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import android.provider.Settings
import android.Manifest

data class Coord(
    val latitude: Double,
    val longitude: Double
)

class LocationService(private val ctx: Context){
    private val fusedLocationClient = getFusedLocationProviderClient(ctx)
    private val locationManager = ctx.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private val _coords = MutableStateFlow<Coord?>(null)
    val coords = _coords.asStateFlow()

    private val _isLoadingLocation = MutableStateFlow(false)
    val isLoadingLocation = _isLoadingLocation.asStateFlow()

    suspend fun getLocation() : Coord?{
        //TODO creare funzione appostita per gestire l'eccezione
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            throw IllegalStateException("Location is disabled")
        }

        if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            throw SecurityException("Location permissions is not granted")
        }

        _isLoadingLocation.value = true

        val location = withContext(Dispatchers.IO){
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                CancellationTokenSource().token
            ).await()
        }
        _isLoadingLocation.value = false

         _coords.value = if(location != null) Coord(location.latitude, location.longitude)
         else null

        return coords.value
    }

    fun openLocationSetting(){
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        if(intent.resolveActivity(ctx.packageManager)!= null){
            ctx.startActivity(intent)
        }
    }
}