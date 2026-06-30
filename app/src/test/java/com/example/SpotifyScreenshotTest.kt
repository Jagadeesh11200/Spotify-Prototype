package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
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
class SpotifyScreenshotTest {

    @get:Rule 
    val composeTestRule = createComposeRule()

    @Before
    fun setup() {
        SessionStateManager.resetAllForTesting()
    }

    @Test
    fun home_screen_screenshot() {
        val viewModel = SpotifyViewModel()
        composeTestRule.setContent {
            MyApplicationTheme {
                HomeScreen(viewModel = viewModel)
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/home_screen.png")
    }

    @Test
    fun profile_drawer_screenshot() {
        composeTestRule.setContent {
            MyApplicationTheme {
                ProfileDrawer(isOpen = true, onDismiss = {})
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/profile_drawer.png")
    }

    @Test
    fun mood_board_screen_screenshot() {
        val viewModel = SpotifyViewModel()
        composeTestRule.setContent {
            MyApplicationTheme {
                MoodBoardScreen(
                    viewModel = viewModel,
                    onNavigateToHome = {}
                )
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/mood_board_screen.png")
    }

    @Test
    fun mood_discover_section_screenshot() {
        val viewModel = SpotifyViewModel()
        // Select the center mood card
        val centerCard = viewModel.moodCards.value[1]
        viewModel.confirmMoodCardSelection(centerCard)

        composeTestRule.setContent {
            MyApplicationTheme {
                HomeScreen(viewModel = viewModel)
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/mood_discover_home_screen.png")
    }
}
