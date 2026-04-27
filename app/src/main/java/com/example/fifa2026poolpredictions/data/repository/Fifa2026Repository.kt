package com.example.fifa2026poolpredictions.data.repository

import com.example.fifa2026poolpredictions.data.model.AdminUser
import com.example.fifa2026poolpredictions.data.model.CreateSelectionRequest
import com.example.fifa2026poolpredictions.data.model.GameState
import com.example.fifa2026poolpredictions.data.model.Match
import com.example.fifa2026poolpredictions.data.model.MobileAuthRequest
import com.example.fifa2026poolpredictions.data.model.MobileAuthResponse
import com.example.fifa2026poolpredictions.data.model.Selection
import com.example.fifa2026poolpredictions.data.model.Team
import com.example.fifa2026poolpredictions.data.model.UpdateGameStateRequest
import com.example.fifa2026poolpredictions.data.model.UpdateMatchRequest
import com.example.fifa2026poolpredictions.data.network.ApiService

class Fifa2026Repository(private val api: ApiService) {
    suspend fun getTeams(): Result<List<Team>> = runCatching { api.getTeams() }
    suspend fun getSelections(): Result<List<Selection>> = runCatching { api.getSelections() }
    suspend fun getMatches(): Result<List<Match>> = runCatching { api.getMatches() }
    suspend fun getGameState(): Result<GameState> = runCatching { api.getGameState() }
    suspend fun getAdminUsers(): Result<List<AdminUser>> = runCatching { api.getAdminUsers() }
    suspend fun mobileAuth(idToken: String): Result<MobileAuthResponse> =
        runCatching { api.mobileAuth(MobileAuthRequest(idToken)) }
    suspend fun createSelection(name: String, teamIds: List<String>): Result<Selection> =
        runCatching { api.createSelection(CreateSelectionRequest(name, teamIds)) }
    suspend fun updateMatch(id: String, req: UpdateMatchRequest): Result<Match> =
        runCatching { api.updateMatch(id, req) }
    suspend fun updateGameState(state: String): Result<GameState> =
        runCatching { api.updateGameState(UpdateGameStateRequest(state)) }
}
