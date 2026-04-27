package com.example.fifa2026poolpredictions.ui.selections

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
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NewSelectionViewModelTest {

    private lateinit var viewModel: NewSelectionViewModel
    private val repository: Fifa2026Repository = mockk()
    private val testDispatcher = StandardTestDispatcher()

    private val mockTeams = listOf(
        Team("1", "France", "🇫🇷", 1, 0.0),
        Team("2", "Germany", "🇩🇪", 2, 0.0),
        Team("3", "USA", "🇺🇸", 3, 0.0),
        Team("4", "Japan", "🇯🇵", 4, 0.0),
        Team("5", "Algeria", "🇩🇿", 5, 0.0),
        Team("6", "Sweden", "🇸🇪", 6, 0.0),
        Team("7", "Iraq", "🇮🇶", 7, 0.0),
        Team("8", "Haiti", "🇭🇹", 8, 0.0)
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        coEvery { repository.getTeams() } returns Result.success(mockTeams)
        viewModel = NewSelectionViewModel(repository)
    }

    @Test
    fun `initial state eventually success with teams`() = runTest {
        advanceUntilIdle()
        assertEquals(false, viewModel.state.value.loading)
        assertEquals(8, viewModel.state.value.teams.size)
        assertEquals(0, viewModel.state.value.picks.size)
    }

    @Test
    fun `onNameChange updates state`() {
        viewModel.onNameChange("My Team")
        assertEquals("My Team", viewModel.state.value.name)
    }

    @Test
    fun `onPickTeam updates picks and clears error`() = runTest {
        advanceUntilIdle()
        viewModel.onPickTeam(1, "1")
        assertEquals("1", viewModel.state.value.picks[1])
        assertEquals(null, viewModel.state.value.error)
    }

    @Test
    fun `submit without name sets error`() = runTest {
        advanceUntilIdle()
        viewModel.submit()
        assertEquals("Please enter a selection name", viewModel.state.value.error)
    }

    @Test
    fun `submit with missing picks sets error`() = runTest {
        advanceUntilIdle()
        viewModel.onNameChange("Valid Name")
        viewModel.onPickTeam(1, "1")
        viewModel.submit()
        assertNotNull(viewModel.state.value.error)
        assertEquals(true, viewModel.state.value.error?.contains("Set 2"))
    }
}
