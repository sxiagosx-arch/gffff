package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.IPTVViewModel
import com.example.ui.theme.Charcoal
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonGreenDim

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentalControlScreen(viewModel: IPTVViewModel) {
    val categories by viewModel.allCategories.collectAsState()
    val blockedItems by viewModel.blockedItems.collectAsState()

    var pinInput by remember { mutableStateOf("") }
    var isUnlocked by remember { mutableStateOf(false) }
    var pinMessage by remember { mutableStateOf("Digite o PIN parental (padrão: 0000)") }
    var isPinSetupRequired by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }
    var currentPinInput by remember { mutableStateOf("") }
    var newPinInput by remember { mutableStateOf("") }
    var resetMessage by remember { mutableStateOf("") }

    // Check if Pin is configured
    LaunchedEffect(Unit) {
        viewModel.isParentalPinSet { hasPin ->
            isPinSetupRequired = !hasPin
            if (isPinSetupRequired) {
                pinMessage = "Configure um novo PIN de 4 dígitos"
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .systemBarsPadding()
            .imePadding()
            .testTag("parental_control_screen")
    ) {
        if (!isUnlocked) {
            // LOCK SCREEN PIN INPUT
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Security,
                    contentDescription = "Cadeado",
                    tint = NeonGreen,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Controle Parental",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Text(
                    text = pinMessage,
                    color = Color.Gray,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
                )

                OutlinedTextField(
                    value = pinInput,
                    onValueChange = { if (it.length <= 4) pinInput = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    visualTransformation = PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonGreen,
                        unfocusedBorderColor = Color.DarkGray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier
                        .width(180.dp)
                        .padding(bottom = 16.dp),
                    singleLine = true
                )

                Button(
                    onClick = {
                        if (isPinSetupRequired) {
                            if (pinInput.length == 4) {
                                viewModel.setParentalPin(pinInput) {
                                    isPinSetupRequired = false
                                    isUnlocked = true
                                    pinInput = ""
                                }
                            } else {
                                pinMessage = "O PIN deve conter exatamente 4 dígitos!"
                            }
                        } else {
                            viewModel.checkParentalPin(
                                inputPin = pinInput,
                                onSuccess = {
                                    isUnlocked = true
                                    pinInput = ""
                                },
                                onFailure = {
                                    pinMessage = "PIN Incorreto! Tente novamente."
                                    pinInput = ""
                                }
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                    modifier = Modifier
                        .width(180.dp)
                        .height(48.dp)
                ) {
                    Text(
                        text = if (isPinSetupRequired) "CONFIGURAR" else "DESBLOQUEAR",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        } else {
            // UNLOCKED: CATEGORIES BLOCKING MANAGER
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Ocultar e Bloquear Conteúdo",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    IconButton(onClick = { isUnlocked = false }) {
                        Icon(imageVector = Icons.Rounded.Lock, contentDescription = "Bloquear", tint = NeonGreen)
                    }
                }
                Text(
                    text = "Escolha as categorias para ocultar/bloquear. O conteúdo bloqueado só será exibido se você digitar a senha (PIN) correta.",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                OutlinedButton(
                    onClick = { showResetDialog = true },
                    modifier = Modifier.padding(bottom = 16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonGreen),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonGreen)
                ) {
                    Icon(imageVector = Icons.Rounded.LockReset, contentDescription = "Redefinir PIN")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Redefinir Senha (PIN)")
                }
                
                if (showResetDialog) {
                    AlertDialog(
                        onDismissRequest = { 
                            showResetDialog = false 
                            resetMessage = ""
                        },
                        title = { Text("Redefinir Senha", color = NeonGreen) },
                        text = {
                            Column {
                                if (resetMessage.isNotEmpty()) {
                                    Text(resetMessage, color = Color.Red, fontSize = 12.sp)
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                                OutlinedTextField(
                                    value = currentPinInput,
                                    onValueChange = { if (it.length <= 4) currentPinInput = it },
                                    label = { Text("Senha Atual", color = Color.Gray) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    visualTransformation = PasswordVisualTransformation(),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonGreen, unfocusedBorderColor = Color.DarkGray, focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                                    singleLine = true
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = newPinInput,
                                    onValueChange = { if (it.length <= 4) newPinInput = it },
                                    label = { Text("Nova Senha", color = Color.Gray) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    visualTransformation = PasswordVisualTransformation(),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonGreen, unfocusedBorderColor = Color.DarkGray, focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                                    singleLine = true
                                )
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                if (currentPinInput.length != 4 || newPinInput.length != 4) {
                                    resetMessage = "Os PINs devem ter 4 dígitos."
                                    return@TextButton
                                }
                                viewModel.checkParentalPin(
                                    inputPin = currentPinInput,
                                    onSuccess = {
                                        viewModel.setParentalPin(newPinInput) {
                                            showResetDialog = false
                                            currentPinInput = ""
                                            newPinInput = ""
                                            resetMessage = ""
                                        }
                                    },
                                    onFailure = {
                                        resetMessage = "Senha atual incorreta!"
                                    }
                                )
                            }) {
                                Text("Salvar", color = NeonGreen)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { 
                                showResetDialog = false 
                                resetMessage = ""
                            }) {
                                Text("Cancelar", color = Color.Gray)
                            }
                        },
                        containerColor = Charcoal
                    )
                }
                
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
                }

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(categories) { cat ->
                        val isBlocked = blockedItems.any { it.blockId == cat.id && it.type == "CATEGORY" }
                        val isHidden = blockedItems.any { it.blockId == cat.id && it.type == "HIDDEN_CATEGORY" }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Charcoal)
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = cat.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(text = if (cat.type == "LIVE") "Canais ao Vivo" else "VOD Filmes/Séries", color = Color.Gray, fontSize = 11.sp)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = if (isBlocked) Icons.Rounded.Lock else Icons.Rounded.LockOpen,
                                        contentDescription = "Bloquear",
                                        tint = if (isBlocked) NeonGreen else Color.Gray,
                                        modifier = Modifier.clickable { viewModel.toggleCategoryBlock(cat.id) }
                                    )
                                    Text("Bloquear", color = if (isBlocked) NeonGreen else Color.Gray, fontSize = 10.sp)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = if (isHidden) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                                        contentDescription = "Ocultar",
                                        tint = if (isHidden) NeonGreen else Color.Gray,
                                        modifier = Modifier.clickable { viewModel.toggleCategoryHidden(cat.id) }
                                    )
                                    Text("Ocultar", color = if (isHidden) NeonGreen else Color.Gray, fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
