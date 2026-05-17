package com.example.fifa2026poolpredictions.ui.selections

import com.example.fifa2026poolpredictions.data.model.GameState
import com.example.fifa2026poolpredictions.data.model.League
import com.example.fifa2026poolpredictions.data.model.Selection
import com.example.fifa2026poolpredictions.data.model.Team
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SelectionsViewModelTest {

    private lateinit var viewModel: SelectionsViewModel
    private val repository: Fifa2026Repository = mockk()
    private val leagueManager: LeagueManager = mockk(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()

    private val mockLeague = League("l1", "OTV", "otv")
    private val mockTeams = listOf(
        Team("t1", "France", "🇫🇷", 1, 6.0),
        Team("t2", "Germany", "🇩🇪", 2, 4.0),
        Team("t3", "Brazil", "🇧🇷", 3, 3.0),
        Team("t4", "Japan", "🇯🇵", 4, 2.0),
        Team("t5", "Morocco", "🇲🇦", 5, 1.0),
        Team("t6", "USA", "🇺🇸", 6, 0.5),
        Team("t7", "Portugal", "🇵🇹", 7, 0.5),
        Team("t8", "Spain", "🇪🇸", 8, 0.0),
    )

    private fun sel(
        id: String, name: String, userId: String, score: Double, createdAt: String = "2026-04-01T12:00:00Z"
    ) = Selection(id, name, userId, mockTeams.map { it.id }, score, createdAt)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { leagueManager.selectedLeagueId } returns MutableStateFlow("l1")
        every { leagueManager.leagues } returns MutableStateFlow(listOf(mockLeague))
        coEvery { repository.getTeams() } returns Result.success(mockTeams)
        coEvery { repository.getGameState() } returns Result.success(GameState("gs1", "PREPARING"))
    }

    private fun buildViewModel(userId: String = "u1"): SelectionsViewModel {
        return SelectionsViewModel(repository, userId, leagueManager)
    }

    // ── Basic loading ─────────────────────────────────────────────────────

    @Test
    fun `load shows only current user selections in My Picks`() = runTest {
        coEvery { repository.getSelections("l1") } returns Result.success(listOf(
            sel("s1", "Alice Pick", "u1", 10.0),
            sel("s2", "Bob Pick", "u2", 8.0),
            sel("s3", "Alice Second", "u1", 6.0),
        ))
        viewModel = buildViewModel("u1")
        advanceUntilIdle()

        val state = viewModel.state.value as SelectionsUiState.Success
        assertEquals(2, state.mySelections.size)
        assertTrue(state.mySelections.all { it.selection.userId == "u1" })
    }

    @Test
    fun `load resolves team objects for each selection`() = runTest {
        coEvery { repository.getSelections("l1") } returns Result.success(listOf(
            sel("s1", "My Pick", "u1", 10.0),
        ))
        viewModel = buildViewModel("u1")
        advanceUntilIdle()

        val state = viewModel.state.value as SelectionsUiState.Success
        assertEquals(8, state.mySelections.first().teams.size)
        assertEquals("France", state.mySelections.first().teams.first().name)
    }

    @Test
    fun `load emits error when teams repository call fails`() = runTest {
        coEvery { repository.getTeams() } returns Result.failure(RuntimeException("Teams unavailable"))
        coEvery { repository.getSelections("l1") } returns Result.success(emptyList())
        viewModel = buildViewModel()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state is SelectionsUiState.Error)
        assertEquals("Teams unavailable", (state as SelectionsUiState.Error).message)
    }

    @Test
    fun `load emits error when selections repository call fails`() = runTest {
        coEvery { repository.getSelections("l1") } returns Result.failure(RuntimeException("No connection"))
        viewModel = buildViewModel()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state is SelectionsUiState.Error)
        assertEquals("No connection", (state as SelectionsUiState.Error).message)
    }

    // ── Ranking logic ─────────────────────────────────────────────────────

    @Test
    fun `my selections show correct rank from overall leaderboard`() = runTest {
        coEvery { repository.getSelections("l1") } returns Result.success(listOf(
            sel("s1", "Alice Pick", "u1", 10.0, "2026-04-01T10:00:00Z"),
            sel("s2", "Bob Pick", "u2", 15.0),
            sel("s3", "Carol Pick", "u3", 10.0, "2026-04-01T12:00:00Z"),
        ))
        viewModel = buildViewModel("u1")
        advanceUntilIdle()

        val state = viewModel.state.value as SelectionsUiState.Success
        // Bob=1st, Alice and Carol tied at 10.0 → rank 2
        val alicePick = state.mySelections.first { it.selection.id == "s1" }
        assertEquals(2, alicePick.rank)
    }

    @Test
    fun `tied selections receive the same rank`() = runTest {
        coEvery { repository.getSelections("l1") } returns Result.success(listOf(
            sel("s1", "Alice A", "u1", 10.0, "2026-04-01T08:00:00Z"),
            sel("s2", "Bob B", "u2", 10.0, "2026-04-01T09:00:00Z"),
            sel("s3", "Alice B", "u1", 6.0),
        ))
        viewModel = buildViewModel("u1")
        advanceUntilIdle()

        val state = viewModel.state.value as SelectionsUiState.Success
        val picks = state.mySelections.sortedByDescending { it.selection.score }
        assertEquals(1, picks.first { it.selection.id == "s1" }.rank)
        assertEquals(3, picks.first { it.selection.id == "s3" }.rank)
    }

    // ── canAddMore logic ──────────────────────────────────────────────────

    @Test
    fun `canAddMore is true when preparing, deadline ahead, less than 3 picks, has league`() = runTest {
        coEvery { repository.getSelections("l1") } returns Result.success(listOf(
            sel("s1", "My Pick", "u1", 5.0),
        ))
        viewModel = buildViewModel("u1")
        advanceUntilIdle()

        val state = viewModel.state.value as SelectionsUiState.Success
        assertTrue(state.canAddMore)
    }

    @Test
    fun `canAddMore is false when game state is STARTED`() = runTest {
        coEvery { repository.getGameState() } returns Result.success(GameState("gs1", "STARTED"))
        coEvery { repository.getSelections("l1") } returns Result.success(emptyList())
        viewModel = buildViewModel("u1")
        advanceUntilIdle()

        val state = viewModel.state.value as SelectionsUiState.Success
        assertEquals(false, state.canAddMore)
    }

    @Test
    fun `canAddMore is false when user already has 3 selections`() = runTest {
        coEvery { repository.getSelections("l1") } returns Result.success(listOf(
            sel("s1", "Pick 1", "u1", 10.0),
            sel("s2", "Pick 2", "u1", 8.0),
            sel("s3", "Pick 3", "u1", 6.0),
        ))
        viewModel = buildViewModel("u1")
        advanceUntilIdle()

        val state = viewModel.state.value as SelectionsUiState.Success
        assertEquals(false, state.canAddMore)
    }

    @Test
    fun `canAddMore is false when user has no league`() = runTest {
        every { leagueManager.leagues } returns MutableStateFlow(emptyList())
        coEvery { repository.getSelections("l1") } returns Result.success(emptyList())
        viewModel = buildViewModel("u1")
        advanceUntilIdle()

        val state = viewModel.state.value as SelectionsUiState.Success
        assertEquals(false, state.canAddMore)
    }

    // ── noLeague flag ─────────────────────────────────────────────────────

    @Test
    fun `noLeague is true when leagues list is empty`() = runTest {
        every { leagueManager.leagues } returns MutableStateFlow(emptyList())
        coEvery { repository.getSelections("l1") } returns Result.success(emptyList())
        viewModel = buildViewModel("u1")
        advanceUntilIdle()

        val state = viewModel.state.value as SelectionsUiState.Success
        assertEquals(true, state.noLeague)
    }

    @Test
    fun `noLeague is false when user has a league`() = runTest {
        coEvery { repository.getSelections("l1") } returns Result.success(emptyList())
        viewModel = buildViewModel("u1")
        advanceUntilIdle()

        val state = viewModel.state.value as SelectionsUiState.Success
        assertEquals(false, state.noLeague)
    }

    // ── allTeams ──────────────────────────────────────────────────────────

    @Test
    fun `allTeams contains full team list for Sets tab`() = runTest {
        coEvery { repository.getSelections("l1") } returns Result.success(emptyList())
        viewModel = buildViewModel("u1")
        advanceUntilIdle()

        val state = viewModel.state.value as SelectionsUiState.Success
        assertEquals(8, state.allTeams.size)
    }
}
