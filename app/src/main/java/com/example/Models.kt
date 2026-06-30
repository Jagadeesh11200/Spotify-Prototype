package com.example

import androidx.compose.ui.graphics.Color

data class Song(
    val id: String,
    val title: String,
    val artist: String,
    val duration: Int, // in seconds
    val isLiked: Boolean = false,
    val imageUrl: String = "",
    val gradientColors: List<Color> = listOf(Color(0xFF282828), Color(0xFF121212))
)

data class Artist(
    val id: String,
    val name: String,
    val imageUrl: String = "",
    val gradientColors: List<Color> = listOf(Color(0xFF535353), Color(0xFF121212))
)

data class Playlist(
    val id: String,
    val name: String,
    val creator: String,
    val isLikedSongs: Boolean = false,
    val songs: List<Song> = emptyList(),
    val gradientColors: List<Color> = listOf(Color(0xFF450E4E), Color(0xFF121212))
)

data class BrowseCategory(
    val id: String,
    val title: String,
    val gradientColors: List<Color>,
    val imageUrl: String = ""
)

data class MoodCardData(
    val id: String,
    val title: String,
    val fragment: String,
    val accentColor: Color,
    val backgroundColors: List<Color>,
    val memoryAnchorSongId: String,
    val memoryAnchorText: String,
    val communityCount: Int,
    val styleType: String // "heavy", "ready", "electric"
)

