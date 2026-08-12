import re

with open('/app/applet/app/src/main/java/com/example/ui/screens/SplashScreen.kt', 'r') as f:
    content = f.read()

target1 = """                androidx.compose.material3.Icon(
                    imageVector = Icons.Rounded.Bolt,
                    contentDescription = null,
                    tint = NeonGreen,
                    modifier = Modifier.size(70.dp)
                )"""
replace1 = """                androidx.compose.material3.Icon(
                    painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.ic_logo),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(100.dp).offset(y = 4.dp)
                )"""
content = content.replace(target1, replace1)

with open('/app/applet/app/src/main/java/com/example/ui/screens/SplashScreen.kt', 'w') as f:
    f.write(content)


with open('/app/applet/app/src/main/java/com/example/ui/screens/LoginScreen.kt', 'r') as f:
    content2 = f.read()

target2 = """                    Icon(
                        imageVector = Icons.Rounded.PlayCircle,
                        contentDescription = "Logo",
                        tint = NeonGreen,
                        modifier = Modifier.size(36.dp)
                    )"""
replace2 = """                    Icon(
                        painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.ic_logo),
                        contentDescription = "Logo",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(54.dp)
                    )"""
content2 = content2.replace(target2, replace2)

with open('/app/applet/app/src/main/java/com/example/ui/screens/LoginScreen.kt', 'w') as f:
    f.write(content2)

