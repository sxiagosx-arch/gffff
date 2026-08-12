import re

def patch_file(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    target = """    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("movie_card"),
        shape = RoundedCornerShape(16.dp),"""
    replace = """    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("movie_card"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Charcoal),"""
    content = content.replace(target, replace)
    
    target_series = """    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("series_card"),
        shape = RoundedCornerShape(16.dp),"""
    replace_series = """    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("series_card"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Charcoal),"""
    content = content.replace(target_series, replace_series)
    
    target_dashboard = """    Card(
        modifier = Modifier
            .width(160.dp)
            .height(240.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),"""
    replace_dashboard = """    Card(
        modifier = Modifier
            .width(160.dp)
            .height(240.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Charcoal),"""
    content = content.replace(target_dashboard, replace_dashboard)

    target_live = """    Card(
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { isFocused = it.isFocused }
            .clickable { onClick() }
            .border(
                width = if (isSelected) 2.dp else if (isFocused) 2.dp else 0.dp,
                color = if (isSelected) NeonGreen else if (isFocused) Color.White else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.DarkGray.copy(alpha = 0.5f))
    )"""
    replace_live = """    Card(
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { isFocused = it.isFocused }
            .clickable { onClick() }
            .border(
                width = if (isSelected) 2.dp else if (isFocused) 2.dp else 0.dp,
                color = if (isSelected) NeonGreen else if (isFocused) Color.White else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Charcoal)
    )"""
    content = content.replace(target_live, replace_live)

    with open(filepath, 'w') as f:
        f.write(content)

patch_file('/app/applet/app/src/main/java/com/example/ui/screens/MoviesScreen.kt')
patch_file('/app/applet/app/src/main/java/com/example/ui/screens/SeriesScreen.kt')
patch_file('/app/applet/app/src/main/java/com/example/ui/screens/MainDashboard.kt')
patch_file('/app/applet/app/src/main/java/com/example/ui/screens/LiveTVScreen.kt')
