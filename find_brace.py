with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'r') as f:
    content = f.read()

lines = content.split('\n')
stack = []
for i, line in enumerate(lines):
    for char in line:
        if char == '{': stack.append(i)
        elif char == '}': 
            if len(stack) > 0: stack.pop()
            else: print(f'Unmatched }} at {i}: {line}')

print(f'Remaining in stack: {len(stack)}')
