import re

with open('/app/applet/app/src/main/java/com/example/ui/screens/MainDashboard.kt', 'r') as f:
    content = f.read()

# Remove the first sports section
target = """        // Esportes / Jogos do Dia
        val sportChannels = channels.filter { it.type == "LIVE" && (it.categoryName.contains("sport", true) || it.categoryName.contains("esporte", true) || it.name.contains("sport", true)) }.take(6)
        if (sportChannels.isNotEmpty()) {
            DashboardSectionHeader("Esportes & Jogos do Dia") { viewModel.navigateTo(Screen.LIVE_TV) }
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(sportChannels) { ch ->
                    LiveSpotlightCard(channel = ch) { viewModel.selectChannel(ch) }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }"""
        
content = content.replace(target, "")

with open('/app/applet/app/src/main/java/com/example/ui/screens/MainDashboard.kt', 'w') as f:
    f.write(content)
