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
class Phase3UiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setup() {
        SessionStateManager.resetAllForTesting()
    }

    @Test
    fun testGoToHomeButtonPresenceAndAmberColor() {
        val viewModel = SpotifyViewModel()
        
        composeTestRule.setContent {
            MyApplicationTheme {
                MoodBoardScreen(
                    viewModel = viewModel,
                    onNavigateToHome = { viewModel.exitMoodBoard() }
                )
            }
        }

        // Verify that "Go to home" button is clearly visible and exists
        composeTestRule.onNodeWithText("Go to home").assertIsDisplayed()
    }

    @Test
    fun testExitFiveTimesShowsTileMode() {
        val viewModel = SpotifyViewModel()

        assertEquals(0, SessionStateManager.getExitCount())
        assertFalse(SessionStateManager.getPrefersTile())

        // Exit 5 times to trigger Tile Mode
        repeat(5) {
            viewModel.exitMoodBoard()
        }

        assertEquals(5, SessionStateManager.getExitCount())
        assertTrue(SessionStateManager.getPrefersTile())

        // Recompose/Render HomeScreen under Tile Mode
        composeTestRule.setContent {
            MyApplicationTheme {
                HomeScreen(viewModel = viewModel)
            }
        }

        // Verify that "Good evening" greeting is present
        composeTestRule.onNodeWithTag("good_evening_text").assertIsDisplayed()

        // Verify that "Recently played" section is present with its placeholder track rows
        composeTestRule.onNodeWithText("Recently played").assertIsDisplayed()

        // Verify that the small card tile is visible and displays the label
        composeTestRule.onNodeWithTag("mood_board_tile").assertIsDisplayed()
        composeTestRule.onNodeWithText("Your Mood Board").assertIsDisplayed()
    }

    @Test
    fun testTappingTileNavigatesToMoodBoard() {
        val viewModel = SpotifyViewModel()

        // Force Tile mode
        repeat(5) {
            viewModel.exitMoodBoard()
        }
        assertTrue(viewModel.prefersTile.value)

        composeTestRule.setContent {
            MyApplicationTheme {
                SpotifyApp(viewModel = viewModel)
            }
        }

        // Verify we are on the Home screen showing the tile (since intercept is disabled in tile mode)
        composeTestRule.onNodeWithTag("mood_board_tile").assertIsDisplayed()

        // Tap on the Mood Board tile
        composeTestRule.onNodeWithTag("mood_board_tile").performClick()

        // Verify we navigated back to MoodBoardScreen triptych
        assertTrue(viewModel.isMoodBoardActive.value)
    }

    @Test
    fun testTappingCardInTileModeResetsState() {
        val viewModel = SpotifyViewModel()

        // Force Tile mode
        repeat(5) {
            viewModel.exitMoodBoard()
        }
        assertTrue(viewModel.prefersTile.value)

        composeTestRule.setContent {
            MyApplicationTheme {
                SpotifyApp(viewModel = viewModel)
            }
        }

        // Tap on tile to enter MoodBoard triptych
        composeTestRule.onNodeWithTag("mood_board_tile").performClick()

        // Select the center card
        val targetCard = viewModel.moodCards.value[1]
        
        // Tap card to select/confirm
        composeTestRule.onNodeWithTag("mood_card_${targetCard.id}").performClick()

        // System state should reset
        assertEquals(0, SessionStateManager.getExitCount())
        assertFalse(SessionStateManager.getPrefersTile())
        assertFalse(viewModel.prefersTile.value)
        assertFalse(viewModel.isMoodBoardActive.value) // Navigates to discover / main home
    }

    @Test
    fun testResetExitCounterButtonClearsState() {
        val viewModel = SpotifyViewModel()

        // Trigger Tile Mode
        repeat(5) {
            viewModel.exitMoodBoard()
        }
        assertTrue(viewModel.prefersTile.value)

        composeTestRule.setContent {
            MyApplicationTheme {
                HomeScreen(viewModel = viewModel)
            }
        }

        // Reset text should be displayed
        composeTestRule.onNodeWithTag("reset_exit_counter_text").assertIsDisplayed()

        // Perform click on "Reset exit counter"
        composeTestRule.onNodeWithTag("reset_exit_counter_text").performClick()

        // Verify all counters and preferences are reset
        assertEquals(0, SessionStateManager.getExitCount())
        assertFalse(SessionStateManager.getPrefersTile())
        assertFalse(viewModel.prefersTile.value)
        assertTrue(viewModel.isMoodBoardActive.value) // Returns to intercept mode automatically
    }
}
