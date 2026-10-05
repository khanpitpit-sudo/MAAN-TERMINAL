package com.maan.terminal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaAnTerminalTheme {
                TerminalScreen(viewModel = viewModel)
            }
        }
    }
}

class MainViewModel : ViewModel() {
    private val _output = mutableStateOf<String>("")
    val output: String get() = _output.value
        private set(value) { _output.value = value }

    private val _currentDir = mutableStateOf<String>(System.getProperty("user.home"))
    val currentDir: String get() = _currentDir.value
        private set(value) { _currentDir.value = value }

    private val _isRunning = mutableStateOf<Boolean>(false)
    val isRunning: Boolean get() = _isRunning.value
        private set(value) { _isRunning.value = value }

    private var shellProcess: Process? = null
    private var outputReader: Thread? = null
    private var writer: OutputStreamWriter? = null

    fun startShell() {
        if (isRunning) return
        _isRunning.value = true
        _output.value = "MaAn Terminal v1.0.0\nType 'help' for commands\n\n$ "

        viewModelScope.launch {
            try {
                shellProcess = Runtime.getRuntime().exec("/system/bin/sh")
                writer = OutputStreamWriter(shellProcess!!.outputStream)

                outputReader = Thread {
                    val reader = BufferedReader(InputStreamReader(shellProcess!!.inputStream))
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        _output.value += "$line\n"
                    }
                }.apply { start() }

                val errorReader = Thread {
                    val reader = BufferedReader(InputStreamReader(shellProcess!!.errorStream))
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        _output.value += "[stderr] $line\n"
                    }
                }.apply { start() }

            } catch (e: Exception) {
                _output.value += "\nError starting shell: ${e.message}\n$ "
                _isRunning.value = false
            }
        }
    }

    fun sendCommand(command: String) {
        if (!isRunning || writer == null) return
        _output.value += "$command\n"
        viewModelScope.launch {
            try {
                writer!!.write("$command\n")
                writer!!.flush()
            } catch (e: Exception) {
                _output.value += "Error: ${e.message}\n$ "
            }
        }
    }

    fun sendControlChar(char: Char) {
        if (!isRunning || writer == null) return
        viewModelScope.launch {
            try {
                writer!!.write(char.toString())
                writer!!.flush()
            } catch (e: Exception) {
                // ignore
            }
        }
    }

    fun restartShell() {
        stopShell()
        startShell()
    }

    fun stopShell() {
        _isRunning.value = false
        outputReader?.interrupt()
        try {
            writer?.close()
            shellProcess?.destroy()
        } catch (e: Exception) {
            // ignore
        }
        shellProcess = null
        writer = null
        outputReader = null
    }

    override fun onCleared() {
        stopShell()
        super.onCleared()
    }
}

@Composable
fun TerminalScreen(viewModel: MainViewModel) {
    val output by viewModel._output.observeAsState("")
    val currentDir by viewModel._currentDir.observeAsState("")
    val isRunning by viewModel._isRunning.observeAsState(false)
    val scrollState = rememberScrollState()
    var inputText by remember { mutableStateOf("") }
    var showToolbar by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        // Header
        TopAppBar(
            title = {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "● MaAn Terminal",
                        color = Color.Green,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Android Shell",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 14.sp
                    )
                }
            },
            navigationIcon = {
                IconButton(onClick = { viewModel.restartShell() }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Restart")
                }
            },
            actions = {
                IconButton(onClick = { showToolbar = !showToolbar }) {
                    Icon(Icons.Default.Keyboard, contentDescription = "Toggle toolbar")
                }
                IconButton(onClick = { /* More menu */ }) {
                    Icon(Icons.Default.Menu, contentDescription = "More")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFF1E1E1E),
                titleContentColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        )

        // Terminal output
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .weight(1f),
            colors = androidx.compose.material3.CardDefaults.cardColors(
                containerColor = Color.Black
            )
        ) {
            androidx.compose.foundation.Text(
                text = output,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
                    .verticalScroll(scrollState),
                color = Color.White,
                fontFamily = FontFamily.Monospace,
                fontSize = 14.sp,
                style = androidx.compose.ui.text.TextStyle(
                    lineHeight = 20.sp
                )
            )
        }

        // Input row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "$ ", color = Color.Green, fontSize = 16.sp, fontFamily = FontFamily.Monospace)
            androidx.compose.material3.TextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .height(48.dp),
                singleLine = true,
                colors = androidx.compose.material3.TextFieldDefaults.textFieldColors(
                    containerColor = Color(0xFF1E1E1E),
                    unfocusedContainerColor = Color(0xFF1E1E1E),
                    focusedContainerColor = Color(0xFF1E1E1E),
                    textColor = Color.White,
                    cursorColor = Color.Green,
                    placeholderColor = Color.White.copy(alpha = 0.4f)
                ),
                placeholder = { Text("Enter command...", color = Color.White.copy(alpha = 0.4f), fontFamily = FontFamily.Monospace) },
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 16.sp,
                    color = Color.White
                ),
                keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions(
                    imeAction = androidx.compose.ui.text.input.ImeAction.Done
                ),
                keyboardActions = androidx.compose.ui.text.input.KeyboardActions(
                    onDone = {
                        if (inputText.isNotBlank()) {
                            viewModel.sendCommand(inputText)
                            inputText = ""
                        }
                    }
                )
            )
            Button(
                onClick = {
                    if (inputText.isNotBlank()) {
                        viewModel.sendCommand(inputText)
                        inputText = ""
                    }
                },
                modifier = Modifier.padding(start = 8.dp).size(48.dp, 48.dp)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Send", tint = Color.White)
            }
        }

        // Toolbar
        if (showToolbar) {
            ToolbarRow(viewModel = viewModel)
        }
    }
}

@Composable
fun ToolbarRow(viewModel: MainViewModel) {
    val keys = listOf(
        "TAB" to '\t',
        "CTRL" to '\u0000', // Special handling
        "ESC" to '\u001B',
        "↑" to '\u001B[A',
        "↓" to '\u001B[B',
        "←" to '\u001B[D',
        "→" to '\u001B[C',
        "HOME" to '\u001B[H',
        "END" to '\u001B[F',
        "CTRL+C" to '\u0003',
        "CTRL+D" to '\u0004',
        "CTRL+L" to '\u000C',
        "CTRL+Z" to '\u001A',
    )

    androidx.compose.foundation.layout.Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .height(48.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        keys.forEach { (label, char) ->
            if (label == "CTRL") {
                // CTRL is a modifier key - handled specially
                Button(
                    onClick = { /* Toggle CTRL mode */ },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF333333)
                    ),
                    modifier = Modifier.width(56.dp).height(40.dp)
                ) {
                    Text(text = label, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            } else if (label.startsWith("CTRL+")) {
                // Control combinations in a submenu would be better, but showing for now
                Button(
                    onClick = { viewModel.sendControlChar(char) },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF442222)
                    ),
                    modifier = Modifier.width(56.dp).height(40.dp)
                ) {
                    Text(text = label, color = Color.White.copy(alpha = 0.8f), fontSize = 10.sp)
                }
            } else {
                Button(
                    onClick = { viewModel.sendControlChar(char) },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2D2D2D)
                    ),
                    modifier = Modifier.width(48.dp).height(40.dp)
                ) {
                    Text(text = label, color = Color.White, fontSize = 11.sp)
                }
            }
        }
    }
}