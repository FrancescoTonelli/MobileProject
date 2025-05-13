package com.hitwaves.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.hitwaves.ui.theme.*
import com.hitwaves.component.SecondaryTextTabs


@Composable
fun Tickets(navController: NavHostController) {
    Box(modifier = Modifier.fillMaxSize()){
        Column (
            modifier = Modifier
                .fillMaxWidth()
        ) {
            SecondaryTextTabs()
        }
    }
}

