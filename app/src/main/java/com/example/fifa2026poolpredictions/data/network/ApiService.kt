package com.example.fifa2026poolpredictions.data.network

import com.example.fifa2026poolpredictions.data.model.AdminUser
import com.example.fifa2026poolpredictions.data.model.CreateSelectionRequest
import com.example.fifa2026poolpredictions.data.model.GameState
import com.example.fifa2026poolpredictions.data.model.Match
import com.example.fifa2026poolpredictions.data.model.MobileAuthRequest
import com.example.fifa2026poolpredictions.data.model.MobileAuthResponse
import com.example.fifa2026poolpredictions.data.model.Selection
import com.example.fifa2026poolpredictions.data.model.Team
import com.example.fifa2026poolpredictions.data.model.Stats
import com.example.fifa2026poolpredictions.data.model.UpdateGameStateRequest
import com.example.fifa2026poolpredictions.data.model.UpdateMatchRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @GET("api/teams")
    suspend fun getTeams(): List<Team>

    @GET("api/selections")
    suspend fun getSelections(): List<Selection>

    @GET("api/matches")
    suspend fun getMatches(): List<Match>

    @GET("api/stats")
    suspend fun getStats(): Stats

    @GET("api/game-state")
    suspend fun getGameState(): GameState

    @GET("api/admin/users")
    suspend fun getAdminUsers(): List<AdminUser>

    @POST("api/auth/mobile")
    suspend fun mobileAuth(@Body body: MobileAuthRequest): MobileAuthResponse

    @POST("api/selections")
    suspend fun createSelection(@Body body: CreateSelectionRequest): Selection

    @PATCH("api/matches/{id}")
    suspend fun updateMatch(@Path("id") id: String, @Body body: UpdateMatchRequest): Match

    @PATCH("api/game-state")
    suspend fun updateGameState(@Body body: UpdateGameStateRequest): GameState
}
