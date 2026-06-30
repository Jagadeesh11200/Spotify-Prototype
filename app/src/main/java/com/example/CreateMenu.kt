package com.example

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateBottomSheet(
    viewModel: SpotifyViewModel,
    modifier: Modifier = Modifier
) {
    val isSheetOpen by viewModel.isCreateSheetOpen.collectAsState()

    if (!isSheetOpen) return

    ModalBottomSheet(
        onDismissRequest = { viewModel.closeCreateSheet() },
        containerColor = ThemeSurface,
        contentColor = ThemeOnBackground,
        dragHandle = { Box(modifier = Modifier.padding(top = 8.dp)) },
        modifier = modifier.testTag("create_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Create",
                color = ThemeOnBackground,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // 1. Playlist
            CreateOptionRow(
                title = "Playlist",
                description = "Create a playlist with songs or episodes",
                icon = Icons.Default.MusicNote,
                iconTint = ThemeOnBackground,
                onClick = { viewModel.openCreatePlaylistDialog() }
            )

            // 2. Collaborative Playlist
            CreateOptionRow(
                title = "Collaborative playlist",
                description = "Create a playlist together with friends",
                icon = Icons.Default.People,
                iconTint = ThemeOnBackground,
                onClick = { viewModel.closeCreateSheet() }
            )

            // 3. Blend
            CreateOptionRow(
                title = "Blend",
                description = "Combine your friends' tastes into a playlist",
                icon = Icons.Default.GroupWork,
                iconTint = ThemePrimary,
                onClick = { viewModel.closeCreateSheet() }
            )

            // 4. Jam
            CreateOptionRow(
                title = "Jam",
                description = "Listen together from anywhere",
                icon = Icons.Default.VolumeUp,
                iconTint = ThemePrimary,
                onClick = { viewModel.closeCreateSheet() }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Custom Elegant Close Button in the Center
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = { viewModel.closeCreateSheet() },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(ThemePrimary)
                        .testTag("close_create_sheet_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = ThemeOnPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CreateOptionRow(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(ThemeBackground),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = ThemeOnBackground,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                color = SpotifyGray,
                fontSize = 13.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePlaylistDialog(
    viewModel: SpotifyViewModel
) {
    val isOpen by viewModel.isCreatePlaylistDialogOpen.collectAsState()
    var playlistName by remember { mutableStateOf("") }

    if (!isOpen) return

    // Reset name when dialog opens
    LaunchedEffect(isOpen) {
        playlistName = ""
    }

    Dialog(onDismissRequest = { viewModel.closeCreatePlaylistDialog() }) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = ThemeSurface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("create_playlist_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Give your playlist a name",
                    color = ThemeOnBackground,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                TextField(
                    value = playlistName,
                    onValueChange = { playlistName = it },
                    placeholder = {
                        Text(
                            text = "My Playlist #1",
                            color = SpotifyGray
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = ThemeBackground,
                        unfocusedContainerColor = ThemeBackground,
                        focusedIndicatorColor = ThemePrimary,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = ThemeOnBackground,
                        unfocusedTextColor = ThemeOnBackground
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("new_playlist_name_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = { viewModel.closeCreatePlaylistDialog() },
                        modifier = Modifier.testTag("cancel_create_playlist")
                    ) {
                        Text(text = "CANCEL", color = ThemeOnBackground)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { viewModel.createPlaylist(playlistName) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ThemePrimary,
                            contentColor = ThemeOnPrimary
                        ),
                        modifier = Modifier.testTag("confirm_create_playlist"),
                        enabled = playlistName.isNotBlank()
                    ) {
                        Text(text = "CREATE", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
