import re

def patch_file(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    # Replace Icon with Image and ic_logo with logo_img
    content = content.replace("id = com.example.R.drawable.ic_logo", "id = com.example.R.drawable.logo_img")
    
    content = content.replace("androidx.compose.material3.Icon(\n                    painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.logo_img),",
                              "androidx.compose.foundation.Image(\n                    painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.logo_img),")
    content = content.replace("Icon(\n                        painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.logo_img),",
                              "androidx.compose.foundation.Image(\n                        painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.logo_img),")
    content = content.replace("Icon(\n                    painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.logo_img),",
                              "androidx.compose.foundation.Image(\n                    painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.logo_img),")

    # Remove tint = Color.Unspecified, as Image doesn't need it (though it might have colorFilter)
    content = content.replace("                    tint = Color.Unspecified,\n", "")
    content = content.replace("                        tint = Color.Unspecified,\n", "")

    with open(filepath, 'w') as f:
        f.write(content)

patch_file('/app/applet/app/src/main/java/com/example/ui/screens/SplashScreen.kt')
patch_file('/app/applet/app/src/main/java/com/example/ui/screens/LoginScreen.kt')
patch_file('/app/applet/app/src/main/java/com/example/ui/screens/MainDashboard.kt')
