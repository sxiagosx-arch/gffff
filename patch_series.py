import re

with open('/app/applet/app/src/main/java/com/example/ui/screens/SeriesScreen.kt', 'r') as f:
    content = f.read()

target1 = """                    if (uiState is IPTVUiState.Loading) {
                        NeonLoadingSkeleton(isLandscape)
                    } else if (filteredSeries.isEmpty()) {"""
replace1 = """                    if (filteredSeries.isEmpty() && uiState !is IPTVUiState.Loading) {"""
content = content.replace(target1, replace1)

# Add overlay at end of Box. 
# Box ends right before `        }` that ends the `SeriesScreen` composable.
# Let's find:
target2 = """            }
        }
    }
}

@Composable
fun SeriesCardItem(series: IPTVSeries, onClick: () -> Unit) {"""
replace2 = """            }
        }
        NeonLoadingOverlay(uiState is IPTVUiState.Loading)
    }
}

@Composable
fun SeriesCardItem(series: IPTVSeries, onClick: () -> Unit) {"""
content = content.replace(target2, replace2)

with open('/app/applet/app/src/main/java/com/example/ui/screens/SeriesScreen.kt', 'w') as f:
    f.write(content)
