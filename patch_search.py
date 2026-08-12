import os
import glob

def patch_file(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    # Search pattern for OutlinedTextField
    target1 = """                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar filme...", color = Color.Gray) },
                    leadingIcon = { Icon(imageVector = Icons.Rounded.Search, contentDescription = "Search", tint = Color.Gray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonGreen,
                        unfocusedBorderColor = Color.DarkGray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )"""
    
    replace1 = """                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar filme...", color = Color.Gray, fontSize = 14.sp) },
                    leadingIcon = { Icon(imageVector = Icons.Rounded.Search, contentDescription = "Search", tint = NeonGreen) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Charcoal,
                        unfocusedContainerColor = Charcoal,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = NeonGreen
                    ),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )"""
                
    target2 = """                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar série...", color = Color.Gray) },
                    leadingIcon = { Icon(imageVector = Icons.Rounded.Search, contentDescription = "Search", tint = Color.Gray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonGreen,
                        unfocusedBorderColor = Color.DarkGray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )"""
                
    replace2 = """                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar série...", color = Color.Gray, fontSize = 14.sp) },
                    leadingIcon = { Icon(imageVector = Icons.Rounded.Search, contentDescription = "Search", tint = NeonGreen) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Charcoal,
                        unfocusedContainerColor = Charcoal,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = NeonGreen
                    ),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )"""

    target3 = """                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar canal...", color = Color.Gray) },
                    leadingIcon = { Icon(imageVector = Icons.Rounded.Search, contentDescription = "Search", tint = Color.Gray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonGreen,
                        unfocusedBorderColor = Color.DarkGray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )"""
                
    replace3 = """                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar canal...", color = Color.Gray, fontSize = 14.sp) },
                    leadingIcon = { Icon(imageVector = Icons.Rounded.Search, contentDescription = "Search", tint = NeonGreen) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Charcoal,
                        unfocusedContainerColor = Charcoal,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = NeonGreen
                    ),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )"""

    content = content.replace(target1, replace1)
    content = content.replace(target2, replace2)
    content = content.replace(target3, replace3)
    
    with open(filepath, 'w') as f:
        f.write(content)

patch_file('/app/applet/app/src/main/java/com/example/ui/screens/MoviesScreen.kt')
patch_file('/app/applet/app/src/main/java/com/example/ui/screens/SeriesScreen.kt')
patch_file('/app/applet/app/src/main/java/com/example/ui/screens/LiveTVScreen.kt')
