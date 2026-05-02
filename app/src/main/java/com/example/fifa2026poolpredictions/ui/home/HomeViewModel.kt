package com.example.fifa2026poolpredictions.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fifa2026poolpredictions.data.repository.Fifa2026Repository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit

private val KICKOFF_DATE = LocalDate.of(2026, 6, 11)
private val FINAL_DATE   = LocalDate.of(2026, 7, 19)

data class HomeUiState(
    val totalPlayers: Int = 0,
    val totalSelections: Int = 0,
    val gameState: String = "PREPARING",
    val daysToKickoff: Long = 0,
    val daysToFinal: Long = 0,
    val isLoading: Boolean = true,
    val error: String? = null
)

class HomeViewModel(private val repository: Fifa2026Repository) : ViewModel() {
    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state

    init { load() }

    fun load() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                coroutineScope {
                    val statsDeferred     = async { repository.getStats().getOrThrow() }
                    val gameStateDeferred = async { repository.getGameState().getOrThrow() }

                    val stats     = statsDeferred.await()
                    val gameState = gameStateDeferred.await()

                    val today = LocalDate.now()
                    val daysToKickoff = ChronoUnit.DAYS.between(today, KICKOFF_DATE).coerceAtLeast(0)
                    val daysToFinal   = ChronoUnit.DAYS.between(today, FINAL_DATE).coerceAtLeast(0)

                    _state.value = HomeUiState(
                        totalPlayers     = stats.totalPlayers,
                        totalSelections  = stats.totalSelections,
                        gameState        = gameState.state,
                        daysToKickoff    = daysToKickoff,
                        daysToFinal      = daysToFinal,
                        isLoading        = false
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = e.message ?: "Failed to load")
            }
        }
    }
}
