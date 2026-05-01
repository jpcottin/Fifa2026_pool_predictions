package com.example.fifa2026poolpredictions.ui.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fifa2026poolpredictions.data.model.Match
import com.example.fifa2026poolpredictions.data.model.MatchResult
import com.example.fifa2026poolpredictions.data.model.Selection
import com.example.fifa2026poolpredictions.data.model.Team
import com.example.fifa2026poolpredictions.data.repository.Fifa2026Repository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
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
        val currentUserId: String?,
        val matchesPlayed: Int,
        val matchesUpcoming: Int
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
    private var allMatches: List<Match> = emptyList()
    private var showMineOnly = false

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = LeaderboardUiState.Loading
            try {
                coroutineScope {
                    val teamsDeferred = async { repository.getTeams().getOrThrow() }
                    val selectionsDeferred = async { repository.getSelections().getOrThrow() }
                    val matchesDeferred = async { repository.getMatches().getOrThrow() }
                    teamMap = teamsDeferred.await().associateBy { it.id }
                    allSelections = selectionsDeferred.await()
                        .sortedWith(compareByDescending<Selection> { it.score }.thenBy { it.createdAt })
                    allMatches = matchesDeferred.await()
                }
                updateState()
            } catch (e: Exception) {
                _state.value = LeaderboardUiState.Error(e.message ?: "Failed to load")
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
        val played = allMatches.count { it.winner != MatchResult.UPCOMING }
        val upcoming = allMatches.count { it.winner == MatchResult.UPCOMING }
        _state.value = LeaderboardUiState.Success(ranked, showMineOnly, currentUserId, played, upcoming)
    }
}
