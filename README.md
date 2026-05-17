# FIFA World Cup 2026 pool prediction game - Android Client

This is the Android companion app for **Pick Your 8**, a FIFA World Cup 2026 pool prediction game. 

Users pick 8 teams (one from each ranked set) and earn points based on their real-world performance throughout the tournament.

## Project Context
This app serves as the mobile UI Client for the service hosted at:
[https://github.com/jpcottin/fifa2026/](https://github.com/jpcottin/fifa2026/)


## Features
- **Google Authentication:** Secure login using modern Android Credential Manager.
- **Leagues:** Players are assigned to leagues by the admin via the web app. The app automatically loads the user's league(s) after sign-in and scopes all leaderboard and selections data to the active league. When a user belongs to multiple leagues, a filter-chip row appears in the Leaderboard screen to switch between them. Users without a league see a friendly prompt to contact their admin.
- **Leaderboard:** Real-time rankings scoped to the user's active league. Supports a league picker when the user belongs to multiple leagues.
- **Home:** Tournament overview with player/selection counts, countdowns to kickoff and the Final, game state badge, and a full "How It Works" scoring guide.
- **WC Results:** Live group standings, match results for all 12 groups, and a full knockout bracket (Round of 32 through Final) with a tab view on phones and a scaled canvas bracket on tablets/foldables.
- **My Picks:** Manage and track your own team selections with live countdowns to the tournament start. Includes a **Sets** tab showing all 8 ranked sets of 6 teams with their current tournament scores.
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
- **Testing:** JUnit 4 & Compose UI Test Rule — 112 instrumented UI tests covering Home, Matches (including extra time and penalty kick annotations), Leaderboard, WC Results (including knockout ET/PK annotations for both phone tab view and tablet canvas view), Selections (including MySelectionCard rank/score/flag rendering and Sets tab with tournament-accurate scores), and Admin (including ET/PK dialog controls) across mid-tournament and full-tournament fixture scenarios, verified on phone, foldable (unfolded), and tablet form factors. 102 local unit tests covering all ViewModels (including SelectionsViewModel and HomeViewModel). Plus 15 host-side screenshot tests (Compose Preview Screenshot Testing) across Phone, Foldable, and Tablet form factors.

## Android Development Skills Applied

This project uses Android CLI skills recommended by Google DevRel to enforce platform best practices:

- **edge-to-edge** — All screens use `WindowInsets.safeDrawing` via `contentPadding` so content scrolls behind system bars correctly.
- **adaptive** — `SetsContent` uses `GridCells.Adaptive(300.dp)` to automatically reflow from 1 column on phones to 2+ on tablets/foldables.
- **testing-setup** — Compose Preview Screenshot Testing (`validateDebugScreenshotTest`) covers 5 screens × 3 form factors (Phone, Foldable, Tablet), 15 reference images total.

## Getting Started
1. Clone the repository.
2. Open in Android Studio.
3. Configure your local server URL in the **Admin** tab settings if testing against a local backend (`http://10.0.2.2:3000`).
4. Build and run!

