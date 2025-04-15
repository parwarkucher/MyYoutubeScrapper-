package com.parwar.myyoutubescrapper.ui.screens.results

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.parwar.myyoutubescrapper.domain.model.SearchResults
import com.parwar.myyoutubescrapper.domain.model.Video
import com.parwar.myyoutubescrapper.ui.components.EmptyState
import com.parwar.myyoutubescrapper.ui.components.ErrorDialog
import com.parwar.myyoutubescrapper.ui.components.LoadingIndicator
import dev.jeziellago.compose.markdowntext.MarkdownText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen(
    navController: NavController,
    searchQueryFromHome: String? = null,
    selectedModelIdFromHome: String? = null,
    maxResultsFromHome: Int? = null,
    timeFilterFromHome: String? = null,
    viewModel: ResultsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(searchQueryFromHome, selectedModelIdFromHome, maxResultsFromHome, timeFilterFromHome) {
        // Use the search parameters passed from the home screen
        if (!searchQueryFromHome.isNullOrBlank()) {
            viewModel.setQuery(searchQueryFromHome)
        }
        if (!selectedModelIdFromHome.isNullOrBlank()) {
            viewModel.setSelectedModelId(selectedModelIdFromHome)
        }
        if (maxResultsFromHome != null) {
            viewModel.setMaxResults(maxResultsFromHome)
        }
        if (!timeFilterFromHome.isNullOrBlank()) {
            viewModel.setTimeFilter(timeFilterFromHome)
        }
        viewModel.startSearch()
    }
    
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    
    if (showErrorDialog) {
        ErrorDialog(
            message = errorMessage,
            onDismiss = { showErrorDialog = false }
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Results") },
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (uiState) {
                is ResultsUiState.Loading -> {
                    LoadingIndicator()
                }
                
                is ResultsUiState.Error -> {
                    LaunchedEffect(key1 = uiState) {
                        errorMessage = (uiState as ResultsUiState.Error).message
                        showErrorDialog = true
                    }
                    
                    EmptyState(message = "Something went wrong. Please try again.")
                }
                
                is ResultsUiState.Success -> {
                    val searchResults = (uiState as ResultsUiState.Success).searchResults
                    ResultsContent(searchResults = searchResults)
                }
            }
        }
    }
}

@Composable
fun ResultsContent(
    searchResults: SearchResults
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Summary sections
        item {
            SummarySection(
                shortSummary = searchResults.shortSummary,
                detailedSummary = searchResults.detailedSummary,
                videoSummaries = searchResults.videoSummaries
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            Divider()
            Spacer(modifier = Modifier.height(16.dp))
            
            if (searchResults.videos.isNotEmpty()) {
                Text(
                    text = "Videos",
                    style = MaterialTheme.typography.titleLarge
                )
                
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
        
        // Video list
        items(searchResults.videos) { video ->
            VideoItem(video = video)
        }
        
        // Show empty state if no videos found
        if (searchResults.videos.isEmpty()) {
            item {
                EmptyState(message = "No videos found for your search criteria. Try adjusting your search terms or time filter.")
            }
        }
    }
}

@Composable
fun SummarySection(
    shortSummary: String?,
    detailedSummary: String?,
    videoSummaries: String? = null
) {
    if (shortSummary == null && detailedSummary == null && videoSummaries == null) {
        return
    }
    
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Summary",
            style = MaterialTheme.typography.titleLarge
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Short Summary
        if (!shortSummary.isNullOrBlank()) {
            Text(
                text = "Short Summary",
                style = MaterialTheme.typography.titleMedium
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            MarkdownText(
                markdown = shortSummary,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Detailed Summary
        if (!detailedSummary.isNullOrBlank()) {
            var expandedDetailed by remember { mutableStateOf(false) }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Detailed Summary",
                    style = MaterialTheme.typography.titleMedium
                )
                
                IconButton(onClick = { expandedDetailed = !expandedDetailed }) {
                    Icon(
                        imageVector = if (expandedDetailed) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (expandedDetailed) "Collapse" else "Expand"
                    )
                }
            }
            
            if (expandedDetailed) {
                MarkdownText(
                    markdown = detailedSummary,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Text(
                    text = detailedSummary,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Video Summaries
        if (!videoSummaries.isNullOrBlank()) {
            var expandedVideoSummaries by remember { mutableStateOf(false) }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Each Video Summary",
                    style = MaterialTheme.typography.titleMedium
                )
                
                IconButton(onClick = { expandedVideoSummaries = !expandedVideoSummaries }) {
                    Icon(
                        imageVector = if (expandedVideoSummaries) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (expandedVideoSummaries) "Collapse" else "Expand"
                    )
                }
            }
            
            if (expandedVideoSummaries) {
                MarkdownText(
                    markdown = videoSummaries,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Text(
                    text = videoSummaries,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun VideoItem(
    video: Video
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(video.thumbnailUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = video.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = video.title,
                style = MaterialTheme.typography.titleMedium
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = video.channelTitle,
                style = MaterialTheme.typography.bodyMedium
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = video.description,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
} 