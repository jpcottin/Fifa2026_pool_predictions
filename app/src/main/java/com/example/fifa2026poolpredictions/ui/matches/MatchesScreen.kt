package com.example.fifa2026poolpredictions.ui.matches

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.example.fifa2026poolpredictions.data.model.Team
import com.example.fifa2026poolpredictions.theme.*
import com.example.fifa2026poolpredictions.ui.wcresults.shortNote

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchesScreen(viewModel: MatchesViewModel, modifier: Modifier = Modifier) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    when (val s = state) {
        is MatchesUiState.Loading -> Column(modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) { CircularProgressIndicator() }
        is MatchesUiState.Error -> Column(modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            Text(s.message, color = MaterialTheme.colorScheme.error)
            Button(onClick = { viewModel.load() }, modifier = Modifier.padding(top = 8.dp)) { Text("Retry") }
        }
        is MatchesUiState.Success -> PullToRefreshBox(
            isRefreshing = false, onRefresh = { viewModel.load() }, modifier = modifier) {
            MatchesList(sections = s.sections)
        }
    }
}

@Composable
fun MatchesList(sections: List<MatchSection>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = WindowInsets.safeDrawing
            .add(WindowInsets(left = 16.dp, top = 16.dp, right = 16.dp, bottom = 16.dp))
            .asPaddingValues()
    ) {
        item {
            Text(
                "Matches",
                style = MaterialTheme.typography.headlineSmall,
                color = AppGreenDark,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }

        if (sections.isEmpty()) {
            item {
                Text(
                    text = "No matches recorded yet.",
                    modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                    textAlign = TextAlign.Center,
                    color = Gray500
                )
            }
        }

        sections.forEach { section ->
            item {
                Text(
                    section.phase.displayName.uppercase(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Gray500,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
            }
            items(section.matches) { match ->
                MatchRow(match = match, modifier = Modifier.padding(vertical = 4.dp))
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun MatchRow(match: Match, modifier: Modifier = Modifier) {
    val tbd1 = match.team1.name.startsWith("TBD")
    val tbd2 = match.team2.name.startsWith("TBD")
    val isPlayed = match.winner != MatchResult.UPCOMING

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Gray200)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (tbd1 && tbd2) {
                val noteText = match.note ?: "TBD vs TBD"
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = noteText,
                        fontSize = 14.sp,
                        color = Gray400,
                        fontStyle = FontStyle.Italic
                    )
                }
                val dateStr = match.date?.take(10)
                if (dateStr != null) {
                    Text(
                        text = dateStr,
                        fontSize = 12.sp,
                        color = Gray400
                    )
                }
            } else {
                val noteParts = match.note?.split(" vs ")
                val t1Label = if (tbd1) noteParts?.getOrNull(0)?.let { shortNote(it) } ?: "TBD" else match.team1.name
                val t2Label = if (tbd2) noteParts?.getOrNull(1)?.let { shortNote(it) } ?: "TBD" else match.team2.name

                // Team 1
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = t1Label,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        fontStyle = if (tbd1) FontStyle.Italic else FontStyle.Normal,
                        color = if (tbd1) Gray400 else Color.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (!tbd1) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = match.team1.flagEmoji,
                            fontSize = 20.sp
                        )
                    }
                }

                // Score / Date
                Column(
                    modifier = Modifier.padding(horizontal = 8.dp).widthIn(min = 75.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (isPlayed) {
                        Text(
                            text = "${match.team1Goals} – ${match.team2Goals}",
                            fontSize = 18.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            maxLines = 1,
                            softWrap = false
                        )
                        if (match.extraTime) {
                            val annotation = if (match.pkTeam1Goals != null && match.pkTeam2Goals != null)
                                "e.t. · p.k. ${match.pkTeam1Goals}–${match.pkTeam2Goals}"
                            else "e.t."
                            Text(
                                text = annotation,
                                fontSize = 10.sp,
                                color = Gray400,
                                fontStyle = FontStyle.Italic
                            )
                        }
                    } else {
                        val dateStr = match.date?.take(10) ?: "TBD"
                        Text(
                            text = dateStr,
                            fontSize = 12.sp,
                            color = Gray400,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }

                // Team 2
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    if (!tbd2) {
                        Text(
                            text = match.team2.flagEmoji,
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        text = t2Label,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        fontStyle = if (tbd2) FontStyle.Italic else FontStyle.Normal,
                        color = if (tbd2) Gray400 else Color.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Badge
                Spacer(modifier = Modifier.width(8.dp))
                MatchBadge(match = match)
            }
        }
    }
}

@Composable
fun MatchBadge(match: Match) {
    val (text, bgColor, textColor, borderColor) = when (match.winner) {
        MatchResult.UPCOMING -> listOf("Upcoming", Gray100, Gray600, Gray200)
        MatchResult.TEAM1 -> listOf(match.team1.name, Gray900, Color.White, Gray900)
        MatchResult.TEAM2 -> listOf(match.team2.name, Gray900, Color.White, Gray900)
        MatchResult.DRAW -> listOf("Draw", Gray900, Color.White, Gray900)
    }

    Box(
        modifier = Modifier
            .background(bgColor as Color, RoundedCornerShape(16.dp))
            .border(1.dp, borderColor as Color, RoundedCornerShape(16.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text as String,
            style = MaterialTheme.typography.labelSmall,
            color = textColor as Color,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.widthIn(max = 70.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MatchesListPreview() {
    val t1 = Team("1", "Mexico", "🇲🇽", 1, 0.0)
    val t2 = Team("2", "South Africa", "🇿🇦", 1, 0.0)
    val t3 = Team("3", "Brazil", "🇧🇷", 1, 0.0)
    val t4 = Team("4", "Morocco", "🇲🇦", 1, 0.0)
    val t5 = Team("5", "France", "🇫🇷", 1, 0.0)
    val t6 = Team("6", "England", "🏴󠁧󠁢󠁥󠁮󠁧󠁿", 1, 0.0)

    val groupMatches = listOf(
        Match("m1", t1.id, t2.id, t1, t2, "2026-06-11T12:00:00Z", Phase.GROUP, MatchResult.TEAM1, 5, 2),
        Match("m2", t3.id, t4.id, t3, t4, "2026-06-12T12:00:00Z", Phase.GROUP, MatchResult.UPCOMING, 0, 0),
        Match("m3", t5.id, t6.id, t5, t6, "2026-06-12T18:00:00Z", Phase.GROUP, MatchResult.DRAW, 1, 1)
    )

    val r16Matches = listOf(
        Match("m4", t1.id, t3.id, t1, t3, "2026-07-04T12:00:00Z", Phase.R16, MatchResult.UPCOMING, 0, 0, note = "Winner Match 1 vs Winner Match 3")
    )

    MyApplicationTheme {
        MatchesList(sections = listOf(
            MatchSection(Phase.GROUP, groupMatches),
            MatchSection(Phase.R16, r16Matches)
        ))
    }
}
