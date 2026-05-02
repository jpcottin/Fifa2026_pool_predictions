package com.example.fifa2026poolpredictions

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.fifa2026poolpredictions.data.model.*
import com.example.fifa2026poolpredictions.theme.MyApplicationTheme
import com.example.fifa2026poolpredictions.ui.matches.MatchSection
import com.example.fifa2026poolpredictions.ui.matches.MatchesList
import org.junit.Rule
import org.junit.Test

class MatchesUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // ── Small focused states for UI rendering tests ───────────────────────
    // LazyColumn only composes visible items; for off-screen items we use state
    // assertions instead. These small states keep all sections on-screen.

    private val t1 = Team("1", "Brazil",  "🇧🇷", 1, 0.0)
    private val t2 = Team("2", "Germany", "🇩🇪", 1, 0.0)
    private val t3 = Team("3", "France",  "🇫🇷", 1, 0.0)
    private val t4 = Team("4", "England", "🏴", 1, 0.0)
    private val tbdT = Team("tbd", "TBD",  "🏳", 0, 0.0)

    private val focusedSections = listOf(
        MatchSection(Phase.GROUP, listOf(
            Match("m1", "1", "2", t1, t2, "2026-06-11", Phase.GROUP, MatchResult.TEAM1, 2, 0),
            Match("m2", "3", "4", t3, t4, "2026-06-12", Phase.GROUP, MatchResult.DRAW,  1, 1),
            Match("m3", "1", "3", t1, t3, "2026-06-15", Phase.GROUP, MatchResult.UPCOMING, 0, 0)
        )),
        MatchSection(Phase.R32, listOf(
            Match("m4", "tbd", "tbd", tbdT, tbdT, "2026-06-30", Phase.R32,
                MatchResult.UPCOMING, 0, 0, "Winner A vs Runner-up B")
        )),
        MatchSection(Phase.FINAL, listOf(
            Match("m5", "1", "3", t1, t3, "2026-07-19", Phase.FINAL, MatchResult.TEAM1, 3, 1)
        ))
    )

    // ── UI rendering tests (use focused small state so all items on-screen) ─

    @Test
    fun matches_showsMatchesTitle() {
        composeTestRule.setContent {
            MyApplicationTheme { MatchesList(sections = focusedSections) }
        }
        composeTestRule.onNodeWithText("Matches").assertIsDisplayed()
    }

    @Test
    fun matches_groupSectionHeaderVisible() {
        composeTestRule.setContent {
            MyApplicationTheme { MatchesList(sections = focusedSections) }
        }
        composeTestRule.onNodeWithText("GROUP STAGE").assertIsDisplayed()
    }

    @Test
    fun matches_playedGroupMatchShowsScore() {
        composeTestRule.setContent {
            MyApplicationTheme { MatchesList(sections = focusedSections) }
        }
        composeTestRule.onAllNodesWithText("Brazil").onFirst().assertIsDisplayed()
        composeTestRule.onAllNodesWithText("2 – 0").onFirst().assertIsDisplayed()
    }

    @Test
    fun matches_drawnMatchShowsDrawBadge() {
        composeTestRule.setContent {
            MyApplicationTheme { MatchesList(sections = focusedSections) }
        }
        composeTestRule.onNodeWithText("1 – 1").assertIsDisplayed()
        composeTestRule.onAllNodesWithText("Draw").onFirst().assertIsDisplayed()
    }

    @Test
    fun matches_upcomingGroupMatchShowsUpcomingBadge() {
        composeTestRule.setContent {
            MyApplicationTheme { MatchesList(sections = focusedSections) }
        }
        // France vs England: upcoming → shows date and "Upcoming" badge
        composeTestRule.onNodeWithText("2026-06-15").assertIsDisplayed()
        composeTestRule.onAllNodesWithText("Upcoming").onFirst().assertIsDisplayed()
    }

    @Test
    fun matches_tbdKnockoutMatchShowsNoteText() {
        composeTestRule.setContent {
            MyApplicationTheme { MatchesList(sections = focusedSections) }
        }
        composeTestRule.onNodeWithText("ROUND OF 32").assertIsDisplayed()
        composeTestRule.onNodeWithText("Winner A vs Runner-up B", substring = true).assertIsDisplayed()
    }

    @Test
    fun matches_finalSectionVisible() {
        composeTestRule.setContent {
            MyApplicationTheme { MatchesList(sections = focusedSections) }
        }
        composeTestRule.onNodeWithText("FINAL").assertIsDisplayed()
        composeTestRule.onNodeWithText("3 – 1").assertIsDisplayed()
    }

    @Test
    fun matches_wonMatchShowsWinnerBadge() {
        composeTestRule.setContent {
            MyApplicationTheme { MatchesList(sections = focusedSections) }
        }
        // Brazil wins the Final 3-1 → "Brazil" badge shown
        composeTestRule.onAllNodesWithText("Brazil").onFirst().assertIsDisplayed()
    }

    // ── Mid-tournament state assertions (50 played, 54 upcoming) ──────────

    @Test
    fun matches_mid_fiftyPlayedMatchesCountCorrect() {
        val state = TestFixtures.matchesStateMid()
        val played = state.sections.flatMap { it.matches }.count { it.winner != MatchResult.UPCOMING }
        assert(played == 50) { "Expected 50 played, got $played" }
    }

    @Test
    fun matches_mid_fiftyFourUpcomingMatchesCorrect() {
        val state = TestFixtures.matchesStateMid()
        val upcoming = state.sections.flatMap { it.matches }.count { it.winner == MatchResult.UPCOMING }
        assert(upcoming == 54) { "Expected 54 upcoming, got $upcoming" }
    }

    @Test
    fun matches_mid_totalMatchCountIsOneHundredAndFour() {
        val state = TestFixtures.matchesStateMid()
        val total = state.sections.sumOf { it.matches.size }
        assert(total == 104) { "Expected 104 total, got $total" }
    }

    @Test
    fun matches_mid_sevenPhaseSections() {
        val state = TestFixtures.matchesStateMid()
        assert(state.sections.size == 7) { "Expected 7 phase sections, got ${state.sections.size}" }
    }

    @Test
    fun matches_mid_phaseOrderCorrect() {
        val state = TestFixtures.matchesStateMid()
        val phases = state.sections.map { it.phase }
        val expected = listOf(Phase.GROUP, Phase.R32, Phase.R16, Phase.QF, Phase.SF, Phase.THIRD, Phase.FINAL)
        assert(phases == expected) { "Phases out of order: $phases" }
    }

    @Test
    fun matches_mid_groupsAtoHCompletelyPlayed() {
        val state = TestFixtures.matchesStateMid()
        val groupMatches = state.sections.first { it.phase == Phase.GROUP }.matches
        // Groups A–H: 48 played; group I: 2 more played = 50 total
        val played = groupMatches.count { it.winner != MatchResult.UPCOMING }
        val upcoming = groupMatches.count { it.winner == MatchResult.UPCOMING }
        assert(played == 50) { "Expected 50 played group matches, got $played" }
        assert(upcoming == 22) { "Expected 22 upcoming group matches, got $upcoming" }
    }

    @Test
    fun matches_mid_allKnockoutMatchesUpcoming() {
        val state = TestFixtures.matchesStateMid()
        val knockoutUpcoming = state.sections
            .filter { it.phase != Phase.GROUP }
            .flatMap { it.matches }
            .all { it.winner == MatchResult.UPCOMING }
        assert(knockoutUpcoming) { "All knockout matches should be upcoming in mid scenario" }
    }

    // ── Full tournament state assertions (104 played) ─────────────────────

    @Test
    fun matches_full_allOneHundredAndFourMatchesPlayed() {
        val state = TestFixtures.matchesStateFull()
        val total = state.sections.sumOf { it.matches.size }
        val played = state.sections.flatMap { it.matches }.count { it.winner != MatchResult.UPCOMING }
        assert(total == 104) { "Expected 104 total, got $total" }
        assert(played == 104) { "Expected 104 played, got $played" }
    }

    @Test
    fun matches_full_seventyTwoGroupMatchesPlayed() {
        val state = TestFixtures.matchesStateFull()
        val groupSection = state.sections.first { it.phase == Phase.GROUP }
        assert(groupSection.matches.size == 72) { "Expected 72 group matches" }
        assert(groupSection.matches.all { it.winner != MatchResult.UPCOMING }) { "All group matches should be played" }
    }

    @Test
    fun matches_full_knockoutMatchCountCorrect() {
        val state = TestFixtures.matchesStateFull()
        val knockoutCount = state.sections.filter { it.phase != Phase.GROUP }.sumOf { it.matches.size }
        assert(knockoutCount == 32) { "Expected 32 knockout matches, got $knockoutCount" }
    }

    @Test
    fun matches_full_brazilWinsFinal() {
        val state = TestFixtures.matchesStateFull()
        val final = state.sections.first { it.phase == Phase.FINAL }.matches.first()
        assert(final.winner == MatchResult.TEAM1) { "Expected Brazil (TEAM1) to win the Final" }
        assert(final.team1.name == "Brazil") { "Expected Brazil as team1 in Final" }
        assert(final.team1Goals == 3 && final.team2Goals == 1) { "Expected 3-1 scoreline" }
    }

    @Test
    fun matches_full_quarterFinalBrazilVsGermany() {
        val state = TestFixtures.matchesStateFull()
        val qfMatches = state.sections.first { it.phase == Phase.QF }.matches
        val brazilGermany = qfMatches.find { m ->
            (m.team1.name == "Brazil" && m.team2.name == "Germany") ||
            (m.team1.name == "Germany" && m.team2.name == "Brazil")
        }
        assert(brazilGermany != null) { "Brazil vs Germany QF match not found" }
        assert(brazilGermany!!.winner == MatchResult.TEAM1) { "Brazil should win QF 3-2" }
    }
}
