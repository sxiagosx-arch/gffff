import re

with open('app/src/main/java/com/example/ui/theme/Type.kt', 'r') as f:
    content = f.read()

target = """import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight"""
replace = """import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.R

val RussoOne = FontFamily(
    Font(R.font.russo_one)
)"""
content = content.replace(target, replace)

with open('app/src/main/java/com/example/ui/theme/Type.kt', 'w') as f:
    f.write(content)
