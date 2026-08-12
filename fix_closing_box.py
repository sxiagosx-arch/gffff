with open('app/src/main/java/com/example/ui/screens/MoviesScreen.kt', 'r') as f:
    content = f.read()

import re

old_end = """                color = Color.White,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }
    }
}"""

new_end = """                color = Color.White,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }
        }
    }
}"""

content = content.replace(old_end, new_end)

with open('app/src/main/java/com/example/ui/screens/MoviesScreen.kt', 'w') as f:
    f.write(content)
