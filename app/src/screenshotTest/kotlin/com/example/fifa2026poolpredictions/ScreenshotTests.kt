package com.example.fifa2026poolpredictions

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import com.example.fifa2026poolpredictions.data.model.GameState
import com.example.fifa2026poolpredictions.data.model.Match
import com.example.fifa2026poolpredictions.data.model.MatchResult
import com.example.fifa2026poolpredictions.data.model.Phase
import com.example.fifa2026poolpredictions.data.model.Selection
import com.example.fifa2026poolpredictions.data.model.SelectionUser
import com.example.fifa2026poolpredictions.data.model.Team
import com.example.fifa2026poolpredictions.theme.MyApplicationTheme
import com.example.fifa2026poolpredictions.ui.leaderboard.LeaderboardContent
import com.example.fifa2026poolpredictions.ui.leaderboard.LeaderboardUiState
import com.example.fifa2026poolpredictions.ui.leaderboard.RankedSelection
import com.example.fifa2026poolpredictions.ui.matches.MatchSection
import com.example.fifa2026poolpredictions.ui.matches.MatchesList
import com.example.fifa2026poolpredictions.ui.selections.MySelection
import com.example.fifa2026poolpredictions.ui.selections.SelectionsContent
import com.example.fifa2026poolpredictions.ui.selections.SelectionsUiState
import com.example.fifa2026poolpredictions.ui.selections.SetsContent

@Preview(name = "Phone", device = Devices.PHONE, showBackground = true)
@Preview(name = "Foldable", device = Devices.FOLDABLE, showBackground = true)
@Preview(name = "Tablet", device = Devices.TABLET, showBackground = true)
annotation class FormFactorPreviews

private val sampleTeams = listOf(
    Team("1", "France", "🇫🇷", 1, 6.0),
    Team("2", "Brazil", "🇧🇷", 2, 4.0),
    Team("3", "Germany", "🇩🇪", 3, 3.0),
    Team("4", "Japan", "🇯🇵", 4, 3.0),
    Team("5", "Morocco", "🇲🇦", 5, 1.5),
    Team("6", "England", "🏴󠁧󠁢󠁥󠁮󠁧󠁿", 6, 1.0),
    Team("7", "USA", "🇺🇸", 7, 0.0),
    Team("8", "Portugal", "🇵🇹", 8, 0.0),
)

private val sampleSelections = listOf(
    MySelection(
        Selection("s1", "My First Choice", "u1", sampleTeams.map { it.id }, 18.5, user = SelectionUser("Alice")),
        rank = 1, teams = sampleTeams
    ),
    MySelection(
        Selection("s2", "Underdogs", "u1", sampleTeams.map { it.id }, 12.0, user = SelectionUser("Alice")),
        rank = 4, teams = sampleTeams
    ),
)

private val sampleRanked = listOf(
    RankedSelection(
        Selection("s1", "Champs 2026", "u1", sampleTeams.map { it.id }, 18.5, user = SelectionUser("Alice")),
        rank = 1, teams = sampleTeams
    ),
    RankedSelection(
        Selection("s2", "Underdogs", "u2", sampleTeams.map { it.id }, 14.0, user = SelectionUser("Bob")),
        rank = 2, teams = sampleTeams
    ),
    RankedSelection(
        Selection("s3", "Dark Horses", "u3", sampleTeams.map { it.id }, 10.0, user = SelectionUser("Carol")),
        rank = 3, teams = sampleTeams
    ),
)

private val sampleMatches = listOf(
    Match("m1", "1", "2", sampleTeams[0], sampleTeams[1], "2026-06-11", Phase.GROUP, MatchResult.TEAM1, 2, 0),
    Match("m2", "3", "4", sampleTeams[2], sampleTeams[3], "2026-06-12", Phase.GROUP, MatchResult.UPCOMING, 0, 0),
    Match("m3", "5", "6", sampleTeams[4], sampleTeams[5], "2026-06-13", Phase.GROUP, MatchResult.DRAW, 1, 1),
)

// ── My Picks (Selections) ────────────────────────────────────────────────────

@PreviewTest
@FormFactorPreviews
@Composable
fun SelectionsScreenPreview() {
    MyApplicationTheme {
        SelectionsContent(
            state = SelectionsUiState.Success(
                mySelections = sampleSelections,
                gameState = GameState("singleton", "STARTED"),
                canAddMore = false,
            ),
            onAddNew = {}
        )
    }
}

@PreviewTest
@FormFactorPreviews
@Composable
fun SelectionsAddablePreview() {
    MyApplicationTheme {
        SelectionsContent(
            state = SelectionsUiState.Success(
                mySelections = emptyList(),
                gameState = GameState("singleton", "PREPARING"),
                canAddMore = true,
            ),
            onAddNew = {}
        )
    }
}

// ── Sets Tab ─────────────────────────────────────────────────────────────────

@PreviewTest
@FormFactorPreviews
@Composable
fun SetsScreenPreview() {
    MyApplicationTheme {
        SetsContent(teams = sampleTeams)
    }
}

// ── Leaderboard ───────────────────────────────────────────────────────────────

@PreviewTest
@FormFactorPreviews
@Composable
fun LeaderboardScreenPreview() {
    MyApplicationTheme {
        LeaderboardContent(
            state = LeaderboardUiState.Success(
                ranked = sampleRanked,
                showMineOnly = false,
                currentUserId = "u1",
                matchesPlayed = 12,
                matchesUpcoming = 92,
                leagues = emptyList(),
                selectedLeagueId = null,
            ),
            onToggleMine = {}
        )
    }
}

// ── Matches ───────────────────────────────────────────────────────────────────

@PreviewTest
@FormFactorPreviews
@Composable
fun MatchesScreenPreview() {
    MyApplicationTheme {
        MatchesList(
            sections = listOf(MatchSection(Phase.GROUP, sampleMatches))
        )
    }
}
