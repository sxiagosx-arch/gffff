with open('app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'r') as f:
    content = f.read()

import re

# Fix top right buttons (Lock, Volume, Cast, Aspect Ratio, Settings) to only show on Mobile?
# Wait, TV might need settings or just hide them?
# "o player dentro do modo tv ainda esta com todos botoes do modo celular nao foi removido nenhum botao quando esta no modo tv"
# Let's hide the top right action row in TV mode, EXCEPT EPG icon? No, they probably don't need any top-right touch buttons in TV mode because they use the remote (D-PAD UP/DOWN/LEFT/RIGHT, OK).

# Center play/pause, prev, next buttons
old_center = """                    // CENTER PLAYBACK / CHANNELS SWITCHERS
                    if (deviceLayoutMode != "TV") {"""

new_center = """                    // CENTER PLAYBACK / CHANNELS SWITCHERS
                    if (deviceLayoutMode != "TV") {"""
# already there? Let's check. 
