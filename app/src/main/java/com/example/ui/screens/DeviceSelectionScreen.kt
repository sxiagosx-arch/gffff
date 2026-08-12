package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Smartphone
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.foundation.focusable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.IPTVViewModel
import com.example.ui.theme.Charcoal
import com.example.ui.theme.MatteBlack
import com.example.ui.theme.NeonGreen

@Composable
fun DeviceSelectionScreen(viewModel: IPTVViewModel) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MatteBlack),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "Selecione o seu Dispositivo",
                color = Color.White,
                fontSize = 28.sp,
                fontFamily = com.example.ui.theme.RussoOne,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Qual será a melhor experiência para você?",
                color = Color.Gray,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(48.dp))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // TV Option
                var isTvFocused by remember { mutableStateOf(false) }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isTvFocused) NeonGreen.copy(alpha=0.3f) else Charcoal)
                        .border(if (isTvFocused) 3.dp else 0.dp, NeonGreen, RoundedCornerShape(16.dp))
                        .onFocusChanged { isTvFocused = it.isFocused }
                        .focusable()
                        .clickable { viewModel.setDeviceLayoutMode("TV") }
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Rounded.Tv,
                            contentDescription = "TV",
                            tint = NeonGreen,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Modo TV",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                }
                
                // Mobile Option
                var isMobileFocused by remember { mutableStateOf(false) }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isMobileFocused) NeonGreen.copy(alpha=0.3f) else Charcoal)
                        .border(if (isMobileFocused) 3.dp else 0.dp, NeonGreen, RoundedCornerShape(16.dp))
                        .onFocusChanged { isMobileFocused = it.isFocused }
                        .focusable()
                        .clickable { viewModel.setDeviceLayoutMode("MOBILE") }
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Rounded.Smartphone,
                            contentDescription = "Mobile",
                            tint = NeonGreen,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Modo Celular",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                }
            }
        }
    }
}
