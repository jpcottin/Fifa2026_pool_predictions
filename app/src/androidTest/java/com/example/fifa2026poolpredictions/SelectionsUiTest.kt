package com.example.fifa2026poolpredictions

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.fifa2026poolpredictions.data.model.*
import com.example.fifa2026poolpredictions.theme.MyApplicationTheme
import com.example.fifa2026poolpredictions.ui.selections.*
import org.junit.Rule
import org.junit.Test

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
}
