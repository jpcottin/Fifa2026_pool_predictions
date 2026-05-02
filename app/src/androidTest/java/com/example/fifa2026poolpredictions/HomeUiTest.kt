package com.example.fifa2026poolpredictions

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.fifa2026poolpredictions.theme.MyApplicationTheme
import com.example.fifa2026poolpredictions.ui.home.HomeContent
import org.junit.Rule
import org.junit.Test

class HomeUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // ── Preparing state (selections open) ────────────────────────────────

    @Test
    fun home_preparing_showsTitleAndTagline() {
        composeTestRule.setContent {
            MyApplicationTheme { HomeContent(state = TestFixtures.homeStatePreparing(), onPickTeams = {}) }
        }
        composeTestRule.onNodeWithText("FIFA World Cup 2026", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Follow Every Match", substring = true).assertIsDisplayed()
    }

    @Test
    fun home_preparing_showsSelectionsOpenBadge() {
        composeTestRule.setContent {
            MyApplicationTheme { HomeContent(state = TestFixtures.homeStatePreparing(), onPickTeams = {}) }
        }
        composeTestRule.onNodeWithText("Selections Open", substring = true).assertIsDisplayed()
    }

    @Test
    fun home_preparing_showsPickMyTeamsButton() {
        composeTestRule.setContent {
            MyApplicationTheme { HomeContent(state = TestFixtures.homeStatePreparing(), onPickTeams = {}) }
        }
        composeTestRule.onNodeWithText("Pick My 8 Teams", substring = true).assertIsDisplayed()
    }

    @Test
    fun home_preparing_showsStatCards() {
        composeTestRule.setContent {
            MyApplicationTheme { HomeContent(state = TestFixtures.homeStatePreparing(), onPickTeams = {}) }
        }
        composeTestRule.onNodeWithText("Players").assertIsDisplayed()
        composeTestRule.onNodeWithText("Selections").assertIsDisplayed()
        composeTestRule.onNodeWithText("12").assertIsDisplayed()
        composeTestRule.onNodeWithText("27").assertIsDisplayed()
    }

    @Test
    fun home_preparing_showsCountdownValues() {
        composeTestRule.setContent {
            MyApplicationTheme { HomeContent(state = TestFixtures.homeStatePreparing(), onPickTeams = {}) }
        }
        composeTestRule.onNodeWithText("41d").assertIsDisplayed()
        composeTestRule.onNodeWithText("79d").assertIsDisplayed()
        composeTestRule.onNodeWithText("To Kickoff").assertIsDisplayed()
        composeTestRule.onNodeWithText("To Final").assertIsDisplayed()
    }

    @Test
    fun home_preparing_showsHowItWorksSection() {
        composeTestRule.setContent {
            MyApplicationTheme { HomeContent(state = TestFixtures.homeStatePreparing(), onPickTeams = {}) }
        }
        composeTestRule.onNodeWithText("How It Works").assertIsDisplayed()
        composeTestRule.onNodeWithText("Pick Your 8 Teams").assertIsDisplayed()
        composeTestRule.onNodeWithText("Earn Points").assertIsDisplayed()
        composeTestRule.onNodeWithText("Your Score = Sum of Your 8 Teams").assertIsDisplayed()
        composeTestRule.onNodeWithText("Follow Live Results").assertIsDisplayed()
    }

    @Test
    fun home_preparing_howItWorksShowsScoringRules() {
        composeTestRule.setContent {
            MyApplicationTheme { HomeContent(state = TestFixtures.homeStatePreparing(), onPickTeams = {}) }
        }
        composeTestRule.onNodeWithText("Win: +3 pts", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Draw: +1 pt", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Goals (group stage)", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Goals (knockout)", substring = true).assertIsDisplayed()
    }

    // ── Started state (competition in progress) ──────────────────────────

    @Test
    fun home_started_showsCompetitionInProgressBadge() {
        composeTestRule.setContent {
            MyApplicationTheme { HomeContent(state = TestFixtures.homeStateStarted(), onPickTeams = {}) }
        }
        composeTestRule.onNodeWithText("Competition in Progress", substring = true).assertIsDisplayed()
    }

    @Test
    fun home_started_hidesPickMyTeamsButton() {
        composeTestRule.setContent {
            MyApplicationTheme { HomeContent(state = TestFixtures.homeStateStarted(), onPickTeams = {}) }
        }
        composeTestRule.onNodeWithText("Pick My 8 Teams", substring = true).assertDoesNotExist()
    }

    @Test
    fun home_started_showsOngoingForKickoff() {
        composeTestRule.setContent {
            MyApplicationTheme { HomeContent(state = TestFixtures.homeStateStarted(), onPickTeams = {}) }
        }
        composeTestRule.onNodeWithText("Live").assertIsDisplayed()
        composeTestRule.onNodeWithText("14d").assertIsDisplayed()
    }

    // ── Finished state (tournament over) ─────────────────────────────────

    @Test
    fun home_finished_showsDoneForFinal() {
        composeTestRule.setContent {
            MyApplicationTheme { HomeContent(state = TestFixtures.homeStateFinished(), onPickTeams = {}) }
        }
        composeTestRule.onNodeWithText("Done!").assertIsDisplayed()
    }

    // ── State-level sanity ────────────────────────────────────────────────

    @Test
    fun home_preparingState_hasCorrectCounts() {
        val state = TestFixtures.homeStatePreparing()
        assert(state.totalPlayers == 12)    { "Expected 12 players" }
        assert(state.totalSelections == 27) { "Expected 27 selections" }
        assert(state.gameState == "PREPARING")
        assert(state.daysToKickoff == 41L)
        assert(state.daysToFinal == 79L)
        assert(!state.isLoading)
        assert(state.error == null)
    }

    @Test
    fun home_startedState_kickoffIsZero() {
        val state = TestFixtures.homeStateStarted()
        assert(state.daysToKickoff == 0L) { "Kickoff should be 0 once started" }
        assert(state.daysToFinal > 0L)   { "Final should still be in the future" }
        assert(state.gameState == "STARTED")
    }

    @Test
    fun home_finishedState_bothCountdownsZero() {
        val state = TestFixtures.homeStateFinished()
        assert(state.daysToKickoff == 0L)
        assert(state.daysToFinal == 0L)
    }
}
