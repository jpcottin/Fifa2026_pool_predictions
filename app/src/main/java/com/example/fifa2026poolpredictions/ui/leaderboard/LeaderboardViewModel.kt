package com.example.fifa2026poolpredictions.ui.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fifa2026poolpredictions.data.model.Selection
import com.example.fifa2026poolpredictions.data.model.Team
import com.example.fifa2026poolpredictions.data.repository.Fifa2026Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class RankedSelection(
    val selection: Selection,
    val rank: Int,
    val teams: List<Team>
)

sealed class LeaderboardUiState {
    object Loading : LeaderboardUiState()
    data class Success(
        val ranked: List<RankedSelection>,
        val showMineOnly: Boolean,
        val currentUserId: String?
    ) : LeaderboardUiState()
    data class Error(val message: String) : LeaderboardUiState()
}

class LeaderboardViewModel(
    private val repository: Fifa2026Repository,
    private val currentUserId: String?
) : ViewModel() {
    private val _state = MutableStateFlow<LeaderboardUiState>(LeaderboardUiState.Loading)
    val state: StateFlow<LeaderboardUiState> = _state

    private var allSelections: List<Selection> = emptyList()
    private var teamMap: Map<String, Team> = emptyMap()
    private var showMineOnly = false

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = LeaderboardUiState.Loading
            val teamsResult = repository.getTeams()
            val selectionsResult = repository.getSelections()
            if (teamsResult.isSuccess && selectionsResult.isSuccess) {
                teamMap = teamsResult.getOrThrow().associateBy { it.id }
                allSelections = selectionsResult.getOrThrow()
                    .sortedWith(compareByDescending<Selection> { it.score }.thenBy { it.createdAt })
                updateState()
            } else {
                val err = (teamsResult.exceptionOrNull() ?: selectionsResult.exceptionOrNull())
                _state.value = LeaderboardUiState.Error(err?.message ?: "Failed to load")
            }
        }
    }

    fun toggleMineOnly() {
        showMineOnly = !showMineOnly
        updateState()
    }

    private fun updateState() {
        val filtered = if (showMineOnly && currentUserId != null) {
            allSelections.filter { it.userId == currentUserId }
        } else allSelections

        var rank = 1
        val ranked = filtered.mapIndexed { idx, sel ->
            if (idx > 0 && sel.score < filtered[idx - 1].score) rank = idx + 1
            RankedSelection(
                selection = sel,
                rank = rank,
                teams = sel.teamIds.mapNotNull { teamMap[it] }
            )
        }
        _state.value = LeaderboardUiState.Success(ranked, showMineOnly, currentUserId)
    }
}
