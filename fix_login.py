with open('app/src/main/java/com/example/ui/screens/LoginScreen.kt', 'r') as f:
    content = f.read()

import re

# Add listName field
old_config_title = """                    Text(
                        text = "Configurar Lista",
                        color = NeonGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    if (selectedTab == 0) {"""

new_config_title = """                    Text(
                        text = "Configurar Lista",
                        color = NeonGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    OutlinedTextField(
                        value = listName,
                        onValueChange = { listName = it },
                        label = { Text("Nome da Lista (ex: Minha TV)", color = Color.Gray) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonGreen,
                            unfocusedBorderColor = Color.DarkGray,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    )
                    if (selectedTab == 0) {"""

content = content.replace(old_config_title, new_config_title)

# Add Device ID Activation Tab
old_tabs = """                    TabItem(
                        title = "URL M3U",
                        selected = selectedTab == 1,
                        modifier = Modifier.weight(1f)
                    ) { selectedTab = 1 }
                }"""

new_tabs = """                    TabItem(
                        title = "URL M3U",
                        selected = selectedTab == 1,
                        modifier = Modifier.weight(1f)
                    ) { selectedTab = 1 }
                    TabItem(
                        title = "Ativação ID",
                        selected = selectedTab == 2,
                        modifier = Modifier.weight(1f)
                    ) { selectedTab = 2 }
                }"""

content = content.replace(old_tabs, new_tabs)

with open('app/src/main/java/com/example/ui/screens/LoginScreen.kt', 'w') as f:
    f.write(content)
