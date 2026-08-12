import re
with open('app/src/main/java/com/example/ui/screens/SplashScreen.kt', 'r') as f:
    content = f.read()

target = """    Box(
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
    }"""
    
replace = """    val floatY by infiniteTransition.animateFloat(
        initialValue = -15f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MatteBlack.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        // Glowing Aura Neon Effect
        Box(
            modifier = Modifier
                .size(200.dp)
                .offset(y = floatY.dp)
                .scale(pulseScale)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            NeonGreen.copy(alpha = 0.6f),
                            Color.Transparent
                        )
                    ),
                    shape = androidx.compose.foundation.shape.CircleShape
                )
                .blur(32.dp)
        )
        
        // The App Logo
        androidx.compose.foundation.Image(
            painter = androidx.compose.ui.res.painterResource(id = R.drawable.logo_img),
            contentDescription = "Logo",
            modifier = Modifier
                .size(160.dp)
                .offset(y = floatY.dp)
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
    }"""

content = content.replace(target, replace)
with open('app/src/main/java/com/example/ui/screens/SplashScreen.kt', 'w') as f:
    f.write(content)
