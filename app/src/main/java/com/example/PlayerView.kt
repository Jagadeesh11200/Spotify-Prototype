package com.example

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Devices
import androidx.compose.material.icons.outlined.Lyrics
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun MiniPlayer(
    viewModel: SpotifyViewModel,
    modifier: Modifier = Modifier
) {
    val currentSong by viewModel.currentSong.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val progress by viewModel.playbackProgress.collectAsState()

    if (currentSong == null) return

    val song = currentSong!!
    val progressPercent = if (song.duration > 0) progress.toFloat() / song.duration else 0f

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(68.dp)
            .padding(horizontal = 8.dp)
            .clip(RoundedCornerShape(12.dp)) // Theme rounded-xl (12.dp)
            .background(ThemeSurface)
            .clickable { viewModel.setPlayerExpanded(true) }
            .testTag("mini_player")
    ) {
        // Thin progress bar at the very bottom of mini player matching theme primary accent
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(ThemeOnBackground.copy(alpha = 0.1f))
                .align(Alignment.BottomCenter)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progressPercent)
                    .height(2.dp)
                    .background(ThemePrimary)
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp)
        ) {
            // Album art thumbnail
            ProceduralArt(
                id = song.id,
                title = song.title,
                gradientColors = song.gradientColors,
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(6.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = song.title,
                    color = ThemeOnBackground,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(1.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Devices,
                        contentDescription = null,
                        tint = ThemePrimary,
                        modifier = Modifier.size(11.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "ZLRWIN-JXV7FG3 - ${song.artist}",
                        color = ThemePrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Casting icon in Primary
            IconButton(
                onClick = { /* Device selection */ },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Devices,
                    contentDescription = "Casting",
                    tint = ThemePrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Like / Add button
            IconButton(
                onClick = { viewModel.toggleLikeSong(song.id) },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = if (song.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Add to Liked Songs",
                    tint = if (song.isLiked) ThemePrimary else ThemeOnBackground,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Play/Pause button
            IconButton(
                onClick = { viewModel.togglePlayback() },
                modifier = Modifier
                    .size(36.dp)
                    .testTag("mini_player_play_pause")
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = ThemeOnBackground,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun FullPlayerScreen(
    viewModel: SpotifyViewModel,
    modifier: Modifier = Modifier
) {
    val currentSong by viewModel.currentSong.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val progress by viewModel.playbackProgress.collectAsState()

    if (currentSong == null) return

    val song = currentSong!!

    val formatTime = { secs: Int ->
        val m = secs / 60
        val s = secs % 60
        String.format("%d:%02d", m, s)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        song.gradientColors.firstOrNull()?.copy(alpha = 0.5f) ?: Color(0xFF2E2E2E),
                        ThemeBackground
                    )
                )
            )
            .padding(horizontal = 24.dp)
            .testTag("full_player_screen")
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Header controls (Collapse button & Source info)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { viewModel.setPlayerExpanded(false) },
                    modifier = Modifier.size(40.dp).testTag("full_player_collapse")
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Collapse Player",
                        tint = ThemeOnBackground,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "PLAYING FROM PLAYLIST",
                        color = ThemeOnBackground.copy(alpha = 0.6f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Liked Songs",
                        color = ThemeOnBackground,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    onClick = { /* More options */ },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Options",
                        tint = ThemeOnBackground
                    )
                }
            }

            Spacer(modifier = Modifier.weight(0.1f))

            // 2. Large Album Art
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .padding(24.dp)
                    .clip(RoundedCornerShape(16.dp)) // Modern round corner 16.dp for album arts
                    .testTag("full_album_art"),
                contentAlignment = Alignment.Center
            ) {
                ProceduralArt(
                    id = song.id,
                    title = song.title,
                    gradientColors = song.gradientColors,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.weight(0.1f))

            // 3. Track Details (Title, Artist, and Favorite/Heart)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = song.title,
                        color = ThemeOnBackground,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = song.artist,
                        color = SpotifyGray,
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                IconButton(
                    onClick = { viewModel.toggleLikeSong(song.id) },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = if (song.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like Song",
                        tint = if (song.isLiked) ThemePrimary else ThemeOnBackground,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            // 4. Progress Slider / Seeker bar
            val progressPercent = if (song.duration > 0) progress.toFloat() / song.duration else 0f
            
            Slider(
                value = progress.toFloat(),
                onValueChange = { viewModel.seekTo(it.toInt()) },
                valueRange = 0f..(song.duration.toFloat()),
                colors = SliderDefaults.colors(
                    activeTrackColor = ThemePrimary,
                    inactiveTrackColor = ThemeOnBackground.copy(alpha = 0.2f),
                    thumbColor = ThemePrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("player_progress_slider")
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatTime(progress),
                    color = ThemeOnBackground.copy(alpha = 0.6f),
                    fontSize = 11.sp
                )
                Text(
                    text = formatTime(song.duration),
                    color = ThemeOnBackground.copy(alpha = 0.6f),
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 5. Main Controls row (Shuffle, Prev, Play/Pause, Next, Repeat)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { /* Shuffle */ }) {
                    Icon(
                        imageVector = Icons.Default.Shuffle,
                        contentDescription = "Shuffle",
                        tint = ThemePrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                IconButton(
                    onClick = { viewModel.playPreviousSong() },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "Previous Song",
                        tint = ThemeOnBackground,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Giant polished play button matching Professional Polish brand accent
                IconButton(
                    onClick = { viewModel.togglePlayback() },
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(ThemePrimary)
                        .testTag("full_player_play_pause")
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = ThemeOnPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                }

                IconButton(
                    onClick = { viewModel.playNextSong() },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next Song",
                        tint = ThemeOnBackground,
                        modifier = Modifier.size(36.dp)
                    )
                }

                IconButton(onClick = { /* Repeat */ }) {
                    Icon(
                        imageVector = Icons.Default.Repeat,
                        contentDescription = "Repeat",
                        tint = ThemeOnBackground,
                        modifier = Modifier.size(22.dp).alpha(0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 6. Bottom Utility Controls row (Casting device, share, sleep timer, lyrics peek)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { /* Device selector */ }) {
                    Icon(
                        imageVector = Icons.Outlined.Devices,
                        contentDescription = "Available Devices",
                        tint = ThemePrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(onClick = { /* Share */ }) {
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = "Share",
                        tint = ThemeOnBackground,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(onClick = { /* Timer */ }) {
                    Icon(
                        imageVector = Icons.Outlined.Timer,
                        contentDescription = "Sleep Timer",
                        tint = ThemeOnBackground,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(onClick = { /* Lyrics */ }) {
                    Icon(
                        imageVector = Icons.Outlined.Lyrics,
                        contentDescription = "Lyrics",
                        tint = ThemeOnBackground,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Lyrics Box Peek
            LyricsCard(song = song)
        }
    }
}

@Composable
fun LyricsCard(
    song: Song,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(ThemePrimary.copy(alpha = 0.12f))
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Lyrics",
                    color = ThemeOnBackground,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(ThemeOnBackground.copy(alpha = 0.1f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "MORE",
                        color = ThemeOnBackground,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "And I feel like playing this track forever...\nYeah, the rhythm goes down my spine...\nSingari, you are making me crazy,\nEvery beat of yours is a lifeline!",
                color = ThemeOnBackground,
                fontSize = 15.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
