package com.hitwaves.model

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.hitwaves.ui.screens.Account
import com.hitwaves.ui.screens.ArtistDetails
import com.hitwaves.ui.screens.ConcertMap
import com.hitwaves.ui.screens.EventDetails
import com.hitwaves.ui.screens.Home
import com.hitwaves.ui.screens.Likes
import com.hitwaves.ui.screens.Login
import com.hitwaves.ui.screens.Notification
import com.hitwaves.ui.screens.Register
import com.hitwaves.ui.screens.Tickets

@Composable
fun LoginNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            Login(navController)
        }
        composable("register") {
            Register(navController)
        }
    }
}