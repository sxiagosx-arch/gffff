with open('app/src/main/java/com/example/ui/screens/MainDashboard.kt', 'r') as f:
    content = f.read()

import re

old_search = """        // Global Search Bar
        var isSearchEditable by remember { mutableStateOf(false) }
        val focusRequester = remember { androidx.compose.ui.focus.FocusRequester() }
        OutlinedTextField(
            value = globalSearchQuery,
            onValueChange = { globalSearchQuery = it },
            readOnly = !isSearchEditable,
            placeholder = { Text("Pesquisar filmes, séries e canais...", color = Color.Gray) },
            leadingIcon = { Icon(imageVector = Icons.Rounded.Search, contentDescription = "Search", tint = NeonGreen) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonGreen,
                unfocusedBorderColor = Charcoal,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = NeonGreen,
                focusedContainerColor = Charcoal,
                unfocusedContainerColor = Charcoal
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .focusRequester(focusRequester)
                .onFocusChanged { if (!it.isFocused) isSearchEditable = false }
                .clickable { isSearchEditable = true; focusRequester.requestFocus() }
        )"""

new_search = """        // Global Search Bar
        val isTv = com.example.util.DeviceUtil.isTv(androidx.compose.ui.platform.LocalContext.current)
        var isSearchEditable by remember { mutableStateOf(false) }
        val focusRequester = remember { androidx.compose.ui.focus.FocusRequester() }
        OutlinedTextField(
            value = globalSearchQuery,
            onValueChange = { globalSearchQuery = it },
            readOnly = if (isTv) !isSearchEditable else false,
            placeholder = { Text("Pesquisar filmes, séries e canais...", color = Color.Gray) },
            leadingIcon = { Icon(imageVector = Icons.Rounded.Search, contentDescription = "Search", tint = NeonGreen) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonGreen,
                unfocusedBorderColor = Charcoal,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = NeonGreen,
                focusedContainerColor = Charcoal,
                unfocusedContainerColor = Charcoal
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .then(if (isTv) Modifier.focusRequester(focusRequester).onFocusChanged { if (!it.isFocused) isSearchEditable = false }.clickable { isSearchEditable = true; focusRequester.requestFocus() } else Modifier)
        )"""

content = content.replace(old_search, new_search)

with open('app/src/main/java/com/example/ui/screens/MainDashboard.kt', 'w') as f:
    f.write(content)
