package com.example

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class Phase2UiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setup() {
        SessionStateManager.resetAllForTesting()
    }

    @Test
    fun testRightNowSectionDisplaysWhenMoodSelected() {
        val viewModel = SpotifyViewModel()
        
        // Select a specific card from the pool, e.g. "heavy" (Amber Interior)
        val heavyCard = viewModel.moodCardsPool.find { it.id == "heavy" }
        assertNotNull("Amber Interior card should exist in pool", heavyCard)
        viewModel.confirmMoodCardSelection(heavyCard!!)

        composeTestRule.setContent {
            MyApplicationTheme {
                HomeScreen(viewModel = viewModel)
            }
        }

        // 1. Verify "Right Now For You" section exists
        composeTestRule.onNodeWithTag("right_now_for_you_section").assertIsDisplayed()

        // 2. Verify Left Header Text is exactly "Right Now For You"
        composeTestRule.onNodeWithTag("right_now_header_left")
            .assertIsDisplayed()
            .assertTextEquals("Right Now For You")

        // 3. Verify the mood fragment is not shown beside the header
        composeTestRule.onNodeWithTag("right_now_header_right").assertDoesNotExist()
    }

    @Test
    fun testRightNowSectionHidesWhenNoMoodSelected() {
        val viewModel = SpotifyViewModel()
        
        // Ensure no mood selected initially
        viewModel.clearMood()

        composeTestRule.setContent {
            MyApplicationTheme {
                HomeScreen(viewModel = viewModel)
            }
        }

        // Section should NOT exist
        composeTestRule.onNodeWithTag("right_now_for_you_section").assertDoesNotExist()
    }

    @Test
    fun testRightNowRecommendationsCount() {
        val viewModel = SpotifyViewModel()
        val heavyCard = viewModel.moodCardsPool.find { it.id == "heavy" }
        viewModel.confirmMoodCardSelection(heavyCard!!)

        composeTestRule.setContent {
            MyApplicationTheme {
                HomeScreen(viewModel = viewModel)
            }
        }

        // Verify that calibrated recommendations exist
        val recommendations = viewModel.rightNowRecommendations.value
        assertEquals("Should have exactly 8 recommendations", 8, recommendations.size)

        // Verify the first 3 recommended songs are displayed in the UI (guaranteed visible without horizontal scroll virtualization)
        try {
            for (song in recommendations.take(3)) {
                composeTestRule.onNodeWithTag("right_now_recommendation_${song.id}").assertExists()
                composeTestRule.onNode(hasTestTag("right_now_title_${song.id}"), useUnmergedTree = true).assertTextEquals(song.title)
                composeTestRule.onNode(hasTestTag("right_now_artist_${song.id}"), useUnmergedTree = true).assertTextEquals(song.artist)
            }
        } catch (e: Throwable) {
            println("=== DEBUG ERROR ===")
            e.printStackTrace()
            try {
                composeTestRule.onRoot().printToLog("Phase2UiTest")
            } catch (p: Throwable) {
                println("Failed to print log: ${p.message}")
            }
            throw e
        }
    }

    @Test
    fun testClickingRecommendationPlaysSongAndTracksMetric() {
        val viewModel = SpotifyViewModel()
        val heavyCard = viewModel.moodCardsPool.find { it.id == "heavy" }
        viewModel.confirmMoodCardSelection(heavyCard!!)

        composeTestRule.setContent {
            MyApplicationTheme {
                HomeScreen(viewModel = viewModel)
            }
        }

        // Ensure current song is null or not the first recommended song
        val firstRecommended = viewModel.rightNowRecommendations.value.first()
        assertNotEquals(firstRecommended, viewModel.currentSong.value)
        assertFalse(viewModel.hasPlayedFromCalibrated.value)

        // Perform click on the first recommendation card
        composeTestRule.onNodeWithTag("right_now_recommendation_${firstRecommended.id}").performClick()

        // Verify song starts playing immediately
        assertEquals(firstRecommended, viewModel.currentSong.value)
        assertTrue(viewModel.isPlaying.value)

        // Verify metric tracker is now true
        assertTrue(viewModel.hasPlayedFromCalibrated.value)
    }

    @Test
    fun testActiveVibeChipAllowsClearing() {
        val viewModel = SpotifyViewModel()
        val heavyCard = viewModel.moodCardsPool.find { it.id == "heavy" }
        viewModel.confirmMoodCardSelection(heavyCard!!)

        composeTestRule.setContent {
            MyApplicationTheme {
                HomeScreen(viewModel = viewModel)
            }
        }

        // Verify the vibe active chip exists at the top of the Home Screen
        composeTestRule.onNodeWithTag("vibe_active_chip").assertIsDisplayed()

        // Perform click on the close button in the vibe chip
        composeTestRule.onNodeWithTag("vibe_clear_chip_button").performClick()

        // Verify mood is cleared in viewModel
        assertNull(viewModel.selectedMood.value)

        // Verify chip and "Right Now For You" section are gone
        composeTestRule.onNodeWithTag("vibe_active_chip").assertDoesNotExist()
        composeTestRule.onNodeWithTag("right_now_for_you_section").assertDoesNotExist()
    }
}
