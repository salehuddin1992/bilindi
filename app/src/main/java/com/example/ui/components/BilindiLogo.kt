package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.BilindiWallRepository
import com.example.ui.theme.BilindiBlue
import com.example.ui.theme.LearningGreen

@Composable
fun BilindiLogoIcon(
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    customLogoUrl: String? = null
) {
    val appLogoUrl by BilindiWallRepository.instance.appLogoUrl.collectAsState()
    val effectiveLogo = customLogoUrl ?: appLogoUrl

    if (!effectiveLogo.isNullOrBlank()) {
        AsyncImage(
            model = effectiveLogo,
            contentDescription = "Logo Aplikasi",
            contentScale = ContentScale.Crop,
            modifier = modifier
                .size(size)
                .clip(RoundedCornerShape(8.dp))
        )
    } else {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = "BilindiWall Logo",
            modifier = modifier.size(size)
        )
    }
}

@Composable
fun BilindiLogoFull(
    modifier: Modifier = Modifier,
    iconSize: Dp = 48.dp,
    textSize: Int = 24,
    customLogoUrl: String? = null
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (iconSize > 0.dp) {
            BilindiLogoIcon(size = iconSize, customLogoUrl = customLogoUrl)
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = buildAnnotatedString {
                withStyle(
                    SpanStyle(
                        color = BilindiBlue,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = textSize.sp,
                        letterSpacing = (-0.5).sp
                    )
                ) {
                    append("Bilindi")
                }
                withStyle(
                    SpanStyle(
                        color = LearningGreen,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = textSize.sp,
                        letterSpacing = (-0.5).sp
                    )
                ) {
                    append("Wall")
                }
            }
        )
    }
}
