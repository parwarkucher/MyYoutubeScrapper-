package com.parwar.myyoutubescrapper.data.source

import com.parwar.myyoutubescrapper.data.model.openrouter.ChatCompletionRequest
import com.parwar.myyoutubescrapper.data.model.openrouter.ChatCompletionResponse
import com.parwar.myyoutubescrapper.data.model.openrouter.Message
import com.parwar.myyoutubescrapper.data.remote.OpenRouterApiService
import com.parwar.myyoutubescrapper.domain.model.Result
import javax.inject.Inject

class OpenRouterDataSource @Inject constructor(
    private val openRouterApiService: OpenRouterApiService
) {
    suspend fun summarizeText(
        text: String,
        modelId: String,
        apiKey: String
    ): Result<ChatCompletionResponse> {
        return try {
            val systemPrompt = """
                You are a helpful assistant that analyzes YouTube video captions and generates three types of summaries.
                Use markdown formatting in your responses to make the summaries visually organized and readable.

                1. SHORT SUMMARY: A concise overview  highlighting only the core points across all videos, addressing the main topic or question,and provide your general view on it based on captions .
                   - Use **bold** for important terms
                   - Keep this very brief but informative

                2. DETAILED SUMMARY: An in-depth comprehensive analysis  that:
                   - Provides thorough coverage of all significant information from the videos
                   - Uses clear section numbering and hierarchy (e.g., "1. **Key Economic Insights**:")
                   - Includes specific data points, statistics, and quotes when available
                   - Uses **bold** for section headers, important data points, and key insights
                   - Uses bullet points (`- `) for listing features, steps, or comparisons
                   - Includes proper formatting like *italics* for emphasis or technical terms
                   - Addresses important context and implications beyond what's directly stated
                   - Ensures a thorough, well-structured analysis that's educational and substantive
                   - Compares contrasting viewpoints from different videos when relevant

                3. VIDEO SUMMARIES: Comprehensive individual summaries for each video:
                   - Create a detailed summary for each video in the format "### Video: [Video Title]"
                   - For each video, provide bullet points of the  most important points from that specific video
                   - Include specific data, numbers, and key details mentioned in each video
                   - Highlight unique insights or perspectives offered by each individual video
                   - Use formatting to make key points stand out (bold for important facts, etc.)
                   - Mention speakers/presenters and their credentials when available
                   - Include timestamps of key moments if possible (e.g., "At 3:45 discusses rate impacts")

                Your response MUST follow this exact format:
                SHORT SUMMARY:
                [your markdown-formatted concise summary here]

                DETAILED SUMMARY:
                [your markdown-formatted comprehensive analysis here]

                VIDEO SUMMARIES:
                [your markdown-formatted individual video summaries here, one for each video]
            """.trimIndent()
            
            val messages = listOf(
                Message(role = "system", content = systemPrompt),
                Message(role = "user", content = "Please thoroughly analyze and provide detailed summaries for the following YouTube video captions: $text")
            )
            
            val request = ChatCompletionRequest(
                model = modelId,
                messages = messages
            )
            
            val response = openRouterApiService.getChatCompletions(
                authorization = "Bearer $apiKey",
                request = request
            )
            
            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
} 