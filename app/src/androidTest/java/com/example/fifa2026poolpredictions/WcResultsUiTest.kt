package com.example.fifa2026poolpredictions

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.fifa2026poolpredictions.data.model.Team
import com.example.fifa2026poolpredictions.theme.MyApplicationTheme
import com.example.fifa2026poolpredictions.ui.wcresults.GroupCard
import com.example.fifa2026poolpredictions.ui.wcresults.GroupData
import com.example.fifa2026poolpredictions.ui.wcresults.Standing
import org.junit.Rule
import org.junit.Test

class WcResultsUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun groupCard_displaysStandings() {
        val t1 = Team("1", "Mexico", "🇲🇽", 1, 0.0)
        val standings = listOf(
            Standing(t1, p = 1, w = 1, d = 0, l = 0, gf = 5, ga = 2, gd = 3, pts = 3)
        )
        val group = GroupData("A", emptyList(), standings)

        composeTestRule.setContent {
            MyApplicationTheme {
                GroupCard(group = group)
            }
        }

        composeTestRule.onNodeWithText("Group A").assertIsDisplayed()
        composeTestRule.onNodeWithText("Mexico").assertIsDisplayed()
        composeTestRule.onNodeWithText("Pts").assertIsDisplayed()
        composeTestRule.onNodeWithText("3").assertIsDisplayed()
        composeTestRule.onNodeWithText("+3").assertIsDisplayed()
    }
}
