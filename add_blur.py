import re
import glob

def add_blur(filepath, is_dashboard=False):
    with open(filepath, 'r') as f:
        content = f.read()
    
    if "import androidx.compose.ui.draw.blur" not in content:
        content = content.replace("import androidx.compose.ui.draw.clip", "import androidx.compose.ui.draw.clip\nimport androidx.compose.ui.draw.blur")
    
    if is_dashboard:
        target = """    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MatteBlack)
            .verticalScroll(scrollState)
            .testTag("main_dashboard_container")
    ) {"""
        replace = """    val blurRadius = if (uiState is IPTVUiState.Loading) 16.dp else 0.dp
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MatteBlack)
            .blur(blurRadius)
            .verticalScroll(scrollState)
            .testTag("main_dashboard_container")
    ) {"""
        content = content.replace(target, replace)
    else:
        # For Movies/Series/LiveTV it's inside a Box, and then there's a Column for the search bar, etc.
        # It's better to just blur the Column that is immediately inside the Box.
        # Wait, for Movies, Series, LiveTV we just replaced the `target1` which was `if (filtered....isEmpty() && uiState !is IPTVUiState.Loading)`.
        # The main Column is:
        target2 = """    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag"""
        replace2 = """    val blurRadius = if (uiState is IPTVUiState.Loading) 16.dp else 0.dp
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag"""
        content = content.replace(target2, replace2)
        
        # Then inside the Box, the first Column has `modifier = Modifier.fillMaxSize()`
        target3 = """    ) {
        Column(modifier = Modifier.fillMaxSize()) {"""
        replace3 = """    ) {
        Column(modifier = Modifier.fillMaxSize().blur(blurRadius)) {"""
        content = content.replace(target3, replace3)
        
        # In SeriesScreen.kt, the Box also has the if (selectedSeries == null) and then Row. So blurring the Column is not enough.
        # Let's blur the Row instead for SeriesScreen.
        target4 = """            // MAIN SERIES VIEW
            Row(modifier = Modifier.fillMaxSize()) {"""
        replace4 = """            // MAIN SERIES VIEW
            Row(modifier = Modifier.fillMaxSize().blur(blurRadius)) {"""
        content = content.replace(target4, replace4)

    with open(filepath, 'w') as f:
        f.write(content)

add_blur('/app/applet/app/src/main/java/com/example/ui/screens/MainDashboard.kt', is_dashboard=True)
add_blur('/app/applet/app/src/main/java/com/example/ui/screens/MoviesScreen.kt')
add_blur('/app/applet/app/src/main/java/com/example/ui/screens/SeriesScreen.kt')
add_blur('/app/applet/app/src/main/java/com/example/ui/screens/LiveTVScreen.kt')
