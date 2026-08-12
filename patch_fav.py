import re

with open('/app/applet/app/src/main/java/com/example/ui/screens/FavoritesScreen.kt', 'r') as f:
    content = f.read()

if "import com.example.ui.components.FallbackAsyncImage" not in content:
    content = content.replace("import coil.compose.SubcomposeAsyncImage", "import coil.compose.SubcomposeAsyncImage\nimport com.example.ui.components.FallbackAsyncImage")

target = """                SubcomposeAsyncImage(
                    model = channel.logo,
                    contentDescription = channel.name,
                    contentScale = if (channel.type == "LIVE") ContentScale.Fit else ContentScale.Crop,
                    modifier = Modifier.fillMaxSize().padding(if (channel.type == "LIVE") 12.dp else 0.dp),
                    error = {
                        Box(modifier = Modifier.fillMaxSize().background(Color.DarkGray), contentAlignment = Alignment.Center) {
                            Icon(imageVector = Icons.Rounded.Movie, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(48.dp))
                        }
                    }
                )"""

replace = """                FallbackAsyncImage(
                    channel = channel,
                    contentScale = if (channel.type == "LIVE") ContentScale.Fit else ContentScale.Crop,
                    modifier = Modifier.fillMaxSize().padding(if (channel.type == "LIVE") 12.dp else 0.dp)
                )"""

content = content.replace(target, replace)

with open('/app/applet/app/src/main/java/com/example/ui/screens/FavoritesScreen.kt', 'w') as f:
    f.write(content)
