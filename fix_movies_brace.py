with open('app/src/main/java/com/example/ui/screens/MoviesScreen.kt', 'r') as f:
    content = f.read()

# Let's count open/close braces
open_b = content.count('{')
close_b = content.count('}')
if close_b > open_b:
    content = content.replace('}\n}\n}\n}\n', '}\n}\n}\n', 1)

with open('app/src/main/java/com/example/ui/screens/MoviesScreen.kt', 'w') as f:
    f.write(content)
