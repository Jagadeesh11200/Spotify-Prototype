package com.example

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun LibraryScreen(
    viewModel: SpotifyViewModel,
    modifier: Modifier = Modifier
) {
    val libraryFilter by viewModel.libraryFilter.collectAsState()
    val playlists by viewModel.playlists.collectAsState()
    val artists by viewModel.artists.collectAsState()
    val songs by viewModel.songs.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ThemeBackground)
            .testTag("library_screen_scroll"),
        contentPadding = PaddingValues(bottom = 140.dp)
    ) {
        // 1. Library Title Header
        item {
            LibraryHeaderRow(viewModel = viewModel)
        }

        // 2. Filter row (Playlists, Podcasts, Albums, Artists)
        item {
            val filters = listOf("Playlists", "Podcasts", "Albums", "Artists")
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(filters) { filter ->
                    val isSelected = filter == libraryFilter
                    FilterChipCustom(
                        text = filter,
                        isSelected = isSelected,
                        onClick = { viewModel.setLibraryFilter(filter) }
                    )
                }
            }
        }

        // 3. Sort & View layout control row
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { /* Sort toggle */ }
                ) {
                    Icon(
                        imageVector = Icons.Default.Sort,
                        contentDescription = "Sort direction",
                        tint = ThemeOnBackground,
                        modifier = Modifier.size(16.dp).alpha(0.8f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Recents",
                        color = ThemeOnBackground,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.alpha(0.8f)
                    )
                }

                IconButton(
                    onClick = { /* Toggle view style */ },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.GridView,
                        contentDescription = "Grid/List layout",
                        tint = ThemeOnBackground,
                        modifier = Modifier.size(18.dp).alpha(0.8f)
                    )
                }
            }
        }

        // 4. Dynamic items based on current library filter
        when (libraryFilter) {
            "Playlists" -> {
                // Liked Songs item
                item {
                    val likedSongsCount = songs.filter { it.isLiked }.size
                    LikedSongsRowItem(
                        count = likedSongsCount,
                        onClick = {
                            // Find liked songs playlist
                            val likedPlaylist = playlists.find { it.isLikedSongs }
                            if (likedPlaylist != null && likedPlaylist.songs.isNotEmpty()) {
                                viewModel.selectSong(likedPlaylist.songs.first())
                            }
                        }
                    )
                }

                // Normal custom playlists
                items(playlists) { playlist ->
                    if (!playlist.isLikedSongs) {
                        PlaylistRowItem(
                            playlist = playlist,
                            onClick = {
                                if (playlist.songs.isNotEmpty()) {
                                    viewModel.selectSong(playlist.songs.first())
                                }
                            }
                        )
                    }
                }
            }

            "Podcasts" -> {
                // New Episodes item
                item {
                    NewEpisodesRowItem(onClick = {})
                }
            }

            "Albums" -> {
                // Mock albums
                items(playlists.take(2)) { playlist ->
                    AlbumRowItem(playlist = playlist, onClick = {})
                }
            }

            "Artists" -> {
                // Artist avatars (Circular!)
                items(artists) { artist ->
                    ArtistRowItem(artist = artist, onClick = {})
                }
            }
        }
    }
}

@Composable
fun LibraryHeaderRow(
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
            text = "Your Library",
            color = ThemeOnBackground,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )

        IconButton(onClick = { viewModel.selectTab("Search") }) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search in library",
                tint = ThemeOnBackground
            )
        }

        IconButton(onClick = { viewModel.openCreatePlaylistDialog() }) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Create playlist",
                tint = ThemeOnBackground
            )
        }
    }
}

@Composable
fun LikedSongsRowItem(
    count: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("liked_songs_library_item"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LikedSongsArt(modifier = Modifier.size(64.dp))

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Liked Songs",
                color = ThemeOnBackground,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.PushPin,
                    contentDescription = "Pinned",
                    tint = ThemePrimary,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Playlist - $count songs",
                    color = SpotifyGray,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
fun NewEpisodesRowItem(
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        NewEpisodesArt(modifier = Modifier.size(64.dp))

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "New Episodes",
                color = ThemeOnBackground,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Updated today",
                color = SpotifyGray,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
fun PlaylistRowItem(
    playlist: Playlist,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("playlist_item_${playlist.id}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ProceduralArt(
            id = playlist.id,
            title = playlist.name,
            gradientColors = playlist.gradientColors,
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(8.dp))
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = playlist.name,
                color = ThemeOnBackground,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Playlist - ${playlist.creator}",
                color = SpotifyGray,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
fun AlbumRowItem(
    playlist: Playlist,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ProceduralArt(
            id = playlist.id,
            title = playlist.name,
            gradientColors = playlist.gradientColors,
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(8.dp))
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = playlist.name,
                color = ThemeOnBackground,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Album - ${playlist.creator}",
                color = SpotifyGray,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
fun ArtistRowItem(
    artist: Artist,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("artist_item_${artist.id}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ProceduralArt(
            id = artist.id,
            title = artist.name,
            isCircle = true,
            gradientColors = artist.gradientColors,
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = artist.name,
                color = ThemeOnBackground,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Artist",
                color = SpotifyGray,
                fontSize = 13.sp
            )
        }
    }
}
