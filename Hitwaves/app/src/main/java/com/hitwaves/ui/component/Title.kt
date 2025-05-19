package com.hitwaves.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hitwaves.ui.theme.*

@Composable
fun Title(title: String){

    Column (
        modifier = Modifier
            .fillMaxSize(),
            //.padding(top=8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            text = title,
            style = Typography.titleLarge.copy(
                fontSize = 24.sp,
                color = Secondary
            )
        )
        HorizontalDivider(
            modifier = Modifier
                .padding(top = 8.dp)
                .width(180.dp),
                //.width(with(LocalDensity.current) { (textWidth + 40).toDp() }),
            thickness = 1.dp,
            color = Secondary
        )
    }
}