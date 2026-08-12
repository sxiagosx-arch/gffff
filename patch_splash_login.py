import re

with open('/app/applet/app/src/main/java/com/example/ui/screens/SplashScreen.kt', 'r') as f:
    content = f.read()

target1 = """            Box(
                modifier = Modifier
                    .size(120.dp)
                    .scale(scale.value)
                    .background(NeonGreenDim.copy(alpha = 0.5f), shape = androidx.compose.foundation.shape.CircleShape)
                    .border(3.dp, NeonGreen, androidx.compose.foundation.shape.CircleShape),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.material3.Icon(
                    painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.ic_logo),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(100.dp).offset(y = 4.dp)
                )
            }"""
replace1 = """                androidx.compose.material3.Icon(
                    painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.ic_logo),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(160.dp).scale(scale.value)
                )"""
content = content.replace(target1, replace1)

with open('/app/applet/app/src/main/java/com/example/ui/screens/SplashScreen.kt', 'w') as f:
    f.write(content)

with open('/app/applet/app/src/main/java/com/example/ui/screens/LoginScreen.kt', 'r') as f:
    content2 = f.read()

target2 = """                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(NeonGreenDim)
                        .border(1.dp, NeonGreen, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.ic_logo),
                        contentDescription = "Logo",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(54.dp)
                    )
                }"""
replace2 = """                Icon(
                    painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.ic_logo),
                    contentDescription = "Logo",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(80.dp)
                )"""
content2 = content2.replace(target2, replace2)

with open('/app/applet/app/src/main/java/com/example/ui/screens/LoginScreen.kt', 'w') as f:
    f.write(content2)

