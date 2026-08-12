import re

def patch_splash():
    with open('app/src/main/java/com/example/ui/screens/SplashScreen.kt', 'r') as f:
        content = f.read()
    
    target = """                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                style = androidx.compose.ui.text.TextStyle("""
    
    replace = """                fontSize = 32.sp,
                fontFamily = com.example.ui.theme.RussoOne,
                letterSpacing = 2.sp,
                style = androidx.compose.ui.text.TextStyle("""
    
    content = content.replace(target, replace)
    with open('app/src/main/java/com/example/ui/screens/SplashScreen.kt', 'w') as f:
        f.write(content)

def patch_dash():
    with open('app/src/main/java/com/example/ui/screens/MainDashboard.kt', 'r') as f:
        content = f.read()
    
    target = """                    Text(
                        text = "UnlockT3am",
                        color = NeonGreen,
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        letterSpacing = 1.5.sp
                    )"""
    
    replace = """                    Text(
                        text = "UnlockT3am",
                        color = NeonGreen,
                        fontFamily = com.example.ui.theme.RussoOne,
                        fontSize = 20.sp,
                        letterSpacing = 1.5.sp
                    )"""
    
    content = content.replace(target, replace)
    with open('app/src/main/java/com/example/ui/screens/MainDashboard.kt', 'w') as f:
        f.write(content)

patch_splash()
patch_dash()
