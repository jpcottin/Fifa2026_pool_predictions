package com.example.fifa2026poolpredictions.league

import com.example.fifa2026poolpredictions.data.model.League
import com.example.fifa2026poolpredictions.data.repository.Fifa2026Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LeagueManager(
    private val repository: Fifa2026Repository,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
) {

    private val _leagues = MutableStateFlow<List<League>>(emptyList())
    val leagues: StateFlow<List<League>> = _leagues.asStateFlow()

    private val _selectedLeagueId = MutableStateFlow<String?>(null)
    val selectedLeagueId: StateFlow<String?> = _selectedLeagueId.asStateFlow()

    fun loadLeagues() {
        scope.launch {
            repository.getLeagues().onSuccess { fetched ->
                _leagues.value = fetched
                val current = _selectedLeagueId.value
                if (current == null || fetched.none { it.id == current }) {
                    _selectedLeagueId.value = fetched.firstOrNull()?.id
                }
            }
        }
    }

    fun selectLeague(id: String) {
        _selectedLeagueId.value = id
    }

    fun clear() {
        _leagues.value = emptyList()
        _selectedLeagueId.value = null
    }
}
