import re

with open('/app/applet/app/src/main/java/com/example/ui/screens/MainDashboard.kt', 'r') as f:
    content = f.read()

target = """            Column {
                Text(
                    text = "BEM-VINDO AO",
                    color = Color.Gray,
                    fontSize = 11.sp,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "UnlockT3am",
                    color = NeonGreen,
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    letterSpacing = 1.5.sp
                )
            }"""
replace = """            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.ic_logo),
                    contentDescription = "Logo",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "BEM-VINDO AO",
                        color = Color.Gray,
                        fontSize = 11.sp,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "UnlockT3am",
                        color = NeonGreen,
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        letterSpacing = 1.5.sp
                    )
                }
            }"""
content = content.replace(target, replace)

with open('/app/applet/app/src/main/java/com/example/ui/screens/MainDashboard.kt', 'w') as f:
    f.write(content)
