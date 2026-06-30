package com.example

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SpotifyViewModel,
    modifier: Modifier = Modifier
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val songs by viewModel.songs.collectAsState()
    val categories by viewModel.categories.collectAsState()

    // Filter songs based on search query
    val filteredSongs = if (searchQuery.isBlank()) {
        emptyList()
    } else {
        songs.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
            it.artist.contains(searchQuery, ignoreCase = true)
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ThemeBackground)
            .testTag("search_screen_scroll"),
        contentPadding = PaddingValues(bottom = 140.dp)
    ) {
        // 1. Search Header Row
        item {
            SearchHeaderRow(viewModel = viewModel)
        }

        // 2. Search Text Input (Polished with dark elegant surface container instead of stark white)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                TextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = {
                        Text(
                            text = "What do you want to listen to?",
                            color = SpotifyGray,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search Icon",
                            tint = ThemeInactiveText
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear search",
                                    tint = ThemeInactiveText
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .testTag("search_text_input"),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = ThemeSurface,
                        unfocusedContainerColor = ThemeSurface,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = ThemeOnBackground,
                        unfocusedTextColor = ThemeOnBackground
                    ),
                    singleLine = true
                )
            }
        }

        // 3. Conditional Content: Search Results vs. Grid Browse Category
        if (searchQuery.isNotEmpty()) {
            if (filteredSongs.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 64.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.MusicOff,
                            contentDescription = null,
                            tint = SpotifyGray,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No results found for \"$searchQuery\"",
                            color = ThemeOnBackground,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Please check spelling or try a different search.",
                            color = SpotifyGray,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                item {
                    Text(
                        text = "Songs and Artists",
                        color = ThemeOnBackground,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
                    )
                }
                items(filteredSongs) { song ->
                    SearchResultRow(song = song, onClick = { viewModel.selectSong(song) })
                }
            }
        } else {
            // "Start browsing" Header
            item {
                Text(
                    text = "Start browsing",
                    color = ThemeOnBackground,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 12.dp)
                )
            }

            // Categories list chunked into rows
            val chunkedCategories = categories.chunked(2)
            items(chunkedCategories) { rowItems ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowItems.forEach { category ->
                        BrowseCategoryCard(
                            category = category,
                            modifier = Modifier
                                .weight(1f)
                                .height(90.dp)
                                .clickable {
                                    // Set category search filter
                                    viewModel.setSearchQuery(category.title)
                                }
                        )
                    }
                    if (rowItems.size < 2) {
                        Box(modifier = Modifier.weight(1f))
                    }
                }
            }

            // "Picked for you" Header
            item {
                Text(
                    text = "Picked for you",
                    color = ThemeOnBackground,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 12.dp)
                )
            }

            // Large Picked For You Card
            item {
                val featuredSong = songs.find { it.id == "s5" } ?: songs.firstOrNull()
                if (featuredSong != null) {
                    PickedForYouCard(
                        song = featuredSong,
                        onPlayClick = { viewModel.selectSong(featuredSong) },
                        onLikeClick = { viewModel.toggleLikeSong(featuredSong.id) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SearchHeaderRow(
    viewModel: SpotifyViewModel
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp, bottom = 8.dp, start = 16.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFF381E72))
                .clickable { viewModel.openProfile() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "J",
                color = ThemePrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = "Search",
            color = ThemeOnBackground,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )

        IconButton(onClick = { /* Search by code */ }) {
            Icon(
                imageVector = Icons.Default.PhotoCamera,
                contentDescription = "Search with Camera",
                tint = ThemeOnBackground
            )
        }
    }
}

@Composable
fun SearchResultRow(
    song: Song,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("search_result_${song.id}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ProceduralArt(
            id = song.id,
            title = song.title,
            gradientColors = song.gradientColors,
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(6.dp))
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = song.title,
                color = ThemeOnBackground,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Song - ${song.artist}",
                color = SpotifyGray,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = "Options",
            tint = SpotifyGray
        )
    }
}

@Composable
fun BrowseCategoryCard(
    category: BrowseCategory,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = category.gradientColors
                )
            )
            .padding(12.dp)
            .testTag("category_${category.id}")
    ) {
        Text(
            text = category.title,
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.TopStart)
        )

        Box(
            modifier = Modifier
                .size(52.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 12.dp, y = 12.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.White.copy(alpha = 0.2f))
        )
    }
}

@Composable
fun PickedForYouCard(
    song: Song,
    onPlayClick: () -> Unit,
    onLikeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(ThemeSurface)
            .padding(16.dp)
            .testTag("picked_for_you_card")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            ProceduralArt(
                id = song.id,
                title = song.title,
                gradientColors = song.gradientColors,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Album",
                    color = ThemePrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = song.title,
                    color = ThemeOnBackground,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = song.artist,
                    color = SpotifyGray,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onLikeClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (song.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Like Song",
                            tint = if (song.isLiked) ThemePrimary else ThemeOnBackground
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    IconButton(
                        onClick = onPlayClick,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(ThemePrimary),
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play",
                            tint = ThemeOnPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}
