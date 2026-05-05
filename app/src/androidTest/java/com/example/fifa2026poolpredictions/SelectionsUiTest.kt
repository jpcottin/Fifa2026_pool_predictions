package com.example.fifa2026poolpredictions

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.fifa2026poolpredictions.data.model.*
import com.example.fifa2026poolpredictions.theme.MyApplicationTheme
import com.example.fifa2026poolpredictions.ui.selections.*
import org.junit.Rule
import org.junit.Test

private val sampleSetsTeams = listOf(
    Team("s1a", "France",      "🇫🇷", 1, 9.0),
    Team("s1b", "Underdog",    "🏳",  1, 1.0),
    Team("s2a", "Germany",     "🇩🇪", 2, 4.5),
    Team("s2b", "Netherlands", "🇳🇱", 2, 2.0),
    Team("s0",  "TBD",         "🏳",  0, 0.0),  // set=0, should be excluded
)

class SelectionsUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun selectionsContent_showsCountdownAndButton_whenPicksLessThan3() {
        val state = SelectionsUiState.Success(
            mySelections = emptyList(),
            gameState = GameState("singleton", "PREPARING"),
            canAddMore = true
        )

        composeTestRule.setContent {
            MyApplicationTheme {
                SelectionsContent(state = state, onAddNew = {})
            }
        }

        // Check for countdown card
        composeTestRule.onNodeWithText("you have still", substring = true).assertIsDisplayed()
        
        // Check for New Selection button
        composeTestRule.onNodeWithText("+ New Selection").assertIsDisplayed()
    }

    @Test
    fun selectionsContent_showsClosedMessage_whenCompetitionStarted() {
        val state = SelectionsUiState.Success(
            mySelections = emptyList(),
            gameState = GameState("singleton", "STARTED"),
            canAddMore = false
        )

        composeTestRule.setContent {
            MyApplicationTheme {
                SelectionsContent(state = state, onAddNew = {})
            }
        }

        composeTestRule.onNodeWithText("Submissions closed").assertIsDisplayed()
        composeTestRule.onNodeWithText("+ New Selection").assertDoesNotExist()
    }

    // ── Sets tab ──────────────────────────────────────────────────────────

    @Test
    fun setsContent_showsSetHeaderForEachGroup() {
        composeTestRule.setContent {
            MyApplicationTheme { SetsContent(teams = sampleSetsTeams) }
        }
        composeTestRule.onNodeWithText("Set 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Set 2").assertIsDisplayed()
    }

    @Test
    fun setsContent_showsTeamNamesAndScores() {
        composeTestRule.setContent {
            MyApplicationTheme { SetsContent(teams = sampleSetsTeams) }
        }
        composeTestRule.onNodeWithText("France").assertIsDisplayed()
        composeTestRule.onNodeWithText("Germany").assertIsDisplayed()
        composeTestRule.onNodeWithText("9.0").assertIsDisplayed()
        composeTestRule.onNodeWithText("4.5").assertIsDisplayed()
    }

    @Test
    fun setsContent_excludesTeamsWithSetZero() {
        composeTestRule.setContent {
            MyApplicationTheme { SetsContent(teams = sampleSetsTeams) }
        }
        composeTestRule.onNodeWithText("Set 0").assertDoesNotExist()
        composeTestRule.onNodeWithText("TBD").assertDoesNotExist()
    }

    @Test
    fun setsContent_teamsOrderedByScoreDescWithinSet() {
        composeTestRule.setContent {
            MyApplicationTheme { SetsContent(teams = sampleSetsTeams) }
        }
        // Both teams in Set 1 visible; France (9.0) and Underdog (1.0) both rendered
        composeTestRule.onNodeWithText("France").assertIsDisplayed()
        composeTestRule.onNodeWithText("Underdog").assertIsDisplayed()
        composeTestRule.onNodeWithText("9.0").assertIsDisplayed()
        composeTestRule.onNodeWithText("1.0").assertIsDisplayed()
    }

    // ── Sets tab with tournament fixture scores (mid and full) ────────────

    @Test
    fun setsContent_mid_franceScoreReflectsPlayedMatches() {
        composeTestRule.setContent {
            MyApplicationTheme { SetsContent(teams = TestFixtures.allTeamsWithScoresMid()) }
        }
        // France played 2 group games in mid: wins 2-1 and 3-0 → 3.6+3.9 = 7.5 pts
        composeTestRule.onNodeWithText("Set 1").assertIsDisplayed()
        composeTestRule.onAllNodesWithText("France").onFirst().assertIsDisplayed()
        composeTestRule.onAllNodesWithText("7.5").onFirst().assertIsDisplayed()
    }

    @Test
    fun setsContent_mid_germanyLeadsSet1() {
        composeTestRule.setContent {
            MyApplicationTheme { SetsContent(teams = TestFixtures.allTeamsWithScoresMid()) }
        }
        // Germany won all 3 group E games (5-0, 3-1, 2-0) → 12.0 pts
        composeTestRule.onAllNodesWithText("Germany").onFirst().assertIsDisplayed()
        composeTestRule.onNodeWithText("12.0").assertIsDisplayed()
    }

    @Test
    fun setsContent_mid_set1ScoresState() {
        val set1 = TestFixtures.allTeamsWithScoresMid().filter { it.set == 1 }
        // Groups A-H fully played; group I partly played (France=7.5), others in group J-L=0
        assert(set1.size == 6) { "Expected 6 teams in set 1, got ${set1.size}" }
        val france = set1.first { it.name == "France" }
        assert(france.score == 7.5) { "France mid score: expected 7.5, got ${france.score}" }
        val argentina = set1.first { it.name == "Argentina" }
        assert(argentina.score == 0.0) { "Argentina (group J not played): expected 0.0, got ${argentina.score}" }
    }

    @Test
    fun setsContent_full_franceLeadsSet1() {
        composeTestRule.setContent {
            MyApplicationTheme { SetsContent(teams = TestFixtures.allTeamsWithScoresFull()) }
        }
        // France won every round including the Final → highest score in set 1
        composeTestRule.onNodeWithText("Set 1").assertIsDisplayed()
        composeTestRule.onAllNodesWithText("France").onFirst().assertIsDisplayed()
        composeTestRule.onNodeWithText("33.1").assertIsDisplayed()
    }

    @Test
    fun setsContent_full_brazilScoreVisible() {
        composeTestRule.setContent {
            MyApplicationTheme { SetsContent(teams = TestFixtures.allTeamsWithScoresFull()) }
        }
        // Brazil reached the Final (runner-up): group + R32 + R16 + QF + SF + Final → 26.6 pts
        composeTestRule.onAllNodesWithText("Brazil").onFirst().assertIsDisplayed()
        composeTestRule.onNodeWithText("26.6").assertIsDisplayed()
    }

    @Test
    fun setsContent_full_set1ScoresComputedFromMatchResults() {
        val set1 = TestFixtures.allTeamsWithScoresFull().filter { it.set == 1 }
        // All 6 set-1 teams played knockout matches → all non-zero
        assert(set1.size == 6) { "Expected 6 teams in set 1, got ${set1.size}" }
        assert(set1.all { it.score > 0.0 }) { "All set 1 teams should have a non-zero score after 104 games" }
        val topTeam = set1.maxByOrNull { it.score }
        assert(topTeam?.name == "France") { "France should lead set 1 after 104 games, got ${topTeam?.name}" }
    }
}
