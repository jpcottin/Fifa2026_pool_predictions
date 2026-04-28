package com.example.fifa2026poolpredictions.ui.admin

import com.example.fifa2026poolpredictions.Fifa2026App
import com.example.fifa2026poolpredictions.data.model.GameState
import com.example.fifa2026poolpredictions.data.model.Match
import com.example.fifa2026poolpredictions.data.model.MatchResult
import com.example.fifa2026poolpredictions.data.model.Phase
import com.example.fifa2026poolpredictions.data.model.Team
import com.example.fifa2026poolpredictions.data.model.UpdateMatchRequest
import com.example.fifa2026poolpredictions.data.repository.Fifa2026Repository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AdminViewModelTest {

    private lateinit var viewModel: AdminViewModel
    private val app: Fifa2026App = mockk()
    private val repository: Fifa2026Repository = mockk()
    private val testDispatcher = StandardTestDispatcher()

    private val team1 = Team("t1", "Mexico", "🇲🇽", 1, 0.0)
    private val team2 = Team("t2", "South Africa", "🇿🇦", 2, 0.0)
    private val testMatch = Match(
        id = "m1",
        team1Id = "t1",
        team2Id = "t2",
        team1 = team1,
        team2 = team2,
        date = "2026-06-11T19:00:00Z",
        phase = Phase.GROUP,
        winner = MatchResult.UPCOMING,
        team1Goals = 0,
        team2Goals = 0,
        note = "Opening match"
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { app.getApiBaseUrl() } returns "https://test.example.com/"
        coEvery { repository.getGameState() } returns Result.success(GameState("gs1", "PREPARING"))
        coEvery { repository.getMatches() } returns Result.success(listOf(testMatch))
        coEvery { repository.getAdminUsers() } returns Result.success(emptyList())
        viewModel = AdminViewModel(app, repository)
    }

    @Test
    fun `saveMatchResult sends all original match fields, not just edited goals`() = runTest {
        advanceUntilIdle()
        viewModel.startEditMatch(testMatch)
        viewModel.updateEditGoals("2", "1")
        viewModel.updateEditWinner(MatchResult.TEAM1)

        val requestSlot = slot<UpdateMatchRequest>()
        coEvery { repository.updateMatch("m1", capture(requestSlot)) } returns Result.success(testMatch)

        viewModel.saveMatchResult()
        advanceUntilIdle()

        val req = requestSlot.captured
        // Edited fields
        assertEquals(2, req.team1Goals)
        assertEquals(1, req.team2Goals)
        assertEquals("TEAM1", req.winner)
        // Non-editable fields must be preserved from the original match
        assertEquals("t1", req.team1Id)
        assertEquals("t2", req.team2Id)
        assertEquals("2026-06-11T19:00:00Z", req.date)
        assertEquals("GROUP", req.phase)
        assertEquals("Opening match", req.note)
    }

    @Test
    fun `saveMatchResult preserves null date and null note`() = runTest {
        val matchNoDate = testMatch.copy(date = null, note = null)
        coEvery { repository.getMatches() } returns Result.success(listOf(matchNoDate))
        val vm = AdminViewModel(app, repository)
        advanceUntilIdle()

        vm.startEditMatch(matchNoDate)

        val requestSlot = slot<UpdateMatchRequest>()
        coEvery { repository.updateMatch("m1", capture(requestSlot)) } returns Result.success(matchNoDate)

        vm.saveMatchResult()
        advanceUntilIdle()

        assertNull(requestSlot.captured.date)
        assertNull(requestSlot.captured.note)
    }

    @Test
    fun `startEditMatch populates edit state from match`() = runTest {
        advanceUntilIdle()
        viewModel.startEditMatch(testMatch)

        val state = viewModel.state.value as AdminUiState.Success
        assertEquals("0", state.editingMatch?.team1Goals)
        assertEquals("0", state.editingMatch?.team2Goals)
        assertEquals(MatchResult.UPCOMING, state.editingMatch?.winner)
    }

    @Test
    fun `dismissEdit clears editing state`() = runTest {
        advanceUntilIdle()
        viewModel.startEditMatch(testMatch)
        viewModel.dismissEdit()

        val state = viewModel.state.value as AdminUiState.Success
        assertNull(state.editingMatch)
    }
}
