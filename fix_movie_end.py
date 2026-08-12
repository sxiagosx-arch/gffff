with open('app/src/main/java/com/example/ui/screens/MoviesScreen.kt', 'r') as f:
    content = f.read()

content = content.replace('fun // MetadataLine', 'fun MetadataLine')
content = content.replace('// MetadataLine(label =', 'MetadataLine(label =')

# Remove duplicate MetadataLine definition
import re
# We have two MetadataLine definitions now.
content = re.sub(r'@androidx\.compose\.runtime\.Composable\nfun MetadataLine\(icon.*?\}', '', content, flags=re.DOTALL)
# One more bracket fix
content = re.sub(r'\}\s*\}\s*$', '}', content)

with open('app/src/main/java/com/example/ui/screens/MoviesScreen.kt', 'w') as f:
    f.write(content)
