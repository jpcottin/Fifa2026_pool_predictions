package com.example.fifa2026poolpredictions

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.fifa2026poolpredictions.auth.AuthState
import com.example.fifa2026poolpredictions.ui.admin.AdminScreen
import com.example.fifa2026poolpredictions.ui.admin.AdminViewModel
import com.example.fifa2026poolpredictions.ui.leaderboard.LeaderboardScreen
import com.example.fifa2026poolpredictions.ui.leaderboard.LeaderboardViewModel
import com.example.fifa2026poolpredictions.ui.login.LoginScreen
import com.example.fifa2026poolpredictions.ui.login.LoginViewModel
import com.example.fifa2026poolpredictions.ui.matches.MatchesScreen
import com.example.fifa2026poolpredictions.ui.matches.MatchesViewModel
import com.example.fifa2026poolpredictions.ui.selections.SelectionsScreen
import com.example.fifa2026poolpredictions.ui.selections.SelectionsViewModel
import com.example.fifa2026poolpredictions.ui.selections.NewSelectionScreen
import com.example.fifa2026poolpredictions.ui.selections.NewSelectionViewModel
import com.example.fifa2026poolpredictions.ui.wcresults.WcResultsScreen
import com.example.fifa2026poolpredictions.ui.wcresults.WcResultsViewModel

data class NavDestination(
    val key: NavKey,
    val label: String,
    val icon: ImageVector
)

@Composable
fun MainNavigation() {
    val app = LocalContext.current.applicationContext as Fifa2026App
    val authState by app.authManager.authState.collectAsStateWithLifecycle(initialValue = AuthState.Loading)

    val backStack = rememberNavBackStack(Login)

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.LoggedIn -> {
                if (backStack.lastOrNull() is Login) {
                    backStack.removeLastOrNull()
                    backStack.add(Leaderboard)
                }
            }
            is AuthState.LoggedOut -> {
                if (backStack.lastOrNull() !is Login) {
                    backStack.clear()
                    backStack.add(Login)
                }
            }
            else -> Unit
        }
    }

    if (authState is AuthState.Loading) {
        return
    }

    val currentDest = backStack.lastOrNull()
    val isLoggedIn = authState is AuthState.LoggedIn
    val isAdmin = (authState as? AuthState.LoggedIn)?.isAdmin == true
    val userId = (authState as? AuthState.LoggedIn)?.userId

    if (!isLoggedIn || currentDest is Login) {
        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            entryProvider = entryProvider {
                entry<Login> {
                    val vm: LoginViewModel = viewModel(factory = viewModelFactory {
                        initializer { LoginViewModel(app.authManager) }
                    })
                    LoginScreen(viewModel = vm)
                }
                // Add these entries even if not logged in to prevent crash during backStack restoration/clearing
                entry<Leaderboard> { Box(Modifier) }
                entry<WcResults> { Box(Modifier) }
                entry<Matches> { Box(Modifier) }
                entry<MySelections> { Box(Modifier) }
                entry<NewSelection> { Box(Modifier) }
                entry<Admin> { Box(Modifier) }
            }
        )
        return
    }

    if (currentDest is NewSelection) {
        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            entryProvider = entryProvider {
                entry<NewSelection> {
                    val vm: NewSelectionViewModel = viewModel(factory = viewModelFactory {
                        initializer { NewSelectionViewModel(app.repository) }
                    })
                    NewSelectionScreen(
                        viewModel = vm,
                        onSuccess = {
                            backStack.removeLastOrNull()
                        },
                        onBack = { backStack.removeLastOrNull() }
                    )
                }
                // Placeholder entries to prevent crash when NewSelection is on top of them
                entry<Login> { Box(Modifier) }
                entry<Leaderboard> { Box(Modifier) }
                entry<WcResults> { Box(Modifier) }
                entry<Matches> { Box(Modifier) }
                entry<MySelections> { Box(Modifier) }
                entry<Admin> { Box(Modifier) }
            }
        )
        return
    }

    var selectedDest by remember { mutableStateOf<NavKey>(Leaderboard) }

    val destinations = buildList {
        add(NavDestination(Leaderboard, "Leaderboard", Icons.Default.EmojiEvents))
        add(NavDestination(WcResults, "WC Results", Icons.Default.TableChart))
        add(NavDestination(Matches, "Matches", Icons.Default.SportsSoccer))
        add(NavDestination(MySelections, "My Picks", Icons.AutoMirrored.Filled.List))
        if (isAdmin) add(NavDestination(Admin, "Admin", Icons.Default.AdminPanelSettings))
    }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            destinations.forEach { dest ->
                item(
                    icon = { Icon(dest.icon, contentDescription = dest.label) },
                    label = { Text(dest.label) },
                    selected = selectedDest == dest.key,
                    onClick = { selectedDest = dest.key }
                )
            }
        }
    ) {
        val repository = app.repository
        when (selectedDest) {
            Leaderboard -> {
                val vm: LeaderboardViewModel = viewModel(key = "leaderboard_$userId", factory = viewModelFactory {
                    initializer { LeaderboardViewModel(repository, userId) }
                })
                LeaderboardScreen(viewModel = vm)
            }
            WcResults -> {
                val vm: WcResultsViewModel = viewModel(key = "wcresults_$userId", factory = viewModelFactory {
                    initializer { WcResultsViewModel(repository) }
                })
                WcResultsScreen(viewModel = vm)
            }
            Matches -> {
                val vm: MatchesViewModel = viewModel(key = "matches_$userId", factory = viewModelFactory {
                    initializer { MatchesViewModel(repository) }
                })
                MatchesScreen(viewModel = vm)
            }
            MySelections -> {
                val vm: SelectionsViewModel = viewModel(key = "selections_$userId", factory = viewModelFactory {
                    initializer { SelectionsViewModel(repository, userId) }
                })
                SelectionsScreen(
                    viewModel = vm,
                    onAddNew = {
                        backStack.add(NewSelection)
                    }
                )
            }
            Admin -> {
                val vm: AdminViewModel = viewModel(key = "admin_$userId", factory = viewModelFactory {
                    initializer { AdminViewModel(app, repository) }
                })
                AdminScreen(viewModel = vm)
            }
        }
    }
}
