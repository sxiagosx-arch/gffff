with open('app/src/main/java/com/example/ui/screens/MoviesScreen.kt', 'r') as f:
    content = f.read()

lines = content.split('\n')
stack = []
for i, line in enumerate(lines):
    for char in line:
        if char == '{': stack.append(i)
        elif char == '}': 
            if len(stack) > 0: stack.pop()
    if 'fun MovieDetailsSheet' in line:
        print(f"MovieDetailsSheet at {i+1} depth {len(stack)}")
print(f"End depth {len(stack)}")
