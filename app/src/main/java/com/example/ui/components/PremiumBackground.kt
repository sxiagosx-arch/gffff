package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.MatteBlack
import com.example.ui.theme.Charcoal
import com.example.ui.theme.NeonGreenGlow

@Composable
fun PremiumBackground(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Charcoal,
                        MatteBlack
                    ),
                    radius = 1500f
                )
            )
    ) {
        // Subtle top glow
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            NeonGreenGlow.copy(alpha = 0.05f),
                            Color.Transparent,
                            Color.Transparent
                        )
                    )
                )
        )
        content()
    }
}
