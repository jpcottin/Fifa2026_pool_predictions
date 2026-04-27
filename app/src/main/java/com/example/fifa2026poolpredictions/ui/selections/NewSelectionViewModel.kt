package com.example.fifa2026poolpredictions.ui.selections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fifa2026poolpredictions.data.model.Team
import com.example.fifa2026poolpredictions.data.repository.Fifa2026Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class NewSelectionUiState(
    val teams: List<Team> = emptyList(),
    val loading: Boolean = true,
    val saving: Boolean = false,
    val error: String? = null,
    val success: Boolean = false,
    val name: String = "",
    val picks: Map<Int, String> = emptyMap() // set -> teamId
)

class NewSelectionViewModel(private val repository: Fifa2026Repository) : ViewModel() {
    private val _state = MutableStateFlow(NewSelectionUiState())
    val state: StateFlow<NewSelectionUiState> = _state

    init {
        loadTeams()
    }

    private fun loadTeams() {
        viewModelScope.launch {
            repository.getTeams().fold(
                onSuccess = { teams ->
                    val pickableTeams = teams.filter { it.set > 0 }
                    _state.value = _state.value.copy(
                        teams = pickableTeams, 
                        loading = false, 
                        picks = emptyMap()
                    )
                },
                onFailure = {
                    _state.value = _state.value.copy(loading = false, error = it.message)
                }
            )
        }
    }

    fun onNameChange(name: String) {
        _state.value = _state.value.copy(name = name)
    }

    fun onPickTeam(set: Int, teamId: String) {
        _state.value = _state.value.copy(
            picks = _state.value.picks + (set to teamId),
            error = null // Clear error when they make a pick
        )
    }

    fun submit() {
        val s = _state.value
        if (s.name.isBlank()) {
            _state.value = s.copy(error = "Please enter a selection name")
            return
        }
        
        val missingSets = (1..8).filter { !s.picks.containsKey(it) }
        if (missingSets.isNotEmpty()) {
            _state.value = s.copy(error = "Please pick a team for: " + missingSets.joinToString(", ") { "Set $it" })
            return
        }

        val teamIds = s.picks.values.toList()
        viewModelScope.launch {
            _state.value = _state.value.copy(saving = true, error = null)
            repository.createSelection(s.name.trim(), teamIds).fold(
                onSuccess = {
                    _state.value = _state.value.copy(saving = false, success = true)
                },
                onFailure = {
                    _state.value = _state.value.copy(saving = false, error = it.message)
                }
            )
        }
    }
}
