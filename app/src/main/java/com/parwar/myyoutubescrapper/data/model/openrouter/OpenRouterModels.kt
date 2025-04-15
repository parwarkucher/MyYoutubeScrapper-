package com.parwar.myyoutubescrapper.data.model.openrouter

import com.google.gson.annotations.SerializedName

// Request models
data class ChatCompletionRequest(
    @SerializedName("model")
    val model: String,
    @SerializedName("messages")
    val messages: List<Message>
)

data class Message(
    @SerializedName("role")
    val role: String,
    @SerializedName("content")
    val content: String
)

// Response models
data class ChatCompletionResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("object")
    val objectType: String,
    @SerializedName("created")
    val created: Long,
    @SerializedName("model")
    val model: String,
    @SerializedName("choices")
    val choices: List<Choice>,
    @SerializedName("usage")
    val usage: Usage
)

data class Choice(
    @SerializedName("index")
    val index: Int,
    @SerializedName("message")
    val message: Message,
    @SerializedName("finish_reason")
    val finishReason: String
)

data class Usage(
    @SerializedName("prompt_tokens")
    val promptTokens: Int,
    @SerializedName("completion_tokens")
    val completionTokens: Int,
    @SerializedName("total_tokens")
    val totalTokens: Int
)

// AI Model representation
data class AIModel(
    val id: String,
    val displayName: String,
    val providerName: String,
    val contextWindow: String,
    val maxOutput: String,
    val isPaid: Boolean = false,
    val pricing: String = "Free"
)

// List of available models
object AvailableModels {
    val models = listOf(
        // Free Models
        AIModel(
            id = "anthropic/claude-3.5-haiku",
            displayName = "Claude 3.5 Haiku",
            providerName = "Anthropic",
            contextWindow = "200K",
            maxOutput = "8K"
        ),
        AIModel(
            id = "anthropic/claude-3.7-sonnet",
            displayName = "Claude 3.7 Sonnet",
            providerName = "Anthropic",
            contextWindow = "200K",
            maxOutput = "128K",
            isPaid = true,
            pricing = "$"
        ),
        AIModel(
            id = "anthropic/claude-3.7-sonnet:thinking",
            displayName = "Claude 3.7 Sonnet (Thinking)",
            providerName = "Anthropic",
            contextWindow = "200K",
            maxOutput = "128K",
            isPaid = true,
            pricing = "$"
        ),
        AIModel(
            id = "deepseek/deepseek-r1",
            displayName = "DeepSeek R1",
            providerName = "DeepSeek",
            contextWindow = "128K",
            maxOutput = "128K",
            isPaid = true,
            pricing = "$"
        ),
        AIModel(
            id = "deepseek/deepseek-r1:free",
            displayName = "DeepSeek R1 (Free)",
            providerName = "DeepSeek",
            contextWindow = "164K",
            maxOutput = "4K"
        ),
        AIModel(
            id = "deepseek/deepseek-r1-distill-llama-70b",
            displayName = "DeepSeek R1 Distill Llama 70B",
            providerName = "DeepSeek",
            contextWindow = "131K",
            maxOutput = "8K",
            isPaid = true,
            pricing = "$"
        ),
        AIModel(
            id = "deepseek/deepseek-chat-v3-0324:free",
            displayName = "DeepSeek V3 0324",
            providerName = "DeepSeek",
            contextWindow = "128K",
            maxOutput = "128K"
        ),
        AIModel(
            id = "meta-llama/llama-3.3-70b-instruct:free",
            displayName = "Llama 3.3 70B Instruct",
            providerName = "Meta",
            contextWindow = "131K",
            maxOutput = "131K"
        ),
        AIModel(
            id = "meta-llama/llama-3.1-8b-instruct",
            displayName = "Llama 3.1 8B Instruct",
            providerName = "Meta",
            contextWindow = "131K",
            maxOutput = "8K",
            isPaid = true,
            pricing = "$0.2"
        ),
        AIModel(
            id = "google/gemini-2.0-pro-exp-02-05:free",
            displayName = "Gemini Pro 2.0 Experimental",
            providerName = "Google",
            contextWindow = "2M",
            maxOutput = "8K"
        ),
        AIModel(
            id = "google/gemini-2.0-flash-001",
            displayName = "Gemini Flash 2.0",
            providerName = "Google",
            contextWindow = "2M",
            maxOutput = "8K",
            isPaid = true,
            pricing = "$"
        ),
        AIModel(
            id = "google/gemini-2.0-flash-thinking-exp-1219:free",
            displayName = "Gemini 2.0 Flash Thinking Experimental",
            providerName = "Google",
            contextWindow = "40K",
            maxOutput = "8K"
        ),
        AIModel(
            id = "google/gemini-2.5-pro-exp-03-25:free",
            displayName = "Gemini Pro 2.5 Experimental",
            providerName = "Google",
            contextWindow = "1M",
            maxOutput = "66K"
        ),
        AIModel(
            id = "google/gemini-flash-1.5",
            displayName = "Gemini Flash 1.5",
            providerName = "Google",
            contextWindow = "1M",
            maxOutput = "8K",
            isPaid = true,
            pricing = "$0.3"
        ),
        AIModel(
            id = "mistralai/mistral-small-24b-instruct-2501",
            displayName = "Mistral Small 3",
            providerName = "Mistral AI",
            contextWindow = "33K",
            maxOutput = "8K",
            isPaid = true,
            pricing = "$0.9"
        ),
        AIModel(
            id = "openai/gpt-4o-mini",
            displayName = "GPT-4o-mini",
            providerName = "OpenAI",
            contextWindow = "128K",
            maxOutput = "16K",
            isPaid = true,
            pricing = "$0.6"
        ),
        // Legacy models kept for compatibility
        AIModel(
            id = "gpt-3.5-turbo",
            displayName = "GPT-3.5 Turbo (Legacy)",
            providerName = "OpenAI",
            contextWindow = "16K",
            maxOutput = "4K"
        ),
        AIModel(
            id = "claude-3-haiku-20240307",
            displayName = "Claude 3 Haiku (Legacy)",
            providerName = "Anthropic",
            contextWindow = "200K",
            maxOutput = "4K"
        ),
        AIModel(
            id = "gemini-pro",
            displayName = "Gemini Pro (Legacy)",
            providerName = "Google",
            contextWindow = "32K",
            maxOutput = "8K"
        ),
        AIModel(
            id = "mistral-7b-instruct",
            displayName = "Mistral 7B Instruct (Legacy)",
            providerName = "Mistral AI",
            contextWindow = "8K",
            maxOutput = "4K"
        )
    )
} 