package com.example.fifa2026poolpredictions.ui.selections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fifa2026poolpredictions.data.model.GameState
import com.example.fifa2026poolpredictions.data.model.Selection
import com.example.fifa2026poolpredictions.data.model.Team
import com.example.fifa2026poolpredictions.data.repository.Fifa2026Repository
import com.example.fifa2026poolpredictions.league.LeagueManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class MySelection(val selection: Selection, val rank: Int, val teams: List<Team>)

sealed class SelectionsUiState {
    object Loading : SelectionsUiState()
    data class Success(
        val mySelections: List<MySelection>,
        val gameState: GameState?,
        val canAddMore: Boolean,
        val allTeams: List<Team> = emptyList(),
        val noLeague: Boolean = false
    ) : SelectionsUiState()
    data class Error(val message: String) : SelectionsUiState()
}

class SelectionsViewModel(
    private val repository: Fifa2026Repository,
    private val currentUserId: String?,
    private val leagueManager: LeagueManager
) : ViewModel() {
    private val _state = MutableStateFlow<SelectionsUiState>(SelectionsUiState.Loading)
    val state: StateFlow<SelectionsUiState> = _state

    init {
        viewModelScope.launch {
            leagueManager.selectedLeagueId.collect { load() }
        }
    }

    fun load() {
        val leagueId = leagueManager.selectedLeagueId.value
        viewModelScope.launch {
            _state.value = SelectionsUiState.Loading
            val teamsResult = repository.getTeams()
            val selectionsResult = repository.getSelections(leagueId)
            val gameStateResult = repository.getGameState()
            if (teamsResult.isSuccess && selectionsResult.isSuccess) {
                val teamMap = teamsResult.getOrThrow().associateBy { it.id }
                val gameState = gameStateResult.getOrNull()
                val noLeague = leagueManager.leagues.value.isEmpty()

                val allSelections = selectionsResult.getOrThrow()
                    .sortedWith(compareByDescending<Selection> { it.score }.thenBy { it.createdAt })

                val ranksMap = mutableMapOf<String, Int>()
                var currentRank = 1
                allSelections.forEachIndexed { idx, sel ->
                    if (idx > 0 && sel.score < allSelections[idx - 1].score) currentRank = idx + 1
                    ranksMap[sel.id] = currentRank
                }

                val mine = allSelections
                    .filter { it.userId == currentUserId }
                    .sortedByDescending { it.createdAt }
                    .map { sel ->
                        MySelection(sel, ranksMap[sel.id] ?: 0, sel.teamIds.mapNotNull { teamMap[it] })
                    }
                val isPreparing = gameState?.state == "PREPARING"
                val deadline = System.currentTimeMillis() < 1_781_204_400_000L // June 11 2026 19:00 UTC (noon PDT)
                _state.value = SelectionsUiState.Success(
                    mySelections = mine,
                    gameState = gameState,
                    canAddMore = isPreparing && deadline && mine.size < 3 && !noLeague,
                    allTeams = teamsResult.getOrThrow(),
                    noLeague = noLeague
                )
            } else {
                val err = teamsResult.exceptionOrNull() ?: selectionsResult.exceptionOrNull()
                _state.value = SelectionsUiState.Error(err?.message ?: "Failed to load")
            }
        }
    }
}
