package com.example.fifa2026poolpredictions.ui.matches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fifa2026poolpredictions.data.model.Match
import com.example.fifa2026poolpredictions.data.model.Phase
import com.example.fifa2026poolpredictions.data.repository.Fifa2026Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class MatchSection(val phase: Phase, val matches: List<Match>)

sealed class MatchesUiState {
    object Loading : MatchesUiState()
    data class Success(val sections: List<MatchSection>) : MatchesUiState()
    data class Error(val message: String) : MatchesUiState()
}

val Phase.displayName: String get() = when (this) {
    Phase.GROUP -> "Group Stage"
    Phase.R32 -> "Round of 32"
    Phase.R16 -> "Round of 16"
    Phase.QF -> "Quarter-Finals"
    Phase.SF -> "Semi-Finals"
    Phase.THIRD -> "Third Place Play-off"
    Phase.FINAL -> "Final"
}

class MatchesViewModel(private val repository: Fifa2026Repository) : ViewModel() {
    private val _state = MutableStateFlow<MatchesUiState>(MatchesUiState.Loading)
    val state: StateFlow<MatchesUiState> = _state

    init { load() }

    fun load() {
        viewModelScope.launch {
            _state.value = MatchesUiState.Loading
            repository.getMatches().fold(
                onSuccess = { matches ->
                    val phaseOrder = listOf(
                        Phase.GROUP, Phase.R32, Phase.R16,
                        Phase.QF, Phase.SF, Phase.THIRD, Phase.FINAL
                    )
                    val sections = matches
                        .groupBy { it.phase }
                        .entries
                        .sortedBy { phaseOrder.indexOf(it.key) }
                        .map { (phase, list) ->
                            MatchSection(phase, list.sortedBy { it.date ?: it.createdAt })
                        }
                    _state.value = MatchesUiState.Success(sections)
                },
                onFailure = { _state.value = MatchesUiState.Error(it.message ?: "Failed to load") }
            )
        }
    }
}
