with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'r') as f:
    content = f.read()

import re

# Fix 1: Make episode items focusable and focusable logic.
# Replace: .clickable { onChannelChange(ch) }
# With: .focusable().clickable { onChannelChange(ch) } in the LazyRow

old_ep_box = """                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSelected) NeonGreen else Color.Black.copy(alpha = 0.65f))
                                            .border(1.dp, NeonGreen, RoundedCornerShape(8.dp))
                                            .clickable { onChannelChange(ch) },"""

new_ep_box = """                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSelected) NeonGreen else Color.Black.copy(alpha = 0.65f))
                                            .border(1.dp, NeonGreen, RoundedCornerShape(8.dp))
                                            .clickable { onChannelChange(ch) },"""
# wait, actually clickable is already focusable. But if we need it to highlight on focus:
# we can use `.onFocusChanged { if(it.isFocused) { ... } }` or just rely on focus styling if any.
