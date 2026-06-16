package com.monnavigateuria.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.random.Random

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
    
    MatrixBackground()
    
    Scaffold(
        bottomBar = { BottomNavigationBar(selectedTab, onTabSelected = { selectedTab = it }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.Black.copy(alpha = 0.9f))
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
                            
                            kotlinx.coroutines.GlobalScope.launch {
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
fun MatrixBackground() {
    val matrixChars = remember { (0..50).map { Random.nextInt(0, 2).toString() } }
    var offsetY by remember { mutableStateOf(0f) }
    
    LaunchedEffect(Unit) {
        while (true) {
            delay(100)
            offsetY += 3f
            if (offsetY > 500f) offsetY = 0f
        }
    }
    
    Canvas(modifier = Modifier.fillMaxSize()) {
        matrixChars.forEachIndexed { index, char ->
            val x = (index * 25) % size.width.toInt()
            val y = ((offsetY + index * 15) % size.height)
            
            drawContext.canvas.nativeCanvas.drawText(
                char,
                x.toFloat(),
                y.toFloat(),
                android.graphics.Paint().apply {
                    color = android.graphics.Color.parseColor("#00FF41")
                    textSize = 10f
                    alpha = 20
                }
            )
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
            Canvas(modifier = Modifier.size(70.dp)) {
                drawCircle(
                    color = Color(0xFFF25D0D),
                    radius = 30.dp.toPx(),
                    style = Stroke(width = 3.dp.toPx())
                )
                drawLine(
                    color = Color(0xFF00D9FF),
                    start = Offset(size.width / 2 - 15, size.height / 2 - 20),
                    end = Offset(size.width / 2 + 15, size.height / 2 + 20),
                    strokeWidth = 5.dp.toPx()
                )
                drawCircle(
                    color = Color(0xFFF25D0D),
                    radius = 5.dp.toPx(),
                    center = Offset(size.width / 2, size.height / 2)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "MonNavigateurIA",
                fontSize = 22.sp,
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
            icon = { Icon(Icons.Default.Compare, contentDescription = "Comparer") },
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
            icon = { Icon(Icons.Default.Settings, contentDescription = "Paramètres") },
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
            Spacer(modifier = Modifier.height(24.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A2E)),
                modifier = Modifier.padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Agents disponibles:", color = Color(0xFF00D9FF), fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("• Veille Technologique", color = Color.White)
                    Text("• Analyse de données", color = Color.White)
                    Text("• Automatisation", color = Color.White)
                }
            }
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
