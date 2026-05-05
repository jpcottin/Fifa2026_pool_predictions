package com.example.fifa2026poolpredictions

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.fifa2026poolpredictions.data.model.*
import com.example.fifa2026poolpredictions.theme.MyApplicationTheme
import com.example.fifa2026poolpredictions.ui.admin.AdminContent
import com.example.fifa2026poolpredictions.ui.admin.AdminUiState
import com.example.fifa2026poolpredictions.ui.admin.MatchEditDialog
import com.example.fifa2026poolpredictions.ui.admin.MatchEditState
import org.junit.Rule
import org.junit.Test

class AdminUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun adminContent_displaysServerSettings() {
        val state = AdminUiState.Success(
            gameState = GameState("singleton", "PREPARING"),
            matches = emptyList(),
            users = emptyList(),
            apiBaseUrl = "http://10.0.2.2:3000/"
        )

        composeTestRule.setContent {
            MyApplicationTheme {
                AdminContent(
                    state = state,
                    onToggleGameState = {},
                    onEditMatch = {},
                    onSetBaseUrl = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Server Settings").assertIsDisplayed()
        composeTestRule.onNodeWithText("API Base URL").assertIsDisplayed()
        composeTestRule.onNodeWithText("Local (10.0.2.2:3000)").assertIsDisplayed()
        composeTestRule.onNodeWithText("Production (Heroku)").assertIsDisplayed()
    }

    @Test
    fun adminContent_displaysGameStateToggle() {
        val state = AdminUiState.Success(
            gameState = GameState("singleton", "PREPARING"),
            matches = emptyList(),
            users = emptyList(),
            apiBaseUrl = "http://10.0.2.2:3000/"
        )

        composeTestRule.setContent {
            MyApplicationTheme {
                AdminContent(
                    state = state,
                    onToggleGameState = {},
                    onEditMatch = {},
                    onSetBaseUrl = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Competition Started").assertIsDisplayed()
        composeTestRule.onNodeWithText("In preparation. Users can still pick teams.").assertIsDisplayed()
    }

    // ── MatchEditDialog ET/PK controls ────────────────────────────────────

    private val kTeam1 = Team("k1", "Brazil",  "🇧🇷", 1, 0.0)
    private val kTeam2 = Team("k2", "Germany", "🇩🇪", 1, 0.0)

    @Test
    fun matchEditDialog_knockoutWithResult_showsExtraTimeCheckbox() {
        val editState = MatchEditState(
            match = Match("m1", "k1", "k2", kTeam1, kTeam2, "2026-07-09", Phase.QF, MatchResult.TEAM1, 2, 1),
            winner = MatchResult.TEAM1
        )
        composeTestRule.setContent {
            MyApplicationTheme {
                MatchEditDialog(editState = editState, onDismiss = {}, onGoalsChange = { _, _ -> },
                    onWinnerChange = {}, onExtraTimeChange = {}, onPkGoalsChange = { _, _ -> }, onSave = {})
            }
        }
        composeTestRule.onNodeWithText("Extra time played").assertIsDisplayed()
    }

    @Test
    fun matchEditDialog_groupMatch_noExtraTimeCheckbox() {
        val editState = MatchEditState(
            match = Match("m2", "k1", "k2", kTeam1, kTeam2, "2026-06-15", Phase.GROUP, MatchResult.TEAM1, 2, 0),
            winner = MatchResult.TEAM1
        )
        composeTestRule.setContent {
            MyApplicationTheme {
                MatchEditDialog(editState = editState, onDismiss = {}, onGoalsChange = { _, _ -> },
                    onWinnerChange = {}, onExtraTimeChange = {}, onPkGoalsChange = { _, _ -> }, onSave = {})
            }
        }
        composeTestRule.onNodeWithText("Extra time played").assertDoesNotExist()
    }

    @Test
    fun matchEditDialog_extraTimeDrawShowsPkFields() {
        val editState = MatchEditState(
            match = Match("m3", "k1", "k2", kTeam1, kTeam2, "2026-07-14", Phase.SF, MatchResult.DRAW, 1, 1),
            winner = MatchResult.DRAW,
            extraTime = true
        )
        composeTestRule.setContent {
            MyApplicationTheme {
                MatchEditDialog(editState = editState, onDismiss = {}, onGoalsChange = { _, _ -> },
                    onWinnerChange = {}, onExtraTimeChange = {}, onPkGoalsChange = { _, _ -> }, onSave = {})
            }
        }
        composeTestRule.onNodeWithText("Extra time played").assertIsDisplayed()
        composeTestRule.onNodeWithText("Penalty kicks").assertIsDisplayed()
    }

    @Test
    fun matchEditDialog_extraTimeWin_noPkFields() {
        val editState = MatchEditState(
            match = Match("m4", "k1", "k2", kTeam1, kTeam2, "2026-07-09", Phase.QF, MatchResult.TEAM1, 2, 1),
            winner = MatchResult.TEAM1,
            extraTime = true
        )
        composeTestRule.setContent {
            MyApplicationTheme {
                MatchEditDialog(editState = editState, onDismiss = {}, onGoalsChange = { _, _ -> },
                    onWinnerChange = {}, onExtraTimeChange = {}, onPkGoalsChange = { _, _ -> }, onSave = {})
            }
        }
        composeTestRule.onNodeWithText("Extra time played").assertIsDisplayed()
        composeTestRule.onNodeWithText("Penalty kicks").assertDoesNotExist()
    }
}
