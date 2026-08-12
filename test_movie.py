with open('app/src/main/java/com/example/ui/screens/MoviesScreen.kt', 'r') as f:
    content = f.read()

import re

# I might have removed it but let's check its scope.
print(content[content.find('MetadataLine(label ='):])
