package com.example.fifa2026poolpredictions

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import com.example.fifa2026poolpredictions.theme.MyApplicationTheme
import com.example.fifa2026poolpredictions.ui.login.LoginContent
import com.example.fifa2026poolpredictions.ui.login.LoginUiState
import org.junit.Rule
import org.junit.Test

class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loginScreen_displaysTitleAndButton() {
        composeTestRule.setContent {
            MyApplicationTheme {
                LoginContent(
                    isLoading = false,
                    errorMessage = null,
                    onSignIn = {}
                )
            }
        }

        composeTestRule.onNodeWithText("FIFA 2026").assertIsDisplayed()
        composeTestRule.onNodeWithText("Pool Predictions").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sign in with Google").assertIsDisplayed()
    }
}
