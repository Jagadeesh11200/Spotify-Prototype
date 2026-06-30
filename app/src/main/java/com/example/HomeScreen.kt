package com.example

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.border
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.remember
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.material.icons.filled.Search
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    viewModel: SpotifyViewModel,
    modifier: Modifier = Modifier
) {
    val songs by viewModel.songs.collectAsState()
    val homeFilter by viewModel.homeFilter.collectAsState()
    val podcasts by viewModel.podcasts.collectAsState()
    val followedArtists by viewModel.followedArtists.collectAsState()
    val newReleases by viewModel.newReleases.collectAsState()
    val topIndianTracks by viewModel.topIndianTracks.collectAsState()
    val acousticChill by viewModel.acousticChill.collectAsState()
    val globalTop50 by viewModel.globalTop50.collectAsState()
    val artists by viewModel.artists.collectAsState()
    val selectedMood by viewModel.selectedMood.collectAsState()
    val prefersTile by viewModel.prefersTile.collectAsState()

    if (prefersTile) {
        // Tile Mode HomeScreen
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFF0A0604))
                .statusBarsPadding()
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // At the very top render a row with padding 12px 16px showing the Spotify logo placeholder text in white on the left and a search icon placeholder on the right.
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Spotify",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.testTag("spotify_logo_placeholder")
                    )
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp).testTag("search_icon_placeholder")
                    )
                }

                // Below that a row with padding 8px 16px showing the text Good evening in fontSize 18 fontWeight 500 color white.
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Good evening",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.testTag("good_evening_text")
                    )
                }

                // Directly below the greeting render the Mood Board tile
                MoodBoardTile(viewModel = viewModel)

                // Below the tile add Recently played placeholders section
                RecentlyPlayedPlaceholders(modifier = Modifier.weight(1f))

                // At the very bottom in tiny text rgba(255,255,255,0.15) add the text Reset exit counter. Tapping this calls a prop function onReset.
                ResetExitCounterRow(
                    onReset = { viewModel.resetExitCounter() }
                )
            }
        }
    } else {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF2E2A36), // Cozy ambient dark purple background header
                            ThemeBackground
                        ),
                        startY = 0f,
                        endY = 800f
                    )
                )
                .testTag("home_screen_scroll"),
            contentPadding = PaddingValues(bottom = 140.dp) // extra padding to avoid overlapping mini player
        ) {
        // 1. Header Area with dynamic greeting and quick action buttons
        item {
            HomeHeaderRow(viewModel = viewModel, activeFilter = homeFilter)
        }

        // 1b. Resonance Sessions Promo (only when no mood is active)
        if (selectedMood == null) {
            item {
                ResonancePromoBanner(
                    onTuneClick = { viewModel.openMoodBoard() }
                )
            }
        }

        // Render sections based on active category filter
        when (homeFilter) {
            "All" -> {
                if (selectedMood != null) {
                    item {
                        val calibratedSongs by viewModel.rightNowRecommendations.collectAsState()
                        RightNowForYouSection(
                            viewModel = viewModel,
                            songs = calibratedSongs
                        )
                    }
                }



                item {
                    val gridItems = songs.filter { !it.id.startsWith("p_ep") }.take(8)
                    RecentPlayedGrid(gridItems = gridItems, onSongClick = { viewModel.selectSong(it) })
                }

                // Recommended list
                item {
                    HomeHorizontalSection(
                        title = "Recommended for today",
                        songs = songs.filter { !it.id.startsWith("p_ep") }.take(8),
                        onSongClick = { viewModel.selectSong(it) }
                    )
                }

                // Popular Albums
                item {
                    HomeHorizontalSection(
                        title = "Popular Albums",
                        songs = songs.filter { !it.id.startsWith("p_ep") }.drop(3).take(8),
                        onSongClick = { viewModel.selectSong(it) }
                    )
                }

                // Hot Hits
                item {
                    HomeHorizontalSection(
                        title = "Hot Hits",
                        songs = globalTop50,
                        onSongClick = { viewModel.selectSong(it) }
                    )
                }

                // Popular Artists
                item {
                    HomeArtistsSection(
                        title = "Popular Artists",
                        artists = artists,
                        onArtistClick = { }
                    )
                }
            }

            "Music" -> {
                if (selectedMood != null) {
                    item {
                        val calibratedSongs by viewModel.rightNowRecommendations.collectAsState()
                        RightNowForYouSection(
                            viewModel = viewModel,
                            songs = calibratedSongs
                        )
                    }
                }



                item {
                    val musicGrid = songs.filter { !it.id.startsWith("p_ep") }.take(8)
                    RecentPlayedGrid(gridItems = musicGrid, onSongClick = { viewModel.selectSong(it) })
                }

                // New Releases Row
                item {
                    HomeHorizontalSection(
                        title = "New Releases",
                        songs = newReleases,
                        onSongClick = { viewModel.selectSong(it) }
                    )
                }

                // Top Indian Tracks Row
                item {
                    HomeHorizontalSection(
                        title = "Top Indian Tracks",
                        songs = topIndianTracks,
                        onSongClick = { viewModel.selectSong(it) }
                    )
                }

                // Acoustic & Chill Row
                item {
                    HomeHorizontalSection(
                        title = "Acoustic & Chill",
                        songs = acousticChill,
                        onSongClick = { viewModel.selectSong(it) }
                    )
                }

                // Global Top 50 Row
                item {
                    HomeHorizontalSection(
                        title = "Global Top 50",
                        songs = globalTop50,
                        onSongClick = { viewModel.selectSong(it) }
                    )
                }

                // Popular Artists Row
                item {
                    HomeArtistsSection(
                        title = "Featured Artists",
                        artists = artists,
                        onArtistClick = { }
                    )
                }
            }

            "Podcasts" -> {
                // Podcasts-specific grid
                item {
                    val podcastGrid = podcasts.take(6)
                    RecentPlayedGrid(gridItems = podcastGrid, onSongClick = { viewModel.selectSong(it) })
                }

                // Recommended Shows
                item {
                    HomeHorizontalSection(
                        title = "Recommended Podcasts",
                        songs = podcasts,
                        subtitleLabel = "Podcast Episode",
                        onSongClick = { viewModel.selectSong(it) }
                    )
                }

                // Trending Shows
                item {
                    HomeHorizontalSection(
                        title = "Trending Shows",
                        songs = podcasts.asReversed(),
                        subtitleLabel = "Trending",
                        onSongClick = { viewModel.selectSong(it) }
                    )
                }

                // Tech & Business
                item {
                    val techBiz = podcasts.filter { 
                        it.artist.contains("Tech") || it.artist.contains("Business") || it.artist.contains("Startup") 
                    }
                    HomeHorizontalSection(
                        title = "Tech & Business Stories",
                        songs = techBiz,
                        subtitleLabel = "Knowledge",
                        onSongClick = { viewModel.selectSong(it) }
                    )
                }
            }

            "Following" -> {
                // Following Header Artists
                item {
                    HomeArtistsSection(
                        title = "Artists you follow",
                        artists = followedArtists,
                        onArtistClick = { }
                    )
                }

                // New releases from followed artists
                item {
                    HomeHorizontalSection(
                        title = "Latest Releases from followed artists",
                        songs = newReleases,
                        onSongClick = { viewModel.selectSong(it) }
                    )
                }

                // Popular with other followers
                item {
                    HomeHorizontalSection(
                        title = "Trending among other fans",
                        songs = globalTop50,
                        onSongClick = { viewModel.selectSong(it) }
                    )
                }

                // Live show previews
                item {
                    HomeHorizontalSection(
                        title = "Concerts & Showcases",
                        songs = topIndianTracks,
                        onSongClick = { viewModel.selectSong(it) }
                    )
                }
            }
        }
    }
}
}

@Composable
fun RecentPlayedGrid(
    gridItems: List<Song>,
    onSongClick: (Song) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        val chunkedList = gridItems.chunked(2)
        chunkedList.forEach { rowItems ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowItems.forEach { song ->
                    RecentPlayedCard(
                        song = song,
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .clickable { onSongClick(song) }
                    )
                }
                if (rowItems.size < 2) {
                    Box(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun HomeHorizontalSection(
    title: String,
    songs: List<Song>,
    subtitleLabel: String = "Single",
    onSongClick: (Song) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Text(
            text = title,
            color = ThemeOnBackground,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(songs) { song ->
                RecommendedAlbumCard(
                    song = song,
                    subtitleLabel = subtitleLabel,
                    onClick = { onSongClick(song) }
                )
            }
        }
    }
}

@Composable
fun HomeArtistsSection(
    title: String,
    artists: List<Artist>,
    onArtistClick: (Artist) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Text(
            text = title,
            color = ThemeOnBackground,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(artists) { artist ->
                ArtistCard(
                    artist = artist,
                    onClick = { onArtistClick(artist) }
                )
            }
        }
    }
}

@Composable
fun HomeHeaderRow(
    viewModel: SpotifyViewModel,
    activeFilter: String
) {
    val localTime = java.time.LocalTime.now()
    val greeting = when (localTime.hour) {
        in 5..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        else -> "Good evening"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp, bottom = 8.dp, start = 16.dp, end = 16.dp)
    ) {
        // Top row with greeting and actions
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // User Profile Avatar
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF381E72)) // Elegant purple avatar
                        .clickable { viewModel.openProfile() }
                        .testTag("user_avatar"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "J",
                        color = ThemePrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = greeting,
                    color = ThemeOnBackground,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp
                )
            }

            // Quick actions matching design HTML
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                // Notification bell with bg-[#2B2930]
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(ThemeSurface)
                        .clickable { /* Notifications */ }
                        .testTag("notifications_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notifications",
                        tint = ThemeOnBackground,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Settings gear with bg-[#2B2930]
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(ThemeSurface)
                        .clickable { /* Settings */ }
                        .testTag("settings_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = "Settings",
                        tint = ThemeOnBackground,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Collect active selected mood
        val selectedMood by viewModel.selectedMood.collectAsState()

        // Horizontal filter chips - DYNAMIC subsets logic matching user spec
        val filters = if (activeFilter == "Music" || activeFilter == "Following") {
            listOf("All", "Music", "Following", "Podcasts")
        } else {
            listOf("All", "Music", "Podcasts")
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // First show the dynamic active mood pill if set!
            if (selectedMood != null) {
                item {
                    Row(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(selectedMood!!.accentColor.copy(alpha = 0.15f))
                            .border(1.dp, selectedMood!!.accentColor.copy(alpha = 0.4f), CircleShape)
                            .clickable { viewModel.openMoodBoard() }
                            .padding(start = 12.dp, end = 6.dp, top = 6.dp, bottom = 6.dp)
                            .testTag("vibe_active_chip"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Vibe: ${selectedMood!!.title}",
                            color = selectedMood!!.accentColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        
                        // Small circular clear button
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.12f))
                                .clickable { viewModel.clearMood() }
                                .testTag("vibe_clear_chip_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear Vibe",
                                tint = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.size(10.dp)
                            )
                        }
                    }
                }
            }

            items(filters) { filterName ->
                val isSelected = filterName == activeFilter
                FilterChipCustom(
                    text = filterName,
                    isSelected = isSelected,
                    onClick = { viewModel.setHomeFilter(filterName) }
                )
            }
        }
    }
}

@Composable
fun FilterChipCustom(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(if (isSelected) ThemePrimary else ThemeSurface)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("filter_chip_$text"),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) ThemeOnPrimary else ThemeInactiveText,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun RecentPlayedCard(
    song: Song,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(ThemeSurface)
            .testTag("recent_card_${song.id}")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize()
        ) {
            ProceduralArt(
                id = song.id,
                title = song.title,
                isCircle = false,
                gradientColors = song.gradientColors,
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = song.title,
                color = ThemeOnBackground,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            )
        }
    }
}

@Composable
fun RecommendedAlbumCard(
    song: Song,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitleLabel: String = "Single"
) {
    Column(
        modifier = modifier
            .width(140.dp)
            .clickable { onClick() }
            .testTag("recommended_card_${song.id}")
    ) {
        ProceduralArt(
            id = song.id,
            title = song.title,
            isCircle = false,
            gradientColors = song.gradientColors,
            modifier = Modifier
                .size(140.dp)
                .clip(RoundedCornerShape(12.dp)) // Modern 12dp round corners for albums
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = subtitleLabel,
            color = ThemePrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = song.title,
            color = ThemeOnBackground,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = song.artist,
            color = SpotifyGray,
            fontSize = 12.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun ArtistCard(
    artist: Artist,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(110.dp)
            .clickable { onClick() }
            .testTag("artist_card_${artist.id}"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProceduralArt(
            id = artist.id,
            title = artist.name,
            isCircle = true,
            gradientColors = artist.gradientColors,
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = artist.name,
            color = ThemeOnBackground,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Artist",
            color = SpotifyGray,
            fontSize = 11.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ResonancePromoBanner(
    onTuneClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1D1826) // Deep mystical premium dark purple
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { onTuneClick() }
            .testTag("resonance_promo_banner")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF140E20),
                            Color(0xFF2E1A47)
                        )
                    )
                )
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1DB954)) // Spotify Green
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "RESONANCE SESSIONS",
                        color = Color(0xFF1DB954),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "How is your mood today?",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Tune your music recommendations to your vibe.",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
            }
            
            Button(
                onClick = onTuneClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1DB954), // Spotify Green
                    contentColor = Color.Black
                ),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.testTag("tune_vibe_button")
            ) {
                Text(
                    text = "Tune",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun MoodDiscoverSection(
    mood: MoodCardData,
    allSongs: List<Song>,
    onSongClick: (Song) -> Unit,
    onChangeMoodClick: () -> Unit,
    onClearMood: () -> Unit
) {
    // Curate songs based on the mood styleType
    val curatedSongs = when (mood.styleType) {
        "heavy" -> allSongs.filter { 
            it.id in listOf("ac1", "ac2", "ac3", "s10", "s9")
        }
        "ready" -> allSongs.filter { 
            it.id in listOf("nr1", "nr3", "tit3", "s3", "gt1")
        }
        "electric" -> allSongs.filter { 
            it.id in listOf("nr2", "tit2", "gt2", "gt3", "s2")
        }
        else -> allSongs.take(5)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    colors = mood.backgroundColors.take(2) + listOf(ThemeSurface)
                )
            )
            .padding(16.dp)
            .testTag("mood_discover_section")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "DISCOVER VIBE",
                color = mood.accentColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Change Vibe",
                    color = Color(0xFF1DB954), // Spotify Green
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .clickable { onChangeMoodClick() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("change_vibe_button")
                )

                Text(
                    text = "Clear Vibe",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .clickable { onClearMood() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("clear_vibe_button")
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = mood.fragment,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 26.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Custom soundtrack based on your recent activity & ${mood.communityCount} active listeners in this state",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 12.sp,
            lineHeight = 16.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Curated horizontal tracks
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(curatedSongs) { song ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Black.copy(alpha = 0.25f)
                    ),
                    modifier = Modifier
                        .width(130.dp)
                        .clickable { onSongClick(song) }
                        .testTag("mood_curated_song_${song.id}"),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        ProceduralArt(
                            id = song.id,
                            title = song.title,
                            gradientColors = song.gradientColors,
                            modifier = Modifier
                                .size(114.dp)
                                .clip(RoundedCornerShape(6.dp))
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = song.title,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = song.artist,
                            color = SpotifyGray,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RightNowForYouSection(
    viewModel: SpotifyViewModel,
    songs: List<Song>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .testTag("right_now_for_you_section")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Right Now For You",
                color = ThemeOnBackground,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.testTag("right_now_header_left")
            )
        }

        // Horizontally scrollable row with 8 recommendation songs
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(songs) { song ->
                RightNowRecommendationCard(
                    song = song,
                    onClick = { viewModel.selectSongFromCalibrated(song) }
                )
            }
        }
    }
}

@Composable
fun RightNowRecommendationCard(
    song: Song,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(140.dp)
            .clickable { onClick() }
            .testTag("right_now_recommendation_${song.id}")
    ) {
        ProceduralArt(
            id = song.id,
            title = song.title,
            isCircle = false,
            gradientColors = song.gradientColors,
            modifier = Modifier
                .size(140.dp)
                .clip(RoundedCornerShape(12.dp))
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = song.title,
            color = ThemeOnBackground,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.testTag("right_now_title_${song.id}")
        )

        Text(
            text = song.artist,
            color = SpotifyGray,
            fontSize = 12.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.testTag("right_now_artist_${song.id}")
        )
    }
}

@Composable
fun MoodBoardTile(
    viewModel: SpotifyViewModel,
    modifier: Modifier = Modifier
) {
    val moodCards by viewModel.moodCards.collectAsState()
    val centerCard = remember(moodCards) { moodCards.getOrNull(1) } ?: return

    val infiniteTransition = rememberInfiniteTransition(label = "tile_breath")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "tile_alpha"
    )

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "tile_scale"
    )

    val bloomAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bloom_alpha"
    )

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = centerCard.backgroundColors.firstOrNull() ?: Color(0xFF0A0604)),
        modifier = modifier
            .fillMaxWidth()
            .height(76.dp)
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            }
            .clickable { viewModel.openMoodBoard() }
            .testTag("mood_board_tile")
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Bloom circle positioned absolute-like at bottom-center
            val bloomColor = centerCard.backgroundColors.getOrNull(1) ?: centerCard.accentColor
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = 20.dp)
                    .size(width = 200.dp, height = 80.dp)
                    .clip(CircleShape)
                    .background(bloomColor.copy(alpha = bloomAlpha))
            )

            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = centerCard.fragment,
                    color = Color.White.copy(alpha = 0.88f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    lineHeight = 16.sp,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "Your Mood Board",
                    color = Color.White.copy(alpha = 0.4f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun RecentlyPlayedPlaceholders(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Recently played",
            color = Color.White.copy(alpha = 0.5f),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        
        repeat(4) { index ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 44px square rounded gray placeholder for album art
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF2E2E2E))
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column {
                    // Track name placeholder text
                    Box(
                        modifier = Modifier
                            .size(width = 120.dp, height = 12.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color(0xFF2E2E2E))
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    // Artist name placeholder text
                    Box(
                        modifier = Modifier
                            .size(width = 80.dp, height = 10.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color(0xFF1E1E1E))
                    )
                }
            }
        }
    }
}

@Composable
fun ResetExitCounterRow(
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Reset exit counter",
            color = Color.White.copy(alpha = 0.15f),
            fontSize = 11.sp,
            modifier = Modifier
                .clickable { onReset() }
                .testTag("reset_exit_counter_text")
                .padding(8.dp)
        )
    }
}
