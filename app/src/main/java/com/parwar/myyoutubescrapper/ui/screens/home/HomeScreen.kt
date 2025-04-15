package com.parwar.myyoutubescrapper.ui.screens.home

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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.parwar.myyoutubescrapper.domain.model.TimeFilter
import com.parwar.myyoutubescrapper.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("YouTube Summarizer") },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            
            if (!uiState.youtubeApiKeySet || !uiState.openRouterApiKeySet) {
                ApiKeysWarningCard(
                    youtubeApiKeySet = uiState.youtubeApiKeySet,
                    openRouterApiKeySet = uiState.openRouterApiKeySet,
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            if (!uiState.youtubeOAuthClientIdSet) {
                OAuthClientIdWarningCard(
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Search query input
            OutlinedTextField(
                value = uiState.query,
                onValueChange = { viewModel.updateQuery(it) },
                label = { Text("Search Query") },
                placeholder = { Text("e.g., latest forex news") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Time filter dropdown
            TimeFilterDropdown(
                selectedTimeFilter = uiState.selectedTimeFilter,
                availableTimeFilters = viewModel.availableTimeFilters,
                onTimeFilterSelected = { viewModel.updateTimeFilter(it) },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Max results dropdown
            MaxResultsDropdown(
                selectedMaxResults = uiState.selectedMaxResults,
                availableMaxResults = viewModel.maxResultsOptions,
                onMaxResultsSelected = { viewModel.updateMaxResults(it) },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // AI model selection
            AIModelDropdown(
                selectedModelId = uiState.selectedModelId,
                availableModels = viewModel.availableModels,
                onModelSelected = { viewModel.updateSelectedModel(it) },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Search button
            Button(
                onClick = {
                    // Pass ALL search parameters to results screen
                    val searchParams = viewModel.createSearchParameters()
                    navController.currentBackStackEntry
                        ?.savedStateHandle?.set("searchQuery", searchParams.query)
                    navController.currentBackStackEntry
                        ?.savedStateHandle?.set("selectedModelId", searchParams.modelId)
                    navController.currentBackStackEntry
                        ?.savedStateHandle?.set("maxResults", searchParams.maxResults)
                    navController.currentBackStackEntry
                        ?.savedStateHandle?.set("timeFilter", searchParams.timeFilter.name)
                    navController.navigate(Screen.Results.route)
                },
                enabled = uiState.query.isNotBlank() && uiState.youtubeApiKeySet && uiState.openRouterApiKeySet,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Search & Summarize")
            }
        }
    }
}

@Composable
fun ApiKeysWarningCard(
    youtubeApiKeySet: Boolean,
    openRouterApiKeySet: Boolean,
    onNavigateToSettings: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "API Keys Required",
                style = MaterialTheme.typography.titleMedium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (!youtubeApiKeySet) {
                Text("• YouTube API key is not set")
            }
            
            if (!openRouterApiKeySet) {
                Text("• OpenRouter API key is not set")
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Button(
                onClick = onNavigateToSettings,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Go to Settings")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeFilterDropdown(
    selectedTimeFilter: TimeFilter,
    availableTimeFilters: List<TimeFilter>,
    onTimeFilterSelected: (TimeFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedTimeFilter.displayName,
            onValueChange = {},
            readOnly = true,
            label = { Text("Time Filter") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            availableTimeFilters.forEach { filter ->
                DropdownMenuItem(
                    text = { Text(filter.displayName) },
                    onClick = {
                        onTimeFilterSelected(filter)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaxResultsDropdown(
    selectedMaxResults: Int,
    availableMaxResults: List<Int>,
    onMaxResultsSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedMaxResults.toString(),
            onValueChange = {},
            readOnly = true,
            label = { Text("Max Results") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            availableMaxResults.forEach { count ->
                DropdownMenuItem(
                    text = { Text(count.toString()) },
                    onClick = {
                        onMaxResultsSelected(count)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIModelDropdown(
    selectedModelId: String,
    availableModels: List<com.parwar.myyoutubescrapper.data.model.openrouter.AIModel>,
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
            label = { Text("AI Model") },
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

@Composable
fun OAuthClientIdWarningCard(
    onNavigateToSettings: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "OAuth Client ID Recommended",
                style = MaterialTheme.typography.titleMedium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text("YouTube OAuth Client ID is not set. This is needed for accessing private video captions.")
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Button(
                onClick = onNavigateToSettings,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Configure OAuth")
            }
        }
    }
} 