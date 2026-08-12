import re

def patch_file(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    target = """        colors = CardDefaults.cardColors(containerColor = Charcoal),
        border = BorderStroke(1.dp, NeonGreen.copy(alpha = 0.3f)),
        colors = CardDefaults.cardColors(containerColor = Charcoal)"""
    replace = """        colors = CardDefaults.cardColors(containerColor = Charcoal),
        border = BorderStroke(1.dp, NeonGreen.copy(alpha = 0.3f))"""
    content = content.replace(target, replace)
    
    target2 = """        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Charcoal),
        colors = CardDefaults.cardColors(containerColor = Color.DarkGray.copy(alpha = 0.5f))"""
    replace2 = """        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Charcoal)"""
    content = content.replace(target2, replace2)

    with open(filepath, 'w') as f:
        f.write(content)

patch_file('/app/applet/app/src/main/java/com/example/ui/screens/MoviesScreen.kt')
patch_file('/app/applet/app/src/main/java/com/example/ui/screens/SeriesScreen.kt')
patch_file('/app/applet/app/src/main/java/com/example/ui/screens/MainDashboard.kt')
patch_file('/app/applet/app/src/main/java/com/example/ui/screens/LiveTVScreen.kt')
