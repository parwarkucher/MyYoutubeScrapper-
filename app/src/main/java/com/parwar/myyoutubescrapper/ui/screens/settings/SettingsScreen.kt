package com.parwar.myyoutubescrapper.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.parwar.myyoutubescrapper.data.model.openrouter.AIModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var settingsSaved by remember { mutableStateOf(false) }
    
    LaunchedEffect(settingsSaved) {
        if (settingsSaved) {
            // Navigate back after saving
            navController.navigateUp()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top
        ) {
            // YouTube API Key section
            Text(
                text = "YouTube API Settings",
                style = MaterialTheme.typography.titleLarge
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = uiState.youtubeApiKey,
                onValueChange = { viewModel.updateYouTubeApiKey(it) },
                label = { Text("YouTube Data API v3 Key") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = uiState.youtubeOAuthClientId,
                onValueChange = { viewModel.updateYouTubeOAuthClientId(it) },
                label = { Text("YouTube OAuth Client ID") },
                placeholder = { Text("For accessing private video captions") },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            YouTubeApiKeyInfoCard()
            
            Spacer(modifier = Modifier.height(24.dp))
            Divider()
            Spacer(modifier = Modifier.height(24.dp))
            
            // OpenRouter API Key section
            Text(
                text = "OpenRouter API Settings",
                style = MaterialTheme.typography.titleLarge
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = uiState.openRouterApiKey,
                onValueChange = { viewModel.updateOpenRouterApiKey(it) },
                label = { Text("OpenRouter API Key") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Model selection
            AIModelDropdown(
                selectedModelId = uiState.selectedModelId,
                availableModels = viewModel.availableModels,
                onModelSelected = { viewModel.updateSelectedModel(it) },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            OpenRouterApiKeyInfoCard()
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Save button
            Button(
                onClick = {
                    viewModel.saveSettings()
                    settingsSaved = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Settings")
            }
        }
    }
}

@Composable
fun YouTubeApiKeyInfoCard() {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                
                Text(
                    text = "How to get YouTube API Key",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "1. Go to Google Cloud Console (console.cloud.google.com)",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Text(
                text = "2. Create a new project or select an existing one",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Text(
                text = "3. Navigate to 'APIs & Services' > 'Library'",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Text(
                text = "4. Search for 'YouTube Data API v3' and enable it",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Text(
                text = "5. Go to 'Credentials' and create an API Key",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Text(
                text = "6. Optional: Restrict the API key to YouTube Data API v3",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                
                Text(
                    text = "OAuth 2.0 Client Setup (for captions)",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "For accessing private video captions, you'll also need to create an OAuth 2.0 client:",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Text(
                text = "1. In Google Cloud Console, go to 'APIs & Services' > 'Credentials'",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Text(
                text = "2. Click 'Create Credentials' and select 'OAuth client ID'",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Text(
                text = "3. Select 'Android' as the application type",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Text(
                text = "4. Enter a name and your app's package name (com.parwar.myyoutubescrapper)",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Text(
                text = "5. Add your SHA-1 signing certificate fingerprint (16:7A:0B:3B:C9:BD:BB:F3:56:FB:57:48:86:3B:B9:C2:8E:A9:1C:E9)",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Text(
                text = "6. Copy the client ID and paste it in the field above",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun OpenRouterApiKeyInfoCard() {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                
                Text(
                    text = "How to get OpenRouter API Key",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "1. Go to OpenRouter website (openrouter.ai)",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Text(
                text = "2. Create an account and log in",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Text(
                text = "3. Navigate to the API Keys section",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Text(
                text = "4. Create a new API key for your application",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Text(
                text = "5. Copy the API key and paste it here",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIModelDropdown(
    selectedModelId: String,
    availableModels: List<AIModel>,
    onModelSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedModel = availableModels.find { it.id == selectedModelId }
        ?: availableModels.firstOrNull()
    
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedModel?.displayName ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Default AI Model") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            availableModels.forEach { model ->
                DropdownMenuItem(
                    text = {
                        Column {
                            Row {
                                Text(model.displayName)
                                if (model.isPaid) {
                                    Text(
                                        " (${model.pricing})",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                } else {
                                    Text(
                                        " (Free)",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.tertiary
                                    )
                                }
                            }
                            Text(
                                "${model.providerName} • Context: ${model.contextWindow} • Max Output: ${model.maxOutput}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    },
                    onClick = {
                        onModelSelected(model.id)
                        expanded = false
                    }
                )
            }
        }
    }
} 