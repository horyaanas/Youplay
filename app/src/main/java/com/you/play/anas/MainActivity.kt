package com.you.play.anas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PlayCircleOutline
import androidx.compose.material.icons.outlined.Subscriptions
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.you.play.anas.ui.AppScreen
import com.you.play.anas.ui.MiniPlayerState
import com.you.play.anas.ui.YouTubeViewModel
import com.you.play.anas.ui.components.CommentsBottomSheet
import com.you.play.anas.ui.components.QuickUrlPlayDialog
import com.you.play.anas.ui.components.SignInDialog
import com.you.play.anas.ui.components.YouTubeTopAppBar
import com.you.play.anas.ui.player.YouTubeWatchOverlay
import com.you.play.anas.ui.screens.HomeScreen
import com.you.play.anas.ui.screens.LibraryScreen
import com.you.play.anas.ui.screens.ShortsScreen
import com.you.play.anas.ui.screens.SubscriptionsScreen
import com.you.play.anas.ui.theme.YouPlayTheme
import com.you.play.anas.ui.theme.YouTubeRed

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            YouPlayTheme {
                YouPlayApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YouPlayApp(
    viewModel: YouTubeViewModel = viewModel()
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val isSearchActive by viewModel.isSearchActive.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val miniPlayerState by viewModel.miniPlayerState.collectAsState()
    val showCommentsSheet by viewModel.showCommentsSheet.collectAsState()
    val showQuickUrlDialog by viewModel.showQuickUrlDialog.collectAsState()
    val showSignInDialog by viewModel.showSignInDialog.collectAsState()
    val activeComments = viewModel.getCommentsForActiveVideo()
    val commentsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val userProfile by viewModel.userProfile.collectAsState()

    // Handle Back Press navigation if not on Home screen
    BackHandler(enabled = currentScreen != AppScreen.HOME && miniPlayerState != MiniPlayerState.EXPANDED) {
        viewModel.setScreen(AppScreen.HOME)
    }

    Scaffold(
        topBar = {
            if (currentScreen != AppScreen.SHORTS) {
                YouTubeTopAppBar(
                    isSearchActive = isSearchActive,
                    searchQuery = searchQuery,
                    onQueryChange = { viewModel.setSearchQuery(it) },
                    onToggleSearch = { viewModel.toggleSearchActive(it) },
                    onOpenQuickUrl = { viewModel.toggleQuickUrlDialog(true) },
                    onOpenAccount = { viewModel.toggleSignInDialog(true) },
                    userInitial = userProfile.name.take(1).uppercase()
                )
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.background,
                tonalElevation = 4.dp,
                modifier = Modifier.testTag("bottom_navigation_bar")
            ) {
                NavigationBarItem(
                    selected = currentScreen == AppScreen.HOME,
                    onClick = { viewModel.setScreen(AppScreen.HOME) },
                    icon = {
                        Icon(
                            imageVector = if (currentScreen == AppScreen.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                            contentDescription = "Home"
                        )
                    },
                    label = { Text("Home") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = YouTubeRed,
                        selectedTextColor = MaterialTheme.colorScheme.onBackground,
                        indicatorColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier.testTag("nav_home")
                )

                NavigationBarItem(
                    selected = currentScreen == AppScreen.SHORTS,
                    onClick = { viewModel.setScreen(AppScreen.SHORTS) },
                    icon = {
                        Icon(
                            imageVector = if (currentScreen == AppScreen.SHORTS) Icons.Filled.PlayCircleFilled else Icons.Outlined.PlayCircleOutline,
                            contentDescription = "Shorts"
                        )
                    },
                    label = { Text("Shorts") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = YouTubeRed,
                        selectedTextColor = MaterialTheme.colorScheme.onBackground,
                        indicatorColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier.testTag("nav_shorts")
                )

                NavigationBarItem(
                    selected = currentScreen == AppScreen.SUBSCRIPTIONS,
                    onClick = { viewModel.setScreen(AppScreen.SUBSCRIPTIONS) },
                    icon = {
                        Icon(
                            imageVector = if (currentScreen == AppScreen.SUBSCRIPTIONS) Icons.Filled.Subscriptions else Icons.Outlined.Subscriptions,
                            contentDescription = "Subscriptions"
                        )
                    },
                    label = { Text("Subscriptions") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = YouTubeRed,
                        selectedTextColor = MaterialTheme.colorScheme.onBackground,
                        indicatorColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier.testTag("nav_subscriptions")
                )

                NavigationBarItem(
                    selected = currentScreen == AppScreen.YOU,
                    onClick = { viewModel.setScreen(AppScreen.YOU) },
                    icon = {
                        Icon(
                            imageVector = if (currentScreen == AppScreen.YOU) Icons.Filled.VideoLibrary else Icons.Outlined.VideoLibrary,
                            contentDescription = "You"
                        )
                    },
                    label = { Text("You") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = YouTubeRed,
                        selectedTextColor = MaterialTheme.colorScheme.onBackground,
                        indicatorColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier.testTag("nav_library")
                )
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Main Screen Content based on selected tab
            when (currentScreen) {
                AppScreen.HOME -> HomeScreen(viewModel = viewModel)
                AppScreen.SHORTS -> ShortsScreen(viewModel = viewModel)
                AppScreen.SUBSCRIPTIONS -> SubscriptionsScreen(viewModel = viewModel)
                AppScreen.YOU -> LibraryScreen(viewModel = viewModel)
            }

            // Draggable In-App YouTube Miniplayer & Watch Overlay
            if (miniPlayerState != MiniPlayerState.HIDDEN) {
                YouTubeWatchOverlay(
                    viewModel = viewModel,
                    modifier = if (miniPlayerState == MiniPlayerState.MINIMIZED) {
                        Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 2.dp)
                    } else {
                        Modifier.fillMaxSize()
                    }
                )
            }
        }
    }

    // Comments Modal Bottom Sheet
    if (showCommentsSheet) {
        CommentsBottomSheet(
            comments = activeComments,
            sheetState = commentsSheetState,
            onDismiss = { viewModel.toggleCommentsSheet(false) },
            onAddComment = { viewModel.postComment(it) }
        )
    }

    // Quick Play YouTube URL Dialog
    if (showQuickUrlDialog) {
        QuickUrlPlayDialog(
            onDismiss = { viewModel.toggleQuickUrlDialog(false) },
            onPlayUrl = { url ->
                viewModel.playCustomUrl(url)
            }
        )
    }

    // Google Sign-In / Account Switcher Dialog
    if (showSignInDialog) {
        SignInDialog(
            onDismiss = { viewModel.toggleSignInDialog(false) },
            onSignIn = { name, email ->
                viewModel.signIn(name, email)
            }
        )
    }
}
