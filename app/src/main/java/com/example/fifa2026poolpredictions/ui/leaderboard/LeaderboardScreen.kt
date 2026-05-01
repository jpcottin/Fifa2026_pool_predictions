package com.example.fifa2026poolpredictions.ui.leaderboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fifa2026poolpredictions.data.model.Selection
import com.example.fifa2026poolpredictions.data.model.SelectionUser
import com.example.fifa2026poolpredictions.data.model.Team
import com.example.fifa2026poolpredictions.theme.MyApplicationTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    viewModel: LeaderboardViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    when (val s = state) {
        is LeaderboardUiState.Loading -> {
            Column(modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center) {
                CircularProgressIndicator()
            }
        }
        is LeaderboardUiState.Error -> {
            Column(modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center) {
                Text(s.message, color = MaterialTheme.colorScheme.error)
                Button(onClick = { viewModel.load() }, modifier = Modifier.padding(top = 8.dp)) {
                    Text("Retry")
                }
            }
        }
        is LeaderboardUiState.Success -> {
            PullToRefreshBox(isRefreshing = false, onRefresh = { viewModel.load() }, modifier = modifier) {
                LeaderboardContent(
                    state = s,
                    onToggleMine = { viewModel.toggleMineOnly() }
                )
            }
        }
    }
}

@Composable
fun LeaderboardContent(
    state: LeaderboardUiState.Success,
    onToggleMine: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val title = if (state.showMineOnly) "My Selections" else "Leaderboard"
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF166534) // green-800
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val activeColors = ButtonDefaults.buttonColors(containerColor = Color(0xFF15803D)) // green-700
                    if (state.showMineOnly) {
                        Button(onClick = onToggleMine, colors = activeColors) { Text("Mine") }
                        OutlinedButton(onClick = onToggleMine) { Text("All") }
                    } else {
                        OutlinedButton(onClick = onToggleMine) { Text("Mine") }
                        Button(onClick = onToggleMine, colors = activeColors) { Text("All") }
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MatchStatCard(
                    label = "Matches Played",
                    value = state.matchesPlayed,
                    valueColor = Color(0xFF15803D), // green-700
                    modifier = Modifier.weight(1f)
                )
                MatchStatCard(
                    label = "Matches to Go",
                    value = state.matchesUpcoming,
                    valueColor = Color(0xFF9CA3AF), // gray-400
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        if (state.ranked.isEmpty()) {
            item {
                Text(
                    text = "No selections yet.",
                    modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                    textAlign = TextAlign.Center,
                    color = Color(0xFF6B7280) // gray-500
                )
            }
        } else {
            items(state.ranked) { item ->
                SelectionRow(item = item, modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
            }
        }
    }
}

@Composable
fun SelectionRow(item: RankedSelection, modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, if (expanded) Color(0xFF4ADE80) else Color(0xFFE5E7EB)) // green-400 or gray-200
    ) {
        BoxWithConstraints {
            val isWide = maxWidth > 600.dp
            
            if (isWide) {
                // Wide/Adaptive Layout for Tablets or Landscape
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    RankDisplay(item.rank, Modifier.width(48.dp))
                    
                    Column(modifier = Modifier.weight(1.5f)) {
                        NameDisplay(item.selection.name, expanded = true) // Always expanded on wide screens
                    }
                    
                    Column(modifier = Modifier.weight(1f)) {
                        UserNameDisplay(item.selection.user?.name ?: "")
                    }
                    
                    FlagsDisplay(item.teams, fontSize = 20.sp, modifier = Modifier.weight(1.5f))
                    
                    ScoreDisplay(item.selection.score)
                }
            } else {
                // Compact Layout for Phones
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    RankDisplay(item.rank, Modifier.width(36.dp).padding(top = 4.dp))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        NameDisplay(item.selection.name, expanded)
                        Spacer(modifier = Modifier.height(2.dp))
                        UserNameDisplay(item.selection.user?.name ?: "")
                        
                        if (expanded) {
                            Spacer(modifier = Modifier.height(8.dp))
                            FlagsDisplay(item.teams, fontSize = 18.sp)
                        }
                    }
                    
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.Top
                    ) {
                        ScoreDisplay(item.selection.score)
                        if (!expanded) {
                            Spacer(modifier = Modifier.height(8.dp))
                            FlagsDisplay(
                                teams = item.teams, 
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RankDisplay(rank: Int, modifier: Modifier = Modifier) {
    val medal = when (rank) {
        1 -> "🥇"; 2 -> "🥈"; 3 -> "🥉"; else -> "#$rank"
    }
    Text(
        text = medal,
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF6B7280), // gray-500
        textAlign = TextAlign.Center,
        modifier = modifier
    )
}

@Composable
fun NameDisplay(name: String, expanded: Boolean) {
    Text(
        text = name,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        color = Color(0xFF111827), // gray-900
        maxLines = if (expanded) Int.MAX_VALUE else 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
fun UserNameDisplay(name: String) {
    Text(
        text = name,
        fontSize = 14.sp,
        color = Color(0xFF4B5563), // gray-600
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
fun FlagsDisplay(teams: List<Team>, fontSize: TextUnit, modifier: Modifier = Modifier) {
    Text(
        text = teams.joinToString("") { it.flagEmoji },
        fontSize = fontSize,
        modifier = modifier,
        textAlign = TextAlign.End,
        maxLines = 1,
        softWrap = false
    )
}

@Composable
fun ScoreDisplay(score: Double) {
    Box(
        modifier = Modifier
            .background(Color(0xFFDCFCE7), RoundedCornerShape(8.dp)) // green-100
            .border(1.dp, Color(0xFF4ADE80), RoundedCornerShape(8.dp)) // green-400
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "%.1f".format(score),
            fontSize = 16.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Black,
            color = Color(0xFF166534) // green-800
        )
    }
}

@Composable
fun MatchStatCard(label: String, value: Int, valueColor: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Text(text = label, fontSize = 12.sp, color = Color(0xFF6B7280))
            Text(text = value.toString(), fontSize = 30.sp, fontWeight = FontWeight.Bold, color = valueColor)
        }
    }
}

@Preview(showBackground = true, widthDp = 400)
@Composable
fun LeaderboardContentCompactPreview() {
    val mockTeams = listOf(
        Team("1", "France", "🇫🇷", 1, 0.0),
        Team("2", "Germany", "🇩🇪", 2, 0.0),
        Team("3", "USA", "🇺🇸", 3, 0.0),
        Team("4", "Japan", "🇯🇵", 4, 0.0),
        Team("5", "Morocco", "🇲🇦", 5, 0.0),
        Team("6", "Brazil", "🇧🇷", 6, 0.0),
        Team("7", "Spain", "🇪🇸", 7, 0.0),
        Team("8", "England", "🏴󠁧󠁢󠁥󠁮󠁧󠁿", 8, 0.0)
    )
    
    val rankedSelections = listOf(
        RankedSelection(
            Selection("s1", "Champs 2026", "u1", mockTeams.map { it.id }, 125.5, user = SelectionUser("Didier", null, null)),
            1, mockTeams
        ),
        RankedSelection(
            Selection("s2", "Total Football", "u2", mockTeams.map { it.id }, 118.0, user = SelectionUser("Johan", null, null)),
            2, mockTeams
        ),
        RankedSelection(
            Selection("s3", "Samba Boys", "u3", mockTeams.map { it.id }, 118.0, user = SelectionUser("Pelé", null, null)),
            2, mockTeams
        ),
        RankedSelection(
            Selection("s4", "The Underdogs", "u4", mockTeams.map { it.id }, 95.2, user = SelectionUser("Luka", null, null)),
            4, mockTeams
        ),
        RankedSelection(
            Selection("s5", "Goal Machines", "u5", mockTeams.map { it.id }, 82.0, user = SelectionUser("Kylian", null, null)),
            5, mockTeams
        ),
        RankedSelection(
            Selection("s6", "Defense First", "u6", mockTeams.map { it.id }, 45.0, user = SelectionUser("Virgil", null, null)),
            6, mockTeams
        )
    )

    MyApplicationTheme {
        LeaderboardContent(
            state = LeaderboardUiState.Success(ranked = rankedSelections, showMineOnly = false, currentUserId = "u1", matchesPlayed = 64, matchesUpcoming = 40),
            onToggleMine = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 800)
@Composable
fun LeaderboardContentWidePreview() {
    val mockTeams = listOf(
        Team("1", "France", "🇫🇷", 1, 0.0),
        Team("2", "Germany", "🇩🇪", 2, 0.0),
        Team("3", "USA", "🇺🇸", 3, 0.0),
        Team("4", "Japan", "🇯🇵", 4, 0.0),
        Team("5", "Morocco", "🇲🇦", 5, 0.0),
        Team("6", "Brazil", "🇧🇷", 6, 0.0),
        Team("7", "Spain", "🇪🇸", 7, 0.0),
        Team("8", "England", "🏴󠁧󠁢󠁥󠁮󠁧󠁿", 8, 0.0)
    )
    
    val rankedSelections = (1..15).map { i ->
        RankedSelection(
            Selection("s$i", "Selection #$i", "u$i", mockTeams.map { it.id }, 150.0 - i, user = SelectionUser("User $i", null, null)),
            i, mockTeams
        )
    }

    MyApplicationTheme {
        LeaderboardContent(
            state = LeaderboardUiState.Success(ranked = rankedSelections, showMineOnly = false, currentUserId = null, matchesPlayed = 64, matchesUpcoming = 40),
            onToggleMine = {}
        )
    }
}
