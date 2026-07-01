package com.example

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.ui.theme.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Album
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.border

fun splitAnchorText(text: String): Pair<String, String> {
    return when {
        text.contains("3× on") || text.contains("3 times on") -> {
            Pair("You came back to this", "3× on Wednesday night")
        }
        text.contains("last session") -> {
            Pair("You ended your", "last session here")
        }
        text.contains("kept finding") -> {
            Pair("This one kept", "finding you")
        }
        text.contains("at 2am") -> {
            Pair("You played this", "at 2am on Thursday")
        }
        text.contains("4× on") -> {
            Pair("You came back to this", "4× on Monday")
        }
        text.contains("last night") -> {
            Pair("This one kept", "finding you last night")
        }
        else -> {
            if (text.length > 20) {
                val words = text.split(" ")
                val mid = words.size / 2
                val first = words.take(mid).joinToString(" ")
                val second = words.drop(mid).joinToString(" ")
                Pair(first, second)
            } else {
                Pair(text, "")
            }
        }
    }
}

@Composable
fun MoodBoardScreen(
    viewModel: SpotifyViewModel,
    onNavigateToHome: () -> Unit
) {
    val moodCards by viewModel.moodCards.collectAsState()
    val swipeHintTransition = rememberInfiniteTransition(label = "swipe_hint")
    val leftHintOffset by swipeHintTransition.animateFloat(
        initialValue = 0f,
        targetValue = -14f,
        animationSpec = infiniteRepeatable(
            animation = tween(1050, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "left_swipe_hint_offset"
    )
    val rightHintOffset by swipeHintTransition.animateFloat(
        initialValue = 0f,
        targetValue = 14f,
        animationSpec = infiniteRepeatable(
            animation = tween(1050, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "right_swipe_hint_offset"
    )
    val hintAlpha by swipeHintTransition.animateFloat(
        initialValue = 0.28f,
        targetValue = 0.82f,
        animationSpec = infiniteRepeatable(
            animation = tween(1050, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "swipe_hint_alpha"
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF19171F))
            .testTag("mood_board_screen")
    ) {
        // Subtle ambient Spotify Green glowing background overlay for branding
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF2A2632),
                            Color(0xFF19171F),
                            Color.Transparent
                        )
                    )
                )
        )

        // Top action bar styled to feel like Spotify Premium interface
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.size(1.dp))

            Text(
                text = "Go to Home",
                color = Color.Black,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(Color(0xFF1ED760))
                    .clickable { viewModel.exitMoodBoard() }
                    .testTag("go_to_home_button")
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            )
        }

        // Subtitle centered below action bar
        Text(
            text = "How are you feeling today?",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(top = 76.dp)
                .padding(horizontal = 24.dp)
        )

        // Main fanned triptych area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.68f)
                .align(Alignment.Center)
                .padding(horizontal = 4.dp)
                .pointerInput(moodCards) {
                    var totalDrag = 0f
                    detectHorizontalDragGestures(
                        onDragStart = { totalDrag = 0f },
                        onHorizontalDrag = { _, dragAmount ->
                            totalDrag += dragAmount
                        },
                        onDragEnd = {
                            when {
                                totalDrag > 64f -> viewModel.selectMoodCard(2)
                                totalDrag < -64f -> viewModel.selectMoodCard(0)
                            }
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            val configuration = LocalConfiguration.current
            val screenWidth = configuration.screenWidthDp.dp
            
            // Center card occupies 68% of screen width, side cards occupy 56% to look balanced and fit perfectly
            val centerWidth = screenWidth * 0.68f
            val sideWidth = screenWidth * 0.56f
 
            // We map over each card. The animations are driven by the current index in the list.
            moodCards.forEachIndexed { listIndex, card ->
                // The list order dictates visual positions:
                // index 0 -> Left card
                // index 1 -> Center card
                // index 2 -> Right card
                
                // Animated parameters
                val targetScale = if (listIndex == 1) 1.0f else 0.85f
                val targetAlpha = if (listIndex == 1) 1.0f else 0.65f // Perfect transparency balance for secondary cards
                val targetZIndex = if (listIndex == 1) 2f else 1f
                val targetRotation = 0f
                
                // Animate properties smoothly over 220ms
                val animatedScale by animateFloatAsState(
                    targetValue = targetScale,
                    animationSpec = tween(250, easing = FastOutSlowInEasing),
                    label = "scale_${card.id}"
                )
                val animatedAlpha by animateFloatAsState(
                    targetValue = targetAlpha,
                    animationSpec = tween(250),
                    label = "alpha_${card.id}"
                )
                val animatedRotation by animateFloatAsState(
                    targetValue = targetRotation,
                    animationSpec = tween(250, easing = FastOutSlowInEasing),
                    label = "rotation_${card.id}"
                )
                
                // Calculate target offset so side cards are clearly visible on the triptych board
                val targetOffsetX = when (listIndex) {
                    0 -> -(screenWidth * 0.26f) // Clear left offset
                    2 -> (screenWidth * 0.26f)  // Clear right offset
                    else -> 0.dp                // Center
                }
                
                val animatedOffsetX by animateDpAsState(
                    targetValue = targetOffsetX,
                    animationSpec = tween(250, easing = FastOutSlowInEasing),
                    label = "offset_${card.id}"
                )

                Box(
                    modifier = Modifier
                        .width(if (listIndex == 1) centerWidth else sideWidth)
                        .fillMaxHeight(0.90f)
                        .offset(x = animatedOffsetX)
                        .scale(animatedScale)
                        .alpha(animatedAlpha)
                        .graphicsLayer {
                            rotationZ = animatedRotation
                        }
                        .zIndex(targetZIndex)
                        .clickable {
                            if (listIndex != 1) {
                                viewModel.selectMoodCard(listIndex)
                            } else {
                                viewModel.confirmMoodCardSelection(card)
                            }
                        }
                        .testTag("mood_card_${card.id}")
                ) {
                    MoodCard(
                        card = card,
                        isCenter = listIndex == 1,
                        viewModel = viewModel,
                        onSongClick = { song ->
                            viewModel.confirmMoodCardSelection(card, song)
                        }
                    )
                }
            }

            SwipeHint(
                direction = SwipeHintDirection.Left,
                accent = Color.White,
                alpha = hintAlpha,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = (18f + leftHintOffset).dp)
                    .testTag("swipe_hint_left")
            )

            SwipeHint(
                direction = SwipeHintDirection.Right,
                accent = Color.White,
                alpha = hintAlpha,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .offset(x = (-18f + rightHintOffset).dp)
                    .testTag("swipe_hint_right")
            )

        }

        // Bottom descriptive label
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Tap the card that feels like today to confirm",
                color = SpotifyGray.copy(alpha = 0.8f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }
    }
}

@Composable
fun MoodSongBoxes(
    card: MoodCardData,
    viewModel: SpotifyViewModel,
    onSongClick: (Song) -> Unit,
    modifier: Modifier = Modifier
) {
    val songs = remember(card) {
        viewModel.getCalibratedRecommendations(card).take(2)
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(3.dp),
        modifier = modifier
            .fillMaxWidth(0.48f)
            .testTag("mood_song_boxes_${card.styleType}")
    ) {
        songs.forEach { song ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.Black.copy(alpha = 0.2f))
                    .border(0.5.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(6.dp))
                    .clickable { onSongClick(song) }
                    .padding(horizontal = 5.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ProceduralArt(
                    id = song.id,
                    title = song.title,
                    isCircle = false,
                    gradientColors = song.gradientColors,
                    modifier = Modifier
                        .size(17.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .shadow(0.5.dp, RoundedCornerShape(4.dp))
                )

                Spacer(modifier = Modifier.width(5.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = song.title,
                        color = Color.White,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = song.artist,
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 6.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun MoodCard(
    card: MoodCardData,
    isCenter: Boolean,
    viewModel: SpotifyViewModel,
    onSongClick: (Song) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.verticalGradient(
                    colors = card.backgroundColors
                )
            )
    ) {
        // LAYER 1: Animated Emotional Illustration Character (Atmospheric Canvas)
        AtmosphericVisualBackground(styleType = card.styleType)

        MoodEnterCue(
            card = card,
            isCenter = isCenter,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-18).dp)
                .testTag("mood_enter_cue_${card.id}")
        )

        // Overlay card contents with top scrim for readability
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .offset(y = (-22).dp)
                    .padding(bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MoodWorldIcon(
                    card = card,
                    modifier = Modifier
                        .size(if (isCenter) 52.dp else 42.dp)
                        .testTag("mood_world_icon_${card.id}")
                )

                Spacer(modifier = Modifier.height(6.dp))

                // TOP HEADER: A list of 2 boxes that previews songs in the mood
                MoodSongBoxes(
                    card = card,
                    viewModel = viewModel,
                    onSongClick = onSongClick
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-6).dp)
                    .padding(bottom = if (isCenter) 24.dp else 18.dp)
            ) {
                // LAYER 2: Emotional Fragment (Warm typography, music-native) - Font sizes adjusted for responsive wrapping without truncation
                Text(
                    text = card.fragment,
                    color = Color.White,
                    fontSize = if (isCenter) 21.sp else 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = if (isCenter) 27.sp else 21.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("fragment_${card.id}"),
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Craft Moment: Thin Accent Line matching the card's specific temperature
                Box(
                    modifier = Modifier
                        .width(48.dp)
                        .height(3.dp)
                        .background(card.accentColor)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // LAYER 3: Emotional Memory Anchor Row (Omits specific song title, re-emphasizes mood) - Shows all text wrapped on couple of lines without truncation
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.Black.copy(alpha = 0.22f))
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Circular outline with a gorgeous glowing music note icon
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.08f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = card.accentColor.copy(alpha = 0.9f),
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = card.memoryAnchorText,
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 11.sp,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 15.sp,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("memory_anchor_${card.id}")
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // LAYER 4: Community Signal
                Text(
                    text = "${card.communityCount} people who hear music like you are here right now.",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 9.sp,
                    lineHeight = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun MoodEnterCue(
    card: MoodCardData,
    isCenter: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "tap_to_enter_${card.id}")
    val textAlpha by infiniteTransition.animateFloat(
        initialValue = if (isCenter) 0.35f else 0.18f,
        targetValue = if (isCenter) 1.0f else 0.44f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "tap_to_enter_alpha_${card.id}"
    )

    Box(
        modifier = modifier
            .width(if (isCenter) 142.dp else 112.dp)
            .height(if (isCenter) 44.dp else 36.dp)
            .alpha(textAlpha),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Tap to enter",
            color = card.accentColor,
            fontSize = if (isCenter) 16.sp else 13.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(horizontal = if (isCenter) 18.dp else 14.dp, vertical = if (isCenter) 9.dp else 7.dp)
        )
    }
}

enum class SwipeHintDirection {
    Left,
    Right
}

@Composable
fun SwipeHint(
    direction: SwipeHintDirection,
    accent: Color,
    alpha: Float,
    modifier: Modifier = Modifier
) {
    val arrow = if (direction == SwipeHintDirection.Left) Icons.Default.KeyboardArrowLeft else Icons.Default.KeyboardArrowRight
    val label = if (direction == SwipeHintDirection.Left) "Swipe" else "Swipe"
    Column(
        modifier = modifier
            .alpha(alpha)
            .clip(RoundedCornerShape(999.dp))
            .background(Color.Black.copy(alpha = 0.22f))
            .border(1.dp, accent.copy(alpha = 0.12f), RoundedCornerShape(999.dp))
            .padding(horizontal = 6.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = arrow,
            contentDescription = "$label ${direction.name.lowercase()}",
            tint = accent.copy(alpha = 0.86f),
            modifier = Modifier.size(26.dp)
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = "swipe",
            color = accent.copy(alpha = 0.68f),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun MoodWorldIcon(
    card: MoodCardData,
    modifier: Modifier = Modifier,
    emphasized: Boolean = false
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        if (emphasized) {
            Icon(
                painter = painterResource(id = moodIconRes(card.id)),
                contentDescription = null,
                tint = card.accentColor.copy(alpha = 0.55f),
                modifier = Modifier
                    .fillMaxSize(0.98f)
                    .offset(x = 0.65.dp)
            )
            Icon(
                painter = painterResource(id = moodIconRes(card.id)),
                contentDescription = null,
                tint = card.accentColor.copy(alpha = 0.45f),
                modifier = Modifier
                    .fillMaxSize(0.98f)
                    .offset(y = 0.65.dp)
            )
        }
        Icon(
            painter = painterResource(id = moodIconRes(card.id)),
            contentDescription = "${card.title} mood icon",
            tint = card.accentColor,
            modifier = Modifier.fillMaxSize(if (emphasized) 1.0f else 0.94f)
        )
    }
}

fun moodIconRes(cardId: String): Int = when (cardId) {
    "heavy" -> R.drawable.mood_01_amber_interior
    "ready" -> R.drawable.mood_02_coastal_morning
    "electric" -> R.drawable.mood_03_urban_night
    "joyful" -> R.drawable.mood_04_sunlit_meadow
    "melancholic" -> R.drawable.mood_05_deep_ocean
    "vast" -> R.drawable.mood_06_foggy_forest
    "world7" -> R.drawable.mood_07_storm_approaching
    "world8" -> R.drawable.mood_08_late_afternoon_gold
    "world9" -> R.drawable.mood_09_open_highway
    "world10" -> R.drawable.mood_10_still_water
    "world11" -> R.drawable.mood_11_electric_blue
    "world12" -> R.drawable.mood_12_warm_kitchen
    "world13" -> R.drawable.mood_13_late_winter
    "world14" -> R.drawable.mood_14_golden_dusk
    "world15" -> R.drawable.mood_15_breaking_open
    else -> R.drawable.mood_01_amber_interior
}

@Composable
fun AtmosphericVisualBackground(styleType: String) {
    // Set up continuous visual animations
    val infiniteTransition = rememberInfiniteTransition(label = "ambient_visual")
    
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    val movingOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "moving_offset"
    )

    val slowOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(18000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "slow_offset"
    )

    val fastOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "fast_offset"
    )

    when (styleType) {
        "heavy" -> {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height
                
                // Base background
                drawRect(color = Color(0xFF100804))

                // Primary bloom: dark amber-brown ellipse occupying the bottom 45 percent
                val primaryPulse = 0.96f + (pulseAlpha * 0.04f)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF3D1A08).copy(alpha = 0.65f), Color.Transparent),
                        center = Offset(width / 2, height),
                        radius = width * 0.9f * primaryPulse
                    )
                )

                // Secondary bloom: slightly more saturated amber at very bottom center
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF6B3010).copy(alpha = 0.85f), Color.Transparent),
                        center = Offset(width / 2, height),
                        radius = width * 0.45f * primaryPulse
                    )
                )

                // Rain-streaked glass lines (angled at roughly 10 degrees)
                val rainAngleRad = Math.toRadians(10.0)
                val lineCount = 5
                for (i in 0 until lineCount) {
                    val phaseOffset = (movingOffset + i * 20f) % 100f
                    val opacity = if (phaseOffset < 50f) phaseOffset / 50f else (100f - phaseOffset) / 50f
                    
                    val startX = width * (0.15f + i * 0.18f)
                    val startY = height * (0.1f + (i * 0.15f)) + (phaseOffset * 2f)
                    
                    val length = 40f
                    val endX = startX + (length * Math.sin(rainAngleRad)).toFloat()
                    val endY = startY + (length * Math.cos(rainAngleRad)).toFloat()

                    drawLine(
                        color = Color(0xFFFFB300).copy(alpha = 0.05f * opacity),
                        start = Offset(startX, startY),
                        end = Offset(endX, endY),
                        strokeWidth = 1.5f
                    )
                }

                // Faint window frame rectangle outline
                drawRect(
                    color = Color(0xFF2A1208).copy(alpha = 0.18f),
                    topLeft = Offset(width * 0.1f, height * 0.1f),
                    size = androidx.compose.ui.geometry.Size(width * 0.8f, height * 0.8f),
                    style = Stroke(width = 2f)
                )
            }
        }
        "ready" -> {
            val coastalPulse = 0.95f + (pulseAlpha * 0.05f)
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height

                // Base background
                drawRect(color = Color(0xFF04101C))

                // Primary bloom: deep ocean blue in center-right
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF0E2D4A).copy(alpha = 0.75f), Color.Transparent),
                        center = Offset(width * 0.75f, height * 0.5f + (slowOffset * 0.1f)),
                        radius = width * 0.8f
                    )
                )

                // Secondary bloom: smaller ellipse at upper right caught by morning light
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF1A4D6E).copy(alpha = 0.85f), Color.Transparent),
                        center = Offset(width * 0.85f, height * 0.25f),
                        radius = width * 0.4f * coastalPulse
                    )
                )

                // 3 to 4 horizontal marks suggesting distant water horizon
                for (i in 0 until 4) {
                    val lineY = height * (0.35f + i * 0.05f)
                    drawLine(
                        color = Color(0xFF0A2030).copy(alpha = 0.35f),
                        start = Offset(width * 0.1f, lineY),
                        end = Offset(width * 0.9f, lineY),
                        strokeWidth = 1f
                    )
                }

                // Faint arc in lower third suggesting curvature of shoreline
                drawArc(
                    color = Color(0xFF071826).copy(alpha = 0.4f),
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(-width * 0.2f, height * 0.6f),
                    size = androidx.compose.ui.geometry.Size(width * 1.4f, height * 0.6f),
                    style = Stroke(width = 2f)
                )
            }
        }
        "electric" -> {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height

                // Base background
                drawRect(color = Color(0xFF07060F))

                // Primary bloom: deep indigo lower center spreading up
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF1A1430).copy(alpha = 0.7f), Color.Transparent),
                        center = Offset(width / 2, height * 0.8f),
                        radius = width * 0.8f
                    )
                )

                // Secondary bloom: warmer violet
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF2A1E40).copy(alpha = 0.65f), Color.Transparent),
                        center = Offset(width / 2, height * 0.5f),
                        radius = width * 0.6f
                    )
                )

                // Faint warm amber marks suggesting neon reflections
                val reflectionPulse = 0.8f + (pulseAlpha * 0.2f)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF5C3010).copy(alpha = 0.18f * reflectionPulse), Color.Transparent),
                        center = Offset(width * 0.3f, height * 0.85f),
                        radius = width * 0.35f
                    )
                )

                // 5 to 7 small horizontal windows at irregular intervals, fading and brightening
                val windowWidth = 10f
                val windowHeight = 5f
                val positions = listOf(
                    Offset(width * 0.2f, height * 0.2f),
                    Offset(width * 0.6f, height * 0.15f),
                    Offset(width * 0.45f, height * 0.3f),
                    Offset(width * 0.75f, height * 0.35f),
                    Offset(width * 0.35f, height * 0.45f),
                    Offset(width * 0.55f, height * 0.5f)
                )

                positions.forEachIndexed { i, offset ->
                    val phase = (fastOffset + i * 30f) % 100f
                    val windowOpacity = if (phase < 50f) phase / 50f else (100f - phase) / 50f
                    val isCool = i % 2 == 0
                    val windowColor = if (isCool) Color(0xFF1A2A3A) else Color(0xFF2A1E40)
                    
                    drawRect(
                        color = windowColor.copy(alpha = 0.25f + (0.5f * windowOpacity)),
                        topLeft = offset,
                        size = androidx.compose.ui.geometry.Size(windowWidth, windowHeight)
                    )
                }

                // 2 to 3 larger brighter windows
                val largePositions = listOf(
                    Offset(width * 0.25f, height * 0.35f),
                    Offset(width * 0.65f, height * 0.45f)
                )
                largePositions.forEachIndexed { i, offset ->
                    val phase = (movingOffset + i * 45f) % 100f
                    val windowOpacity = if (phase < 50f) phase / 50f else (100f - phase) / 50f
                    drawRect(
                        color = Color(0xFF3A2840).copy(alpha = 0.3f + (0.45f * windowOpacity)),
                        topLeft = offset,
                        size = androidx.compose.ui.geometry.Size(windowWidth * 1.5f, windowHeight * 1.5f)
                    )
                }
            }
        }
        "joyful" -> {
            val grassSway by infiniteTransition.animateFloat(
                initialValue = -5f,
                targetValue = 5f,
                animationSpec = infiniteRepeatable(
                    animation = tween(4000, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "grass_sway"
            )

            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height

                // Base background
                drawRect(color = Color(0xFF0C0A04))

                // Primary bloom: dark golden-olive spread across lower half
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF3A2C08).copy(alpha = 0.6f), Color.Transparent),
                        center = Offset(width / 2, height * 0.75f),
                        radius = width * 0.85f
                    )
                )

                // Secondary bloom: golden center hit
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF5A4010).copy(alpha = 0.8f), Color.Transparent),
                        center = Offset(width / 2, height * 0.65f),
                        radius = width * 0.45f * (0.95f + pulseAlpha * 0.05f)
                    )
                )

                // Sky area dark warm blue top
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF141E28).copy(alpha = 0.45f), Color.Transparent),
                        startY = 0f,
                        endY = height * 0.35f
                    )
                )

                // Thin vertical organic marks in lower half (tall grass swaying)
                val grassCount = 10
                for (i in 0 until grassCount) {
                    val progress = i.toFloat() / grassCount
                    val baseX = width * (0.1f + progress * 0.8f)
                    val grassHeight = height * (0.25f + (i % 3) * 0.08f)
                    
                    val swayX = grassSway * (1f + (i % 2) * 0.5f)
                    
                    drawLine(
                        color = Color(0xFF4A3810).copy(alpha = 0.25f),
                        start = Offset(baseX, height),
                        end = Offset(baseX + swayX, height - grassHeight),
                        strokeWidth = 1.5f + (i % 2).toFloat()
                    )
                }
            }
        }
        "melancholic" -> {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height

                // Base background
                drawRect(color = Color(0xFF060A08))

                // Primary bloom: grey-green diffused mist
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF0E1A14).copy(alpha = 0.85f), Color.Transparent),
                        center = Offset(width / 2, height / 2),
                        radius = width * 0.85f * (0.94f + pulseAlpha * 0.06f)
                    )
                )

                // Secondary bloom: filtered light from top
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF162418).copy(alpha = 0.55f), Color.Transparent),
                        center = Offset(width / 2, 0f),
                        radius = width * 0.65f
                    )
                )

                // Thin vertical tree trunks, geometric forms completely still
                val trunkCount = 5
                val trunkWidths = listOf(14f, 8f, 18f, 10f, 6f)
                val trunkHeights = listOf(0.75f, 0.65f, 0.82f, 0.7f, 0.68f)
                val trunkPositionsX = listOf(0.2f, 0.38f, 0.55f, 0.72f, 0.85f)

                for (i in 0 until trunkCount) {
                    val rectWidth = trunkWidths[i]
                    val rectHeight = height * trunkHeights[i]
                    val rectLeft = width * trunkPositionsX[i] - (rectWidth / 2)
                    
                    drawRect(
                        color = Color(0xFF040806).copy(alpha = 0.65f),
                        topLeft = Offset(rectLeft, height - rectHeight),
                        size = androidx.compose.ui.geometry.Size(rectWidth, rectHeight)
                    )
                }
            }
        }
        "vast" -> {
            val sparklesPulse by infiniteTransition.animateFloat(
                initialValue = 0.2f,
                targetValue = 0.95f,
                animationSpec = infiniteRepeatable(
                    animation = tween(3000, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "sparkles"
            )

            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height

                // Base deep blue gradient
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF0A2E46).copy(alpha = 0.4f),
                            Color(0xFF031625).copy(alpha = 0.6f)
                        ),
                        start = Offset(width / 2, 0f),
                        end = Offset(width / 2, height)
                    )
                )

                // Large soft bioluminescent deep swell glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF00E5FF).copy(alpha = 0.18f * pulseAlpha),
                            Color(0xFF006064).copy(alpha = 0.06f * pulseAlpha),
                            Color.Transparent
                        ),
                        center = Offset(width / 2, height * 0.8f),
                        radius = width * 0.9f
                    )
                )

                // Bioluminescent floating sparkling dots pulsing
                val dotCount = 8
                for (i in 0 until dotCount) {
                    val dotX = width * (0.15f + (i * 0.11f))
                    val dotY = height * (0.25f + ((i * 7) % 5) * 0.12f) + (movingOffset * 0.3f)
                    val alphaScale = ((i + 1) * 0.15f).coerceIn(0.1f, 1.0f)

                    drawCircle(
                        color = Color(0xFF00E5FF).copy(alpha = 0.55f * sparklesPulse * alphaScale),
                        radius = 3f + (i % 2) * 2f,
                        center = Offset(dotX, dotY % height)
                    )
                    
                    // Tiny outer halo
                    drawCircle(
                        color = Color(0xFF00E5FF).copy(alpha = 0.15f * sparklesPulse * alphaScale),
                        radius = 8f + (i % 2) * 4f,
                        center = Offset(dotX, dotY % height)
                    )
                }
            }
        }
        "storm" -> {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height

                // Base background
                drawRect(color = Color(0xFF060508))

                // Primary bloom: deep storm-grey purple upper portion
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF1A1520).copy(alpha = 0.75f), Color.Transparent),
                        center = Offset(width / 2, height * 0.3f),
                        radius = width * 0.85f
                    )
                )

                // Secondary bloom: strange ochre pre-storm light at horizon
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF241C10).copy(alpha = 0.6f * (0.8f + pulseAlpha * 0.2f)), Color.Transparent),
                        center = Offset(width / 2, height * 0.65f),
                        radius = width * 0.55f
                    )
                )

                // Cloud masses: overlapping irregular dark shapes shifting slowly
                val cloudShift = Math.sin(Math.toRadians(slowOffset.toDouble() * 3.6)).toFloat() * 10f
                val cloudPositions = listOf(
                    Offset(width * 0.2f + cloudShift, height * 0.2f),
                    Offset(width * 0.5f - cloudShift, height * 0.15f),
                    Offset(width * 0.8f + cloudShift * 0.5f, height * 0.25f)
                )
                val cloudRadii = listOf(width * 0.45f, width * 0.55f, width * 0.4f)
                cloudPositions.forEachIndexed { idx, offset ->
                    drawCircle(
                        color = Color(0xFF101018).copy(alpha = 0.75f),
                        radius = cloudRadii[idx],
                        center = offset
                    )
                }

                // Flat dark land line
                drawLine(
                    color = Color(0xFF3A2A10).copy(alpha = 0.35f),
                    start = Offset(0f, height * 0.65f),
                    end = Offset(width, height * 0.65f),
                    strokeWidth = 2f
                )
            }
        }
        "gold" -> {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height

                // Base background
                drawRect(color = Color(0xFF0C0804))

                // Primary bloom: rich warm amber-gold
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF4A2C08).copy(alpha = 0.8f), Color.Transparent),
                        center = Offset(width * 0.75f, height * 0.55f),
                        radius = width * 0.75f * (0.93f + pulseAlpha * 0.07f)
                    )
                )

                // Secondary bloom: smaller intense ellipse core
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF6E4010).copy(alpha = 0.85f), Color.Transparent),
                        center = Offset(width * 0.7f, height * 0.52f),
                        radius = width * 0.35f
                    )
                )

                // Diagonal light beam edges cutting across floor at 22 degrees
                val startX = width * 0.15f
                val startY = height * 0.7f
                val endX = width * 0.95f
                val endY = height * 0.4f
                drawLine(
                    color = Color(0xFF7A5020).copy(alpha = 0.18f),
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = 1.5f
                )

                // Tiny dust particles floating slowly
                val moteCount = 5
                for (i in 0 until moteCount) {
                    val progress = (slowOffset + i * 20f) % 100f
                    val opacity = if (progress < 50f) progress / 50f else (100f - progress) / 50f
                    
                    val x = width * (0.35f + i * 0.12f) + (progress * 0.3f)
                    val y = height * (0.45f + (i % 2) * 0.12f) - (progress * 0.2f)
                    
                    drawCircle(
                        color = Color(0xFFE8C060).copy(alpha = 0.12f * opacity),
                        radius = 2.5f,
                        center = Offset(x, y)
                    )
                }
            }
        }
        "highway" -> {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height

                // Base background
                drawRect(color = Color(0xFF0C0804))

                // Primary bloom: deep orange burnt sky
                val skyShiftX = Math.sin(Math.toRadians(slowOffset.toDouble() * 3.6)).toFloat() * 15f
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF5A2A08).copy(alpha = 0.85f), Color.Transparent),
                        center = Offset(width / 2 + skyShiftX, height * 0.3f),
                        radius = width * 0.85f
                    )
                )

                // Secondary bloom: highway horizon
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF3A1A04).copy(alpha = 0.7f), Color.Transparent),
                        center = Offset(width / 2, height * 0.55f),
                        radius = width * 0.55f
                    )
                )

                // Roads: converging lines towards vanishing point
                val vpX = width / 2
                val vpY = height * 0.55f

                // Left road edge
                drawLine(
                    color = Color(0xFFE86A1A).copy(alpha = 0.25f),
                    start = Offset(width * 0.05f, height),
                    end = Offset(vpX, vpY),
                    strokeWidth = 2f
                )

                // Right road edge
                drawLine(
                    color = Color(0xFFE86A1A).copy(alpha = 0.25f),
                    start = Offset(width * 0.95f, height),
                    end = Offset(vpX, vpY),
                    strokeWidth = 2f
                )

                // Dotted highway center line
                val centerDashes = 5
                for (i in 0 until centerDashes) {
                    val progress = (i.toFloat() + (fastOffset / 100f)) / centerDashes
                    val dashY = vpY + (height - vpY) * progress
                    val dashX = vpX
                    val dashHeight = 15f * progress

                    if (progress in 0f..1f) {
                        drawLine(
                            color = Color(0xFFE86A1A).copy(alpha = 0.35f * progress),
                            start = Offset(dashX, dashY),
                            end = Offset(dashX, dashY + dashHeight),
                            strokeWidth = 1.5f
                        )
                    }
                }
            }
        }
        "still" -> {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height

                // Base background
                drawRect(color = Color(0xFF04080C))

                // Primary bloom: deep pre-dawn blue
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF0C1E2C).copy(alpha = 0.8f), Color.Transparent),
                        center = Offset(width / 2, height / 2),
                        radius = width * 0.75f
                    )
                )

                // Mirror horizontal horizon line
                drawLine(
                    color = Color(0xFF1A3040).copy(alpha = 0.45f),
                    start = Offset(0f, height / 2),
                    end = Offset(width, height / 2),
                    strokeWidth = 1f
                )

                // Soft mist ellipses expanding horizontally
                val mistWidthScale = 1f + (slowOffset / 100f) * 0.04f
                val mistPositionsY = listOf(height * 0.48f, height * 0.52f)
                mistPositionsY.forEachIndexed { i, y ->
                    drawOval(
                        color = Color(0xFF0A1820).copy(alpha = 0.12f),
                        topLeft = Offset(width * 0.2f, y),
                        size = androidx.compose.ui.geometry.Size(width * 0.6f * mistWidthScale, 8f)
                    )
                }
            }
        }
        "electric_blue" -> {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height

                // Base background
                drawRect(color = Color(0xFF030410))

                // Primary bloom: deep electric blue upper portion
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF0A0E40).copy(alpha = 0.85f), Color.Transparent),
                        center = Offset(width / 2, height * 0.25f),
                        radius = width * 0.85f
                    )
                )

                // Secondary bloom: stage beams center intensity
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF1A1A60).copy(alpha = 0.75f), Color.Transparent),
                        center = Offset(width / 2, height * 0.35f),
                        radius = width * 0.5f * (0.9f + pulseAlpha * 0.1f)
                    )
                )

                // Radiating light beam lines shifting angles
                val sweepX = Math.sin(Math.toRadians(movingOffset.toDouble() * 3.6)).toFloat() * 40f
                val beamEndpoints = listOf(
                    Offset(width * 0.2f + sweepX, height),
                    Offset(width * 0.4f + sweepX * 0.5f, height),
                    Offset(width * 0.6f - sweepX * 0.5f, height),
                    Offset(width * 0.8f - sweepX, height)
                )
                beamEndpoints.forEach { endpoint ->
                    drawLine(
                        color = Color(0xFF1E20A0).copy(alpha = 0.28f),
                        start = Offset(width / 2, height * 0.1f),
                        end = endpoint,
                        strokeWidth = 3f
                    )
                }

                // Tiny sparkling crowd screens in lower third pulsing
                val dotPositions = listOf(
                    Offset(width * 0.15f, height * 0.85f),
                    Offset(width * 0.35f, height * 0.92f),
                    Offset(width * 0.55f, height * 0.82f),
                    Offset(width * 0.75f, height * 0.88f),
                    Offset(width * 0.45f, height * 0.95f),
                    Offset(width * 0.85f, height * 0.9f)
                )
                dotPositions.forEachIndexed { i, offset ->
                    val dotPhase = (fastOffset * 2f + i * 40f) % 100f
                    val dotOpacity = if (dotPhase < 50f) dotPhase / 50f else (100f - dotPhase) / 50f
                    drawCircle(
                        color = Color(0xFF8080FF).copy(alpha = 0.1f + 0.4f * dotOpacity),
                        radius = 2.5f,
                        center = offset
                    )
                }
            }
        }
        "kitchen" -> {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height

                // Base background
                drawRect(color = Color(0xFF0A0604))

                // Primary bloom: table center lamp glow
                val breathingScale = 0.96f + (pulseAlpha * 0.04f)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF3A1C08).copy(alpha = 0.85f), Color.Transparent),
                        center = Offset(width / 2, height * 0.65f),
                        radius = width * 0.7f * breathingScale
                    )
                )

                // Secondary bloom: warm wood tabletop center hit
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF5A2C10).copy(alpha = 0.8f), Color.Transparent),
                        center = Offset(width / 2, height * 0.68f),
                        radius = width * 0.45f * breathingScale
                    )
                )

                // Table edge rectangle shape
                val tableTopY = height * 0.68f
                drawRect(
                    color = Color(0xFF4A2A10).copy(alpha = 0.45f),
                    topLeft = Offset(width * 0.15f, tableTopY),
                    size = androidx.compose.ui.geometry.Size(width * 0.7f, 4f)
                )

                // Mug bottom base circle
                drawCircle(
                    color = Color(0xFF8A5020).copy(alpha = 0.35f),
                    radius = 8f,
                    center = Offset(width / 2, tableTopY)
                )

                // Delicate steam marks rising
                for (i in 0 until 2) {
                    val phase = (movingOffset + i * 50f) % 100f
                    val opacity = if (phase < 50f) phase / 50f else (100f - phase) / 50f
                    val steamX = width / 2 + Math.sin(Math.toRadians(phase.toDouble() * 3.6)).toFloat() * 4f
                    val steamY = tableTopY - 10f - (phase * 0.35f)
                    
                    drawCircle(
                        color = Color(0xFF5A3818).copy(alpha = 0.15f * opacity),
                        radius = 3f,
                        center = Offset(steamX, steamY)
                    )
                }
            }
        }
        "winter" -> {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height

                // Base background
                drawRect(color = Color(0xFF060808))

                // Primary bloom: cold winter dawn grey sky
                val dawnPulseX = (slowOffset / 100f) * 15f
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF0E1618).copy(alpha = 0.85f), Color.Transparent),
                        center = Offset(width * 0.3f + dawnPulseX, height * 0.25f),
                        radius = width * 0.8f
                    )
                )

                // Ground mist
                val mistOpacity = 0.7f + (pulseAlpha * 0.15f)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF0A1010).copy(alpha = 0.8f * mistOpacity), Color.Transparent),
                        center = Offset(width / 2, height),
                        radius = width * 0.65f
                    )
                )

                // Branching leafless dark tree forms
                val treeXPositions = listOf(0.25f, 0.45f, 0.65f, 0.8f)
                val treeHeights = listOf(0.55f, 0.62f, 0.5f, 0.58f)
                
                treeXPositions.forEachIndexed { idx, xFrac ->
                    val treeX = width * xFrac
                    val treeH = height * treeHeights[idx]
                    val startY = height
                    val endY = height - treeH

                    // Main trunk
                    drawLine(
                        color = Color(0xFF0A0C0A),
                        start = Offset(treeX, startY),
                        end = Offset(treeX, endY),
                        strokeWidth = 3f
                    )

                    // Branches (2-3 per tree)
                    drawLine(
                        color = Color(0xFF0A0C0A),
                        start = Offset(treeX, endY + treeH * 0.3f),
                        end = Offset(treeX - 15f, endY + treeH * 0.15f),
                        strokeWidth = 1.5f
                    )
                    drawLine(
                        color = Color(0xFF0A0C0A),
                        start = Offset(treeX, endY + treeH * 0.5f),
                        end = Offset(treeX + 18f, endY + treeH * 0.35f),
                        strokeWidth = 1.5f
                    )
                }
            }
        }
        "dusk" -> {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height

                // Base background
                drawRect(color = Color(0xFF080508))

                // Sunset transition gradient via overlapping blooms
                val duskFade = (slowOffset / 100f) * 0.05f
                
                // Orange horizon bloom
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF5A2A08).copy(alpha = 0.85f - duskFade), Color.Transparent),
                        center = Offset(width / 2, height * 0.75f),
                        radius = width * 0.85f
                    )
                )

                // Violet-blue sunset fading down
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF2A1040).copy(alpha = 0.6f + duskFade), Color.Transparent),
                        center = Offset(width / 2, height * 0.25f),
                        radius = width * 0.8f
                    )
                )

                // Rooftops silhouettes: simple rectangles at bottom
                val roofHeights = listOf(35f, 50f, 25f, 40f, 60f, 30f)
                val segmentWidth = width / roofHeights.size
                for (i in roofHeights.indices) {
                    val rectLeft = i * segmentWidth
                    val rectHeight = roofHeights[i]
                    drawRect(
                        color = Color(0xFF040306),
                        topLeft = Offset(rectLeft, height - rectHeight),
                        size = androidx.compose.ui.geometry.Size(segmentWidth + 1f, rectHeight)
                    )
                }
            }
        }
        "break" -> {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height

                // Base background
                drawRect(color = Color(0xFF040408))

                // Primary storm sky navy-black bloom
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF0C0C20).copy(alpha = 0.8f), Color.Transparent),
                        center = Offset(width / 2, height / 2),
                        radius = width * 0.9f
                    )
                )

                // Cloud cover surrounding the center
                val cloudCenters = listOf(
                    Offset(width * 0.15f, height * 0.3f),
                    Offset(width * 0.85f, height * 0.35f)
                )
                cloudCenters.forEach { center ->
                    drawCircle(
                        color = Color(0xFF060614).copy(alpha = 0.75f),
                        radius = width * 0.55f,
                        center = center
                    )
                }

                // Light break bright center ellipse pulsing gently
                val breakPulse = 0.88f + (pulseAlpha * 0.12f)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFD039FF).copy(alpha = 0.15f * breakPulse),
                            Color(0xFFD0D8FF).copy(alpha = 0.28f * breakPulse),
                            Color.Transparent
                        ),
                        center = Offset(width / 2, height * 0.35f),
                        radius = width * 0.32f
                    )
                )

                // Light break reflection on water
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFFA0A8CC).copy(alpha = 0.14f * breakPulse), Color.Transparent),
                        center = Offset(width / 2, height * 0.78f),
                        radius = width * 0.22f
                    )
                )

                // Wave lines in lower third
                for (i in 0 until 3) {
                    val waveY = height * (0.65f + i * 0.08f)
                    drawLine(
                        color = Color(0xFF0C1020).copy(alpha = 0.3f),
                        start = Offset(width * 0.2f, waveY),
                        end = Offset(width * 0.8f, waveY),
                        strokeWidth = 1f
                    )
                }
            }
        }
    }
}
