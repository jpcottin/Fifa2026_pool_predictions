# FIFA World Cup 2026 pool prediction game - Android Client

This is the Android companion app for **Pick Your 8**, a FIFA World Cup 2026 pool prediction game. 

Users pick 8 teams (one from each ranked set) and earn points based on their real-world performance throughout the tournament.

## Project Context
This app serves as the mobile UI Client for the service hosted at:
[https://github.com/jpcottin/fifa2026/](https://github.com/jpcottin/fifa2026/)


## Features
- **Google Authentication:** Secure login using modern Android Credential Manager.
- **Leaderboard:** Real-time global rankings of all user selections.
- **WC Results:** Live standings and match results for all 12 World Cup groups.
- **My Picks:** Manage and track your own team selections with live countdowns to the tournament start.
- **Adaptive Layout:** Optimized for Phones, Tablets, and Foldables using Jetpack Compose.
- **Admin Tools:** Toggle competition state and switch between local/production environments for testing.

## Tech Stack
- **Language:** Kotlin
- **UI Framework:** Jetpack Compose (Modern declarative UI)
- **Architecture:** MVVM (Model-View-ViewModel)
- **Networking:** Retrofit & OkHttp
- **Serialization:** Kotlinx Serialization
- **Navigation:** Navigation3 (modern Compose-first navigation)
- **Dependency Injection:** Manual factory-based injection
- **Local Storage:** DataStore (for tokens) and SharedPreferences (for settings)
- **Testing:** JUnit 4 & Compose Test Rule

## Getting Started
1. Clone the repository.
2. Open in Android Studio.
3. Configure your local server URL in the **Admin** tab settings if testing against a local backend (`http://10.0.2.2:3000`).
4. Build and run!

---
*Created for the 2026 FIFA World Cup.*
