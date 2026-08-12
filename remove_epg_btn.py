with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'r') as f:
    content = f.read()

import re

epg_btn_pattern = r'                            if \(channel\.type == "LIVE" && deviceLayoutMode != "MOBILE"\) \{\n                                IconButton\(onClick = \{ showEPG = !showEPG \}\) \{\n                                    Icon\(\n                                        imageVector = Icons\.Rounded\.DateRange,\n                                        contentDescription = "Guia EPG",\n                                        tint = if \(showEPG\) NeonGreen else Color\.White\n                                    \)\n                                \}\n                            \}\n'
content = re.sub(epg_btn_pattern, '', content)

with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'w') as f:
    f.write(content)
