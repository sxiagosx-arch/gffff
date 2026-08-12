with open('app/src/main/java/com/example/MainActivity.kt', 'r') as f:
    content = f.read()

import re

# Remove blur logic
content = re.sub(r'                val blurRadius by androidx\.compose\.animation\.core\.animateDpAsState\([\s\S]*?label = "blur"\n                \)', '', content)
content = content.replace('.blur(blurRadius)', '')

with open('app/src/main/java/com/example/MainActivity.kt', 'w') as f:
    f.write(content)
