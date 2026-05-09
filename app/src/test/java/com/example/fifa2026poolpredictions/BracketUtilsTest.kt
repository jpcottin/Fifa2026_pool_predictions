package com.example.fifa2026poolpredictions

import com.example.fifa2026poolpredictions.ui.wcresults.shortNote
import org.junit.Assert.assertEquals
import org.junit.Test

class BracketUtilsTest {

    @Test
    fun `abbreviates group winner`() {
        assertEquals("I1", shortNote("Winner Group I"))
    }

    @Test
    fun `abbreviates group runner-up`() {
        assertEquals("A2", shortNote("Runner-up Group A"))
    }

    @Test
    fun `abbreviates 3rd-place spec preserving group letters`() {
        assertEquals("3rd C/D/F/G/H", shortNote("3rd Place Group C/D/F/G/H"))
    }

    @Test
    fun `abbreviates full partial-TBD note correctly`() {
        assertEquals("I1 vs 3rd C/D/F/G/H", shortNote("Winner Group I vs 3rd Place Group C/D/F/G/H"))
    }

    @Test
    fun `abbreviates full both-TBD R32 note`() {
        assertEquals("A2 vs B2", shortNote("Runner-up Group A vs Runner-up Group B"))
    }

    @Test
    fun `abbreviates winner-match refs`() {
        assertEquals("W74 vs W77", shortNote("Winner Match 74 vs Winner Match 77"))
    }

    @Test
    fun `abbreviates loser-match refs`() {
        assertEquals("L101 vs L102", shortNote("Loser Match 101 vs Loser Match 102"))
    }

    @Test
    fun `abbreviates R32 note with 3rd-place group pool`() {
        assertEquals("E1 vs 3rd A/B/C/D/F", shortNote("Winner Group E vs 3rd Place Group A/B/C/D/F"))
    }
}
