import re
import os

def patch_file(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    # 1. Add animateItem to cards
    if "fun MovieCardItem(movie: IPTVChannel, onClick: () -> Unit)" in content:
        content = content.replace(
            "fun MovieCardItem(movie: IPTVChannel, onClick: () -> Unit) {",
            "fun MovieCardItem(movie: IPTVChannel, modifier: Modifier = Modifier, onClick: () -> Unit) {"
        )
        content = content.replace(
            """    Card(
        modifier = Modifier""",
            """    Card(
        modifier = modifier"""
        )
        content = content.replace("items(filteredMovies) { mv ->", "items(items = filteredMovies, key = { it.id }) { mv ->")
        content = content.replace("MovieCardItem(movie = mv) {", "MovieCardItem(movie = mv, modifier = Modifier.animateItem()) {")

    if "fun SeriesCardItem(series: IPTVSeries, onClick: () -> Unit)" in content:
        content = content.replace(
            "fun SeriesCardItem(series: IPTVSeries, onClick: () -> Unit) {",
            "fun SeriesCardItem(series: IPTVSeries, modifier: Modifier = Modifier, onClick: () -> Unit) {"
        )
        content = content.replace(
            """    Card(
        modifier = Modifier""",
            """    Card(
        modifier = modifier"""
        )
        content = content.replace("items(filteredSeries) { ser ->", "items(items = filteredSeries, key = { it.id }) { ser ->")
        content = content.replace("SeriesCardItem(series = ser) {", "SeriesCardItem(series = ser, modifier = Modifier.animateItem()) {")

    if "fun LiveChannelCard(channel: IPTVChannel, onClick: () -> Unit)" in content:
        content = content.replace(
            "fun LiveChannelCard(channel: IPTVChannel, onClick: () -> Unit) {",
            "fun LiveChannelCard(channel: IPTVChannel, modifier: Modifier = Modifier, onClick: () -> Unit) {"
        )
        content = content.replace(
            """    Card(
        modifier = Modifier""",
            """    Card(
        modifier = modifier"""
        )
        content = content.replace("items(filteredChannels) { ch ->", "items(items = filteredChannels, key = { it.id }) { ch ->")
        content = content.replace("LiveChannelCard(channel = ch) {", "LiveChannelCard(channel = ch, modifier = Modifier.animateItem()) {")
        
        # Also in LiveTVScreen categories
        content = content.replace("items(liveCategories) { cat ->", "items(items = liveCategories, key = { it.id }) { cat ->")

    # In MoviesScreen categories
    content = content.replace("items(movieCategories) { cat ->", "items(items = movieCategories, key = { it.id }) { cat ->")
    # In SeriesScreen categories
    content = content.replace("items(sortedCategories) { cat ->", "items(items = sortedCategories, key = { it.id }) { cat ->")
    
    # In MainDashboard
    if "ContinueWatchingCard(historyItem = hist)" in content:
        content = content.replace("items(watchHistory) { hist ->", "items(items = watchHistory, key = { it.streamId }) { hist ->")
        content = content.replace("ContinueWatchingCard(historyItem = hist) {", "ContinueWatchingCard(historyItem = hist, modifier = Modifier.width(200.dp).animateItem()) {")
        
    if "FavoriteGridCard(favorite = fav)" in content:
        content = content.replace("items(favorites) { fav ->", "items(items = favorites, key = { it.streamId }) { fav ->")
        content = content.replace("FavoriteGridCard(favorite = fav) {", "FavoriteGridCard(favorite = fav, modifier = Modifier.width(160.dp).animateItem()) {")

    # Spotlight cards inside Dashboard
    if "LiveSpotlightCard(channel = ch) {" in content:
        content = content.replace("LiveSpotlightCard(channel = ch) {", "LiveSpotlightCard(channel = ch, modifier = Modifier.width(160.dp).animateItem()) {")
        content = content.replace("items(channels.filter { it.type == \"LIVE\" }.take(10)) { ch ->", "items(items = channels.filter { it.type == \"LIVE\" }.take(10), key = { it.id }) { ch ->")
    if "MovieSpotlightCard(movie = mv) {" in content:
        content = content.replace("MovieSpotlightCard(movie = mv) {", "MovieSpotlightCard(movie = mv, modifier = Modifier.width(140.dp).animateItem()) {")
        content = content.replace("items(channels.filter { it.type == \"MOVIE\" }.take(10)) { mv ->", "items(items = channels.filter { it.type == \"MOVIE\" }.take(10), key = { it.id }) { mv ->")
    if "SeriesSpotlightCard(series = ser) {" in content:
        content = content.replace("SeriesSpotlightCard(series = ser) {", "SeriesSpotlightCard(series = ser, modifier = Modifier.width(140.dp).animateItem()) {")
        content = content.replace("items(seriesList.take(10)) { ser ->", "items(items = seriesList.take(10), key = { it.id }) { ser ->")

    # Ensure animateItem doesn't break if signature not updated (we update signatures below for dashboard)
    if "fun LiveSpotlightCard(channel: IPTVChannel, onClick: () -> Unit)" in content:
        content = content.replace("fun LiveSpotlightCard(channel: IPTVChannel, onClick: () -> Unit)", "fun LiveSpotlightCard(channel: IPTVChannel, modifier: Modifier = Modifier.width(160.dp), onClick: () -> Unit)")
        content = content.replace("    Box(\n        modifier = Modifier\n            .width(160.dp)", "    Box(\n        modifier = modifier")
    
    if "fun MovieSpotlightCard(movie: IPTVChannel, onClick: () -> Unit)" in content:
        content = content.replace("fun MovieSpotlightCard(movie: IPTVChannel, onClick: () -> Unit)", "fun MovieSpotlightCard(movie: IPTVChannel, modifier: Modifier = Modifier.width(140.dp), onClick: () -> Unit)")
        content = content.replace("    Box(\n        modifier = Modifier\n            .width(140.dp)", "    Box(\n        modifier = modifier")

    if "fun SeriesSpotlightCard(series: IPTVSeries, onClick: () -> Unit)" in content:
        content = content.replace("fun SeriesSpotlightCard(series: IPTVSeries, onClick: () -> Unit)", "fun SeriesSpotlightCard(series: IPTVSeries, modifier: Modifier = Modifier.width(140.dp), onClick: () -> Unit)")
        content = content.replace("    Box(\n        modifier = Modifier\n            .width(140.dp)", "    Box(\n        modifier = modifier")
        
    if "fun FavoriteGridCard(favorite: Favorite, onClick: () -> Unit)" in content:
        content = content.replace("fun FavoriteGridCard(favorite: Favorite, onClick: () -> Unit)", "fun FavoriteGridCard(favorite: Favorite, modifier: Modifier = Modifier.width(160.dp), onClick: () -> Unit)")
        content = content.replace("    Box(\n        modifier = Modifier\n            .width(160.dp)", "    Box(\n        modifier = modifier")

    with open(filepath, 'w') as f:
        f.write(content)

patch_file('/app/applet/app/src/main/java/com/example/ui/screens/MoviesScreen.kt')
patch_file('/app/applet/app/src/main/java/com/example/ui/screens/SeriesScreen.kt')
patch_file('/app/applet/app/src/main/java/com/example/ui/screens/LiveTVScreen.kt')
patch_file('/app/applet/app/src/main/java/com/example/ui/screens/MainDashboard.kt')
