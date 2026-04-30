package com.example.fifa2026poolpredictions.ui.wcresults

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fifa2026poolpredictions.data.model.Match
import com.example.fifa2026poolpredictions.data.model.MatchResult
import com.example.fifa2026poolpredictions.data.model.Phase
import com.example.fifa2026poolpredictions.data.model.Team
import com.example.fifa2026poolpredictions.data.model.WC_GROUPS
import com.example.fifa2026poolpredictions.data.model.WcGroup
import com.example.fifa2026poolpredictions.data.repository.Fifa2026Repository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class Standing(
    val team: Team,
    var p: Int = 0,
    var w: Int = 0,
    var d: Int = 0,
    var l: Int = 0,
    var gf: Int = 0,
    var ga: Int = 0,
    var gd: Int = 0,
    var pts: Int = 0
)

data class GroupData(
    val letter: String,
    val groupMatches: List<Match>,
    val sortedStandings: List<Standing>
)

sealed class WcResultsUiState {
    object Loading : WcResultsUiState()
    data class Success(
        val groups: List<GroupData>,
        val knockoutByPhase: Map<Phase, List<Match>>
    ) : WcResultsUiState()
    data class Error(val message: String) : WcResultsUiState()
}

class WcResultsViewModel(private val repository: Fifa2026Repository) : ViewModel() {
    private val _state = MutableStateFlow<WcResultsUiState>(WcResultsUiState.Loading)
    val state: StateFlow<WcResultsUiState> = _state

    init { load() }

    fun load() {
        viewModelScope.launch {
            _state.value = WcResultsUiState.Loading
            try {
                coroutineScope {
                    val teamsDeferred = async { repository.getTeams().getOrThrow() }
                    val matchesDeferred = async { repository.getMatches().getOrThrow() }

                    val allTeams = teamsDeferred.await()
                    val allMatches = matchesDeferred.await()

                    val teamByName = allTeams.associateBy { it.name }
                    val groupMatchesOnly = allMatches.filter { it.phase == Phase.GROUP }

                    val groupsData = WC_GROUPS.map { group ->
                        buildGroupData(group, teamByName, groupMatchesOnly)
                    }

                    val knockoutPhaseOrder = listOf(Phase.R32, Phase.R16, Phase.QF, Phase.SF, Phase.THIRD, Phase.FINAL)
                    val knockoutByPhase = allMatches
                        .filter { it.phase != Phase.GROUP }
                        .groupBy { it.phase }
                        .let { map -> knockoutPhaseOrder.filter { map.containsKey(it) }.associateWith { map[it]!! } }

                    _state.value = WcResultsUiState.Success(groupsData, knockoutByPhase)
                }
            } catch (e: Exception) {
                _state.value = WcResultsUiState.Error(e.message ?: "Failed to load WC Results")
            }
        }
    }

    private fun buildGroupData(
        group: WcGroup,
        teamByName: Map<String, Team>,
        allMatches: List<Match>
    ): GroupData {
        val teamInfos = group.teams.mapNotNull { teamByName[it] }
        val teamIds = teamInfos.map { it.id }.toSet()

        val groupMatches = allMatches.filter {
            teamIds.contains(it.team1.id) && teamIds.contains(it.team2.id)
        }

        val standingsMap = teamInfos.associate { it.id to Standing(team = it) }.toMutableMap()

        for (m in groupMatches) {
            if (m.winner == MatchResult.UPCOMING) continue

            val s1 = standingsMap[m.team1.id] ?: continue
            val s2 = standingsMap[m.team2.id] ?: continue

            s1.p++
            s2.p++
            s1.gf += m.team1Goals
            s1.ga += m.team2Goals
            s2.gf += m.team2Goals
            s2.ga += m.team1Goals

            when (m.winner) {
                MatchResult.TEAM1 -> {
                    s1.w++
                    s1.pts += 3
                    s2.l++
                }
                MatchResult.TEAM2 -> {
                    s2.w++
                    s2.pts += 3
                    s1.l++
                }
                MatchResult.DRAW -> {
                    s1.d++
                    s1.pts++
                    s2.d++
                    s2.pts++
                }
                else -> {}
            }
        }

        standingsMap.values.forEach { it.gd = it.gf - it.ga }

        val sortedStandings = standingsMap.values.toList().sortedWith(
            compareByDescending<Standing> { it.pts }
                .thenByDescending { it.gd }
                .thenByDescending { it.gf }
                .thenBy { it.team.name }
        )

        return GroupData(
            letter = group.letter,
            groupMatches = groupMatches.sortedBy { it.date ?: it.createdAt },
            sortedStandings = sortedStandings
        )
    }
}
