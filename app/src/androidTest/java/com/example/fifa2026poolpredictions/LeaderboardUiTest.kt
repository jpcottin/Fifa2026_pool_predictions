package com.example.fifa2026poolpredictions

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.fifa2026poolpredictions.data.model.Selection
import com.example.fifa2026poolpredictions.data.model.SelectionUser
import com.example.fifa2026poolpredictions.data.model.Team
import com.example.fifa2026poolpredictions.theme.MyApplicationTheme
import com.example.fifa2026poolpredictions.ui.leaderboard.LeaderboardContent
import com.example.fifa2026poolpredictions.ui.leaderboard.LeaderboardUiState
import com.example.fifa2026poolpredictions.ui.leaderboard.RankedSelection
import org.junit.Rule
import org.junit.Test

class LeaderboardUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun leaderboard_displaysRankedUsers() {
        val mockTeams = listOf(Team("1", "France", "🇫🇷", 1, 0.0))
        val rankedSelections = listOf(
            RankedSelection(
                Selection("s1", "Top Team", "u1", listOf("1"), 100.0, user = SelectionUser("User A")),
                1, mockTeams
            ),
            RankedSelection(
                Selection("s2", "Second Team", "u2", listOf("1"), 90.0, user = SelectionUser("User B")),
                2, mockTeams
            )
        )

        composeTestRule.setContent {
            MyApplicationTheme {
                LeaderboardContent(
                    state = LeaderboardUiState.Success(ranked = rankedSelections, showMineOnly = false, currentUserId = null, matchesPlayed = 4, matchesUpcoming = 100),
                    onToggleMine = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Leaderboard").assertIsDisplayed()
        composeTestRule.onNodeWithText("Top Team").assertIsDisplayed()
        composeTestRule.onNodeWithText("User A").assertIsDisplayed()
        composeTestRule.onNodeWithText("Second Team").assertIsDisplayed()
        composeTestRule.onNodeWithText("User B").assertIsDisplayed()
        
        // Check for medal or rank
        composeTestRule.onNodeWithText("🥇").assertIsDisplayed()
        composeTestRule.onNodeWithText("🥈").assertIsDisplayed()
    }
}
