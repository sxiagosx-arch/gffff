with open('app/src/main/java/com/example/ui/screens/MoviesScreen.kt', 'r') as f:
    content = f.read()

import re

# Just to be safe, I'll remove any trailing braces and count them again accurately
content = re.sub(r'\}\s*$', '', content)
content = re.sub(r'\}\s*$', '', content)
content = re.sub(r'\}\s*$', '', content)
content = re.sub(r'\}\s*$', '', content)
content = re.sub(r'\}\s*$', '', content)

open_b = content.count('{')
close_b = content.count('}')
if open_b > close_b:
    content += '\n}' * (open_b - close_b)

with open('app/src/main/java/com/example/ui/screens/MoviesScreen.kt', 'w') as f:
    f.write(content)
