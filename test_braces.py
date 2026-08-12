with open('app/src/main/java/com/example/ui/screens/MoviesScreen.kt', 'r') as f:
    content = f.read()

lines = content.split('\n')
stack = []
for i, line in enumerate(lines):
    for char in line:
        if char == '{': stack.append(i)
        elif char == '}': 
            if len(stack) > 0: stack.pop()
    if 'fun MetadataLine(label' in line:
        print(f"MetadataLine found at line {i+1}. Current stack depth: {len(stack)}")
