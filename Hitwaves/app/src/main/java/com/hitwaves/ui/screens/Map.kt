package com.hitwaves.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.hitwaves.ui.theme.*

import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState


@Composable
fun ConcertMap(navController: NavController){
//    Box(modifier = Modifier.fillMaxSize()){
//        Column (
//            modifier = Modifier.fillMaxSize().align(Alignment.Center),
//            verticalArrangement = Arrangement.Center,
//            horizontalAlignment = Alignment.CenterHorizontally) {
//            Text(text = "Map", fontSize = 22.sp, color = Secondary)
//        }
//    }
    val viewportState = rememberMapViewportState {
        setCameraOptions {
            zoom(2.0)
            center(Point.fromLngLat(-98.0, 39.5))
            pitch(0.0)
            bearing(0.0)
        }
    }

    Box(modifier = Modifier.fillMaxSize()){
        MapboxMap(
            modifier = Modifier.fillMaxSize(),
            mapViewportState = viewportState
        )
    }

}

