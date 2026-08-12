import re

def patch_file(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    target = """            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(GraySurface)"""
            
    replace = """            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(com.example.ui.theme.MatteBlack)"""
            
    content = content.replace(target, replace)
    
    with open(filepath, 'w') as f:
        f.write(content)

patch_file('/app/applet/app/src/main/java/com/example/ui/screens/MoviesScreen.kt')
patch_file('/app/applet/app/src/main/java/com/example/ui/screens/SeriesScreen.kt')
