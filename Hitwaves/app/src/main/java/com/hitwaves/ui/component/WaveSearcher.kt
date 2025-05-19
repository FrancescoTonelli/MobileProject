package com.hitwaves.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hitwaves.ui.theme.*
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.hitwaves.R

@Composable
fun WaveSearcher(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    Box(
        modifier = Modifier
            .height(50.dp)
            .background(FgDark, shape = RoundedCornerShape(30.dp))
            .border(1.dp, Secondary, shape = RoundedCornerShape(30.dp))
            .fillMaxWidth(0.9f),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.search),
                contentDescription = null,
                tint = Secondary,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .size(20.dp)
            )


            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = Typography.bodyLarge.copy(
                    color = Secondary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                ),
                modifier = Modifier.weight(1f),
                decorationBox = { innerTextField ->
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = Typography.bodyLarge.copy(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Normal,
                                color = Secondary.copy(alpha = 0.5f)
                            ),
                        )
                    }
                    innerTextField()
                },
                cursorBrush = SolidColor(Primary),
            )
        }
    }
}
