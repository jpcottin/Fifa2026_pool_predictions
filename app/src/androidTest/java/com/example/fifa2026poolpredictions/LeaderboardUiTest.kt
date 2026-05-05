package com.example.fifa2026poolpredictions

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.fifa2026poolpredictions.theme.MyApplicationTheme
import com.example.fifa2026poolpredictions.ui.leaderboard.LeaderboardContent
import org.junit.Rule
import org.junit.Test

class LeaderboardUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // ── Mid-tournament (50 games played) ──────────────────────────────────

    @Test
    fun leaderboard_mid_displaysMatchStats() {
        composeTestRule.setContent {
            MyApplicationTheme {
                LeaderboardContent(state = TestFixtures.leaderboardStateMid(), onToggleMine = {})
            }
        }
        composeTestRule.onNodeWithText("Matches Played").assertIsDisplayed()
        composeTestRule.onNodeWithText("Matches to Go").assertIsDisplayed()
        composeTestRule.onNodeWithText("50").assertIsDisplayed()
        composeTestRule.onNodeWithText("54").assertIsDisplayed()
    }

    @Test
    fun leaderboard_mid_displaysLeaderboardTitle() {
        composeTestRule.setContent {
            MyApplicationTheme {
                LeaderboardContent(state = TestFixtures.leaderboardStateMid(), onToggleMine = {})
            }
        }
        composeTestRule.onNodeWithText("Leaderboard").assertIsDisplayed()
        composeTestRule.onNodeWithText("Mine").assertIsDisplayed()
        composeTestRule.onNodeWithText("All").assertIsDisplayed()
    }

    @Test
    fun leaderboard_mid_rankOneHasSingleGold() {
        composeTestRule.setContent {
            MyApplicationTheme {
                LeaderboardContent(state = TestFixtures.leaderboardStateMid(), onToggleMine = {})
            }
        }
        // s2 Alice Safe leads alone (47.0), no tie → exactly one gold
        composeTestRule.onAllNodesWithText("🥇").assertCountEquals(1)
        composeTestRule.onNodeWithText("🥈").assertIsDisplayed()
        composeTestRule.onNodeWithText("🥉").assertIsDisplayed()
    }

    @Test
    fun leaderboard_mid_displaysScoreBadgesForTopSelections() {
        composeTestRule.setContent {
            MyApplicationTheme {
                LeaderboardContent(state = TestFixtures.leaderboardStateMid(), onToggleMine = {})
            }
        }
        // s2 Alice Safe=47.0, s7 Diana Rocket=46.7, s1 Alice Dream=45.0
        composeTestRule.onNodeWithText("47.0").assertIsDisplayed()
        composeTestRule.onNodeWithText("46.7").assertIsDisplayed()
        composeTestRule.onNodeWithText("45.0").assertIsDisplayed()
    }

    @Test
    fun leaderboard_mid_topSelectionsVisibleOnLoad() {
        composeTestRule.setContent {
            MyApplicationTheme {
                LeaderboardContent(state = TestFixtures.leaderboardStateMid(), onToggleMine = {})
            }
        }
        // Rank 1: Alice Safe (47.0), Rank 2: Diana Rocket (46.7), Rank 3: Alice Dream (45.0)
        composeTestRule.onNodeWithText("Alice Safe").assertIsDisplayed()
        composeTestRule.onNodeWithText("Diana Rocket").assertIsDisplayed()
        composeTestRule.onNodeWithText("Alice Dream").assertIsDisplayed()
    }

    @Test
    fun leaderboard_mid_showMineOnly_filtersToCurrentUser() {
        // Pass a pre-filtered state (only Alice's 2 selections, as the ViewModel would do)
        composeTestRule.setContent {
            MyApplicationTheme {
                LeaderboardContent(state = TestFixtures.leaderboardStateMidMineOnly(), onToggleMine = {})
            }
        }
        composeTestRule.onNodeWithText("My Selections").assertIsDisplayed()
        composeTestRule.onNodeWithText("Alice Dream").assertIsDisplayed()
        composeTestRule.onNodeWithText("Alice Safe").assertIsDisplayed()
        // Other players' selections must not appear
        composeTestRule.onNodeWithText("Bob Bold").assertDoesNotExist()
        composeTestRule.onNodeWithText("Hannah Rocket").assertDoesNotExist()
    }

    // ── State-level sanity (fixture verification, no UI rendering) ─────────

    @Test
    fun leaderboard_mid_sixteenSelections() {
        val state = TestFixtures.leaderboardStateMid()
        assert(state.ranked.size == 16) { "Expected 16 selections, got ${state.ranked.size}" }
    }

    @Test
    fun leaderboard_mid_eightUniquePlayersInRanking() {
        val state = TestFixtures.leaderboardStateMid()
        val userIds = state.ranked.map { it.selection.userId }.toSet()
        assert(userIds.size == 8) { "Expected 8 unique players, got ${userIds.size}" }
    }

    @Test
    fun leaderboard_mid_aliceHasTwoSelections() {
        val state = TestFixtures.leaderboardStateMid()
        val aliceSelections = state.ranked.filter { it.selection.userId == "u1" }
        assert(aliceSelections.size == 2) { "Alice (u1) should have 2 selections" }
    }

    @Test
    fun leaderboard_mid_rankingIsCorrectlyOrdered() {
        val state = TestFixtures.leaderboardStateMid()
        val scores = state.ranked.map { it.selection.score }
        val sorted = scores.sortedDescending()
        assert(scores == sorted) { "Ranked selections should be in descending score order" }
    }

    // ── Full tournament (104 games played) ────────────────────────────────

    @Test
    fun leaderboard_full_displaysMatchStats() {
        composeTestRule.setContent {
            MyApplicationTheme {
                LeaderboardContent(state = TestFixtures.leaderboardStateFull(), onToggleMine = {})
            }
        }
        composeTestRule.onNodeWithText("104").assertIsDisplayed()
        composeTestRule.onNodeWithText("0").assertIsDisplayed()
        composeTestRule.onNodeWithText("Matches Played").assertIsDisplayed()
        composeTestRule.onNodeWithText("Matches to Go").assertIsDisplayed()
    }

    @Test
    fun leaderboard_full_aliceDreamLeads() {
        composeTestRule.setContent {
            MyApplicationTheme {
                LeaderboardContent(state = TestFixtures.leaderboardStateFull(), onToggleMine = {})
            }
        }
        // Alice Dream leads with 91.1 after all 104 matches
        composeTestRule.onNodeWithText("Alice Dream").assertIsDisplayed()
        composeTestRule.onNodeWithText("91.1").assertIsDisplayed()
    }

    @Test
    fun leaderboard_full_medalsDisplayed() {
        composeTestRule.setContent {
            MyApplicationTheme {
                LeaderboardContent(state = TestFixtures.leaderboardStateFull(), onToggleMine = {})
            }
        }
        composeTestRule.onNodeWithText("🥇").assertIsDisplayed()
        composeTestRule.onNodeWithText("🥈").assertIsDisplayed()
        composeTestRule.onNodeWithText("🥉").assertIsDisplayed()
    }

    @Test
    fun leaderboard_full_rankingChangedFromMid() {
        val ranked = TestFixtures.rankedSelections(TestFixtures.selections104())
        assert(ranked.first().selection.id == "s1") {
            "Expected Alice Dream (s1) at rank 1 after 104 games, got ${ranked.first().selection.id}"
        }
        assert(ranked.first().rank == 1)
    }

    @Test
    fun leaderboard_full_eightPlayersWithTwoSelectionsEach() {
        val state = TestFixtures.leaderboardStateFull()
        assert(state.ranked.size == 16) { "Expected 16 selections total" }
        val playerCounts = state.ranked.groupBy { it.selection.userId }
        assert(playerCounts.size == 8) { "Expected 8 unique players" }
        playerCounts.values.forEach { sels ->
            assert(sels.size == 2) { "Each player should have exactly 2 selections" }
        }
    }
}
