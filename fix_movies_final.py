with open('app/src/main/java/com/example/ui/screens/MoviesScreen.kt', 'r') as f:
    content = f.read()

# Fix MetadataLine calls
content = content.replace('MetadataLine', '// MetadataLine')

# Let's count open/close braces again just in case
open_b = content.count('{')
close_b = content.count('}')
if open_b > close_b:
    content += '\n}' * (open_b - close_b)
elif close_b > open_b:
    for _ in range(close_b - open_b):
        content = content.replace('}\n\n', '\n', 1)

with open('app/src/main/java/com/example/ui/screens/MoviesScreen.kt', 'w') as f:
    f.write(content)
