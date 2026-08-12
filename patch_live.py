import re

with open('/app/applet/app/src/main/java/com/example/ui/screens/LiveTVScreen.kt', 'r') as f:
    content = f.read()

target1 = """                    if (uiState is IPTVUiState.Loading) {
                        NeonLoadingSkeleton(isLandscape)
                    } else if (filteredChannels.isEmpty()) {"""
replace1 = """                    if (filteredChannels.isEmpty() && uiState !is IPTVUiState.Loading) {"""
content = content.replace(target1, replace1)

# Add overlay at end of Box/Column
target2 = """            }
        }
    }
}

@Composable
fun LiveChannelCard"""
replace2 = """            }
        }
        NeonLoadingOverlay(uiState is IPTVUiState.Loading)
    }
}

@Composable
fun LiveChannelCard"""
content = content.replace(target2, replace2)

with open('/app/applet/app/src/main/java/com/example/ui/screens/LiveTVScreen.kt', 'w') as f:
    f.write(content)
