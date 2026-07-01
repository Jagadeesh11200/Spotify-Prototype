package com.example

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class InterPhaseRegressionTest {

    @Before
    fun setup() {
        SessionStateManager.resetAllForTesting()
    }

    @Test
    fun phase1PlaybackSurvivesPhase2MoodDiscovery() {
        val viewModel = SpotifyViewModel()
        viewModel.initMoodCards(shuffle = false)

        val originallyPlaying = viewModel.songs.value.first { it.id == "h03" }
        viewModel.selectSong(originallyPlaying)
        assertEquals(originallyPlaying.id, viewModel.currentSong.value?.id)
        assertTrue(viewModel.isPlaying.value)

        val selectedMood = viewModel.moodCards.value[1]
        viewModel.confirmMoodCardSelection(selectedMood)

        assertEquals("Home", viewModel.currentTab.value)
        assertEquals("Music", viewModel.homeFilter.value)
        assertEquals(selectedMood.id, viewModel.selectedMood.value?.id)
        assertEquals(originallyPlaying.id, viewModel.currentSong.value?.id)
        assertFalse(viewModel.rightNowRecommendations.value.any { it.id.startsWith("p_ep") })
        assertEquals(10, viewModel.rightNowRecommendations.value.size)
    }

    @Test
    fun phase2RecommendationPlayFeedsPhase1PlayerAndLibraryLikeState() {
        val viewModel = SpotifyViewModel()
        viewModel.initMoodCards(shuffle = false)

        viewModel.confirmMoodCardSelection(viewModel.moodCards.value[1])
        val recommendation = viewModel.rightNowRecommendations.value.first { !it.isLiked }

        viewModel.selectSongFromCalibrated(recommendation)
        assertEquals(recommendation.id, viewModel.currentSong.value?.id)
        assertTrue(viewModel.isPlaying.value)
        assertTrue(viewModel.hasPlayedFromCalibrated.value)

        viewModel.toggleLikeSong(recommendation.id)
        assertTrue(viewModel.songs.value.first { it.id == recommendation.id }.isLiked)
        assertTrue(
            viewModel.playlists.value
                .first { it.isLikedSongs }
                .songs
                .any { it.id == recommendation.id }
        )
    }

    @Test
    fun phase3TilePreferenceCanReturnToPhase2AndResetCleanly() {
        val viewModel = SpotifyViewModel()
        viewModel.initMoodCards(shuffle = false)

        repeat(5) {
            viewModel.exitMoodBoard()
        }

        assertTrue(SessionStateManager.getPrefersTile())
        assertTrue(viewModel.prefersTile.value)
        assertFalse(viewModel.isMoodBoardActive.value)

        viewModel.openMoodBoard()
        assertTrue(viewModel.isMoodBoardActive.value)

        val selectedMood = viewModel.moodCards.value[1]
        viewModel.confirmMoodCardSelection(selectedMood)

        assertFalse(SessionStateManager.getPrefersTile())
        assertEquals(0, SessionStateManager.getExitCount())
        assertFalse(viewModel.prefersTile.value)
        assertFalse(viewModel.isMoodBoardActive.value)
        assertEquals(selectedMood.id, viewModel.selectedMood.value?.id)
        assertEquals("Home", viewModel.currentTab.value)
        assertEquals("Music", viewModel.homeFilter.value)
        assertEquals(10, viewModel.rightNowRecommendations.value.size)
    }

    @Test
    fun resetExitCounterReturnsToPhase3EntryWithoutStalePhase2State() {
        val viewModel = SpotifyViewModel()
        viewModel.initMoodCards(shuffle = false)

        viewModel.confirmMoodCardSelection(viewModel.moodCards.value[1])
        viewModel.selectSongFromCalibrated(viewModel.rightNowRecommendations.value.first())
        assertNotNull(viewModel.selectedMood.value)
        assertTrue(viewModel.hasPlayedFromCalibrated.value)

        repeat(5) {
            viewModel.exitMoodBoard()
        }

        viewModel.resetExitCounter()

        assertEquals(0, SessionStateManager.getExitCount())
        assertFalse(SessionStateManager.getPrefersTile())
        assertFalse(viewModel.prefersTile.value)
        assertTrue(viewModel.isMoodBoardActive.value)
        assertNull(viewModel.selectedMood.value)
        assertTrue(viewModel.rightNowRecommendations.value.isEmpty())
        assertFalse(viewModel.hasPlayedFromCalibrated.value)
    }
}
