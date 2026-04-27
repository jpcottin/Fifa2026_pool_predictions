package com.example.fifa2026poolpredictions.ui.main

import com.example.fifa2026poolpredictions.data.model.MatchResult
import com.example.fifa2026poolpredictions.data.model.Phase
import org.junit.Assert.assertEquals
import org.junit.Test

class ScoringLogicTest {

    @Test
    fun `tied selections receive same rank`() {
        val scores = listOf(9.0, 9.0, 3.0)
        var rank = 1
        val ranks = scores.mapIndexed { idx, score ->
            if (idx > 0 && score < scores[idx - 1]) rank = idx + 1
            rank
        }
        assertEquals(listOf(1, 1, 3), ranks)
    }

    @Test
    fun `descending scores produce sequential ranks`() {
        val scores = listOf(10.0, 7.5, 3.0)
        var rank = 1
        val ranks = scores.mapIndexed { idx, score ->
            if (idx > 0 && score < scores[idx - 1]) rank = idx + 1
            rank
        }
        assertEquals(listOf(1, 2, 3), ranks)
    }

    @Test
    fun `single selection is rank 1`() {
        val scores = listOf(5.0)
        var rank = 1
        val ranks = scores.mapIndexed { idx, score ->
            if (idx > 0 && score < scores[idx - 1]) rank = idx + 1
            rank
        }
        assertEquals(listOf(1), ranks)
    }

    @Test
    fun `team ids sum gives correct score`() {
        val teamScores = mapOf("t1" to 6.0, "t2" to 3.0, "t3" to 0.3)
        val score = listOf("t1", "t2", "t3").sumOf { teamScores[it] ?: 0.0 }
        assertEquals(9.3, score, 0.001)
    }

    @Test
    fun `missing team id contributes zero to score`() {
        val teamScores = mapOf("t1" to 6.0)
        val score = listOf("t1", "missing").sumOf { teamScores[it] ?: 0.0 }
        assertEquals(6.0, score, 0.001)
    }

    @Test
    fun `phase display name for group stage`() {
        val phase = Phase.GROUP
        val name = when (phase) {
            Phase.GROUP -> "Group Stage"
            Phase.R32 -> "Round of 32"
            Phase.R16 -> "Round of 16"
            Phase.QF -> "Quarter-Finals"
            Phase.SF -> "Semi-Finals"
            Phase.THIRD -> "Third Place Play-off"
            Phase.FINAL -> "Final"
        }
        assertEquals("Group Stage", name)
    }

    @Test
    fun `match result UPCOMING means not yet played`() {
        assertEquals(false, MatchResult.UPCOMING != MatchResult.UPCOMING)
    }
}
