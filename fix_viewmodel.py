with open('app/src/main/java/com/example/ui/IPTVViewModel.kt', 'r') as f:
    content = f.read()

import re

new_error = """    fun showError(message: String) {
        _uiState.value = IPTVUiState.Error(message)
    }

    fun clearError() {"""

content = content.replace("    fun clearError() {", new_error)

with open('app/src/main/java/com/example/ui/IPTVViewModel.kt', 'w') as f:
    f.write(content)
