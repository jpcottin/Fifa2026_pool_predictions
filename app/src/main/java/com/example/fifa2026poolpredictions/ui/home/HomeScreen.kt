package com.example.fifa2026poolpredictions.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel, onPickTeams: () -> Unit, modifier: Modifier = Modifier) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    if (state.isLoading) {
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (state.error != null) {
        Column(
            modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(state.error!!, color = MaterialTheme.colorScheme.error)
            Button(onClick = { viewModel.load() }, modifier = Modifier.padding(top = 8.dp)) {
                Text("Retry")
            }
        }
        return
    }

    PullToRefreshBox(isRefreshing = false, onRefresh = { viewModel.load() }, modifier = modifier) {
        HomeContent(state = state, onPickTeams = onPickTeams)
    }
}

@Composable
fun HomeContent(state: HomeUiState, onPickTeams: () -> Unit, modifier: Modifier = Modifier) {
    val isPreparing = state.gameState == "PREPARING"

    LazyColumn(
        modifier = modifier.fillMaxSize().padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
            // ── Hero ────────────────────────────────────────────────────────
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "⚽ FIFA World Cup 2026",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF166534),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Pick Your 8 · Follow Every Match · Win the Bragging Rights",
                        fontSize = 14.sp,
                        color = Color(0xFF6B7280),
                        textAlign = TextAlign.Center
                    )
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isPreparing) Color(0xFF166534) else Color(0xFF6B7280)
                    ) {
                        Text(
                            text = if (isPreparing) "🟢 Selections Open" else "🔴 Competition in Progress",
                            fontSize = 13.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // ── CTA ─────────────────────────────────────────────────────────
            if (isPreparing) {
                item {
                    Button(
                        onClick = onPickTeams,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF15803D))
                    ) {
                        Text("Pick My 8 Teams →", fontSize = 16.sp)
                    }
                }
            }

            // ── Stat cards ───────────────────────────────────────────────────
            item {
                val kickoffLabel = when {
                    state.daysToKickoff > 0 -> "${state.daysToKickoff}d"
                    else -> "Live"
                }
                val finalLabel = when {
                    state.daysToFinal > 0 -> "${state.daysToFinal}d"
                    else -> "Done!"
                }
                val cards = listOf(
                    "Players"             to state.totalPlayers.toString(),
                    "Selections"          to state.totalSelections.toString(),
                    "To Kickoff"          to kickoffLabel,
                    "To Final"            to finalLabel,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    cards.forEach { (label, value) ->
                        StatCard(label = label, value = value, modifier = Modifier.weight(1f))
                    }
                }
            }

            // ── How It Works ─────────────────────────────────────────────────
            item {
                HowItWorksCard()
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }
        }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Column(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF15803D)
            )
            Text(
                text = label,
                fontSize = 11.sp,
                color = Color(0xFF9CA3AF),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun HowItWorksCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                text = "How It Works",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
            HowItWorksStep(
                number = "1",
                title = "Pick Your 8 Teams",
                body = "48 qualified nations are divided into 8 Sets of 6, ranked by the official FIFA rankings of April 2026. You pick one team per set for a total of 8 teams."
            )
            HowItWorksStep(
                number = "2",
                title = "Earn Points",
                body = null
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Each of your teams earns points throughout the tournament:", fontSize = 13.sp, color = Color(0xFF374151))
                    listOf(
                        "Win: +3 pts",
                        "Draw: +1 pt",
                        "Goals (group stage): +0.3 pts each",
                        "Goals (knockout): +0.5 pts each"
                    ).forEach { line ->
                        Row {
                            Text("• ", fontSize = 13.sp, color = Color(0xFF374151))
                            Text(line, fontSize = 13.sp, color = Color(0xFF374151))
                        }
                    }
                }
            }
            HowItWorksStep(
                number = "3",
                title = "Your Score = Sum of Your 8 Teams",
                body = "The more your teams win and score, the higher you climb the leaderboard. You can create up to 3 selections."
            )
            HowItWorksStep(
                number = "4",
                title = "Follow Live Results",
                body = "Once the tournament starts, results are updated after each match. Check the leaderboard and team scores anytime."
            )
        }
    }
}

@Composable
private fun HowItWorksStep(
    number: String,
    title: String,
    body: String?,
    extra: (@Composable () -> Unit)? = null
) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(Color(0xFFF0FDF4), RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(number, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF166534))
        }
        Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF166534))
            if (body != null) {
                Text(body, fontSize = 13.sp, color = Color(0xFF374151))
            }
            extra?.invoke()
        }
    }
}
