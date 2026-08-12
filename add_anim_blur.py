import re

def update(filepath):
    with open(filepath, 'r') as f:
        content = f.read()
    
    if "import androidx.compose.animation.core.animateDpAsState" not in content:
        content = content.replace("import androidx.compose.ui.draw.blur", "import androidx.compose.ui.draw.blur\nimport androidx.compose.animation.core.animateDpAsState")
        
    target = "val blurRadius = if (uiState is IPTVUiState.Loading) 16.dp else 0.dp"
    replace = "val blurRadius by animateDpAsState(targetValue = if (uiState is IPTVUiState.Loading) 16.dp else 0.dp, label = \"blur\")"
    content = content.replace(target, replace)
    
    with open(filepath, 'w') as f:
        f.write(content)

update('/app/applet/app/src/main/java/com/example/ui/screens/MainDashboard.kt')
update('/app/applet/app/src/main/java/com/example/ui/screens/MoviesScreen.kt')
update('/app/applet/app/src/main/java/com/example/ui/screens/SeriesScreen.kt')
update('/app/applet/app/src/main/java/com/example/ui/screens/LiveTVScreen.kt')
