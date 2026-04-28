package com.example.fifa2026poolpredictions.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fifa2026poolpredictions.Fifa2026App
import com.example.fifa2026poolpredictions.data.model.AdminUser
import com.example.fifa2026poolpredictions.data.model.GameState
import com.example.fifa2026poolpredictions.data.model.Match
import com.example.fifa2026poolpredictions.data.model.MatchResult
import com.example.fifa2026poolpredictions.data.model.UpdateMatchRequest
import com.example.fifa2026poolpredictions.data.repository.Fifa2026Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class MatchEditState(
    val match: Match,
    val team1Goals: String = match.team1Goals.toString(),
    val team2Goals: String = match.team2Goals.toString(),
    val winner: MatchResult = match.winner,
    val saving: Boolean = false
)

sealed class AdminUiState {
    object Loading : AdminUiState()
    data class Success(
        val gameState: GameState?,
        val matches: List<Match>,
        val users: List<AdminUser>,
        val editingMatch: MatchEditState? = null,
        val togglingState: Boolean = false,
        val apiBaseUrl: String = ""
    ) : AdminUiState()
    data class Error(val message: String) : AdminUiState()
}

class AdminViewModel(
    private val app: Fifa2026App,
    private val repository: Fifa2026Repository
) : ViewModel() {
    private val _state = MutableStateFlow<AdminUiState>(AdminUiState.Loading)
    val state: StateFlow<AdminUiState> = _state

    init { load() }

    fun load() {
        viewModelScope.launch {
            _state.value = AdminUiState.Loading
            val gsResult = repository.getGameState()
            val matchesResult = repository.getMatches()
            val usersResult = repository.getAdminUsers()
            _state.value = AdminUiState.Success(
                gameState = gsResult.getOrNull(),
                matches = matchesResult.getOrDefault(emptyList()),
                users = usersResult.getOrDefault(emptyList()),
                apiBaseUrl = app.getApiBaseUrl()
            )
        }
    }

    fun setApiBaseUrl(url: String) {
        app.setApiBaseUrl(url)
        load()
    }

    fun startEditMatch(match: Match) {
        val s = _state.value as? AdminUiState.Success ?: return
        _state.value = s.copy(editingMatch = MatchEditState(match))
    }

    fun dismissEdit() {
        val s = _state.value as? AdminUiState.Success ?: return
        _state.value = s.copy(editingMatch = null)
    }

    fun updateEditGoals(team1Goals: String, team2Goals: String) {
        val s = _state.value as? AdminUiState.Success ?: return
        _state.value = s.copy(editingMatch = s.editingMatch?.copy(
            team1Goals = team1Goals, team2Goals = team2Goals))
    }

    fun updateEditWinner(winner: MatchResult) {
        val s = _state.value as? AdminUiState.Success ?: return
        _state.value = s.copy(editingMatch = s.editingMatch?.copy(winner = winner))
    }

    fun saveMatchResult() {
        val s = _state.value as? AdminUiState.Success ?: return
        val edit = s.editingMatch ?: return
        viewModelScope.launch {
            _state.value = s.copy(editingMatch = edit.copy(saving = true))
            val req = UpdateMatchRequest(
                team1Id = edit.match.team1Id,
                team2Id = edit.match.team2Id,
                date = edit.match.date,
                team1Goals = edit.team1Goals.toIntOrNull() ?: 0,
                team2Goals = edit.team2Goals.toIntOrNull() ?: 0,
                winner = edit.winner.name,
                phase = edit.match.phase.name,
                note = edit.match.note
            )
            repository.updateMatch(edit.match.id, req).fold(
                onSuccess = { updated ->
                    val current = _state.value as? AdminUiState.Success ?: return@fold
                    _state.value = current.copy(
                        editingMatch = null,
                        matches = current.matches.map { if (it.id == updated.id) updated else it }
                    )
                },
                onFailure = {
                    val current = _state.value as? AdminUiState.Success ?: return@fold
                    _state.value = current.copy(editingMatch = edit.copy(saving = false))
                }
            )
        }
    }

    fun toggleGameState() {
        val s = _state.value as? AdminUiState.Success ?: return
        val currentState = s.gameState?.state ?: "PREPARING"
        val nextState = if (currentState == "PREPARING") "STARTED" else "PREPARING"
        
        viewModelScope.launch {
            _state.value = s.copy(togglingState = true)
            repository.updateGameState(nextState).fold(
                onSuccess = { newGs ->
                    val current = _state.value as? AdminUiState.Success ?: return@fold
                    _state.value = current.copy(gameState = newGs, togglingState = false)
                },
                onFailure = {
                    val current = _state.value as? AdminUiState.Success ?: return@fold
                    _state.value = current.copy(togglingState = false)
                }
            )
        }
    }
}
