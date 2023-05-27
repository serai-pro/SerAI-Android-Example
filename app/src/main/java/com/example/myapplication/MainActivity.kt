package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.*
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextAlign


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                MyApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApp() {
    var textState by remember { mutableStateOf("") }
    var messages by remember { mutableStateOf(listOf<String>()) }
    var showWelcomeText by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(5000)
        showWelcomeText = false
    }

    Surface(
        color = Color.LightGray,
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (showWelcomeText) {
                Text(
                    text = "SerAI",
                    modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Loading..",
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    textAlign = TextAlign.Center
                )
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(messages) { message ->
                        val isUserMessage = message.startsWith("")
                        MessageRow(messageText = message, isUserMessage = isUserMessage)
                    }
                }
                TextField(
                    value = textState,
                    onValueChange = { textState = it },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = {
                        val userInput = textState
                        messages = messages + " $userInput"
                        textState = ""

                        CoroutineScope(Dispatchers.Main).launch {
                            val botResponse = fetchBotResponse(userInput)
                            messages = messages + "$botResponse"
                        }
                    }),
                )
            }
        }
    }
}


@Composable
fun MessageRow(messageText: String, isUserMessage: Boolean) {
    val alignment = if (isUserMessage) Alignment.Start else Alignment.End
    val backgroundColor = if (isUserMessage) Color.Green else Color.Blue

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (isUserMessage) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .shadow(1.dp)
                .padding(8.dp)
                .background(backgroundColor, RoundedCornerShape(8.dp))
        ) {
            Text(
                text = messageText,
                color = Color.White,
                modifier = Modifier.padding(8.dp),
                textAlign = TextAlign.Start
            )
        }
    }
}



suspend fun fetchBotResponse(userMessage: String): String {
    val apiUrl = "https://serai.pro/thunderbird?key=a49fa4fc7e5f1669771a1af6025511bc&thunderbird=${userMessage.replace(" ", "%20")}"

    return withContext(Dispatchers.IO) {
        try {
            val response = java.net.URL(apiUrl).readText()
            return@withContext "$response"
        } catch (e: Exception) {
            return@withContext "Error 1: Error fetching response"
        }
    }
}

@Composable
fun PreviewMyApp() {
    MyApplicationTheme {
        MyApp()
    }
}
