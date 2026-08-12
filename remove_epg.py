with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'r') as f:
    content = f.read()

import re

# Remove showEPG state
content = re.sub(r'    var showEPG by remember \{ mutableStateOf\(false\) \}\n', '', content)

# Remove EPG from tap gesture
content = content.replace("                        if (showChannelsList || showEPG) {\n                            showChannelsList = false\n                            showEPG = false\n                        } else {", "                        if (showChannelsList) {\n                            showChannelsList = false\n                        } else {")

# Remove EPG button from top bar
epg_btn_pattern = r'                            if \(channel\.type == "LIVE" && deviceLayoutMode != "MOBILE"\) \{\n                                IconButton\(onClick = \{ showEPG = !showEPG \}\) \{\n                                    Icon\(\n                                        imageVector = Icons\.Rounded\.DateRange,\n                                        contentDescription = "Guia EPG",\n                                        tint = if \(showEPG\) NeonGreen else Color\.White\n                                    \)\n                                \}\n                            \}\n'
content = re.sub(epg_btn_pattern, '', content)

# Remove the EPG AnimatedVisibility block at the end
# The block starts around line 1274: AnimatedVisibility( visible = !isLocked && showEPG && epgList.isNotEmpty(), ...
epg_panel_pattern = r'        // EPG Panel\n        AnimatedVisibility\([\s\S]*?\} // End EPG Panel\n'
# Let's check how the EPG panel is actually formatted
