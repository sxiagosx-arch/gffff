import re

with open('app/src/main/java/com/example/ui/screens/LoginScreen.kt', 'r') as f:
    content = f.read()

imports = """import androidx.compose.ui.unit.sp
import androidx.compose.animation.core.*
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.geometry.Offset"""

content = content.replace("import androidx.compose.ui.unit.sp", imports)

target_title = """                Text(
                    text = "UNLOCK PLAYER",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    letterSpacing = 2.sp
                )"""

replace_title = """                val infiniteTransition = rememberInfiniteTransition(label = "neon")
                val neonAlpha by infiniteTransition.animateFloat(
                    initialValue = 0.5f,
                    targetValue = 1.0f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1200, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "alpha"
                )
                Text(
                    text = "Unlock Player",
                    color = Color.White,
                    fontFamily = com.example.ui.theme.RussoOne,
                    fontSize = 24.sp,
                    letterSpacing = 2.sp,
                    style = androidx.compose.ui.text.TextStyle(
                        shadow = Shadow(
                            color = NeonGreen.copy(alpha = neonAlpha),
                            offset = Offset(0f, 0f),
                            blurRadius = 16f * neonAlpha
                        )
                    )
                )"""

content = content.replace(target_title, replace_title)
with open('app/src/main/java/com/example/ui/screens/LoginScreen.kt', 'w') as f:
    f.write(content)

