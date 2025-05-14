package com.hitwaves.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hitwaves.R
import com.hitwaves.component.IconData
import com.hitwaves.ui.theme.*

@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            val logo = IconData(
                route = "logo",
                label = "Logo",
                icon = ImageVector.vectorResource(id = R.drawable.logo)
            )

            Icon(
                imageVector = logo.icon,
                contentDescription = "Logo",
                modifier = Modifier.size(140.dp),
                tint = Color.White
            )

            Spacer(modifier = Modifier.size(28.dp))

            Text(
                text = "Hitwaves",
                style = Typography.titleLarge.copy(
                    fontSize = 48.sp,
                    color = Secondary,
                    drawStyle = Stroke(width = 4f)
                )
            )

            Spacer(modifier = Modifier.size(8.dp))

            Text(
                text = "Find your perfect wave!",
                style = Typography.labelSmall.copy(
                    fontSize = 20.sp,
                    color = Secondary
                ),
                modifier = Modifier.padding(bottom = 40.dp)
            )

            CircularProgressIndicator(
                color = Primary,
                strokeWidth = 4.dp
            )
        }
    }
}
