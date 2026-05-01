package com.example.fifa2026poolpredictions.ui.wcresults

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fifa2026poolpredictions.data.model.Match
import com.example.fifa2026poolpredictions.data.model.MatchResult
import com.example.fifa2026poolpredictions.data.model.Phase
import kotlin.math.min

// ── Layout constants (dp, unscaled) ──────────────────────────────────────────
private const val SLOT_H   = 64f
private const val CARD_H   = 46f
private const val CARD_W   = 180f
private const val CONN_W   = 24f
private const val HEADER_H = 48f
private const val PAD      = 12f
private const val CHAMP_W  = 108f
private const val CHAMP_H  = 68f
private const val TOTAL_H  = HEADER_H + 16 * SLOT_H          // 1072 dp
private const val TOTAL_W  = PAD + 5 * (CARD_W + CONN_W) + CHAMP_W + PAD  // 1142 dp

private val LINE_COLOR = Color(0xFFD1D5DB)

// ── Round metadata ────────────────────────────────────────────────────────────
private val BRACKET_ROUNDS = listOf(Phase.R32, Phase.R16, Phase.QF, Phase.SF, Phase.FINAL)

private val ROUND_LABELS = mapOf(
    Phase.R32   to "Round of 32",
    Phase.R16   to "Round of 16",
    Phase.QF    to "Quarter-finals",
    Phase.SF    to "Semi-finals",
    Phase.FINAL to "Final",
)
private val ROUND_DATES = mapOf(
    Phase.R32   to "Jun 28 – Jul 3",
    Phase.R16   to "Jul 4 – Jul 7",
    Phase.QF    to "Jul 9 – Jul 11",
    Phase.SF    to "Jul 14–15",
    Phase.FINAL to "Jul 19",
)

private const val THIRD_PLACE_NOTE = "Loser Match 101 vs Loser Match 102"

private val BRACKET_NOTES = mapOf(
    Phase.R32 to listOf(
        "Winner Group E vs 3rd Place Group A/B/C/D/F",
        "Winner Group I vs 3rd Place Group C/D/F/G/H",
        "Runner-up Group A vs Runner-up Group B",
        "Winner Group F vs Runner-up Group C",
        "Runner-up Group K vs Runner-up Group L",
        "Winner Group H vs Runner-up Group J",
        "Winner Group D vs 3rd Place Group B/E/F/I/J",
        "Winner Group G vs 3rd Place Group A/E/H/I/J",
        "Winner Group C vs Runner-up Group F",
        "Runner-up Group E vs Runner-up Group I",
        "Winner Group A vs 3rd Place Group C/E/F/H/I",
        "Winner Group L vs 3rd Place Group E/H/I/J/K",
        "Winner Group J vs Runner-up Group H",
        "Runner-up Group D vs Runner-up Group G",
        "Winner Group B vs 3rd Place Group E/F/G/I/J",
        "Winner Group K vs 3rd Place Group D/E/I/J/L",
    ),
    Phase.R16 to listOf(
        "Winner Match 74 vs Winner Match 77",
        "Winner Match 73 vs Winner Match 75",
        "Winner Match 83 vs Winner Match 84",
        "Winner Match 81 vs Winner Match 82",
        "Winner Match 76 vs Winner Match 78",
        "Winner Match 79 vs Winner Match 80",
        "Winner Match 86 vs Winner Match 88",
        "Winner Match 85 vs Winner Match 87",
    ),
    Phase.QF to listOf(
        "Winner Match 89 vs Winner Match 90",
        "Winner Match 93 vs Winner Match 94",
        "Winner Match 91 vs Winner Match 92",
        "Winner Match 95 vs Winner Match 96",
    ),
    Phase.SF to listOf(
        "Winner Match 97 vs Winner Match 98",
        "Winner Match 99 vs Winner Match 100",
    ),
    Phase.FINAL to listOf("Winner Match 101 vs Winner Match 102"),
)

// ── shortNote ─────────────────────────────────────────────────────────────────
private fun shortNote(note: String): String = note
    .replace(Regex("Winner Group ([A-L])"))    { "${it.groupValues[1]}1" }
    .replace(Regex("Runner-up Group ([A-L])")) { "${it.groupValues[1]}2" }
    .replace(Regex("3rd Place Group [A-L/]+"), "3rd")
    .replace(Regex("Winner Match (\\d+)"))     { "W${it.groupValues[1]}" }
    .replace(Regex("Loser Match (\\d+)"))      { "L${it.groupValues[1]}" }

// ── Entry point ───────────────────────────────────────────────────────────────
@Composable
fun KnockoutBracket(knockoutByPhase: Map<Phase, List<Match>>) {
    val byNote = remember(knockoutByPhase) {
        buildMap { knockoutByPhase.values.flatten().forEach { m -> m.note?.let { put(it, m) } } }
    }
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        if (maxWidth < 600.dp) {
            BracketTabView(knockoutByPhase)
        } else {
            val scale = min(1f, maxWidth.value / TOTAL_W)
            BracketCanvasView(byNote, scale)
        }
    }
}

// ── Compact: scrollable tab view ──────────────────────────────────────────────
@Composable
private fun BracketTabView(knockoutByPhase: Map<Phase, List<Match>>) {
    val phases = knockoutByPhase.keys.toList()
    var selectedIndex by remember { mutableIntStateOf(0) }

    Column {
        PrimaryScrollableTabRow(
            selectedTabIndex = selectedIndex,
            containerColor = Color.Transparent,
            contentColor = Color(0xFF15803D),
            edgePadding = 0.dp,
        ) {
            phases.forEachIndexed { index, phase ->
                Tab(
                    selected = selectedIndex == index,
                    onClick = { selectedIndex = index },
                    text = {
                        Text(
                            text = ROUND_LABELS[phase] ?: phaseLabel(phase),
                            fontSize = 12.sp,
                            maxLines = 1,
                        )
                    },
                )
            }
        }

        val matches = phases.getOrNull(selectedIndex)?.let { knockoutByPhase[it] } ?: emptyList()
        Column(
            modifier = Modifier.padding(top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            matches.forEach { match -> KnockoutMatchRow(match) }
        }
    }
}

// ── Expanded: scaled canvas bracket ──────────────────────────────────────────
@Composable
private fun BracketCanvasView(byNote: Map<String, Match>, scale: Float) {
    // All positions are in dp (scaled)
    val slotH   = SLOT_H   * scale
    val cardH   = CARD_H   * scale
    val cardW   = CARD_W   * scale
    val connW   = CONN_W   * scale
    val headerH = HEADER_H * scale
    val pad     = PAD      * scale
    val champW  = CHAMP_W  * scale
    val champH  = CHAMP_H  * scale

    val centerY      = headerH + 8 * slotH
    val finalRight   = pad + 4 * (cardW + connW) + cardW
    val finalColLeft = pad + 4 * (cardW + connW)

    val thirdLabelTop = headerH + (16 * slotH + cardH) / 2 + 16 * scale
    val thirdCardTop  = thirdLabelTop + 17 * scale

    val finalMatch = byNote[BRACKET_NOTES[Phase.FINAL]!!.first()]
    val thirdMatch = byNote[THIRD_PLACE_NOTE]
    val champion = when (finalMatch?.winner) {
        MatchResult.TEAM1 -> finalMatch.team1
        MatchResult.TEAM2 -> finalMatch.team2
        else -> null
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height((TOTAL_H * scale).dp),
    ) {
        // ── Connector lines ──────────────────────────────────────────────────
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = 1.dp.toPx()

            BRACKET_ROUNDS.forEachIndexed { r, round ->
                if (r >= BRACKET_ROUNDS.lastIndex) return@forEachIndexed
                val slotsPerMatch  = (1 shl r).toFloat()
                val roundRightPx   = (pad + r * (cardW + connW) + cardW).dp.toPx()
                val halfConnPx     = (connW / 2).dp.toPx()
                val connWPx        = connW.dp.toPx()
                val numNext        = BRACKET_NOTES[BRACKET_ROUNDS[r + 1]]!!.size

                repeat(numNext) { i ->
                    val cy1  = (headerH + (2 * i + 0.5f) * slotsPerMatch * slotH).dp.toPx()
                    val cy2  = (headerH + (2 * i + 1.5f) * slotsPerMatch * slotH).dp.toPx()
                    val midY = (cy1 + cy2) / 2f
                    // H from top match → vertical bar
                    drawLine(LINE_COLOR, Offset(roundRightPx, cy1), Offset(roundRightPx + halfConnPx, cy1), stroke)
                    // H from bottom match → vertical bar
                    drawLine(LINE_COLOR, Offset(roundRightPx, cy2), Offset(roundRightPx + halfConnPx, cy2), stroke)
                    // Vertical bar
                    drawLine(LINE_COLOR, Offset(roundRightPx + halfConnPx, cy1), Offset(roundRightPx + halfConnPx, cy2), stroke)
                    // H from vertical bar → next round
                    drawLine(LINE_COLOR, Offset(roundRightPx + halfConnPx, midY), Offset(roundRightPx + connWPx, midY), stroke)
                }
            }
            // H-line from Final → champion card
            drawLine(
                LINE_COLOR,
                Offset(finalRight.dp.toPx(), centerY.dp.toPx()),
                Offset((finalRight + connW).dp.toPx(), centerY.dp.toPx()),
                stroke,
            )
        }

        // ── Round headers ────────────────────────────────────────────────────
        BRACKET_ROUNDS.forEachIndexed { r, round ->
            Column(
                modifier = Modifier
                    .offset(x = (pad + r * (cardW + connW)).dp, y = 0.dp)
                    .width(cardW.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = ROUND_LABELS[round] ?: "",
                    fontSize = (11 * scale).sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF4B5563),
                    letterSpacing = 0.3.sp,
                )
                Text(
                    text = ROUND_DATES[round] ?: "",
                    fontSize = (10 * scale).sp,
                    color = Color(0xFF9CA3AF),
                )
            }
        }

        // ── Match cards ──────────────────────────────────────────────────────
        BRACKET_ROUNDS.forEachIndexed { r, round ->
            val slotsPerMatch = 1 shl r
            BRACKET_NOTES[round]?.forEachIndexed { i, note ->
                val top  = headerH + i * slotsPerMatch * slotH + (slotsPerMatch * slotH - cardH) / 2
                val left = pad + r * (cardW + connW)
                Box(modifier = Modifier.offset(x = left.dp, y = top.dp)) {
                    BracketCard(match = byNote[note], cardW = cardW.dp, cardH = cardH.dp, scale = scale)
                }
            }
        }

        // ── Champion card ────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .offset(x = (finalRight + connW).dp, y = (centerY - champH / 2).dp)
                .size(champW.dp, champH.dp)
                .background(Color(0xFFF0FDF4), RoundedCornerShape(8.dp))
                .border(1.dp, Color(0xFF86EFAC), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(4.dp),
            ) {
                Text("🏆", fontSize = (22 * scale).sp)
                if (champion != null) {
                    Text(champion.flagEmoji, fontSize = (13 * scale).sp)
                    Text(
                        champion.name,
                        fontSize = (11 * scale).sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF166534),
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                } else {
                    Text("World Cup", fontSize = (10 * scale).sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF166534))
                    Text("Champion", fontSize = (10 * scale).sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF166534))
                }
            }
        }

        // ── Third Place Play-off ─────────────────────────────────────────────
        Text(
            text = "3rd Place Play-off · Jul 18",
            modifier = Modifier
                .offset(x = finalColLeft.dp, y = thirdLabelTop.dp)
                .width(cardW.dp),
            fontSize = (10 * scale).sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF6B7280),
            textAlign = TextAlign.Center,
        )
        Box(modifier = Modifier.offset(x = finalColLeft.dp, y = thirdCardTop.dp)) {
            BracketCard(match = thirdMatch, cardW = cardW.dp, cardH = cardH.dp, scale = scale)
        }
    }
}

// ── Individual match card ─────────────────────────────────────────────────────
@Composable
private fun BracketCard(match: Match?, cardW: Dp, cardH: Dp, scale: Float) {
    Box(
        modifier = Modifier
            .size(cardW, cardH)
            .background(if (match != null) Color.White else Color(0xFFF9FAFB), RoundedCornerShape(4.dp))
            .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(4.dp)),
    ) {
        if (match == null) {
            Text("–", fontSize = (10 * scale).sp, color = Color(0xFFD1D5DB), modifier = Modifier.align(Alignment.Center))
            return@Box
        }

        val isTbd = match.team1.name.startsWith("TBD") || match.team2.name.startsWith("TBD")

        if (isTbd) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = (8 * scale).dp, vertical = (4 * scale).dp),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = shortNote(match.note ?: "TBD"),
                    fontSize = (11 * scale).sp,
                    fontStyle = FontStyle.Italic,
                    color = Color(0xFF6B7280),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = formatMatchDate(match.date),
                    fontSize = (10 * scale).sp,
                    color = Color(0xFF9CA3AF),
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
            return@Box
        }

        val w1       = match.winner == MatchResult.TEAM1
        val w2       = match.winner == MatchResult.TEAM2
        val upcoming = match.winner == MatchResult.UPCOMING

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = (8 * scale).dp),
            verticalArrangement = Arrangement.SpaceEvenly,
        ) {
            // Team 1
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(match.team1.flagEmoji, fontSize = (12 * scale).sp)
                Text(
                    text = match.team1.name,
                    fontSize = (11 * scale).sp,
                    fontWeight = if (w1) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (w1) Color(0xFF15803D) else Color(0xFF374151),
                    modifier = Modifier.weight(1f).padding(horizontal = (2 * scale).dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (!upcoming) {
                    Text(
                        text = match.team1Goals.toString(),
                        fontSize = (11 * scale).sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = if (w1) Color(0xFF15803D) else Color(0xFF6B7280),
                    )
                }
            }
            // Date (upcoming only)
            if (upcoming) {
                Text(
                    text = formatMatchDate(match.date),
                    fontSize = (10 * scale).sp,
                    color = Color(0xFF9CA3AF),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
            }
            // Team 2
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(match.team2.flagEmoji, fontSize = (12 * scale).sp)
                Text(
                    text = match.team2.name,
                    fontSize = (11 * scale).sp,
                    fontWeight = if (w2) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (w2) Color(0xFF15803D) else Color(0xFF374151),
                    modifier = Modifier.weight(1f).padding(horizontal = (2 * scale).dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (!upcoming) {
                    Text(
                        text = match.team2Goals.toString(),
                        fontSize = (11 * scale).sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = if (w2) Color(0xFF15803D) else Color(0xFF6B7280),
                    )
                }
            }
        }
    }
}
