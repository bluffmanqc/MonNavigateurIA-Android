package com.monnavigateuria.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF0A0A0A)) {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    var selectedTab by remember { mutableStateOf("Chat") }
    
    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Box(modifier = Modifier.fillMaxWidth().background(Color(0xFF1A1A2E)).padding(16.dp), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("MonNavigateurIA", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF25D0D))
                Text("Hub IA Local & Distant", fontSize = 12.sp, color = Color(0xFF00D9FF))
            }
        }
        
        // Contenu central
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            when (selectedTab) {
                "Chat" -> Text("Chat avec Odysseus", color = Color.White, fontSize = 18.sp)
                "Agents" -> Text("Agents IA", color = Color.White, fontSize = 18.sp)
                "Compare" -> Text("Mode VS", color = Color.White, fontSize = 18.sp)
                "Settings" -> Text("Paramètres", color = Color.White, fontSize = 18.sp)
            }
        }
        
        // Bottom navigation
        NavigationBar(containerColor = Color(0xFF1A1A2E)) {
            NavigationBarItem(
                selected = selectedTab == "Chat",
                onClick = { selectedTab = "Chat" },
                icon = { Text("💬") },
                label = { Text("Chat", fontSize = 10.sp, color = if (selectedTab == "Chat") Color(0xFFF25D0D) else Color.Gray) }
            )
            NavigationBarItem(
                selected = selectedTab == "Agents",
                onClick = { selectedTab = "Agents" },
                icon = { Text("🤖") },
                label = { Text("Agents", fontSize = 10.sp, color = if (selectedTab == "Agents") Color(0xFFF25D0D) else Color.Gray) }
            )
            NavigationBarItem(
                selected = selectedTab == "Compare",
                onClick = { selectedTab = "Compare" },
                icon = { Text("⚔️") },
                label = { Text("Comparer", fontSize = 10.sp, color = if (selectedTab == "Compare") Color(0xFFF25D0D) else Color.Gray) }
            )
            NavigationBarItem(
                selected = selectedTab == "Settings",
                onClick = { selectedTab = "Settings" },
                icon = { Text("⚙️") },
                label = { Text("Paramètres", fontSize = 10.sp, color = if (selectedTab == "Settings") Color(0xFFF25D0D) else Color.Gray) }
            )
        }
    }
}
