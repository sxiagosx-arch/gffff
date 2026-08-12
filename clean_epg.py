with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'r') as f:
    lines = f.readlines()

new_lines = []
skip = False
for line in lines:
    if "var showEPG by remember" in line:
        continue
    if "showEPG = false" in line:
        continue
    if "if (showChannelsList || showEPG)" in line:
        new_lines.append(line.replace(" || showEPG", ""))
        continue
    if 'if (channel.type == "LIVE" && deviceLayoutMode != "MOBILE") {' in line:
        # Check next line to see if it's the EPG button
        pass
    
    new_lines.append(line)

with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'w') as f:
    f.writelines(new_lines)
