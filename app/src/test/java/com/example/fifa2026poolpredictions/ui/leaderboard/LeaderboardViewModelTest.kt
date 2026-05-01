package com.example.fifa2026poolpredictions.ui.leaderboard

import com.example.fifa2026poolpredictions.data.model.Match
import com.example.fifa2026poolpredictions.data.model.MatchResult
import com.example.fifa2026poolpredictions.data.model.Selection
import com.example.fifa2026poolpredictions.data.model.Team
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
class LeaderboardViewModelTest {

    private lateinit var viewModel: LeaderboardViewModel
    private val repository: Fifa2026Repository = mockk()
    private val testDispatcher = StandardTestDispatcher()

    private val mockTeams = listOf(
        Team("1", "Team A", "🇦", 1, 0.0),
        Team("2", "Team B", "🇧", 1, 0.0)
    )

    private val mockSelections = listOf(
        Selection("s1", "Best", "u1", listOf("1"), 10.0, "2026-04-01T12:00:00Z"),
        Selection("s2", "Second", "u2", listOf("2"), 8.0, "2026-04-01T13:00:00Z"),
        Selection("s3", "Also Best", "u3", listOf("1"), 10.0, "2026-04-01T14:00:00Z")
    )

    private val dummyTeam = Team("t", "X", "🏳", 1, 0.0)
    private val mockMatches = listOf(
        Match("m1", "t", "t", dummyTeam, dummyTeam, winner = MatchResult.TEAM1, team1Goals = 1, team2Goals = 0, phase = com.example.fifa2026poolpredictions.data.model.Phase.GROUP),
        Match("m2", "t", "t", dummyTeam, dummyTeam, winner = MatchResult.DRAW,  team1Goals = 1, team2Goals = 1, phase = com.example.fifa2026poolpredictions.data.model.Phase.GROUP),
        Match("m3", "t", "t", dummyTeam, dummyTeam, winner = MatchResult.UPCOMING, team1Goals = 0, team2Goals = 0, phase = com.example.fifa2026poolpredictions.data.model.Phase.GROUP),
        Match("m4", "t", "t", dummyTeam, dummyTeam, winner = MatchResult.UPCOMING, team1Goals = 0, team2Goals = 0, phase = com.example.fifa2026poolpredictions.data.model.Phase.GROUP)
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        coEvery { repository.getTeams() } returns Result.success(mockTeams)
        coEvery { repository.getSelections() } returns Result.success(mockSelections)
        coEvery { repository.getMatches() } returns Result.success(mockMatches)
        viewModel = LeaderboardViewModel(repository, "u1")
    }

    @Test
    fun `load calculates correct ranks with ties`() = runTest {
        advanceUntilIdle()
        val state = viewModel.state.value as LeaderboardUiState.Success
        
        // Sorted by score (desc) then createdAt (asc)
        // s1: 10.0 (12:00) -> Rank 1
        // s3: 10.0 (14:00) -> Rank 1
        // s2: 8.0 (13:00) -> Rank 3
        
        assertEquals(3, state.ranked.size)
        assertEquals("s1", state.ranked[0].selection.id)
        assertEquals(1, state.ranked[0].rank)
        
        assertEquals("s3", state.ranked[1].selection.id)
        assertEquals(1, state.ranked[1].rank)
        
        assertEquals("s2", state.ranked[2].selection.id)
        assertEquals(3, state.ranked[2].rank)
    }

    @Test
    fun `load computes correct match counts`() = runTest {
        advanceUntilIdle()
        val state = viewModel.state.value as LeaderboardUiState.Success
        assertEquals(2, state.matchesPlayed)
        assertEquals(2, state.matchesUpcoming)
    }

    @Test
    fun `toggleMineOnly filters correctly`() = runTest {
        advanceUntilIdle()
        viewModel.toggleMineOnly()

        val state = viewModel.state.value as LeaderboardUiState.Success
        assertEquals(true, state.showMineOnly)
        assertEquals(1, state.ranked.size)
        assertEquals("u1", state.ranked[0].selection.userId)
    }

    @Test
    fun `toggleMineOnly toggles back to all selections`() = runTest {
        advanceUntilIdle()
        viewModel.toggleMineOnly() // on
        viewModel.toggleMineOnly() // off

        val state = viewModel.state.value as LeaderboardUiState.Success
        assertEquals(false, state.showMineOnly)
        assertEquals(3, state.ranked.size)
    }

    @Test
    fun `toggleMineOnly with null userId still shows all selections`() = runTest {
        // Without a logged-in user, "mine" filter cannot apply — all selections are returned
        val anonViewModel = LeaderboardViewModel(repository, currentUserId = null)
        advanceUntilIdle()
        anonViewModel.toggleMineOnly()

        val state = anonViewModel.state.value as LeaderboardUiState.Success
        assertEquals(true, state.showMineOnly)
        assertEquals(3, state.ranked.size)
    }
}
