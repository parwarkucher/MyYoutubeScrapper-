# YouTube Video Summarizer

An Android application that searches YouTube videos based on queries and uses AI to summarize their content by extracting and analyzing video captions.

## Features

- Search YouTube videos with customizable filters
- Extract captions from YouTube videos
- Generate AI-powered summaries of video content
- Support for different AI models through OpenRouter
- Save and manage API keys securely
- Adjust search parameters (time filter, max results)
- Modern Material 3 UI with dark mode support


## Architecture

This application follows Clean Architecture principles with MVVM pattern:

- **UI Layer**: Jetpack Compose UI with ViewModels
- **Domain Layer**: Use cases that orchestrate data operations
- **Data Layer**: Repositories and data sources that manage API communication

### Tech Stack

- **Kotlin**: Primary programming language
- **Jetpack Compose**: Modern declarative UI toolkit
- **Coroutines & Flow**: Asynchronous programming
- **Hilt**: Dependency injection
- **Retrofit**: API communication
- **DataStore**: Secure key-value storage
- **JSoup**: HTML parsing for captions extraction
- **Navigation Compose**: In-app navigation

## Setup

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or newer
- Minimum SDK 26 (Android 8.0 Oreo)
- Java 17

### API Keys Required

This app requires the following API keys to function:

1. **YouTube Data API Key** - For searching videos
2. **OpenRouter API Key** - For AI summarization
3. **YouTube OAuth Client ID** (Optional) - For accessing private video captions

### Getting API Keys

#### YouTube Data API Key:
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project
3. Enable the YouTube Data API v3
4. Create credentials for an API key
5. Restrict the key to YouTube Data API v3

#### OpenRouter API Key:
1. Go to [OpenRouter](https://openrouter.ai/)
2. Create an account or sign in
3. Generate an API key in the dashboard

#### YouTube OAuth Client ID (Optional):
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. In the same project where you enabled YouTube Data API
3. Create credentials for an OAuth 2.0 Client ID
4. Configure the OAuth consent screen
5. Set the application type as "Android"

### Building the Project

1. Clone the repository
```
git clone https://github.com/parwarkucher/MyYoutubeScrapper.git
```

2. Open the project in Android Studio

3. Sync Gradle files

4. Run the app on an emulator or physical device

5. Enter your API keys in the Settings screen before using the app

## Usage

1. Enter a search query in the home screen
2. Adjust search parameters if needed (time filter, max results)
3. Select AI model for summarization
4. Tap the search button
5. View the generated summaries and video list
6. Tap on videos to see individual video summaries

## License

MIT License

Copyright (c) 2025 parwarkucher

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

