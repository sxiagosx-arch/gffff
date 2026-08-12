import re

# 1. LiveTVScreen LiveChannelListItem signature check
with open('/app/applet/app/src/main/java/com/example/ui/screens/LiveTVScreen.kt', 'r') as f:
    livetv = f.read()

# Let's just fix it thoroughly
sig_target = """fun LiveChannelListItem(
    channel: IPTVChannel,
    isFav: Boolean,
    onToggleFav: () -> Unit,
    onClick: () -> Unit
)"""
sig_replace = """fun LiveChannelListItem(
    channel: IPTVChannel,
    isFav: Boolean,
    isSelected: Boolean = false,
    onToggleFav: () -> Unit,
    onClick: () -> Unit
)"""
livetv = livetv.replace(sig_target, sig_replace)

with open('/app/applet/app/src/main/java/com/example/ui/screens/LiveTVScreen.kt', 'w') as f:
    f.write(livetv)

# 2. SeriesScreen imports
with open('/app/applet/app/src/main/java/com/example/ui/screens/SeriesScreen.kt', 'r') as f:
    series = f.read()

if "import androidx.compose.foundation.lazy.LazyColumn" not in series:
    series = series.replace("import androidx.compose.foundation.lazy.LazyRow", "import androidx.compose.foundation.lazy.LazyRow\nimport androidx.compose.foundation.lazy.LazyColumn")

if "import androidx.compose.foundation.lazy.items" not in series:
    series = series.replace("import androidx.compose.foundation.lazy.LazyColumn", "import androidx.compose.foundation.lazy.LazyColumn\nimport androidx.compose.foundation.lazy.items")

with open('/app/applet/app/src/main/java/com/example/ui/screens/SeriesScreen.kt', 'w') as f:
    f.write(series)

