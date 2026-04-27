package com.example.fifa2026poolpredictions

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.fifa2026poolpredictions.data.model.GameState
import com.example.fifa2026poolpredictions.theme.MyApplicationTheme
import com.example.fifa2026poolpredictions.ui.admin.AdminContent
import com.example.fifa2026poolpredictions.ui.admin.AdminUiState
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
}
