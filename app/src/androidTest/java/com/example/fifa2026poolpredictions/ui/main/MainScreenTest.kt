package com.example.fifa2026poolpredictions.ui.main

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.fifa2026poolpredictions.ui.login.LoginContent
import org.junit.Rule
import org.junit.Test

class LoginScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun loginScreen_showsSignInButton() {
        composeTestRule.setContent {
            LoginContent(isLoading = false, errorMessage = null, onSignIn = {})
        }
        composeTestRule.onNodeWithText("Sign in with Google").assertIsDisplayed()
    }

    @Test
    fun loginScreen_showsErrorMessage() {
        val errorMsg = "Sign-in failed"
        composeTestRule.setContent {
            LoginContent(isLoading = false, errorMessage = errorMsg, onSignIn = {})
        }
        composeTestRule.onNodeWithText(errorMsg).assertIsDisplayed()
    }

    @Test
    fun loginScreen_showsLoadingIndicator_whenLoading() {
        composeTestRule.setContent {
            LoginContent(isLoading = true, errorMessage = null, onSignIn = {})
        }
        composeTestRule.onNodeWithText("Sign in with Google").assertDoesNotExist()
    }
}
