package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SpotifyBlack
import com.example.ui.theme.SpotifyGreen
import com.example.ui.theme.SpotifyPureBlack

import com.example.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SessionStateManager.initialize(applicationContext)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                SpotifyApp()
            }
        }
    }
}

@Composable
fun SpotifyApp(viewModel: SpotifyViewModel = viewModel()) {
    val currentTab by viewModel.currentTab.collectAsState()
    val isPlayerExpanded by viewModel.isPlayerExpanded.collectAsState()
    val isProfileOpen by viewModel.isProfileOpen.collectAsState()
    val isMoodBoardActive by viewModel.isMoodBoardActive.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = ThemeBackground
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (isMoodBoardActive) {
                MoodBoardScreen(
                    viewModel = viewModel,
                    onNavigateToHome = { viewModel.exitMoodBoard() }
                )
            } else {
                Scaffold(
                    bottomBar = {
                        SpotifyBottomNavigation(
                            currentTab = currentTab,
                            onTabSelected = { viewModel.selectTab(it) }
                        )
                    },
                    modifier = Modifier.fillMaxSize(),
                    containerColor = ThemeBackground
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = innerPadding.calculateBottomPadding())
                    ) {
                        // Page Content Switcher with smooth animation
                        AnimatedContent(
                            targetState = currentTab,
                            transitionSpec = {
                                fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(220))
                            },
                            label = "screen_transition"
                        ) { tab ->
                            when (tab) {
                                "Home" -> HomeScreen(viewModel = viewModel)
                                "Search" -> SearchScreen(viewModel = viewModel)
                                "Your Library" -> LibraryScreen(viewModel = viewModel)
                                else -> HomeScreen(viewModel = viewModel)
                            }
                        }

                        // Floating MiniPlayer just above the Bottom Navigation
                        MiniPlayer(
                            viewModel = viewModel,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 8.dp)
                        )
                    }
                }
            }

            // Full Screen Player Slide-Up overlay
            AnimatedVisibility(
                visible = isPlayerExpanded,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300)),
                exit = slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300)),
                modifier = Modifier.fillMaxSize()
            ) {
                FullPlayerScreen(viewModel = viewModel)
            }

            // Bottom sheets and dialogs
            CreateBottomSheet(viewModel = viewModel)
            CreatePlaylistDialog(viewModel = viewModel)

            // Global Profile Section sliding drawer overlay
            ProfileDrawer(
                isOpen = isProfileOpen,
                onDismiss = { viewModel.closeProfile() }
            )
        }
    }
}

@Composable
fun SpotifyBottomNavigation(
    currentTab: String,
    onTabSelected: (String) -> Unit
) {
    // Custom bottom navigation bar aligned to Professional Polish theme
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ThemeBackground)
            .navigationBarsPadding()
    ) {
        // Subtle divider representing border-t border-[#49454F]
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(ThemeBorder)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(76.dp)
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            BottomNavItem(
                label = "Home",
                icon = if (currentTab == "Home") Icons.Filled.Home else Icons.Outlined.Home,
                isActive = currentTab == "Home",
                onClick = { onTabSelected("Home") }
            )

            BottomNavItem(
                label = "Search",
                icon = Icons.Default.Search,
                isActive = currentTab == "Search",
                onClick = { onTabSelected("Search") }
            )

            BottomNavItem(
                label = "Your Library",
                icon = if (currentTab == "Your Library") Icons.Filled.LibraryMusic else Icons.Outlined.LibraryMusic,
                isActive = currentTab == "Your Library",
                onClick = { onTabSelected("Your Library") }
            )

            BottomNavItem(
                label = "Create",
                icon = Icons.Default.Add,
                isActive = false,
                onClick = { onTabSelected("Create") }
            )
        }
    }
}

@Composable
fun BottomNavItem(
    label: String,
    icon: ImageVector,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .width(76.dp)
            .fillMaxHeight()
            .clickable(
                interactionSource = null,
                indication = null
            ) { onClick() }
            .testTag("nav_item_$label")
    ) {
        if (isActive) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(ThemeActivePill)
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = ThemeOnActivePill,
                    modifier = Modifier.size(20.dp)
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = ThemeInactiveText.copy(alpha = 0.6f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = label,
            color = ThemeOnBackground,
            fontSize = 11.sp,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
            modifier = Modifier.alpha(if (isActive) 1f else 0.6f)
        )
    }
}
