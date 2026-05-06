package com.example.fifa2026poolpredictions.ui.selections

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fifa2026poolpredictions.data.model.GameState
import com.example.fifa2026poolpredictions.data.model.Selection
import com.example.fifa2026poolpredictions.data.model.SelectionUser
import com.example.fifa2026poolpredictions.data.model.Team
import com.example.fifa2026poolpredictions.theme.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectionsScreen(
    viewModel: SelectionsViewModel,
    onAddNew: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    when (val s = state) {
        is SelectionsUiState.Loading -> Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        is SelectionsUiState.Error -> Column(modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            Text(s.message, color = MaterialTheme.colorScheme.error)
            Button(onClick = { viewModel.load() }, modifier = Modifier.padding(top = 8.dp)) { Text("Retry") }
        }
        is SelectionsUiState.Success -> PullToRefreshBox(
            isRefreshing = false, onRefresh = { viewModel.load() }, modifier = modifier) {
            var selectedTab by remember { mutableIntStateOf(0) }
            Column(modifier = Modifier.fillMaxSize()) {
                PrimaryTabRow(selectedTabIndex = selectedTab) {
                    Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 },
                        text = { Text("My Picks") })
                    Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 },
                        text = { Text("Sets") })
                }
                when (selectedTab) {
                    0 -> SelectionsContent(state = s, onAddNew = onAddNew)
                    else -> SetsContent(teams = s.allTeams)
                }
            }
        }
    }
}

@Composable
fun SelectionsContent(
    state: SelectionsUiState.Success,
    onAddNew: () -> Unit,
    modifier: Modifier = Modifier
) {
    val deadline = 1781204400000L // June 11 2026 19:00 UTC (noon PDT)

    LazyColumn(modifier = modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        item {
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Text("My Picks", style = MaterialTheme.typography.headlineSmall,
                    color = AppGreenDark)
                if (!state.canAddMore && state.mySelections.size < 3 && state.gameState?.state != "PREPARING") {
                    Text("Submissions closed", style = MaterialTheme.typography.bodySmall, color = Gray400)
                }
            }
        }

        if (state.canAddMore) {
            item {
                CountdownTimer(deadline = deadline)
            }
            item {
                Button(
                    onClick = onAddNew,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                ) {
                    Text("+ New Selection", fontWeight = FontWeight.Bold)
                }
            }
        }

        if (state.mySelections.isEmpty()) {
            item {
                Text(
                    text = "You haven't made any picks yet.",
                    modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                    textAlign = TextAlign.Center,
                    color = Gray500
                )
            }
        } else {
            items(state.mySelections) { item ->
                MySelectionCard(item = item, modifier = Modifier.padding(vertical = 6.dp))
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun MySelectionCard(item: MySelection, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Gray200)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val medal = when (item.rank) {
                        1 -> "🥇"
                        2 -> "🥈"
                        3 -> "🥉"
                        else -> "#${item.rank}"
                    }
                    Text(
                        text = medal,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Gray400,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = item.selection.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Gray900
                    )
                }

                // Score Badge
                Box(
                    modifier = Modifier
                        .background(AppGreenLight, RoundedCornerShape(8.dp))
                        .border(1.dp, AppGreenBright, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "%.1f".format(item.selection.score),
                        style = ScoreBadgeTextStyle,
                        color = AppGreenDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Flags
            Text(
                text = item.teams.joinToString(" ") { it.flagEmoji },
                fontSize = 28.sp,
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Team Names Matrix
            val chunkedTeams = item.teams.chunked(4)
            chunkedTeams.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    row.forEach { team ->
                        Text(
                            text = team.name,
                            style = MaterialTheme.typography.bodySmall,
                            color = Gray500,
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                    // Pad out empty spots if row < 4 items
                    repeat(4 - row.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun SetsContent(teams: List<Team>, modifier: Modifier = Modifier) {
    val setGroups = teams
        .filter { it.set > 0 }
        .groupBy { it.set }
        .toSortedMap()
    val sortedSets = setGroups.entries.toList()

    if (sortedSets.isEmpty()) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "Team data unavailable.\nPull down to refresh.",
                textAlign = TextAlign.Center,
                color = Gray500,
                modifier = Modifier.padding(32.dp)
            )
        }
        return
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isWide = maxWidth > 600.dp
        if (isWide) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(sortedSets, key = { it.key }) { (setNum, setTeams) ->
                    SetCard(setNumber = setNum, teams = setTeams.sortedByDescending { it.score })
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(sortedSets, key = { it.key }) { (setNum, setTeams) ->
                    SetCard(setNumber = setNum, teams = setTeams.sortedByDescending { it.score })
                }
            }
        }
    }
}

@Composable
private fun SetCard(setNumber: Int, teams: List<Team>, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Gray200)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "Set $setNumber",
                style = MaterialTheme.typography.titleSmall,
                color = AppGreenDark,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            teams.forEach { team ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(team.flagEmoji, fontSize = 20.sp)
                    Text(
                        text = team.name,
                        fontSize = 13.sp,
                        modifier = Modifier.weight(1f),
                        color = Gray700
                    )
                    Box(
                        modifier = Modifier
                            .background(AppGreenSurface, RoundedCornerShape(4.dp))
                            .border(1.dp, AppGreenBorder, RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "%.1f".format(team.score),
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            color = AppGreen
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SelectionsContentPreview() {
    val dummyTeams = listOf(
        Team("1", "France", "🇫🇷", 1, 6.0),
        Team("2", "Brazil", "🇧🇷", 2, 4.0),
        Team("3", "Germany", "🇩🇪", 3, 3.0),
        Team("4", "Japan", "🇯🇵", 4, 3.0),
        Team("5", "Morocco", "🇲🇦", 5, 1.5),
        Team("6", "England", "🏴󠁧󠁢󠁥󠁮󠁧󠁿", 6, 1.0),
        Team("7", "USA", "🇺🇸", 7, 0.0),
        Team("8", "Portugal", "🇵🇹", 8, 0.0)
    )
    val picks = listOf(
        MySelection(Selection("s1", "My First Choice", "u1", dummyTeams.map { it.id }, 18.5, user = SelectionUser("John")), 1, dummyTeams),
        MySelection(Selection("s2", "Underdogs", "u1", dummyTeams.map { it.id }, 12.0, user = SelectionUser("John")), 15, dummyTeams),
        MySelection(Selection("s3", "Set Picks", "u1", dummyTeams.map { it.id }, 5.5, user = SelectionUser("John")), 42, dummyTeams)
    )
    MyApplicationTheme {
        SelectionsContent(
            state = SelectionsUiState.Success(
                mySelections = picks,
                gameState = GameState("singleton", "PREPARING"),
                canAddMore = false
            ),
            onAddNew = {}
        )
    }
}
