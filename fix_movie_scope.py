with open('app/src/main/java/com/example/ui/screens/MoviesScreen.kt', 'r') as f:
    content = f.read()

import re

# Move MetadataLine outside
parts = content.split('@Composable\nfun MetadataLine(label: String, value: String) {')
part1 = parts[0]
part2 = parts[1]

# balance part1
open_b = part1.count('{')
close_b = part1.count('}')
if open_b > close_b:
    part1 += '\n}' * (open_b - close_b)

new_content = part1 + '\n\n@Composable\nfun MetadataLine(label: String, value: String) {' + part2
with open('app/src/main/java/com/example/ui/screens/MoviesScreen.kt', 'w') as f:
    f.write(new_content)
