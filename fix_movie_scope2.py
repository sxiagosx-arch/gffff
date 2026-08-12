with open('app/src/main/java/com/example/ui/screens/MoviesScreen.kt', 'r') as f:
    content = f.read()

import re

# find @Composable\nfun MetadataLine
parts = content.split('@Composable\nfun MetadataLine(label: String, value: String) {')
part1 = parts[0]
part2 = parts[1]

# Balance part1
open_b = part1.count('{')
close_b = part1.count('}')
if open_b > close_b:
    part1 += '\n}' * (open_b - close_b)

# Balance part2
open_b2 = part2.count('{')
close_b2 = part2.count('}')
if close_b2 > open_b2:
    # remove trailing braces
    for _ in range(close_b2 - open_b2):
        part2 = part2.rsplit('}', 1)
        part2 = "".join(part2)

new_content = part1 + '\n\n@Composable\nfun MetadataLine(label: String, value: String) {' + part2
with open('app/src/main/java/com/example/ui/screens/MoviesScreen.kt', 'w') as f:
    f.write(new_content)
