package com.example.fifa2026poolpredictions

import com.example.fifa2026poolpredictions.data.model.*
import com.example.fifa2026poolpredictions.ui.wcresults.Standing
import org.junit.Assert.assertEquals
import org.junit.Test

class StandingsTest {

    @Test
    fun testStandingsCalculation() {
        val t1 = Team("1", "Team A", "🇦", 1, 0.0)
        val t2 = Team("2", "Team B", "🇧", 1, 0.0)
        
        val standings = mutableMapOf(
            "1" to Standing(t1),
            "2" to Standing(t2)
        )

        val match = Match("m1", "1", "2", t1, t2, null, Phase.GROUP, MatchResult.TEAM1, 2, 0)
        
        // Simulate calculation logic from VM
        val s1 = standings["1"]!!
        val s2 = standings["2"]!!
        
        s1.p++
        s2.p++
        s1.gf += match.team1Goals
        s1.ga += match.team2Goals
        s2.gf += match.team2Goals
        s2.ga += match.team1Goals
        
        s1.w++
        s1.pts += 3
        s2.l++
        
        s1.gd = s1.gf - s1.ga
        s2.gd = s2.gf - s2.ga

        assertEquals(3, s1.pts)
        assertEquals(2, s1.gd)
        assertEquals(0, s2.pts)
        assertEquals(-2, s2.gd)
    }
}
