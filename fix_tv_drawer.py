with open('app/src/main/java/com/example/ui/screens/MainDashboard.kt', 'r') as f:
    content = f.read()

import re

old_drawer = """                Column {
                    Text(
                        text = "BEM-VINDO AO",
                        color = Color.Gray,
                        fontSize = 11.sp,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "UnlockT3am",
                        color = NeonGreen,
                        fontFamily = com.example.ui.theme.RussoOne,
                        fontSize = 20.sp,
                        letterSpacing = 1.5.sp
                    )
                }"""

new_drawer = """                Column {
                    if (com.example.util.DeviceUtil.isTv(androidx.compose.ui.platform.LocalContext.current)) {
                        Text(
                            text = "Conta Ativa:",
                            color = Color.Gray,
                            fontSize = 11.sp,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = viewModel.activeAccount.collectAsState().value?.username ?: "Nenhuma",
                            color = NeonGreen,
                            fontFamily = com.example.ui.theme.RussoOne,
                            fontSize = 16.sp,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Exp: " + viewModel.accountExpiration.collectAsState().value,
                            color = Color.White,
                            fontSize = 12.sp,
                            letterSpacing = 1.sp
                        )
                    } else {
                        Text(
                            text = "BEM-VINDO AO",
                            color = Color.Gray,
                            fontSize = 11.sp,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Unlock Player",
                            color = NeonGreen,
                            fontFamily = com.example.ui.theme.RussoOne,
                            fontSize = 20.sp,
                            letterSpacing = 1.5.sp
                        )
                    }
                }"""

content = content.replace(old_drawer, new_drawer)
with open('app/src/main/java/com/example/ui/screens/MainDashboard.kt', 'w') as f:
    f.write(content)
