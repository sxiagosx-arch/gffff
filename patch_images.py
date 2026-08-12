import os
import glob

def patch_file(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    # Import the FallbackAsyncImage
    if "import com.example.ui.components.FallbackAsyncImage" not in content:
        content = content.replace("import coil.compose.SubcomposeAsyncImage", "import coil.compose.SubcomposeAsyncImage\nimport com.example.ui.components.FallbackAsyncImage")

    # Pattern for MovieCardItem and SeriesCardItem
    target = """                SubcomposeAsyncImage(
                    model = channel.logo,
                    contentDescription = channel.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    error = {
                        Box(modifier = Modifier.fillMaxSize().background(Color.DarkGray), contentAlignment = Alignment.Center) {
                            Icon(imageVector = Icons.Rounded.Movie, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(32.dp))
                        }
                    }
                )"""
    replace = """                FallbackAsyncImage(
                    channel = channel,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )"""
    content = content.replace(target, replace)

    # For MovieDetailsSheet
    target2 = """                SubcomposeAsyncImage(
                    model = movie.logo,
                    contentDescription = movie.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(120.dp)
                        .height(180.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    error = {
                        Box(modifier = Modifier.fillMaxSize().background(Color.DarkGray), contentAlignment = Alignment.Center) {
                            Icon(imageVector = Icons.Rounded.Movie, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(48.dp))
                        }
                    }
                )"""
    replace2 = """                FallbackAsyncImage(
                    channel = movie,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(120.dp)
                        .height(180.dp)
                        .clip(RoundedCornerShape(8.dp))
                )"""
    content = content.replace(target2, replace2)
    
    # For Spotlight Card in Dashboard
    target3 = """                SubcomposeAsyncImage(
                    model = channel.logo,
                    contentDescription = channel.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    error = {
                        Box(modifier = Modifier.fillMaxSize().background(Color.DarkGray), contentAlignment = Alignment.Center) {
                            Icon(imageVector = Icons.Rounded.LiveTv, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(48.dp))
                        }
                    }
                )"""
    replace3 = """                FallbackAsyncImage(
                    channel = channel,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )"""
    content = content.replace(target3, replace3)
    
    # For Spotlight Card in Dashboard (Movie/Series)
    target4 = """                SubcomposeAsyncImage(
                    model = channel.logo,
                    contentDescription = channel.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    error = {
                        Box(modifier = Modifier.fillMaxSize().background(Color.DarkGray), contentAlignment = Alignment.Center) {
                            Icon(imageVector = Icons.Rounded.Movie, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(48.dp))
                        }
                    }
                )"""
    content = content.replace(target4, replace3)

    with open(filepath, 'w') as f:
        f.write(content)

patch_file('/app/applet/app/src/main/java/com/example/ui/screens/MoviesScreen.kt')
patch_file('/app/applet/app/src/main/java/com/example/ui/screens/SeriesScreen.kt')
patch_file('/app/applet/app/src/main/java/com/example/ui/screens/MainDashboard.kt')
