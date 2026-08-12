import re

with open('app/src/main/java/com/example/ui/screens/ParentalControlScreen.kt', 'r') as f:
    content = f.read()

target = """                Text(
                    text = "As categorias marcadas serão ocultadas e exigirão a digitação do PIN para serem reproduzidas.",
                    color = Color.Gray,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )"""

replace = """                Text(
                    text = "As categorias marcadas serão ocultadas e exigirão a digitação do PIN para serem reproduzidas.",
                    color = Color.Gray,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                val blockAdult by viewModel.blockAdult.collectAsState()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Charcoal)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Bloquear Conteúdo Adulto (+18)", color = Color.White, fontWeight = FontWeight.Bold)
                        Text(text = "Oculta automaticamente todas as categorias e canais adultos.", color = Color.Gray, fontSize = 11.sp)
                    }
                    Switch(
                        checked = blockAdult,
                        onCheckedChange = { viewModel.setBlockAdult(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = NeonGreen,
                            checkedTrackColor = NeonGreenDim,
                            uncheckedThumbColor = Color.Gray,
                            uncheckedTrackColor = Color.DarkGray
                        )
                    )
                }"""

content = content.replace(target, replace)

with open('app/src/main/java/com/example/ui/screens/ParentalControlScreen.kt', 'w') as f:
    f.write(content)
