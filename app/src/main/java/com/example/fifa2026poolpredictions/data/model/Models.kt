package com.example.fifa2026poolpredictions.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Team(
    val id: String,
    val name: String,
    val flagEmoji: String,
    val set: Int,
    val score: Double,
    val createdAt: String = ""
)

@Serializable
data class SelectionUser(
    val name: String? = null,
    val email: String? = null,
    val image: String? = null
)

@Serializable
data class Selection(
    val id: String,
    val name: String,
    val userId: String,
    val teamIds: List<String>,
    val score: Double,
    val createdAt: String = "",
    val user: SelectionUser? = null
)

@Serializable
enum class MatchResult { UPCOMING, TEAM1, TEAM2, DRAW }

@Serializable
enum class Phase { GROUP, R32, R16, QF, SF, THIRD, FINAL }

@Serializable
data class Match(
    val id: String,
    val team1Id: String,
    val team2Id: String,
    val team1: Team,
    val team2: Team,
    val date: String? = null,
    val phase: Phase,
    val winner: MatchResult,
    val team1Goals: Int,
    val team2Goals: Int,
    val note: String? = null,
    val createdAt: String = ""
)

@Serializable
data class GameState(
    val id: String,
    val state: String
)

@Serializable
data class AdminUser(
    val id: String,
    val name: String? = null,
    val email: String? = null,
    val image: String? = null,
    val role: String,
    val createdAt: String = ""
)

@Serializable
data class MobileAuthRequest(val idToken: String)

@Serializable
data class MobileAuthResponse(val token: String)

@Serializable
data class CreateSelectionRequest(
    val name: String,
    val teamIds: List<String>
)

@Serializable
data class UpdateMatchRequest(
    val team1Id: String,
    val team2Id: String,
    val date: String?,
    val team1Goals: Int,
    val team2Goals: Int,
    val winner: String,
    val phase: String,
    val note: String?
)

@Serializable
data class UpdateGameStateRequest(val state: String)

@Serializable
data class JwtPayload(
    val id: String = "",
    val role: String = "PLAYER",
    val name: String? = null,
    val email: String? = null,
    val image: String? = null
)
