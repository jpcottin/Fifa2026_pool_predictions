package com.example.fifa2026poolpredictions.ui.wcresults

import com.example.fifa2026poolpredictions.data.model.*
import com.example.fifa2026poolpredictions.data.repository.Fifa2026Repository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WcResultsViewModelTest {

    private lateinit var viewModel: WcResultsViewModel
    private val repository: Fifa2026Repository = mockk()
    private val testDispatcher = StandardTestDispatcher()

    private val t1 = Team("1", "Mexico", "🇲🇽", 1, 0.0)
    private val t2 = Team("2", "South Africa", "🇿🇦", 1, 0.0)
    private val t3 = Team("3", "South Korea", "🇰🇷", 1, 0.0)
    private val t4 = Team("4", "Czechia", "🇨🇿", 1, 0.0)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        coEvery { repository.getTeams() } returns Result.success(listOf(t1, t2, t3, t4))
    }

    @Test
    fun `load calculates standings correctly for Group A`() = runTest {
        val matches = listOf(
            Match("m1", "1", "2", t1, t2, null, Phase.GROUP, MatchResult.TEAM1, 5, 2),
            Match("m2", "3", "4", t3, t4, null, Phase.GROUP, MatchResult.DRAW, 1, 1)
        )
        coEvery { repository.getMatches() } returns Result.success(matches)
        
        viewModel = WcResultsViewModel(repository)
        advanceUntilIdle()
        
        val state = viewModel.state.value as WcResultsUiState.Success
        val groupA = state.groups.first { it.letter == "A" }
        
        // Mexico should be 1st with 3 pts
        assertEquals("Mexico", groupA.sortedStandings[0].team.name)
        assertEquals(3, groupA.sortedStandings[0].pts)
        assertEquals(3, groupA.sortedStandings[0].gd)
        
        // South Korea/Czechia should be 2nd/3rd with 1 pt
        assertEquals(1, groupA.sortedStandings[1].pts)
        assertEquals(0, groupA.sortedStandings[1].gd)
        
        // South Africa should be 4th with 0 pts
        assertEquals("South Africa", groupA.sortedStandings[3].team.name)
        assertEquals(0, groupA.sortedStandings[3].pts)
        assertEquals(-3, groupA.sortedStandings[3].gd)
    }

    @Test
    fun `knockout matches are grouped by phase in correct order`() = runTest {
        val tbd1 = Team("tbd1", "TBD 1", "❓", 0, 0.0)
        val tbd2 = Team("tbd2", "TBD 2", "❓", 0, 0.0)
        val matches = listOf(
            Match("r32", "tbd1", "tbd2", tbd1, tbd2, "2026-06-28T12:00:00Z", Phase.R32, MatchResult.UPCOMING, 0, 0, "Runner-up Group A vs Runner-up Group B"),
            Match("final", "tbd1", "tbd2", tbd1, tbd2, "2026-07-19T12:00:00Z", Phase.FINAL, MatchResult.UPCOMING, 0, 0, "Winner Match 101 vs Winner Match 102"),
            Match("r16", "tbd1", "tbd2", tbd1, tbd2, "2026-07-04T12:00:00Z", Phase.R16, MatchResult.UPCOMING, 0, 0, "Winner Match 74 vs Winner Match 77"),
        )
        coEvery { repository.getMatches() } returns Result.success(matches)

        viewModel = WcResultsViewModel(repository)
        advanceUntilIdle()

        val state = viewModel.state.value as WcResultsUiState.Success
        val phases = state.knockoutByPhase.keys.toList()
        assertEquals(listOf(Phase.R32, Phase.R16, Phase.FINAL), phases)
        assertEquals("Runner-up Group A vs Runner-up Group B", state.knockoutByPhase[Phase.R32]?.first()?.note)
    }

    @Test
    fun `tie-breaking follows GD then GF`() = runTest {
        // Both win 1-0, but t1 wins another 5-0 while t3 wins 2-0
        val matches = listOf(
            Match("m1", "1", "2", t1, t2, null, Phase.GROUP, MatchResult.TEAM1, 5, 0),
            Match("m2", "3", "4", t3, t4, null, Phase.GROUP, MatchResult.TEAM1, 2, 0)
        )
        coEvery { repository.getMatches() } returns Result.success(matches)
        
        viewModel = WcResultsViewModel(repository)
        advanceUntilIdle()
        
        val state = viewModel.state.value as WcResultsUiState.Success
        val groupA = state.groups.first { it.letter == "A" }
        
        assertEquals("Mexico", groupA.sortedStandings[0].team.name) // +5 GD
        assertEquals("South Korea", groupA.sortedStandings[1].team.name) // +2 GD
    }
}
