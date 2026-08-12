import re

with open('/app/applet/app/src/main/java/com/example/ui/screens/MoviesScreen.kt', 'r') as f:
    content = f.read()

target1 = """                if (uiState is IPTVUiState.Loading) {
                    NeonLoadingSkeleton(isLandscape)
                } else if (filteredMovies.isEmpty()) {"""
replace1 = """                if (filteredMovies.isEmpty() && uiState !is IPTVUiState.Loading) {"""
content = content.replace(target1, replace1)

# Add overlay at end of Box
target2 = """    if (selectedMovie != null) {
        MovieDetailsSheet(
            movie = selectedMovie!!,"""
replace2 = """    NeonLoadingOverlay(uiState is IPTVUiState.Loading)

    if (selectedMovie != null) {
        MovieDetailsSheet(
            movie = selectedMovie!!,"""
content = content.replace(target2, replace2)

with open('/app/applet/app/src/main/java/com/example/ui/screens/MoviesScreen.kt', 'w') as f:
    f.write(content)
