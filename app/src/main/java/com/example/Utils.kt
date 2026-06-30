package com.example

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Podcasts
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SpotifyGreen

@Composable
fun ProceduralArt(
    id: String,
    title: String,
    isCircle: Boolean = false,
    gradientColors: List<Color> = listOf(Color(0xFF282828), Color(0xFF121212)),
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(if (isCircle) CircleShape else androidx.compose.foundation.shape.RoundedCornerShape(4.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = gradientColors
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Draw some subtle procedural lines/circles on a Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            
            // Draw 2 ambient light glowing circles
            drawCircle(
                color = Color.White.copy(alpha = 0.05f),
                radius = width * 0.4f,
                center = androidx.compose.ui.geometry.Offset(width * 0.2f, height * 0.2f)
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.03f),
                radius = width * 0.5f,
                center = androidx.compose.ui.geometry.Offset(width * 0.8f, height * 0.8f)
            )
        }

        // Draw character overlay representing the item
        val initial = if (title.isNotEmpty()) title.substring(0, 1).uppercase() else "?"
        
        Text(
            text = initial,
            color = Color.White.copy(alpha = 0.25f),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif
        )
        
        // Music note or person icon on top center/bottom
        Icon(
            imageVector = if (isCircle) Icons.Default.Person else Icons.Default.MusicNote,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.15f),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun LikedSongsArt(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(4.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF450E4E),
                        Color(0xFF65156E),
                        Color(0xFF1E0320)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            drawCircle(
                color = Color.White.copy(alpha = 0.08f),
                radius = width * 0.6f,
                center = androidx.compose.ui.geometry.Offset(width * 0.5f, height * 0.5f)
            )
        }
        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = "Liked Songs",
            tint = Color.White,
            modifier = Modifier.size(28.dp)
        )
    }
}

@Composable
fun NewEpisodesArt(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(4.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF004D40),
                        Color(0xFF00796B),
                        Color(0xFF00241E)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Podcasts,
            contentDescription = "New Episodes",
            tint = SpotifyGreen,
            modifier = Modifier.size(28.dp)
        )
    }
}
