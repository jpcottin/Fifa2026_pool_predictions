package com.example.fifa2026poolpredictions.league

import com.example.fifa2026poolpredictions.data.model.League
import com.example.fifa2026poolpredictions.data.repository.Fifa2026Repository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
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
class LeagueManagerTest {

    private val repository: Fifa2026Repository = mockk()
    private val testDispatcher = StandardTestDispatcher()

    private val otv = League("league-otv", "OTV", "otv")
    private val letsplay = League("league-lp", "LetsPlay", "letsplay")

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    private fun makeManager() = LeagueManager(repository, CoroutineScope(testDispatcher))

    @Test
    fun `initial state is empty with null selection`() {
        val manager = makeManager()
        assertEquals(emptyList<League>(), manager.leagues.value)
        assertNull(manager.selectedLeagueId.value)
    }

    @Test
    fun `loadLeagues populates leagues and auto-selects first`() = runTest(testDispatcher) {
        val manager = makeManager()
        coEvery { repository.getLeagues() } returns Result.success(listOf(otv, letsplay))
        manager.loadLeagues()
        advanceUntilIdle()

        assertEquals(listOf(otv, letsplay), manager.leagues.value)
        assertEquals(otv.id, manager.selectedLeagueId.value)
    }

    @Test
    fun `loadLeagues with empty result leaves selection null`() = runTest(testDispatcher) {
        val manager = makeManager()
        coEvery { repository.getLeagues() } returns Result.success(emptyList())
        manager.loadLeagues()
        advanceUntilIdle()

        assertEquals(emptyList<League>(), manager.leagues.value)
        assertNull(manager.selectedLeagueId.value)
    }

    @Test
    fun `selectLeague updates selectedLeagueId`() = runTest(testDispatcher) {
        val manager = makeManager()
        coEvery { repository.getLeagues() } returns Result.success(listOf(otv, letsplay))
        manager.loadLeagues()
        advanceUntilIdle()

        manager.selectLeague(letsplay.id)
        assertEquals(letsplay.id, manager.selectedLeagueId.value)
    }

    @Test
    fun `selectLeague preserves existing selection on reload if still valid`() = runTest(testDispatcher) {
        val manager = makeManager()
        coEvery { repository.getLeagues() } returns Result.success(listOf(otv, letsplay))
        manager.loadLeagues()
        advanceUntilIdle()
        manager.selectLeague(letsplay.id)

        manager.loadLeagues()
        advanceUntilIdle()
        assertEquals(letsplay.id, manager.selectedLeagueId.value)
    }

    @Test
    fun `reload resets to first if previous selection no longer exists`() = runTest(testDispatcher) {
        val manager = makeManager()
        coEvery { repository.getLeagues() } returns Result.success(listOf(otv, letsplay))
        manager.loadLeagues()
        advanceUntilIdle()
        manager.selectLeague(letsplay.id)

        coEvery { repository.getLeagues() } returns Result.success(listOf(otv))
        manager.loadLeagues()
        advanceUntilIdle()
        assertEquals(otv.id, manager.selectedLeagueId.value)
    }

    @Test
    fun `clear resets all state`() = runTest(testDispatcher) {
        val manager = makeManager()
        coEvery { repository.getLeagues() } returns Result.success(listOf(otv, letsplay))
        manager.loadLeagues()
        advanceUntilIdle()

        manager.clear()
        assertEquals(emptyList<League>(), manager.leagues.value)
        assertNull(manager.selectedLeagueId.value)
    }

    @Test
    fun `loadLeagues failure leaves state unchanged`() = runTest(testDispatcher) {
        val manager = makeManager()
        coEvery { repository.getLeagues() } returns Result.failure(Exception("network error"))
        manager.loadLeagues()
        advanceUntilIdle()

        assertEquals(emptyList<League>(), manager.leagues.value)
        assertNull(manager.selectedLeagueId.value)
    }
}
