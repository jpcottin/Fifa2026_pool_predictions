package com.example.fifa2026poolpredictions.ui.home

import com.example.fifa2026poolpredictions.data.model.GameState
import com.example.fifa2026poolpredictions.data.model.League
import com.example.fifa2026poolpredictions.data.model.Stats
import com.example.fifa2026poolpredictions.data.repository.Fifa2026Repository
import com.example.fifa2026poolpredictions.league.LeagueManager
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private lateinit var viewModel: HomeViewModel
    private val repository: Fifa2026Repository = mockk()
    private val leagueManager: LeagueManager = mockk(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()

    private val mockLeague = League("l1", "OTV", "otv")
    private val mockStats = Stats(totalPlayers = 12, totalSelections = 27)
    private val preparingState = GameState("gs1", "PREPARING")
    private val startedState = GameState("gs1", "STARTED")

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { leagueManager.selectedLeagueId } returns MutableStateFlow("l1")
        every { leagueManager.leagues } returns MutableStateFlow(listOf(mockLeague))
        coEvery { repository.getStats("l1") } returns Result.success(mockStats)
        coEvery { repository.getGameState() } returns Result.success(preparingState)
    }

    // ── Successful load ───────────────────────────────────────────────────

    @Test
    fun `load sets totalPlayers and totalSelections from stats`() = runTest {
        viewModel = HomeViewModel(repository, leagueManager)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(12, state.totalPlayers)
        assertEquals(27, state.totalSelections)
        assertEquals(false, state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `load sets gameState from repository`() = runTest {
        viewModel = HomeViewModel(repository, leagueManager)
        advanceUntilIdle()

        assertEquals("PREPARING", viewModel.state.value.gameState)
    }

    @Test
    fun `load reflects STARTED game state`() = runTest {
        coEvery { repository.getGameState() } returns Result.success(startedState)
        viewModel = HomeViewModel(repository, leagueManager)
        advanceUntilIdle()

        assertEquals("STARTED", viewModel.state.value.gameState)
    }

    // ── Countdown values ──────────────────────────────────────────────────

    @Test
    fun `daysToKickoff is non-negative`() = runTest {
        viewModel = HomeViewModel(repository, leagueManager)
        advanceUntilIdle()

        assertTrue(viewModel.state.value.daysToKickoff >= 0)
    }

    @Test
    fun `daysToFinal is non-negative`() = runTest {
        viewModel = HomeViewModel(repository, leagueManager)
        advanceUntilIdle()

        assertTrue(viewModel.state.value.daysToFinal >= 0)
    }

    @Test
    fun `daysToFinal is greater than or equal to daysToKickoff`() = runTest {
        viewModel = HomeViewModel(repository, leagueManager)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state.daysToFinal >= state.daysToKickoff)
    }

    // ── Error states ──────────────────────────────────────────────────────

    @Test
    fun `load emits error when stats call fails`() = runTest {
        coEvery { repository.getStats("l1") } returns Result.failure(RuntimeException("Server error"))
        viewModel = HomeViewModel(repository, leagueManager)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(false, state.isLoading)
        assertEquals("Server error", state.error)
    }

    @Test
    fun `load emits error when gameState call fails`() = runTest {
        coEvery { repository.getGameState() } returns Result.failure(RuntimeException("Timeout"))
        viewModel = HomeViewModel(repository, leagueManager)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(false, state.isLoading)
        assertEquals("Timeout", state.error)
    }

    @Test
    fun `isLoading starts true and becomes false after load`() = runTest {
        viewModel = HomeViewModel(repository, leagueManager)
        assertTrue(viewModel.state.value.isLoading)
        advanceUntilIdle()
        assertEquals(false, viewModel.state.value.isLoading)
    }

    // ── League scoping ────────────────────────────────────────────────────

    @Test
    fun `load uses selected leagueId when fetching stats`() = runTest {
        every { leagueManager.selectedLeagueId } returns MutableStateFlow("l2")
        coEvery { repository.getStats("l2") } returns Result.success(Stats(5, 10))
        viewModel = HomeViewModel(repository, leagueManager)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(5, state.totalPlayers)
        assertEquals(10, state.totalSelections)
    }

    @Test
    fun `load with null leagueId passes null to stats`() = runTest {
        every { leagueManager.selectedLeagueId } returns MutableStateFlow(null)
        coEvery { repository.getStats(null) } returns Result.success(Stats(3, 6))
        viewModel = HomeViewModel(repository, leagueManager)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(3, state.totalPlayers)
        assertEquals(6, state.totalSelections)
    }
}
