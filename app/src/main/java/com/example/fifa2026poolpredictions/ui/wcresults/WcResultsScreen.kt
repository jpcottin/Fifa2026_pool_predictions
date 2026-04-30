package com.example.fifa2026poolpredictions.ui.wcresults

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fifa2026poolpredictions.data.model.Match
import com.example.fifa2026poolpredictions.data.model.MatchResult
import com.example.fifa2026poolpredictions.data.model.Phase
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WcResultsScreen(viewModel: WcResultsViewModel, modifier: Modifier = Modifier) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    when (val s = state) {
        is WcResultsUiState.Loading -> {
            Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is WcResultsUiState.Error -> {
            Column(
                modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(s.message, color = MaterialTheme.colorScheme.error)
                Button(onClick = { viewModel.load() }, modifier = Modifier.padding(top = 8.dp)) {
                    Text("Retry")
                }
            }
        }
        is WcResultsUiState.Success -> {
            PullToRefreshBox(isRefreshing = false, onRefresh = { viewModel.load() }, modifier = modifier) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Text(
                            text = "WC Results",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF166534), // green-800
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    items(s.groups) { group ->
                        GroupCard(group)
                    }

                    if (s.knockoutByPhase.isNotEmpty()) {
                        item {
                            Text(
                                text = "Knockout Stage",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF166534),
                                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                            )
                        }
                        s.knockoutByPhase.forEach { (phase, matches) ->
                            item {
                                Text(
                                    text = phaseLabel(phase),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF6B7280),
                                    letterSpacing = 0.8.sp,
                                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                                )
                            }
                            items(matches) { match ->
                                KnockoutMatchRow(match)
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun GroupCard(group: GroupData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Group ${group.letter}",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Standings Table Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Team", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF9CA3AF), modifier = Modifier.weight(1f))
                Text("P", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF9CA3AF), modifier = Modifier.width(24.dp), textAlign = TextAlign.Center)
                Text("W", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF9CA3AF), modifier = Modifier.width(24.dp), textAlign = TextAlign.Center)
                Text("D", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF9CA3AF), modifier = Modifier.width(24.dp), textAlign = TextAlign.Center)
                Text("L", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF9CA3AF), modifier = Modifier.width(24.dp), textAlign = TextAlign.Center)
                Text("GD", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF9CA3AF), modifier = Modifier.width(32.dp), textAlign = TextAlign.Center)
                Text("Pts", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF9CA3AF), modifier = Modifier.width(32.dp), textAlign = TextAlign.Center)
            }
            HorizontalDivider(color = Color(0xFFE5E7EB))

            // Standings Rows
            group.sortedStandings.forEachIndexed { index, standing ->
                val bgColor = when (index) {
                    0, 1 -> Color(0xFFF0FDF4) // green-50
                    2 -> Color(0xFFFEFCE8) // yellow-50
                    else -> Color.Transparent
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(bgColor)
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                        Text(standing.team.flagEmoji, fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(standing.team.name, fontSize = 14.sp, color = Color.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    Text(standing.p.toString(), fontSize = 14.sp, color = Color(0xFF6B7280), modifier = Modifier.width(24.dp), textAlign = TextAlign.Center)
                    Text(standing.w.toString(), fontSize = 14.sp, color = Color(0xFF6B7280), modifier = Modifier.width(24.dp), textAlign = TextAlign.Center)
                    Text(standing.d.toString(), fontSize = 14.sp, color = Color(0xFF6B7280), modifier = Modifier.width(24.dp), textAlign = TextAlign.Center)
                    Text(standing.l.toString(), fontSize = 14.sp, color = Color(0xFF6B7280), modifier = Modifier.width(24.dp), textAlign = TextAlign.Center)
                    val gdStr = if (standing.gd > 0) "+${standing.gd}" else standing.gd.toString()
                    Text(gdStr, fontSize = 14.sp, color = Color(0xFF6B7280), modifier = Modifier.width(32.dp), textAlign = TextAlign.Center)
                    Text(standing.pts.toString(), fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.Black, modifier = Modifier.width(32.dp), textAlign = TextAlign.Center)
                }
                if (index < group.sortedStandings.size - 1) {
                    HorizontalDivider(color = Color(0xFFE5E7EB))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Legend
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(10.dp).background(Color(0xFFDCFCE7), RoundedCornerShape(2.dp)).border(1.dp, Color(0xFF86EFAC), RoundedCornerShape(2.dp)))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Advances", fontSize = 12.sp, color = Color(0xFF9CA3AF))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(10.dp).background(Color(0xFFFEF08A), RoundedCornerShape(2.dp)).border(1.dp, Color(0xFFFDE047), RoundedCornerShape(2.dp)))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("May advance", fontSize = 12.sp, color = Color(0xFF9CA3AF))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Group Matches
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                group.groupMatches.forEach { match ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF9FAFB), RoundedCornerShape(8.dp))
                            .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(8.dp))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Team 1
                        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.End) {
                            Text(
                                match.team1.name, 
                                fontSize = 14.sp, 
                                fontWeight = if (match.winner == MatchResult.TEAM1) FontWeight.SemiBold else FontWeight.Medium,
                                color = if (match.winner == MatchResult.TEAM1) Color(0xFF15803D) else Color(0xFF374151),
                                maxLines = 1, overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(match.team1.flagEmoji, fontSize = 18.sp)
                        }
                        
                        // Score
                        Box(modifier = Modifier.widthIn(min = 75.dp).padding(horizontal = 4.dp), contentAlignment = Alignment.Center) {
                            if (match.winner == MatchResult.UPCOMING) {
                                val dateStr = match.date?.take(10) ?: "TBD"
                                Text(dateStr, fontSize = 12.sp, color = Color(0xFF9CA3AF), maxLines = 1, softWrap = false)
                            } else {
                                Text(
                                    "${match.team1Goals} – ${match.team2Goals}",
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        }
                        
                        // Team 2
                        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start) {
                            Text(match.team2.flagEmoji, fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                match.team2.name, 
                                fontSize = 14.sp, 
                                fontWeight = if (match.winner == MatchResult.TEAM2) FontWeight.SemiBold else FontWeight.Medium,
                                color = if (match.winner == MatchResult.TEAM2) Color(0xFF15803D) else Color(0xFF374151),
                                maxLines = 1, overflow = TextOverflow.Ellipsis
                            )
                        }
                        
                        // Result Badge
                        if (match.winner == MatchResult.DRAW) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFF3F4F6), RoundedCornerShape(16.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("Draw", fontSize = 10.sp, color = Color(0xFF4B5563))
                            }
                        }
                    }
                }
            }
        }
    }
}
fun phaseLabel(phase: Phase): String = when (phase) {
    Phase.R32 -> "ROUND OF 32"
    Phase.R16 -> "ROUND OF 16"
    Phase.QF -> "QUARTER-FINALS"
    Phase.SF -> "SEMI-FINALS"
    Phase.THIRD -> "THIRD PLACE PLAY-OFF"
    Phase.FINAL -> "FINAL"
    Phase.GROUP -> ""
}

fun formatMatchDate(dateStr: String?): String {
    if (dateStr == null) return "TBD"
    return try {
        val inFmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val outFmt = SimpleDateFormat("MMM d", Locale.US)
        outFmt.format(inFmt.parse(dateStr.take(10))!!)
    } catch (e: Exception) {
        dateStr.take(10)
    }
}

@Composable
fun KnockoutMatchRow(match: Match) {
    val isTbd = match.team1.name.startsWith("TBD") || match.team2.name.startsWith("TBD")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF9FAFB), RoundedCornerShape(8.dp))
            .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(8.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isTbd) {
            Text(
                text = match.note ?: "TBD vs TBD",
                fontSize = 13.sp,
                fontStyle = FontStyle.Italic,
                color = Color(0xFF6B7280),
                modifier = Modifier.weight(1f)
            )
            Text(
                text = formatMatchDate(match.date),
                fontSize = 12.sp,
                color = Color(0xFF9CA3AF)
            )
        } else {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.End) {
                Text(
                    match.team1.name,
                    fontSize = 14.sp,
                    fontWeight = if (match.winner == MatchResult.TEAM1) FontWeight.SemiBold else FontWeight.Medium,
                    color = if (match.winner == MatchResult.TEAM1) Color(0xFF15803D) else Color(0xFF374151),
                    maxLines = 1, overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(match.team1.flagEmoji, fontSize = 18.sp)
            }
            Box(modifier = Modifier.widthIn(min = 75.dp).padding(horizontal = 4.dp), contentAlignment = Alignment.Center) {
                if (match.winner == MatchResult.UPCOMING) {
                    Text(formatMatchDate(match.date), fontSize = 12.sp, color = Color(0xFF9CA3AF), maxLines = 1, softWrap = false)
                } else {
                    Text(
                        "${match.team1Goals} – ${match.team2Goals}",
                        fontSize = 16.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start) {
                Text(match.team2.flagEmoji, fontSize = 18.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    match.team2.name,
                    fontSize = 14.sp,
                    fontWeight = if (match.winner == MatchResult.TEAM2) FontWeight.SemiBold else FontWeight.Medium,
                    color = if (match.winner == MatchResult.TEAM2) Color(0xFF15803D) else Color(0xFF374151),
                    maxLines = 1, overflow = TextOverflow.Ellipsis
                )
            }
            if (match.winner == MatchResult.DRAW) {
                Spacer(modifier = Modifier.width(4.dp))
                Box(
                    modifier = Modifier
                        .background(Color(0xFFF3F4F6), RoundedCornerShape(16.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("Draw", fontSize = 10.sp, color = Color(0xFF4B5563))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WcResultsRichPreview() {
    val t1 = com.example.fifa2026poolpredictions.data.model.Team("1", "Mexico", "🇲🇽", 1, 0.0)
    val t2 = com.example.fifa2026poolpredictions.data.model.Team("2", "South Africa", "🇿🇦", 1, 0.0)
    val t3 = com.example.fifa2026poolpredictions.data.model.Team("3", "South Korea", "🇰🇷", 1, 0.0)
    val t4 = com.example.fifa2026poolpredictions.data.model.Team("4", "Czechia", "🇨🇿", 1, 0.0)
    
    val standings = listOf(
        Standing(t1, p = 1, w = 1, d = 0, l = 0, gf = 5, ga = 2, gd = 3, pts = 3),
        Standing(t3, p = 0, w = 0, d = 0, l = 0, gf = 0, ga = 0, gd = 0, pts = 0),
        Standing(t4, p = 0, w = 0, d = 0, l = 0, gf = 0, ga = 0, gd = 0, pts = 0),
        Standing(t2, p = 1, w = 0, d = 0, l = 1, gf = 2, ga = 5, gd = -3, pts = 0)
    )
    
    val groups = listOf(
        GroupData("A", emptyList(), standings),
        GroupData("B", emptyList(), standings),
        GroupData("C", emptyList(), standings)
    )
    
    com.example.fifa2026poolpredictions.theme.MyApplicationTheme {
        androidx.compose.material3.Surface {
            androidx.compose.foundation.lazy.LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "WC Results",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF166534)
                    )
                }
                items(groups) { group ->
                    GroupCard(group)
                }
            }
        }
    }
}
