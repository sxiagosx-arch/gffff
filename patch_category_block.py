import re

screens = ['LiveTVScreen.kt', 'MoviesScreen.kt', 'SeriesScreen.kt']
for screen in screens:
    path = f"app/src/main/java/com/example/ui/screens/{screen}"
    with open(path, 'r') as f:
        content = f.read()
    
    target_state = """    var isSortAZ by remember { mutableStateOf(true) }"""
    replace_state = """    var isSortAZ by remember { mutableStateOf(true) }
    var showPinDialogForCat by remember { mutableStateOf<String?>(null) }
    var pinInput by remember { mutableStateOf("") }
    
    if (showPinDialogForCat != null) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showPinDialogForCat = null; pinInput = "" },
            title = { androidx.compose.material3.Text("Conteúdo Bloqueado", color = Color.White) },
            text = { 
                Column {
                    androidx.compose.material3.Text("Digite o PIN para acessar esta categoria:", color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    androidx.compose.material3.OutlinedTextField(
                        value = pinInput,
                        onValueChange = { if (it.length <= 4) pinInput = it },
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                        visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                        singleLine = true,
                        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White, unfocusedTextColor = Color.White
                        )
                    )
                }
            },
            confirmButton = {
                androidx.compose.material3.TextButton(onClick = {
                    viewModel.checkParentalPin(pinInput, onSuccess = {
                        selectedCategoryId = showPinDialogForCat!!
                        showPinDialogForCat = null
                        pinInput = ""
                    }, onFailure = {
                        pinInput = ""
                    })
                }) { androidx.compose.material3.Text("Desbloquear", color = NeonGreen) }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { showPinDialogForCat = null; pinInput = "" }) {
                    androidx.compose.material3.Text("Cancelar", color = Color.Gray)
                }
            },
            containerColor = com.example.ui.theme.Charcoal
        )
    }"""
    
    content = content.replace(target_state, replace_state)
    
    target_click = """{ selectedCategoryId = cat.id }"""
    replace_click = """{ 
                                if (viewModel.isCategoryBlocked(cat.id, cat.name)) {
                                    showPinDialogForCat = cat.id
                                } else {
                                    selectedCategoryId = cat.id 
                                }
                            }"""
    content = content.replace(target_click, replace_click)
    
    with open(path, 'w') as f:
        f.write(content)
