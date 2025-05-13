package com.hitwaves.component

import androidx.compose.ui.graphics.vector.ImageVector

data class IconData(
    val route : String,
    val label : String,
    val icon : ImageVector,
    val selectedIcon: ImageVector?
)