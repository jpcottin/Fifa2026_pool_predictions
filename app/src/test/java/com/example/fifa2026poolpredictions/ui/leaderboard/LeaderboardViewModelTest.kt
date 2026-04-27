package com.example.fifa2026poolpredictions.ui.leaderboard

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

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        coEvery { repository.getTeams() } returns Result.success(mockTeams)
        coEvery { repository.getSelections() } returns Result.success(mockSelections)
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
    fun `toggleMineOnly filters correctly`() = runTest {
        advanceUntilIdle()
        viewModel.toggleMineOnly()
        
        val state = viewModel.state.value as LeaderboardUiState.Success
        assertEquals(true, state.showMineOnly)
        assertEquals(1, state.ranked.size)
        assertEquals("u1", state.ranked[0].selection.userId)
    }
}
