content = """package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.example.ui.theme.MatteBlack
import com.example.ui.theme.NeonGreen
import com.example.R

@Composable
fun SplashScreen() {
    val scale = remember { Animatable(0.5f) }
    
    // Rotation for aura
    val infiniteTransition = rememberInfiniteTransition(label = "aura")
    val auraRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    
    // Pulse for aura
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    
    // Shimmer effect
    val shimmerTranslate = remember { Animatable(-500f) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1.3f, // Scale the logo up
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        // Start shimmer loops
        while (true) {
            shimmerTranslate.snapTo(-500f)
            shimmerTranslate.animateTo(
                targetValue = 2000f,
                animationSpec = tween(1500, easing = FastOutSlowInEasing)
            )
            kotlinx.coroutines.delay(1000)
        }
    }
    
    val shimmerBrush = Brush.linearGradient(
        colors = listOf(
            Color.Transparent,
            Color.White.copy(alpha = 0.6f),
            Color.Transparent
        ),
        start = Offset(shimmerTranslate.value, shimmerTranslate.value),
        end = Offset(shimmerTranslate.value + 300f, shimmerTranslate.value + 300f)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MatteBlack),
        contentAlignment = Alignment.Center
    ) {
        // Glowing Aura Neon Effect
        Box(
            modifier = Modifier
                .size(240.dp)
                .scale(pulseScale)
                .rotate(auraRotation)
                .background(
                    Brush.sweepGradient(
                        colors = listOf(
                            Color.Transparent,
                            NeonGreen.copy(alpha = 0.4f),
                            Color.Transparent,
                            NeonGreen.copy(alpha = 0.8f),
                            Color.Transparent
                        )
                    ),
                    shape = androidx.compose.foundation.shape.CircleShape
                )
                .blur(24.dp)
        )
        
        // The App Logo
        androidx.compose.foundation.Image(
            painter = androidx.compose.ui.res.painterResource(id = R.drawable.logo_img),
            contentDescription = "Logo",
            modifier = Modifier
                .size(200.dp)
                .scale(scale.value)
                .graphicsLayer { compositingStrategy = androidx.compose.ui.graphics.CompositingStrategy.Offscreen }
                .drawWithContent {
                    drawContent()
                    drawRect(
                        brush = shimmerBrush,
                        blendMode = BlendMode.SrcAtop
                    )
                }
        )
    }
}
"""
with open('app/src/main/java/com/example/ui/screens/SplashScreen.kt', 'w') as f:
    f.write(content)
