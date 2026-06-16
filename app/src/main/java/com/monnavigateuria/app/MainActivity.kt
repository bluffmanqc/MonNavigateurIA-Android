package com.monnavigateuria.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MonNavigateurIAApp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonNavigateurIAApp() {
    var selectedTab by remember { mutableStateOf("Chat") }
    var messages by remember { mutableStateOf(listOf<Message>()) }
    var userInput by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    
    Scaffold(
        bottomBar = { BottomNavigationBar(selectedTab, onTabSelected = { selectedTab = it }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFF0A0A0A))
        ) {
            Header()
            
            when (selectedTab) {
                "Chat" -> ChatScreen(messages, userInput, isLoading,
                    onUserInputChanged = { userInput = it },
                    onSendMessage = {
                        if (userInput.isNotBlank()) {
                            isLoading = true
                            val userMessage = Message("user", userInput)
                            messages = messages + userMessage
                            val currentInput = userInput
                            userInput = ""
                            
                            coroutineScope.launch {
                                delay(1500)
                                val aiResponse = Message("ai", "Réponse de Odysseus pour: $currentInput")
                                messages = messages + aiResponse
                                isLoading = false
                            }
                        }
                    }
                )
                "Agents" -> AgentsScreen()
                "Compare" -> CompareScreen()
                "Settings" -> SettingsScreen()
            }
        }
    }
}

@Composable
fun Header() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFF1A1A2E), Color(0xFF16213E))
                )
            )
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "⚡ MonNavigateurIA",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF25D0D)
            )
            Text(
                text = "Hub IA Local & Distant",
                fontSize = 12.sp,
                color = Color(0xFF00D9FF)
            )
        }
    }
}

data class Message(val sender: String, val content: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    messages: List<Message>,
    userInput: String,
    isLoading: Boolean,
    onUserInputChanged: (String) -> Unit,
    onSendMessage: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.weight(1f).padding(16.dp),
            reverseLayout = true
        ) {
            items(messages.reversed()) { message ->
                MessageBubble(message)
                Spacer(modifier = Modifier.height(8.dp))
            }
            if (isLoading) {
                item {
                    Surface(
                        color = Color(0xFF1A1A2E),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color(0xFF00D9FF),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Odysseus écrit...", color = Color.Gray, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            OutlinedTextField(
                value = userInput,
                onValueChange = onUserInputChanged,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Message Odysseus...", color = Color.Gray) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF00D9FF),
                    unfocusedBorderColor = Color.Gray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(24.dp),
                maxLines = 3
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = onSendMessage,
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFFF25D0D), RoundedCornerShape(24.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Envoyer",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun MessageBubble(message: Message) {
    val isUser = message.sender == "user"
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            color = if (isUser) Color(0xFF0F3460) else Color(0xFF1A1A2E),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = if (isUser) "Vous" else "Odysseus",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isUser) Color(0xFF00D9FF) else Color(0xFFF25D0D)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = message.content, color = Color.White, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun BottomNavigationBar(selectedTab: String, onTabSelected: (String) -> Unit) {
    NavigationBar(containerColor = Color(0xFF1A1A2E)) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Chat, contentDescription = "Chat") },
            label = { Text("Chat", fontSize = 10.sp) },
            selected = selectedTab == "Chat",
            onClick = { onTabSelected("Chat") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFFF25D0D),
                selectedTextColor = Color(0xFFF25D0D),
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Agents") },
            label = { Text("Agents", fontSize = 10.sp) },
            selected = selectedTab == "Agents",
            onClick = { onTabSelected("Agents") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFFF25D0D),
                selectedTextColor = Color(0xFFF25D0D),
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Compare, contentDescription = "Compare") },
            label = { Text("Comparer", fontSize = 10.sp) },
            selected = selectedTab == "Compare",
            onClick = { onTabSelected("Compare") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFFF25D0D),
                selectedTextColor = Color(0xFFF25D0D),
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
            label = { Text("Paramètres", fontSize = 10.sp) },
            selected = selectedTab == "Settings",
            onClick = { onTabSelected("Settings") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFFF25D0D),
                selectedTextColor = Color(0xFFF25D0D),
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
        )
    }
}

@Composable
fun AgentsScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = Color(0xFFF25D0D)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Agents IA",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Configuration des agents autonomes",
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun CompareScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Default.Compare,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = Color(0xFF00D9FF)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Mode VS",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Comparer Groq, Gemini et OpenRouter",
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun SettingsScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Default.Settings,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = Color.Gray
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Paramètres",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Personnaliser l'application",
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
    }
}
