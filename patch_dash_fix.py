import re

with open('/app/applet/app/src/main/java/com/example/ui/screens/MainDashboard.kt', 'r') as f:
    content = f.read()

target = """            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable"""
replace = """            Spacer(modifier = Modifier.height(40.dp))
        }
    }
    NeonLoadingOverlay(uiState is IPTVUiState.Loading)
    }
}

@Composable"""
content = content.replace(target, replace)

with open('/app/applet/app/src/main/java/com/example/ui/screens/MainDashboard.kt', 'w') as f:
    f.write(content)
