package com.example.fifa2026poolpredictions.ui.matches

import com.example.fifa2026poolpredictions.data.model.Match
import com.example.fifa2026poolpredictions.data.model.MatchResult
import com.example.fifa2026poolpredictions.data.model.Phase
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MatchesViewModelTest {

    private lateinit var viewModel: MatchesViewModel
    private val repository: Fifa2026Repository = mockk()
    private val testDispatcher = StandardTestDispatcher()

    private val t1 = Team("1", "France", "🇫🇷", 1, 0.0)
    private val t2 = Team("2", "Brazil", "🇧🇷", 2, 0.0)

    private fun match(
        id: String,
        phase: Phase,
        date: String? = null,
        winner: MatchResult = MatchResult.UPCOMING
    ) = Match(id, t1.id, t2.id, t1, t2, date = date, phase = phase, winner = winner, team1Goals = 0, team2Goals = 0)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @Test
    fun `load groups matches by phase in tournament order`() = runTest {
        coEvery { repository.getMatches() } returns Result.success(listOf(
            match("f1", Phase.FINAL),
            match("g1", Phase.GROUP),
            match("r1", Phase.R32),
            match("s1", Phase.SF),
            match("g2", Phase.GROUP),
        ))
        viewModel = MatchesViewModel(repository)
        advanceUntilIdle()

        val state = viewModel.state.value as MatchesUiState.Success
        val phases = state.sections.map { it.phase }
        assertEquals(listOf(Phase.GROUP, Phase.R32, Phase.SF, Phase.FINAL), phases)
    }

    @Test
    fun `load sorts matches within a phase by date`() = runTest {
        coEvery { repository.getMatches() } returns Result.success(listOf(
            match("g3", Phase.GROUP, date = "2026-06-20"),
            match("g1", Phase.GROUP, date = "2026-06-11"),
            match("g2", Phase.GROUP, date = "2026-06-15"),
        ))
        viewModel = MatchesViewModel(repository)
        advanceUntilIdle()

        val state = viewModel.state.value as MatchesUiState.Success
        val ids = state.sections.first().matches.map { it.id }
        assertEquals(listOf("g1", "g2", "g3"), ids)
    }

    @Test
    fun `load emits error state when repository fails`() = runTest {
        coEvery { repository.getMatches() } returns Result.failure(RuntimeException("Network error"))
        viewModel = MatchesViewModel(repository)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state is MatchesUiState.Error)
        assertEquals("Network error", (state as MatchesUiState.Error).message)
    }

    @Test
    fun `load with empty match list produces no sections`() = runTest {
        coEvery { repository.getMatches() } returns Result.success(emptyList())
        viewModel = MatchesViewModel(repository)
        advanceUntilIdle()

        val state = viewModel.state.value as MatchesUiState.Success
        assertTrue(state.sections.isEmpty())
    }
}
