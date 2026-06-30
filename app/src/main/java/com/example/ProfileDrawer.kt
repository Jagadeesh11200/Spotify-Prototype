package com.example

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun ProfileDrawer(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Intercept back button to close drawer first
    BackHandler(enabled = isOpen) {
        onDismiss()
    }

    Box(modifier = modifier.fillMaxSize()) {
        // 1. Semi-transparent black background overlay
        if (isOpen) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f))
                    .clickable { onDismiss() }
                    .testTag("profile_scrim")
            )
        }

        // 2. Sliding Drawer Content
        AnimatedVisibility(
            visible = isOpen,
            enter = slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = tween(durationMillis = 300)
            ),
            exit = slideOutHorizontally(
                targetOffsetX = { -it },
                animationSpec = tween(durationMillis = 250)
            )
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.82f) // Width covers ~82% of screen width
                    .clickable(enabled = false) { /* Prevent clicks through */ }
                    .testTag("profile_drawer_content"),
                color = ThemeBackground // Modern solid dark theme background
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 24.dp)
                ) {
                    // Profile Header Row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        // Blue Avatar Circle
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1E88E5)), // Premium solid sky blue
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "J",
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = "Jagadeesh",
                                color = ThemeOnBackground,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "View profile",
                                color = SpotifyGray,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Divider separating profile info from menu options
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = ThemeOnBackground.copy(alpha = 0.12f)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Menu Options
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // 1. Add Account
                        ProfileDrawerItem(
                            title = "Add account",
                            icon = Icons.Default.Add,
                            onClick = { onDismiss() }
                        )

                        // 2. Your Premium (Custom Spotify brand logo with green badge)
                        ProfileDrawerPremiumItem(
                            title = "Your Premium",
                            badgeText = "Standard",
                            onClick = { onDismiss() }
                        )

                        // 3. Your Sound Capsule
                        ProfileDrawerItem(
                            title = "Your Sound Capsule",
                            icon = Icons.Default.Equalizer,
                            onClick = { onDismiss() }
                        )

                        // 4. Recents
                        ProfileDrawerItem(
                            title = "Recents",
                            icon = Icons.Default.History,
                            onClick = { onDismiss() }
                        )

                        // 5. Your Updates
                        ProfileDrawerItem(
                            title = "Your Updates",
                            icon = Icons.Default.Notifications,
                            onClick = { onDismiss() }
                        )

                        // 6. Settings and privacy
                        ProfileDrawerItem(
                            title = "Settings and privacy",
                            icon = Icons.Default.Settings,
                            onClick = { onDismiss() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileDrawerItem(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = ThemeOnBackground,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            color = ThemeOnBackground,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun ProfileDrawerPremiumItem(
    title: String,
    badgeText: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Beautiful, vector-drawn Spotify logo with curved soundwaves on Canvas
        Box(
            modifier = Modifier.size(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val r = size.minDimension / 2
                // Draw circle of Spotify Green
                drawCircle(color = Color(0xFF1DB954), radius = r)

                val strokeWidth = 1.8.dp.toPx()
                val extraStroke = 0.5.dp.toPx()
                
                // Top wave (larger arch)
                val path1 = Path().apply {
                    moveTo(6.5.dp.toPx(), 9.5.dp.toPx())
                    quadraticTo(12.dp.toPx(), 6.5.dp.toPx(), 17.5.dp.toPx(), 9.5.dp.toPx())
                }
                drawPath(path1, color = Color(0xFF121212), style = Stroke(width = strokeWidth + extraStroke))

                // Middle wave
                val path2 = Path().apply {
                    moveTo(8.dp.toPx(), 13.dp.toPx())
                    quadraticTo(12.dp.toPx(), 10.5.dp.toPx(), 16.dp.toPx(), 13.dp.toPx())
                }
                drawPath(path2, color = Color(0xFF121212), style = Stroke(width = strokeWidth))

                // Bottom wave (smaller arch)
                val path3 = Path().apply {
                    moveTo(9.5.dp.toPx(), 16.5.dp.toPx())
                    quadraticTo(12.dp.toPx(), 14.5.dp.toPx(), 14.5.dp.toPx(), 16.5.dp.toPx())
                }
                drawPath(path3, color = Color(0xFF121212), style = Stroke(width = strokeWidth - extraStroke))
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = title,
            color = ThemeOnBackground,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f)
        )

        // Rounded badge "Standard" in green
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(100.dp))
                .background(Color(0xFF1ED760)) // Spotify green accent
                .padding(horizontal = 12.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = badgeText,
                color = Color.Black,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
