package com.example.fifa2026poolpredictions.ui.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fifa2026poolpredictions.data.model.Match
import com.example.fifa2026poolpredictions.data.model.MatchResult
import com.example.fifa2026poolpredictions.theme.MyApplicationTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(viewModel: AdminViewModel, modifier: Modifier = Modifier) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    when (val s = state) {
        is AdminUiState.Loading -> Column(modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) { CircularProgressIndicator() }
        is AdminUiState.Error -> Column(modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            Text(s.message, color = MaterialTheme.colorScheme.error)
            Button(onClick = { viewModel.load() }, modifier = Modifier.padding(top = 8.dp)) { Text("Retry") }
        }
        is AdminUiState.Success -> {
            if (s.editingMatch != null) {
                MatchEditDialog(
                    editState = s.editingMatch,
                    onDismiss = { viewModel.dismissEdit() },
                    onGoalsChange = { t1, t2 -> viewModel.updateEditGoals(t1, t2) },
                    onWinnerChange = { viewModel.updateEditWinner(it) },
                    onSave = { viewModel.saveMatchResult() }
                )
            }
            PullToRefreshBox(isRefreshing = false, onRefresh = { viewModel.load() }, modifier = modifier) {
                AdminContent(
                    state = s,
                    onToggleGameState = { viewModel.toggleGameState() },
                    onEditMatch = { viewModel.startEditMatch(it) },
                    onSetBaseUrl = { viewModel.setApiBaseUrl(it) }
                )
            }
        }
    }
}

@Composable
fun AdminContent(
    state: AdminUiState.Success,
    onToggleGameState: () -> Unit,
    onEditMatch: (Match) -> Unit,
    onSetBaseUrl: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        item {
            Text("Admin", style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp))
        }
        
        // Settings section
        item {
            Text("Server Settings", style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
        }
        item {
            Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("API Base URL", fontWeight = FontWeight.SemiBold)
                    
                    val localUrl = "http://10.0.2.2:3000/"
                    val prodUrl = "https://fifa-jpc-2026-adbb917e1c6e.herokuapp.com/"
                    
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { onSetBaseUrl(localUrl) }) {
                        RadioButton(selected = state.apiBaseUrl == localUrl, onClick = { onSetBaseUrl(localUrl) })
                        Text("Local (10.0.2.2:3000)", style = MaterialTheme.typography.bodyMedium)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { onSetBaseUrl(prodUrl) }) {
                        RadioButton(selected = state.apiBaseUrl == prodUrl, onClick = { onSetBaseUrl(prodUrl) })
                        Text("Production (Heroku)", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        // Game state section
        item {
            Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Competition Started", fontWeight = FontWeight.SemiBold)
                        Text(
                            text = if (state.gameState?.state == "STARTED") 
                                "Competition is live. Selections are locked." 
                                else "In preparation. Users can still pick teams.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = state.gameState?.state == "STARTED",
                        onCheckedChange = { onToggleGameState() },
                        enabled = !state.togglingState
                    )
                }
            }
        }

        // Matches header
        item {
            Text("Matches", style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
        }
        items(state.matches) { match ->
            AdminMatchRow(match = match, onClick = { onEditMatch(match) },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp))
        }
        // Users header
        if (state.users.isNotEmpty()) {
            item {
                Text("Users", style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            }
            items(state.users) { user ->
                Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 2.dp)) {
                    Row(modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text(user.name ?: "Unknown", fontWeight = FontWeight.SemiBold)
                            Text(user.email ?: "", style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text(user.role, style = MaterialTheme.typography.labelSmall,
                            color = if (user.role == "ADMIN") MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminMatchRow(match: Match, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth().clickable { onClick() }) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Text("${match.team1.flagEmoji} ${match.team1.name}", modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium)
            Text(
                if (match.winner == MatchResult.UPCOMING) "vs"
                else "${match.team1Goals}–${match.team2Goals}",
                modifier = Modifier.padding(horizontal = 8.dp),
                fontWeight = FontWeight.Bold
            )
            Text("${match.team2.name} ${match.team2.flagEmoji}", modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun MatchEditDialog(
    editState: MatchEditState,
    onDismiss: () -> Unit,
    onGoalsChange: (String, String) -> Unit,
    onWinnerChange: (MatchResult) -> Unit,
    onSave: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("${editState.match.team1.name} vs ${editState.match.team2.name}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = editState.team1Goals,
                        onValueChange = { onGoalsChange(it, editState.team2Goals) },
                        label = { Text(editState.match.team1.flagEmoji) },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = editState.team2Goals,
                        onValueChange = { onGoalsChange(editState.team1Goals, it) },
                        label = { Text(editState.match.team2.flagEmoji) },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }
                Text("Result", style = MaterialTheme.typography.labelMedium)
                listOf(
                    MatchResult.TEAM1 to "${editState.match.team1.name} wins",
                    MatchResult.DRAW to "Draw",
                    MatchResult.TEAM2 to "${editState.match.team2.name} wins",
                    MatchResult.UPCOMING to "Upcoming"
                ).forEach { (result, label) ->
                    Row(verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onWinnerChange(result) }) {
                        RadioButton(selected = editState.winner == result,
                            onClick = { onWinnerChange(result) })
                        Text(label, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onSave, enabled = !editState.saving) {
                if (editState.saving) CircularProgressIndicator() else Text("Save")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Preview(showBackground = true)
@Composable
fun AdminContentPreview() {
    MyApplicationTheme {
        AdminContent(
            state = AdminUiState.Success(gameState = null, matches = emptyList(), users = emptyList(), apiBaseUrl = "http://10.0.2.2:3000/"),
            onToggleGameState = {},
            onEditMatch = {},
            onSetBaseUrl = {}
        )
    }
}
