import re

with open('/app/applet/app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'r') as f:
    player = f.read()

target = """                            if (channel.type == "SERIES") {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Start
                                ) {
                                    Button(
                                        onClick = { showChannelsList = !showChannelsList },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    ) {
                                        Icon(imageVector = Icons.Rounded.List, contentDescription = "Episódios", tint = NeonGreen)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Episódios", color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }"""

player = player.replace(target, "")

with open('/app/applet/app/src/main/java/com/example/ui/player/CustomIPTVPlayer.kt', 'w') as f:
    f.write(player)
