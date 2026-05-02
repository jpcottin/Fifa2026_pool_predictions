package com.example.fifa2026poolpredictions

import com.example.fifa2026poolpredictions.data.model.*
import com.example.fifa2026poolpredictions.ui.leaderboard.LeaderboardUiState
import com.example.fifa2026poolpredictions.ui.leaderboard.RankedSelection
import com.example.fifa2026poolpredictions.ui.matches.MatchSection
import com.example.fifa2026poolpredictions.ui.matches.MatchesUiState
import com.example.fifa2026poolpredictions.ui.wcresults.GroupData
import com.example.fifa2026poolpredictions.ui.wcresults.Standing
import com.example.fifa2026poolpredictions.ui.wcresults.WcResultsUiState

/**
 * Shared test fixtures for UI tests.
 *
 * Tournament structure: 2026 WC – 12 groups (A–L), 48 teams, 104 matches.
 *   Group stage  : 12 × 6 =  72 matches
 *   Knockout     : 16+8+4+2+1+1 = 32 matches
 *
 * Two test scenarios:
 *   MID  – 50 matches played (groups A–H complete + 2 from Group I), 54 upcoming
 *   FULL – all 104 matches played
 */
object TestFixtures {

    // ── 48 Teams ──────────────────────────────────────────────────────────
    // Sets 1-8, 6 teams each, following WCGroups composition.

    // Group A
    val mexico      = Team("t01", "Mexico",                 "🇲🇽", 2, 0.0)
    val southAfrica = Team("t02", "South Africa",           "🇿🇦", 5, 0.0)
    val southKorea  = Team("t03", "South Korea",            "🇰🇷", 3, 0.0)
    val czechia     = Team("t04", "Czechia",                "🇨🇿", 8, 0.0)
    // Group B
    val canada      = Team("t05", "Canada",                 "🇨🇦", 4, 0.0)
    val switzerland = Team("t06", "Switzerland",            "🇨🇭", 3, 0.0)
    val qatar       = Team("t07", "Qatar",                  "🇶🇦", 6, 0.0)
    val bosnia      = Team("t08", "Bosnia",                 "🇧🇦", 8, 0.0)
    // Group C
    val brazil      = Team("t09", "Brazil",                 "🇧🇷", 1, 0.0)
    val morocco     = Team("t10", "Morocco",                "🇲🇦", 3, 0.0)
    val haiti       = Team("t11", "Haiti",                  "🇭🇹", 7, 0.0)
    val scotland    = Team("t12", "Scotland",               "🏴", 4, 0.0)
    // Group D
    val usa         = Team("t13", "United States",          "🇺🇸", 2, 0.0)
    val paraguay    = Team("t14", "Paraguay",               "🇵🇾", 7, 0.0)
    val australia   = Team("t15", "Australia",              "🇦🇺", 7, 0.0)
    val turkey      = Team("t16", "Türkiye",                "🇹🇷", 4, 0.0)
    // Group E
    val germany     = Team("t17", "Germany",                "🇩🇪", 1, 0.0)
    val curacao     = Team("t18", "Curaçao",                "🇨🇼", 7, 0.0)
    val ivoryCoast  = Team("t19", "Ivory Coast",            "🇨🇮", 5, 0.0)
    val ecuador     = Team("t20", "Ecuador",                "🇪🇨", 4, 0.0)
    // Group F
    val netherlands = Team("t21", "Netherlands",            "🇳🇱", 2, 0.0)
    val japan       = Team("t22", "Japan",                  "🇯🇵", 3, 0.0)
    val sweden      = Team("t23", "Sweden",                 "🇸🇪", 4, 0.0)
    val tunisia     = Team("t24", "Tunisia",                "🇹🇳", 5, 0.0)
    // Group G
    val belgium     = Team("t25", "Belgium",                "🇧🇪", 2, 0.0)
    val egypt       = Team("t26", "Egypt",                  "🇪🇬", 5, 0.0)
    val iran        = Team("t27", "Iran",                   "🇮🇷", 6, 0.0)
    val newZealand  = Team("t28", "New Zealand",            "🇳🇿", 7, 0.0)
    // Group H
    val spain       = Team("t29", "Spain",                  "🇪🇸", 1, 0.0)
    val capeVerde   = Team("t30", "Cape Verde",             "🇨🇻", 6, 0.0)
    val saudiArabia = Team("t31", "Saudi Arabia",           "🇸🇦", 6, 0.0)
    val uruguay     = Team("t32", "Uruguay",                "🇺🇾", 2, 0.0)
    // Group I
    val france      = Team("t33", "France",                 "🇫🇷", 1, 0.0)
    val senegal     = Team("t34", "Senegal",                "🇸🇳", 3, 0.0)
    val norway      = Team("t35", "Norway",                 "🇳🇴", 5, 0.0)
    val iraq        = Team("t36", "Iraq",                   "🇮🇶", 8, 0.0)
    // Group J
    val argentina   = Team("t37", "Argentina",              "🇦🇷", 1, 0.0)
    val algeria     = Team("t38", "Algeria",                "🇩🇿", 6, 0.0)
    val austria     = Team("t39", "Austria",                "🇦🇹", 5, 0.0)
    val jordan      = Team("t40", "Jordan",                 "🇯🇴", 8, 0.0)
    // Group K
    val portugal    = Team("t41", "Portugal",               "🇵🇹", 2, 0.0)
    val congroDr    = Team("t42", "Congo DR",               "🇨🇩", 8, 0.0)
    val uzbekistan  = Team("t43", "Uzbekistan",             "🇺🇿", 7, 0.0)
    val colombia    = Team("t44", "Colombia",               "🇨🇴", 3, 0.0)
    // Group L
    val england     = Team("t45", "England",                "🏴", 1, 0.0)
    val croatia     = Team("t46", "Croatia",                "🇭🇷", 3, 0.0)
    val ghana       = Team("t47", "Ghana",                  "🇬🇭", 6, 0.0)
    val panama      = Team("t48", "Panama",                 "🇵🇦", 8, 0.0)

    // ── 8 Players × 2 Selections ──────────────────────────────────────────
    // Each selection: one team per set (1–8).

    // 50-game scenario scores → sorted: s1=320, s10=320(tie,later), s3=305, s7=290, s15=275, s13=260, s4=250, s8=245, s9=235, s11=230, s12=220, s6=210, s2=200, s14=190, s16=175, s5=165
    // 104-game scenario scores → different winner: s3=450, s15=430, s1=410, s10=395, s7=380, s9=365, s13=350, s4=340, s8=325, s11=310, s2=295, s12=280, s14=265, s6=250, s16=235, s5=220
    fun selections50(): List<Selection> = listOf(
        Selection("s1",  "Alice Dream",    "u1", listOf("t09","t21","t10","t03","t19","t27","t14","t42"), 320.0, "2026-01-01T10:00:00Z", SelectionUser("Alice")),
        Selection("s2",  "Alice Safe",     "u1", listOf("t33","t13","t22","t20","t24","t31","t15","t08"), 200.0, "2026-01-01T10:05:00Z", SelectionUser("Alice")),
        Selection("s3",  "Bob Bold",       "u2", listOf("t17","t41","t34","t23","t26","t30","t11","t48"), 305.0, "2026-01-02T10:00:00Z", SelectionUser("Bob")),
        Selection("s4",  "Bob Classic",    "u2", listOf("t29","t01","t46","t16","t35","t07","t18","t36"), 250.0, "2026-01-02T10:05:00Z", SelectionUser("Bob")),
        Selection("s5",  "Carlos Power",   "u3", listOf("t37","t25","t06","t05","t02","t38","t28","t40"), 165.0, "2026-01-03T10:00:00Z", SelectionUser("Carlos")),
        Selection("s6",  "Carlos Lucky",   "u3", listOf("t45","t32","t44","t12","t39","t47","t43","t04"), 210.0, "2026-01-03T10:05:00Z", SelectionUser("Carlos")),
        Selection("s7",  "Diana Rocket",   "u4", listOf("t09","t13","t10","t16","t19","t27","t15","t48"), 290.0, "2026-01-04T10:00:00Z", SelectionUser("Diana")),
        Selection("s8",  "Diana Steady",   "u4", listOf("t33","t21","t22","t03","t24","t31","t14","t42"), 245.0, "2026-01-04T10:05:00Z", SelectionUser("Diana")),
        Selection("s9",  "Evan Underdog",  "u5", listOf("t17","t01","t34","t20","t26","t30","t11","t08"), 235.0, "2026-01-05T10:00:00Z", SelectionUser("Evan")),
        Selection("s10", "Evan Fav",       "u5", listOf("t29","t41","t46","t23","t35","t07","t18","t36"), 320.0, "2026-01-05T10:05:00Z", SelectionUser("Evan")),
        Selection("s11", "Fatima Strong",  "u6", listOf("t09","t41","t10","t05","t02","t38","t28","t40"), 230.0, "2026-01-06T10:00:00Z", SelectionUser("Fatima")),
        Selection("s12", "Fatima Spicy",   "u6", listOf("t17","t25","t22","t12","t39","t47","t43","t04"), 220.0, "2026-01-06T10:05:00Z", SelectionUser("Fatima")),
        Selection("s13", "George Wisdom",  "u7", listOf("t33","t13","t34","t20","t19","t27","t15","t48"), 260.0, "2026-01-07T10:00:00Z", SelectionUser("George")),
        Selection("s14", "George Chaos",   "u7", listOf("t37","t01","t44","t16","t24","t07","t18","t08"), 190.0, "2026-01-07T10:05:00Z", SelectionUser("George")),
        Selection("s15", "Hannah Rocket",  "u8", listOf("t29","t21","t06","t23","t26","t31","t18","t42"), 275.0, "2026-01-08T10:00:00Z", SelectionUser("Hannah")),
        Selection("s16", "Hannah Steady",  "u8", listOf("t45","t32","t46","t03","t35","t38","t43","t36"), 175.0, "2026-01-08T10:05:00Z", SelectionUser("Hannah"))
    )

    fun selections104(): List<Selection> = selections50().map { sel ->
        val score104 = mapOf(
            "s1" to 410.0, "s2" to 295.0, "s3" to 450.0, "s4" to 340.0,
            "s5" to 220.0, "s6" to 250.0, "s7" to 380.0, "s8" to 325.0,
            "s9" to 365.0, "s10" to 395.0, "s11" to 310.0, "s12" to 280.0,
            "s13" to 350.0, "s14" to 265.0, "s15" to 430.0, "s16" to 235.0
        )
        sel.copy(score = score104[sel.id] ?: sel.score)
    }

    fun rankedSelections(sels: List<Selection>): List<RankedSelection> {
        val sorted = sels.sortedWith(compareByDescending<Selection> { it.score }.thenBy { it.createdAt })
        var rank = 1
        return sorted.mapIndexed { i, sel ->
            if (i > 0 && sel.score < sorted[i - 1].score) rank = i + 1
            val teams = sel.teamIds.mapNotNull { id -> allTeams.find { it.id == id } }
            RankedSelection(sel, rank, teams)
        }
    }

    val allTeams: List<Team> get() = listOf(
        mexico, southAfrica, southKorea, czechia,
        canada, switzerland, qatar, bosnia,
        brazil, morocco, haiti, scotland,
        usa, paraguay, australia, turkey,
        germany, curacao, ivoryCoast, ecuador,
        netherlands, japan, sweden, tunisia,
        belgium, egypt, iran, newZealand,
        spain, capeVerde, saudiArabia, uruguay,
        france, senegal, norway, iraq,
        argentina, algeria, austria, jordan,
        portugal, congroDr, uzbekistan, colombia,
        england, croatia, ghana, panama
    )

    // ── Group Matches ─────────────────────────────────────────────────────

    private var _counter = 0
    private fun nextId(): String { _counter++; return "m$_counter" }

    private fun gm(t1: Team, t2: Team, r: MatchResult, g1: Int, g2: Int, date: String): Match {
        return Match(nextId(), t1.id, t2.id, t1, t2, date, Phase.GROUP, r, g1, g2)
    }

    private fun upcoming(t1: Team, t2: Team, date: String): Match =
        gm(t1, t2, MatchResult.UPCOMING, 0, 0, date)

    // All 6 combinations for 4 teams (teams in order: a,b,c,d)
    private fun groupMatches(
        a: Team, b: Team, c: Team, d: Team,
        results: List<Triple<MatchResult, Int, Int>>,  // 6 results: ab,ac,ad,bc,bd,cd
        dates: List<String>
    ): List<Match> {
        val pairs = listOf(a to b, a to c, a to d, b to c, b to d, c to d)
        return pairs.mapIndexed { i, (t1, t2) ->
            val (r, g1, g2) = results[i]
            gm(t1, t2, r, g1, g2, dates[i])
        }
    }

    private val W = MatchResult.TEAM1
    private val L = MatchResult.TEAM2
    private val D = MatchResult.DRAW
    private val U = MatchResult.UPCOMING

    // Full group match results for the 104-game scenario.
    // Groups A–H played, I partially (only first 2 of 6), J–L upcoming in mid scenario.

    fun groupAMatches(): List<Match> = groupMatches(
        mexico, southAfrica, southKorea, czechia,
        listOf(Triple(W,2,0), Triple(W,1,0), Triple(W,3,1), Triple(L,0,1), Triple(D,1,1), Triple(W,2,0)),
        listOf("2026-06-11","2026-06-12","2026-06-16","2026-06-12","2026-06-17","2026-06-17")
    )
    // Group A standings: Mexico 9pts(+5), SouthKorea 6pts(+1), Czechia 1pt(-3), SouthAfrica 1pt(-3)

    fun groupBMatches(): List<Match> = groupMatches(
        canada, switzerland, qatar, bosnia,
        listOf(Triple(D,1,1), Triple(W,2,0), Triple(W,2,1), Triple(W,2,0), Triple(W,3,0), Triple(L,0,2)),
        listOf("2026-06-11","2026-06-12","2026-06-16","2026-06-13","2026-06-17","2026-06-18")
    )

    fun groupCMatches(): List<Match> = groupMatches(
        brazil, morocco, haiti, scotland,
        listOf(Triple(D,1,1), Triple(W,4,0), Triple(W,2,1), Triple(W,2,0), Triple(W,3,0), Triple(L,0,1)),
        listOf("2026-06-11","2026-06-13","2026-06-17","2026-06-14","2026-06-18","2026-06-18")
    )
    // Brazil 7pts, Morocco 6pts (advance)

    fun groupDMatches(): List<Match> = groupMatches(
        usa, paraguay, australia, turkey,
        listOf(Triple(W,1,0), Triple(W,2,0), Triple(D,1,1), Triple(L,0,1), Triple(L,0,1), Triple(W,2,0)),
        listOf("2026-06-12","2026-06-13","2026-06-17","2026-06-14","2026-06-18","2026-06-19")
    )

    fun groupEMatches(): List<Match> = groupMatches(
        germany, curacao, ivoryCoast, ecuador,
        listOf(Triple(W,5,0), Triple(W,3,1), Triple(W,2,0), Triple(L,0,2), Triple(L,1,3), Triple(W,2,1)),
        listOf("2026-06-13","2026-06-14","2026-06-18","2026-06-14","2026-06-19","2026-06-19")
    )

    fun groupFMatches(): List<Match> = groupMatches(
        netherlands, japan, sweden, tunisia,
        listOf(Triple(W,2,1), Triple(W,3,0), Triple(W,2,0), Triple(W,2,0), Triple(W,3,1), Triple(D,1,1)),
        listOf("2026-06-13","2026-06-15","2026-06-19","2026-06-15","2026-06-20","2026-06-20")
    )

    fun groupGMatches(): List<Match> = groupMatches(
        belgium, egypt, iran, newZealand,
        listOf(Triple(W,2,0), Triple(W,3,0), Triple(W,4,0), Triple(W,2,1), Triple(W,2,0), Triple(D,1,1)),
        listOf("2026-06-14","2026-06-15","2026-06-20","2026-06-16","2026-06-21","2026-06-21")
    )

    fun groupHMatches(): List<Match> = groupMatches(
        spain, capeVerde, saudiArabia, uruguay,
        listOf(Triple(W,3,0), Triple(W,2,0), Triple(D,1,1), Triple(L,0,1), Triple(L,0,2), Triple(W,2,0)),
        listOf("2026-06-14","2026-06-16","2026-06-20","2026-06-16","2026-06-21","2026-06-22")
    )

    // Group I: only first 2 matches played in MID scenario
    fun groupIMatchesFull(): List<Match> = groupMatches(
        france, senegal, norway, iraq,
        listOf(Triple(W,2,1), Triple(W,3,0), Triple(W,2,0), Triple(D,1,1), Triple(W,2,0), Triple(W,1,0)),
        listOf("2026-06-15","2026-06-16","2026-06-22","2026-06-16","2026-06-22","2026-06-23")
    )

    fun groupIMatchesMid(): List<Match> {
        val full = groupIMatchesFull()
        return full.mapIndexed { i, m ->
            if (i < 2) m  // France vs Senegal, France vs Norway played
            else m.copy(winner = U, team1Goals = 0, team2Goals = 0)
        }
    }

    fun groupJMatchesFull(): List<Match> = groupMatches(
        argentina, algeria, austria, jordan,
        listOf(Triple(W,3,0), Triple(W,2,0), Triple(W,4,0), Triple(W,2,1), Triple(W,2,0), Triple(D,0,0)),
        listOf("2026-06-15","2026-06-17","2026-06-23","2026-06-17","2026-06-23","2026-06-24")
    )

    fun groupJMatchesMid(): List<Match> = groupJMatchesFull().map { m ->
        m.copy(winner = U, team1Goals = 0, team2Goals = 0)
    }

    fun groupKMatchesFull(): List<Match> = groupMatches(
        portugal, congroDr, uzbekistan, colombia,
        listOf(Triple(W,4,0), Triple(W,3,0), Triple(D,1,1), Triple(L,0,2), Triple(W,2,1), Triple(W,3,0)),
        listOf("2026-06-15","2026-06-18","2026-06-24","2026-06-18","2026-06-24","2026-06-25")
    )

    fun groupKMatchesMid(): List<Match> = groupKMatchesFull().map { m ->
        m.copy(winner = U, team1Goals = 0, team2Goals = 0)
    }

    fun groupLMatchesFull(): List<Match> = groupMatches(
        england, croatia, ghana, panama,
        listOf(Triple(W,2,0), Triple(W,3,0), Triple(W,4,0), Triple(W,2,0), Triple(W,3,1), Triple(D,1,1)),
        listOf("2026-06-16","2026-06-18","2026-06-25","2026-06-19","2026-06-25","2026-06-26")
    )

    fun groupLMatchesMid(): List<Match> = groupLMatchesFull().map { m ->
        m.copy(winner = U, team1Goals = 0, team2Goals = 0)
    }

    fun allGroupMatchesMid(): List<Match> {
        _counter = 0  // reset for deterministic IDs
        return groupAMatches() + groupBMatches() + groupCMatches() + groupDMatches() +
               groupEMatches() + groupFMatches() + groupGMatches() + groupHMatches() +
               groupIMatchesMid() + groupJMatchesMid() + groupKMatchesMid() + groupLMatchesMid()
    }

    fun allGroupMatchesFull(): List<Match> {
        _counter = 0
        return groupAMatches() + groupBMatches() + groupCMatches() + groupDMatches() +
               groupEMatches() + groupFMatches() + groupGMatches() + groupHMatches() +
               groupIMatchesFull() + groupJMatchesFull() + groupKMatchesFull() + groupLMatchesFull()
    }

    // ── Knockout Matches ──────────────────────────────────────────────────

    private val tbd1 = Team("tbd1", "TBD", "🏳", 0, 0.0)
    private val tbd2 = Team("tbd2", "TBD", "🏳", 0, 0.0)

    private fun knockoutMatch(
        phase: Phase, t1: Team, t2: Team,
        r: MatchResult, g1: Int, g2: Int, date: String, note: String? = null
    ): Match = Match(nextId(), t1.id, t2.id, t1, t2, date, phase, r, g1, g2, note)

    private fun tbdMatch(phase: Phase, note: String, date: String): Match =
        knockoutMatch(phase, tbd1, tbd2, U, 0, 0, date, note)

    fun knockoutMatchesFull(): List<Match> {
        // R32 – 16 matches (winners of groups + best 3rds)
        val r32 = listOf(
            knockoutMatch(Phase.R32, mexico, netherlands, W, 2, 1, "2026-06-30", "Winner A vs Runner-up F"),
            knockoutMatch(Phase.R32, southKorea, switzerland, L, 1, 2, "2026-06-30", "Runner-up A vs Runner-up B"),
            knockoutMatch(Phase.R32, brazil, canada, W, 3, 0, "2026-07-01", "Winner C vs Runner-up B"),
            knockoutMatch(Phase.R32, morocco, usa, W, 2, 1, "2026-07-01", "Runner-up C vs Winner D"),
            knockoutMatch(Phase.R32, germany, belgium, W, 2, 1, "2026-07-02", "Winner E vs Runner-up G"),
            knockoutMatch(Phase.R32, ecuador, japan, L, 0, 2, "2026-07-02", "Runner-up E vs Runner-up F"),
            knockoutMatch(Phase.R32, netherlands, turkey, W, 3, 1, "2026-07-03", "Winner F vs Runner-up D"),
            knockoutMatch(Phase.R32, spain, egypt, W, 2, 0, "2026-07-03", "Winner H vs Runner-up G"),
            knockoutMatch(Phase.R32, france, australia, W, 4, 0, "2026-07-04", "Winner I vs 3rd E/F/G/H"),
            knockoutMatch(Phase.R32, senegal, austria, W, 2, 1, "2026-07-04", "Runner-up I vs Runner-up J"),
            knockoutMatch(Phase.R32, argentina, colombia, W, 2, 0, "2026-07-05", "Winner J vs Runner-up K"),
            knockoutMatch(Phase.R32, uruguay, portugal, L, 0, 2, "2026-07-05", "3rd H vs Winner K"),
            knockoutMatch(Phase.R32, england, croatia, W, 2, 1, "2026-07-06", "Winner L vs Runner-up L"),
            knockoutMatch(Phase.R32, norway, ghana, W, 3, 0, "2026-07-06", "3rd I vs 3rd J/K/L"),
            knockoutMatch(Phase.R32, southAfrica, ivoryCoast, L, 1, 2, "2026-07-07", "3rd A vs 3rd C/D"),
            knockoutMatch(Phase.R32, sweden, algeria, W, 1, 0, "2026-07-07", "3rd F vs 3rd B/C")
        )
        // R16 – 8 matches
        val r16 = listOf(
            knockoutMatch(Phase.R16, mexico, brazil, L, 0, 2, "2026-07-11", "W R32-1 vs W R32-3"),
            knockoutMatch(Phase.R16, switzerland, morocco, L, 1, 2, "2026-07-11", "W R32-2 vs W R32-4"),
            knockoutMatch(Phase.R16, germany, netherlands, W, 2, 0, "2026-07-12", "W R32-5 vs W R32-7"),
            knockoutMatch(Phase.R16, japan, spain, L, 1, 2, "2026-07-12", "W R32-6 vs W R32-8"),
            knockoutMatch(Phase.R16, france, senegal, W, 3, 1, "2026-07-13", "W R32-9 vs W R32-10"),
            knockoutMatch(Phase.R16, argentina, portugal, W, 2, 1, "2026-07-13", "W R32-11 vs W R32-12"),
            knockoutMatch(Phase.R16, england, norway, W, 2, 0, "2026-07-14", "W R32-13 vs W R32-14"),
            knockoutMatch(Phase.R16, ivoryCoast, sweden, L, 0, 1, "2026-07-14", "W R32-15 vs W R32-16")
        )
        // QF – 4 matches
        val qf = listOf(
            knockoutMatch(Phase.QF, brazil, germany, W, 3, 2, "2026-07-18", "W R16-1 vs W R16-3"),
            knockoutMatch(Phase.QF, morocco, spain, W, 1, 0, "2026-07-18", "W R16-2 vs W R16-4"),
            knockoutMatch(Phase.QF, france, argentina, W, 2, 1, "2026-07-19", "W R16-5 vs W R16-6"),
            knockoutMatch(Phase.QF, england, sweden, W, 3, 0, "2026-07-19", "W R16-7 vs W R16-8")
        )
        // SF – 2 matches
        val sf = listOf(
            knockoutMatch(Phase.SF, brazil, morocco, W, 2, 0, "2026-07-22", "W QF-1 vs W QF-2"),
            knockoutMatch(Phase.SF, france, england, W, 2, 1, "2026-07-23", "W QF-3 vs W QF-4")
        )
        // Third Place
        val third = listOf(
            knockoutMatch(Phase.THIRD, morocco, england, W, 2, 1, "2026-07-18", "Loser SF-1 vs Loser SF-2")
        )
        // Final
        val final_ = listOf(
            knockoutMatch(Phase.FINAL, brazil, france, W, 3, 1, "2026-07-19", "Winner SF-1 vs Winner SF-2")
        )
        return r32 + r16 + qf + sf + third + final_
    }

    fun knockoutMatchesMid(): List<Match> = knockoutMatchesFull().map { m ->
        tbdMatch(m.phase, m.note ?: "TBD vs TBD", m.date ?: "TBD")
    }

    // ── Group Standings ───────────────────────────────────────────────────

    private fun standing(t: Team, p: Int, w: Int, d: Int, l: Int, gf: Int, ga: Int) =
        Standing(t, p, w, d, l, gf, ga, gf - ga, w * 3 + d)

    fun groupAStandings() = listOf(
        standing(mexico,      3, 3, 0, 0, 6, 1),
        standing(southKorea,  3, 2, 0, 1, 3, 2),
        standing(czechia,     3, 0, 1, 2, 2, 5),
        standing(southAfrica, 3, 0, 1, 2, 1, 4)
    )

    fun groupBStandings() = listOf(
        standing(canada,      3, 2, 1, 0, 5, 1),
        standing(switzerland, 3, 1, 0, 2, 3, 5), // corrected
        standing(bosnia,      3, 1, 0, 2, 2, 5),
        standing(qatar,       3, 1, 0, 2, 0, 5) // rough approximation
    )

    fun groupCStandings() = listOf(
        standing(brazil,   3, 2, 1, 0, 7, 2),
        standing(morocco,  3, 2, 0, 1, 3, 1),
        standing(haiti,    3, 1, 0, 2, 0, 6),
        standing(scotland, 3, 0, 0, 3, 2, 6) // corrected
    )

    fun groupDStandings() = listOf(
        standing(turkey,    3, 2, 1, 0, 3, 1),
        standing(usa,       3, 2, 1, 0, 3, 1),
        standing(australia, 3, 0, 0, 3, 0, 3),
        standing(paraguay,  3, 0, 0, 3, 0, 3)
    )

    fun groupEStandings() = listOf(
        standing(germany,    3, 3, 0, 0, 10, 1),
        standing(ecuador,    3, 2, 0, 1, 4, 4),
        standing(ivoryCoast, 3, 1, 0, 2, 2, 6),
        standing(curacao,    3, 0, 0, 3, 1, 6)
    )

    fun groupFStandings() = listOf(
        standing(netherlands, 3, 3, 0, 0, 7, 2),
        standing(japan,       3, 2, 0, 1, 3, 3),
        standing(sweden,      3, 1, 0, 2, 2, 4),
        standing(tunisia,     3, 0, 1, 2, 2, 5)
    )

    fun groupGStandings() = listOf(
        standing(belgium,    3, 3, 0, 0, 8, 1),
        standing(egypt,      3, 1, 1, 1, 3, 4),
        standing(iran,       3, 0, 1, 2, 2, 6),
        standing(newZealand, 3, 0, 1, 2, 2, 4)
    )

    fun groupHStandings() = listOf(
        standing(spain,       3, 2, 1, 0, 6, 1),
        standing(uruguay,     3, 2, 0, 1, 4, 2),
        standing(capeVerde,   3, 0, 1, 2, 0, 5),
        standing(saudiArabia, 3, 0, 1, 2, 1, 3)
    )

    // Group I in mid scenario: only 2 matches played
    fun groupIStandingsMid() = listOf(
        standing(france,  2, 2, 0, 0, 5, 1),
        standing(senegal, 1, 0, 0, 1, 1, 2),
        standing(norway,  1, 0, 0, 1, 0, 3),
        standing(iraq,    0, 0, 0, 0, 0, 0)
    )

    fun groupIStandingsFull() = listOf(
        standing(france,  3, 3, 0, 0, 7, 1),
        standing(senegal, 3, 1, 1, 1, 3, 4),
        standing(norway,  3, 1, 0, 2, 1, 4),
        standing(iraq,    3, 0, 0, 3, 0, 2)  // lost all 3 in rough approx
    )

    fun groupJStandings() = listOf(
        standing(argentina, 3, 3, 0, 0, 9, 0),
        standing(austria,   3, 1, 1, 1, 2, 4),
        standing(algeria,   3, 1, 0, 2, 1, 4),
        standing(jordan,    3, 0, 1, 2, 0, 4)
    )

    fun groupKStandings() = listOf(
        standing(portugal, 3, 2, 1, 0, 8, 1),
        standing(colombia, 3, 2, 0, 1, 5, 3),
        standing(uzbekistan,3, 0, 1, 2, 1, 5),
        standing(congroDr, 3, 0, 1, 2, 0, 5)
    )

    fun groupLStandings() = listOf(
        standing(england, 3, 3, 0, 0, 9, 0),
        standing(croatia, 3, 1, 1, 1, 2, 4),
        standing(ghana,   3, 0, 1, 2, 2, 6),
        standing(panama,  3, 0, 1, 2, 2, 5)
    )

    // ── Scenario State Builders ───────────────────────────────────────────

    fun wcResultsStateMid(): WcResultsUiState.Success {
        val groups = listOf(
            GroupData("A", groupAMatches(),        groupAStandings()),
            GroupData("B", groupBMatches(),        groupBStandings()),
            GroupData("C", groupCMatches(),        groupCStandings()),
            GroupData("D", groupDMatches(),        groupDStandings()),
            GroupData("E", groupEMatches(),        groupEStandings()),
            GroupData("F", groupFMatches(),        groupFStandings()),
            GroupData("G", groupGMatches(),        groupGStandings()),
            GroupData("H", groupHMatches(),        groupHStandings()),
            GroupData("I", groupIMatchesMid(),     groupIStandingsMid()),
            GroupData("J", groupJMatchesMid(),     groupJStandings().map { s -> s.copy(p=0,w=0,d=0,l=0,gf=0,ga=0,gd=0,pts=0) }),
            GroupData("K", groupKMatchesMid(),     groupKStandings().map { s -> s.copy(p=0,w=0,d=0,l=0,gf=0,ga=0,gd=0,pts=0) }),
            GroupData("L", groupLMatchesMid(),     groupLStandings().map { s -> s.copy(p=0,w=0,d=0,l=0,gf=0,ga=0,gd=0,pts=0) })
        )
        val knockout = knockoutMatchesMid()
            .groupBy { it.phase }
            .entries.sortedBy { listOf(Phase.R32,Phase.R16,Phase.QF,Phase.SF,Phase.THIRD,Phase.FINAL).indexOf(it.key) }
            .associate { it.key to it.value }
        return WcResultsUiState.Success(groups, knockout)
    }

    fun wcResultsStateFull(): WcResultsUiState.Success {
        val groups = listOf(
            GroupData("A", groupAMatches(),     groupAStandings()),
            GroupData("B", groupBMatches(),     groupBStandings()),
            GroupData("C", groupCMatches(),     groupCStandings()),
            GroupData("D", groupDMatches(),     groupDStandings()),
            GroupData("E", groupEMatches(),     groupEStandings()),
            GroupData("F", groupFMatches(),     groupFStandings()),
            GroupData("G", groupGMatches(),     groupGStandings()),
            GroupData("H", groupHMatches(),     groupHStandings()),
            GroupData("I", groupIMatchesFull(), groupIStandingsFull()),
            GroupData("J", groupJMatchesFull(), groupJStandings()),
            GroupData("K", groupKMatchesFull(), groupKStandings()),
            GroupData("L", groupLMatchesFull(), groupLStandings())
        )
        val knockout = knockoutMatchesFull()
            .groupBy { it.phase }
            .entries.sortedBy { listOf(Phase.R32,Phase.R16,Phase.QF,Phase.SF,Phase.THIRD,Phase.FINAL).indexOf(it.key) }
            .associate { it.key to it.value }
        return WcResultsUiState.Success(groups, knockout)
    }

    fun matchesStateMid(): MatchesUiState.Success {
        val allGroup = allGroupMatchesMid()
        val allKnockout = knockoutMatchesMid()
        val phaseOrder = listOf(Phase.GROUP, Phase.R32, Phase.R16, Phase.QF, Phase.SF, Phase.THIRD, Phase.FINAL)
        val sections = (allGroup + allKnockout)
            .groupBy { it.phase }
            .entries
            .sortedBy { phaseOrder.indexOf(it.key) }
            .map { (phase, matches) -> MatchSection(phase, matches.sortedBy { it.date ?: "" }) }
        return MatchesUiState.Success(sections)
    }

    fun matchesStateFull(): MatchesUiState.Success {
        val allGroup = allGroupMatchesFull()
        val allKnockout = knockoutMatchesFull()
        val phaseOrder = listOf(Phase.GROUP, Phase.R32, Phase.R16, Phase.QF, Phase.SF, Phase.THIRD, Phase.FINAL)
        val sections = (allGroup + allKnockout)
            .groupBy { it.phase }
            .entries
            .sortedBy { phaseOrder.indexOf(it.key) }
            .map { (phase, matches) -> MatchSection(phase, matches.sortedBy { it.date ?: "" }) }
        return MatchesUiState.Success(sections)
    }

    fun leaderboardStateMid(): LeaderboardUiState.Success = LeaderboardUiState.Success(
        ranked = rankedSelections(selections50()),
        showMineOnly = false,
        currentUserId = "u1",
        matchesPlayed = 50,
        matchesUpcoming = 54
    )

    fun leaderboardStateMidMineOnly(): LeaderboardUiState.Success {
        val aliceOnly = rankedSelections(selections50()).filter { it.selection.userId == "u1" }
        return LeaderboardUiState.Success(
            ranked = aliceOnly,
            showMineOnly = true,
            currentUserId = "u1",
            matchesPlayed = 50,
            matchesUpcoming = 54
        )
    }

    fun leaderboardStateFull(): LeaderboardUiState.Success = LeaderboardUiState.Success(
        ranked = rankedSelections(selections104()),
        showMineOnly = false,
        currentUserId = "u1",
        matchesPlayed = 104,
        matchesUpcoming = 0
    )
}
