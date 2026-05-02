package com.example.fifa2026poolpredictions

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Column
import com.example.fifa2026poolpredictions.theme.MyApplicationTheme
import com.example.fifa2026poolpredictions.ui.wcresults.GroupCard
import org.junit.Rule
import org.junit.Test

class WcResultsUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // Helper: render all 12 groups in a scrollable Column (all items composed upfront)
    @Composable
    private fun AllGroupsContent(stateIsMid: Boolean) {
        val state = if (stateIsMid) TestFixtures.wcResultsStateMid() else TestFixtures.wcResultsStateFull()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            state.groups.forEach { group -> GroupCard(group) }
        }
    }

    // ── GroupCard unit tests ───────────────────────────────────────────────

    @Test
    fun groupCard_displaysStandingsHeadersAndLegend() {
        composeTestRule.setContent {
            MyApplicationTheme {
                GroupCard(group = TestFixtures.wcResultsStateFull().groups.first { it.letter == "A" })
            }
        }
        composeTestRule.onNodeWithText("Group A").assertIsDisplayed()
        composeTestRule.onNodeWithText("Pts").assertIsDisplayed()
        composeTestRule.onNodeWithText("GD").assertIsDisplayed()
        composeTestRule.onNodeWithText("Advances").assertIsDisplayed()
        composeTestRule.onNodeWithText("May advance").assertIsDisplayed()
    }

    @Test
    fun groupCard_groupA_mexicoLeadsWithNinePoints() {
        composeTestRule.setContent {
            MyApplicationTheme {
                GroupCard(group = TestFixtures.wcResultsStateFull().groups.first { it.letter == "A" })
            }
        }
        // Mexico appears in standings row and 3 match rows → use onFirst
        composeTestRule.onAllNodesWithText("Mexico").onFirst().assertIsDisplayed()
        composeTestRule.onNodeWithText("9").assertIsDisplayed()   // pts (unique in group A)
        composeTestRule.onNodeWithText("+5").assertIsDisplayed()  // gd (unique in group A)
        composeTestRule.onAllNodesWithText("South Korea").onFirst().assertIsDisplayed()
    }

    @Test
    fun groupCard_groupC_allFourTeamsDisplayed() {
        composeTestRule.setContent {
            MyApplicationTheme {
                GroupCard(group = TestFixtures.wcResultsStateFull().groups.first { it.letter == "C" })
            }
        }
        composeTestRule.onNodeWithText("Group C").assertIsDisplayed()
        listOf("Brazil", "Morocco", "Haiti", "Scotland").forEach { team ->
            composeTestRule.onAllNodesWithText(team).onFirst().assertIsDisplayed()
        }
    }

    @Test
    fun groupCard_groupC_brazilLeadsWithSevenPoints() {
        composeTestRule.setContent {
            MyApplicationTheme {
                GroupCard(group = TestFixtures.wcResultsStateFull().groups.first { it.letter == "C" })
            }
        }
        // Brazil: 7 pts (2W+1D), unique in group C standings
        composeTestRule.onNodeWithText("7").assertIsDisplayed()
    }

    @Test
    fun groupCard_mid_groupI_showsPartialStandings() {
        composeTestRule.setContent {
            MyApplicationTheme {
                GroupCard(group = TestFixtures.wcResultsStateMid().groups.first { it.letter == "I" })
            }
        }
        composeTestRule.onNodeWithText("Group I").assertIsDisplayed()
        listOf("France", "Senegal", "Norway", "Iraq").forEach { team ->
            composeTestRule.onAllNodesWithText(team).onFirst().assertIsDisplayed()
        }
        // France played 2 matches: pts = 6
        composeTestRule.onNodeWithText("6").assertIsDisplayed()
    }

    @Test
    fun groupCard_mid_groupJ_showsZeroStandingsAllTeamsPresent() {
        composeTestRule.setContent {
            MyApplicationTheme {
                GroupCard(group = TestFixtures.wcResultsStateMid().groups.first { it.letter == "J" })
            }
        }
        composeTestRule.onNodeWithText("Group J").assertIsDisplayed()
        listOf("Argentina", "Algeria", "Austria", "Jordan").forEach { team ->
            composeTestRule.onAllNodesWithText(team).onFirst().assertIsDisplayed()
        }
    }

    @Test
    fun groupCard_mid_groupI_twoMatchesPlayedWithScores() {
        composeTestRule.setContent {
            MyApplicationTheme {
                GroupCard(group = TestFixtures.wcResultsStateMid().groups.first { it.letter == "I" })
            }
        }
        // France vs Senegal 2-1 and France vs Norway 3-0 are played
        composeTestRule.onAllNodesWithText("2 – 1").onFirst().assertIsDisplayed()
        composeTestRule.onAllNodesWithText("3 – 0").onFirst().assertIsDisplayed()
        // Upcoming matches show a date (2026-06-22)
        composeTestRule.onAllNodesWithText("2026-06-22").onFirst().assertIsDisplayed()
    }

    @Test
    fun groupCard_full_groupA_allMatchesShowScores() {
        composeTestRule.setContent {
            MyApplicationTheme {
                GroupCard(group = TestFixtures.wcResultsStateFull().groups.first { it.letter == "A" })
            }
        }
        // All 6 matches are played → Mexico vs South Africa 2-0 is the first match in group A
        composeTestRule.onAllNodesWithText("2 – 0").onFirst().assertIsDisplayed()
        // No date string visible (scores replace dates for played matches)
        composeTestRule.onNodeWithText("2026-06-16").assertDoesNotExist()
    }

    // ── All 12 groups visible (scrollable Column so all items are composed) ─

    @Test
    fun wcResults_mid_allTwelveGroupsVisible() {
        composeTestRule.setContent {
            MyApplicationTheme { AllGroupsContent(stateIsMid = true) }
        }
        ('A'..'L').forEach { letter ->
            composeTestRule.onNodeWithText("Group $letter").performScrollTo().assertIsDisplayed()
        }
    }

    @Test
    fun wcResults_full_allTwelveGroupsVisible() {
        composeTestRule.setContent {
            MyApplicationTheme { AllGroupsContent(stateIsMid = false) }
        }
        ('A'..'L').forEach { letter ->
            composeTestRule.onNodeWithText("Group $letter").performScrollTo().assertIsDisplayed()
        }
    }

    // ── State-level assertions (fixture sanity checks) ─────────────────────

    @Test
    fun wcResultsState_mid_fiftyMatchesPlayed() {
        val state = TestFixtures.wcResultsStateMid()
        val playedGroup = state.groups
            .flatMap { it.groupMatches }
            .count { it.winner.name != "UPCOMING" }
        assert(playedGroup == 50) { "Expected 50 played group matches, got $playedGroup" }
    }

    @Test
    fun wcResultsState_full_allGroupMatchesPlayed() {
        val state = TestFixtures.wcResultsStateFull()
        val totalGroup = state.groups.sumOf { it.groupMatches.size }
        assert(totalGroup == 72) { "Expected 72 group matches total, got $totalGroup" }
        val allPlayed = state.groups.flatMap { it.groupMatches }.all { it.winner.name != "UPCOMING" }
        assert(allPlayed) { "All group matches should be played in full scenario" }
    }

    @Test
    fun wcResultsState_full_knockoutHasSixPhases() {
        val state = TestFixtures.wcResultsStateFull()
        assert(state.knockoutByPhase.size == 6) {
            "Expected 6 knockout phases, got ${state.knockoutByPhase.size}"
        }
    }

    @Test
    fun wcResultsState_full_brazilWinsFinal() {
        val state = TestFixtures.wcResultsStateFull()
        val finalMatch = state.knockoutByPhase.entries
            .first { it.key.name == "FINAL" }.value.first()
        assert(finalMatch.winner.name == "TEAM1") { "Expected Brazil (TEAM1) to win the Final" }
        assert(finalMatch.team1.name == "Brazil") { "Expected Brazil as team1 in the Final" }
    }

    @Test
    fun wcResultsState_mid_allKnockoutMatchesUpcoming() {
        val state = TestFixtures.wcResultsStateMid()
        val anyPlayed = state.knockoutByPhase.values.flatten().any { it.winner.name != "UPCOMING" }
        assert(!anyPlayed) { "All knockout matches should be upcoming in mid scenario" }
    }
}
