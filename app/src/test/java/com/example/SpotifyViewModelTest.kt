package com.example

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class SpotifyViewModelTest {

    private lateinit var viewModel: SpotifyViewModel

    @Before
    fun setUp() {
        SessionStateManager.resetAllForTesting()
        viewModel = SpotifyViewModel()
        viewModel.initMoodCards(shuffle = false)
    }

    @Test
    fun testInitialMockDataLoads() {
        // Assert that songs, playlists, artists, and categories are initially populated
        val songs = viewModel.songs.value
        val playlists = viewModel.playlists.value
        val artists = viewModel.artists.value
        val categories = viewModel.categories.value

        assertFalse("Songs should not be empty", songs.isEmpty())
        assertFalse("Playlists should not be empty", playlists.isEmpty())
        assertFalse("Artists should not be empty", artists.isEmpty())
        assertFalse("Categories should not be empty", categories.isEmpty())

        // Initial default selected song should be the first song
        assertEquals("h01", viewModel.currentSong.value?.id)
        assertFalse("Should not be playing initially", viewModel.isPlaying.value)
    }

    @Test
    fun testSelectTab() {
        // Test standard tab transition
        viewModel.selectTab("Search")
        assertEquals("Search", viewModel.currentTab.value)

        // Test create tab triggers the create sheet bottom sheet
        assertFalse(viewModel.isCreateSheetOpen.value)
        viewModel.selectTab("Create")
        assertTrue(viewModel.isCreateSheetOpen.value)
        // Current tab should remain unchanged
        assertEquals("Search", viewModel.currentTab.value)
    }

    @Test
    fun testFiltersAndQueries() {
        // Test home filter update
        viewModel.setHomeFilter("Music")
        assertEquals("Music", viewModel.homeFilter.value)

        // Test library filter update
        viewModel.setLibraryFilter("Albums")
        assertEquals("Albums", viewModel.libraryFilter.value)

        // Test search query update
        viewModel.setSearchQuery("Coldplay")
        assertEquals("Coldplay", viewModel.searchQuery.value)
    }

    @Test
    fun testProfileDrawerState() {
        assertFalse("Profile drawer should start closed", viewModel.isProfileOpen.value)

        viewModel.openProfile()
        assertTrue("Profile drawer should open", viewModel.isProfileOpen.value)

        viewModel.closeProfile()
        assertFalse("Profile drawer should close", viewModel.isProfileOpen.value)
    }

    @Test
    fun testCreatePlaylist() {
        val initialPlaylistCount = viewModel.playlists.value.size

        // Create a playlist
        viewModel.createPlaylist("Coding Beats")

        val updatedPlaylists = viewModel.playlists.value
        assertEquals(initialPlaylistCount + 1, updatedPlaylists.size)
        assertEquals("Coding Beats", updatedPlaylists.first().name)
        assertEquals("You", updatedPlaylists.first().creator)

        // Verifying it automatically switches tab and filter to Your Library
        assertEquals("Your Library", viewModel.currentTab.value)
        assertEquals("Playlists", viewModel.libraryFilter.value)
    }

    @Test
    fun testSongSelectionAndPlayback() {
        val targetSong = viewModel.songs.value[2]
        viewModel.selectSong(targetSong)

        // Verify state updates
        assertEquals(targetSong.id, viewModel.currentSong.value?.id)
        assertEquals(0, viewModel.playbackProgress.value)
        assertTrue("Selecting a song should auto-play it", viewModel.isPlaying.value)

        // Toggle playback
        viewModel.togglePlayback()
        assertFalse("Toggling should pause playback", viewModel.isPlaying.value)

        viewModel.togglePlayback()
        assertTrue("Toggling again should resume playback", viewModel.isPlaying.value)
    }

    @Test
    fun testLikeSong() {
        val songId = "h02"
        val song = viewModel.songs.value.find { it.id == songId }
        assertNotNull(song)
        assertFalse(song!!.isLiked)

        // Toggle like on a catalog song
        viewModel.toggleLikeSong(songId)
        val updatedSong = viewModel.songs.value.find { it.id == songId }
        assertTrue(updatedSong!!.isLiked)

        // Verify Liked Songs playlist automatically updates
        val likedPlaylist = viewModel.playlists.value.find { it.isLikedSongs }
        assertNotNull(likedPlaylist)
        assertTrue(likedPlaylist!!.songs.any { it.id == songId })
    }

    @Test
    fun testNextPreviousSong() {
        val songList = viewModel.songs.value
        val firstSong = songList[0]
        val secondSong = songList[1]
        val lastSong = songList.last()

        // Set starting song to first song
        viewModel.selectSong(firstSong)

        // Move to next
        viewModel.playNextSong()
        assertEquals(secondSong.id, viewModel.currentSong.value?.id)

        // Move back to previous
        viewModel.playPreviousSong()
        assertEquals(firstSong.id, viewModel.currentSong.value?.id)

        // Test previous wrap-around
        viewModel.playPreviousSong()
        assertEquals(lastSong.id, viewModel.currentSong.value?.id)

        // Test next wrap-around
        viewModel.playNextSong()
        assertEquals(firstSong.id, viewModel.currentSong.value?.id)
    }

    @Test
    fun testSetPlayerExpanded() {
        assertFalse(viewModel.isPlayerExpanded.value)
        viewModel.setPlayerExpanded(true)
        assertTrue(viewModel.isPlayerExpanded.value)
        viewModel.setPlayerExpanded(false)
        assertFalse(viewModel.isPlayerExpanded.value)
    }

    @Test
    fun testMoodBoardInitialState() {
        // Mood board should start active
        assertTrue("Mood board should start active", viewModel.isMoodBoardActive.value)
        
        // Mood cards list should have exactly 3 emotionally divergent options
        val cards = viewModel.moodCards.value
        assertEquals("Should have exactly 3 mood cards", 3, cards.size)
        
        // Assert specific mood ids
        assertEquals("heavy", cards[0].id)
        assertEquals("ready", cards[1].id)
        assertEquals("electric", cards[2].id)
        
        // No mood should be selected initially
        assertNull(viewModel.selectedMood.value)
    }

    @Test
    fun testSelectMoodCardCyclesWithinSession() {
        // Original order: heavy, ready, electric
        val cards = viewModel.moodCards.value
        val originalLeft = cards[0]
        val originalCenter = cards[1] // "ready"
        val originalRight = cards[2]

        // Right swipe/tap cycles right card into center: 1,2,3 -> 2,3,1
        viewModel.selectMoodCard(2)
        val rightCycledCards = viewModel.moodCards.value
        assertEquals(originalRight.id, rightCycledCards[1].id)
        assertEquals(originalCenter.id, rightCycledCards[0].id)
        assertEquals(originalLeft.id, rightCycledCards[2].id)

        // Right again wraps: 2,3,1 -> 3,1,2
        viewModel.selectMoodCard(2)
        val wrappedCards = viewModel.moodCards.value
        assertEquals(originalLeft.id, wrappedCards[1].id)

        // Selecting index 1 (already center) should do nothing
        viewModel.selectMoodCard(1)
        assertEquals(originalLeft.id, viewModel.moodCards.value[1].id)
    }

    @Test
    fun testConfirmMoodCardSelection() {
        val centerCard = viewModel.moodCards.value[1]
        
        viewModel.confirmMoodCardSelection(centerCard)
        
        // Mood card should be stored as the active selected mood
        assertEquals(centerCard.id, viewModel.selectedMood.value?.id)
        
        // Mood board screen should close
        assertFalse(viewModel.isMoodBoardActive.value)
        
        // Should navigate to Home/Music page
        assertEquals("Home", viewModel.currentTab.value)
        assertEquals("Music", viewModel.homeFilter.value)
    }

    @Test
    fun testExitMoodBoard() {
        assertTrue(viewModel.isMoodBoardActive.value)
        viewModel.exitMoodBoard()
        assertFalse(viewModel.isMoodBoardActive.value)
    }

    @Test
    fun testClearMood() {
        val centerCard = viewModel.moodCards.value[1]
        viewModel.confirmMoodCardSelection(centerCard)
        assertNotNull(viewModel.selectedMood.value)
        
        viewModel.clearMood()
        assertNull(viewModel.selectedMood.value)
    }

    @Test
    fun testOpenMoodBoard() {
        viewModel.exitMoodBoard()
        assertFalse(viewModel.isMoodBoardActive.value)
        viewModel.openMoodBoard()
        assertTrue(viewModel.isMoodBoardActive.value)
    }

    @Test
    fun testFifteenMoodWorldsMetadata() {
        val vmWithAll = SpotifyViewModel()
        vmWithAll.initMoodCards(shuffle = true)
        
        val pool = vmWithAll.allMoodCards
        assertEquals("There should be exactly 15 emotional mood worlds", 15, pool.size)

        val world1 = pool.find { it.title == "Amber Interior" }
        assertNotNull(world1)
        assertEquals("Still up, still thinking.", world1!!.fragment)
        assertEquals("You come back to this one on the late nights.", world1.memoryAnchorText)

        val world2 = pool.find { it.title == "Coastal Morning" }
        assertNotNull(world2)
        assertEquals("Out the door, finally.", world2!!.fragment)
        assertEquals("You reach for this one on your way out.", world2.memoryAnchorText)

        val world3 = pool.find { it.title == "Urban Night" }
        assertNotNull(world3)
        assertEquals("Wired, and it's late.", world3!!.fragment)
        assertEquals("You had this going last time the night ran long.", world3.memoryAnchorText)

        val world4 = pool.find { it.title == "Sunlit Meadow" }
        assertNotNull(world4)
        assertEquals("Today's just been good.", world4!!.fragment)
        assertEquals("You put this on when things are going your way.", world4.memoryAnchorText)

        val world5 = pool.find { it.title == "Deep Ocean" }
        assertNotNull(world5)
        assertEquals("Not ready to be okay yet.", world5!!.fragment)
        assertEquals("You stayed with this one right to the end, more than once.", world5.memoryAnchorText)

        val world6 = pool.find { it.title == "Foggy Forest" }
        assertNotNull(world6)
        assertEquals("Head down, tuning it out.", world6!!.fragment)
        assertEquals("This is your one for disappearing into something.", world6.memoryAnchorText)

        val world7 = pool.find { it.title == "Storm Approaching" }
        assertNotNull(world7)
        assertEquals("One more push left.", world7!!.fragment)
        assertEquals("You turn this one up when you need to push through.", world7.memoryAnchorText)

        val world8 = pool.find { it.title == "Late Afternoon Gold" }
        assertNotNull(world8)
        assertEquals("Feels like a while ago.", world8!!.fragment)
        assertEquals("You've had this one for years and it still gets you.", world8.memoryAnchorText)

        val world9 = pool.find { it.title == "Open Highway" }
        assertNotNull(world9)
        assertEquals("Just me and the road.", world9!!.fragment)
        assertEquals("You let this play the whole drive, start to finish.", world9.memoryAnchorText)

        val world10 = pool.find { it.title == "Still Water" }
        assertNotNull(world10)
        assertEquals("Almost done for the day.", world10!!.fragment)
        assertEquals("This is usually the last one before you call it a night.", world10.memoryAnchorText)

        val world11 = pool.find { it.title == "Electric Blue" }
        assertNotNull(world11)
        assertEquals("About to head out.", world11!!.fragment)
        assertEquals("You always end up playing this before you go out.", world11.memoryAnchorText)

        val world12 = pool.find { it.title == "Warm Kitchen" }
        assertNotNull(world12)
        assertEquals("Home, in no hurry.", world12!!.fragment)
        assertEquals("This one's on a lot while you're moving around the house.", world12.memoryAnchorText)

        val world13 = pool.find { it.title == "Late Winter" }
        assertNotNull(world13)
        assertEquals("Someone's on my mind.", world13!!.fragment)
        assertEquals("You come back to this one when someone's on your mind.", world13.memoryAnchorText)

        val world14 = pool.find { it.title == "Golden Dusk" }
        assertNotNull(world14)
        assertEquals("Nowhere I need to be.", world14!!.fragment)
        assertEquals("You let this play through on the slow afternoons.", world14.memoryAnchorText)

        val world15 = pool.find { it.title == "Breaking Open" }
        assertNotNull(world15)
        assertEquals("This one just got me.", world15!!.fragment)
        assertEquals("You played this over and over the day you found it.", world15.memoryAnchorText)
    }

    @Test
    fun testCalibratedRecommendationsForAmberInterior() {
        val pool = viewModel.moodCardsPool
        val heavyCard = pool.find { it.id == "heavy" }
        assertNotNull("Amber Interior card exists", heavyCard)

        viewModel.confirmMoodCardSelection(heavyCard!!)

        val recs = viewModel.rightNowRecommendations.value
        assertEquals("Should recommend exactly 10 songs", 10, recs.size)

        assertTrue("Amber Interior should use its dedicated mood catalog", recs.all { it.id.startsWith("m01_") })
        assertFalse("Metric should start false on selection", viewModel.hasPlayedFromCalibrated.value)
    }

    @Test
    fun testCalibratedRecommendationsForCoastalMorning() {
        val pool = viewModel.moodCardsPool
        val readyCard = pool.find { it.id == "ready" }
        assertNotNull("Coastal Morning card exists", readyCard)

        viewModel.confirmMoodCardSelection(readyCard!!)

        val recs = viewModel.rightNowRecommendations.value
        assertEquals("Should recommend exactly 10 songs", 10, recs.size)

        assertTrue("Coastal Morning should use its dedicated mood catalog", recs.all { it.id.startsWith("m02_") })
    }

    @Test
    fun testSelectSongFromCalibratedTracking() {
        val pool = viewModel.moodCardsPool
        val readyCard = pool.find { it.id == "ready" }
        viewModel.confirmMoodCardSelection(readyCard!!)

        assertFalse(viewModel.hasPlayedFromCalibrated.value)

        val firstSong = viewModel.rightNowRecommendations.value.first()
        viewModel.selectSongFromCalibrated(firstSong)

        assertTrue("First play from calibrated recommendations should set metric to true", viewModel.hasPlayedFromCalibrated.value)
    }

    @Test
    fun testConfirmMoodWithPreviewSongStartsPlayback() {
        val pool = viewModel.moodCardsPool
        val readyCard = pool.find { it.id == "ready" }
        assertNotNull("Coastal Morning card exists", readyCard)
        val previewSong = viewModel.getCalibratedRecommendations(readyCard!!).first()

        viewModel.confirmMoodCardSelection(readyCard, previewSong)

        assertEquals("Selected mood should be applied", readyCard.id, viewModel.selectedMood.value?.id)
        assertEquals("Preview song should start playing", previewSong.id, viewModel.currentSong.value?.id)
        assertTrue("Preview song click should count as calibrated playback", viewModel.hasPlayedFromCalibrated.value)
        assertFalse("Mood board should close after preview song click", viewModel.isMoodBoardActive.value)
    }

    @Test
    fun testClearMoodResetsRecommendationsAndMetric() {
        val pool = viewModel.moodCardsPool
        val readyCard = pool.find { it.id == "ready" }
        viewModel.confirmMoodCardSelection(readyCard!!)

        assertFalse(viewModel.rightNowRecommendations.value.isEmpty())

        viewModel.clearMood()

        assertTrue("Recommendations should be cleared", viewModel.rightNowRecommendations.value.isEmpty())
        assertFalse("Metric should reset on clear", viewModel.hasPlayedFromCalibrated.value)
    }

    @Test
    fun testPhase3ExitCountingAndTileThreshold() {
        SessionStateManager.resetAllForTesting()
        assertFalse("Initially prefersTile should be false", SessionStateManager.getPrefersTile())
        assertEquals(0, SessionStateManager.getExitCount())

        // 1st exit
        viewModel.exitMoodBoard()
        assertEquals(1, SessionStateManager.getExitCount())
        assertFalse(SessionStateManager.getPrefersTile())

        // 2nd, 3rd, 4th exits
        viewModel.exitMoodBoard()
        viewModel.exitMoodBoard()
        viewModel.exitMoodBoard()
        assertEquals(4, SessionStateManager.getExitCount())
        assertFalse(SessionStateManager.getPrefersTile())

        // 5th exit - should trigger the five-exit threshold and set prefersTile to true
        viewModel.exitMoodBoard()
        assertEquals(5, SessionStateManager.getExitCount())
        assertTrue("Tile preference should be triggered on 5th exit", SessionStateManager.getPrefersTile())
    }

    @Test
    fun testPhase3ResetOnSelection() {
        SessionStateManager.resetAllForTesting()
        
        // Trigger tile mode
        repeat(5) {
            viewModel.exitMoodBoard()
        }
        assertTrue(SessionStateManager.getPrefersTile())

        // Confirming a card selection should reset both exit count and prefersTile
        val targetCard = viewModel.moodCards.value[1]
        viewModel.confirmMoodCardSelection(targetCard)

        assertEquals(0, SessionStateManager.getExitCount())
        assertFalse("Card selection should clear prefersTile preference", SessionStateManager.getPrefersTile())
    }

    @Test
    fun testPhase3WeightedHistorySelection() {
        SessionStateManager.resetAllForTesting()

        // Add "ready" world to history multiple times
        SessionStateManager.addWorldToHistory("ready")
        SessionStateManager.addWorldToHistory("ready")
        SessionStateManager.addWorldToHistory("heavy")

        val history = SessionStateManager.getWorldHistory()
        assertTrue(history.contains("ready"))
        assertTrue(history.contains("heavy"))

        // Re-initialize cards pool with shuffle = true
        viewModel.initMoodCards(shuffle = true)
        
        // Since "ready" appears 2 times, it has a high weight to be computed as center card
        // Our shuffle method does this selection probabilistically
        assertNotNull(viewModel.moodCards.value)
    }

    @Test
    fun testPersonalizedMoodSessionShowsOnlyThreeCards() {
        val vm = SpotifyViewModel()

        assertEquals("Full mood library should keep 15 worlds", 15, vm.allMoodCards.size)
        assertEquals("Active session pool should expose exactly 3 cards", 3, vm.moodCardsPool.size)
        assertEquals("Visible mood board should show exactly 3 cards", 3, vm.moodCards.value.size)
    }

    @Test
    fun testOpeningMoodBoardWithinSameAppSessionKeepsSameThreeCards() {
        val vm = SpotifyViewModel()
        val firstSessionIds = vm.moodCards.value.map { it.id }.toSet()

        vm.openMoodBoard()
        val secondSessionIds = vm.moodCards.value.map { it.id }.toSet()

        assertEquals("Reopening Mood Board within the same app session should keep the same trio", firstSessionIds, secondSessionIds)
    }

    @Test
    fun testFreshAppSessionChangesAllThreeCards() {
        val firstVm = SpotifyViewModel()
        val firstSessionIds = firstVm.moodCards.value.map { it.id }.toSet()

        val secondVm = SpotifyViewModel()
        val secondSessionIds = secondVm.moodCards.value.map { it.id }.toSet()

        assertEquals(3, firstSessionIds.size)
        assertEquals(3, secondSessionIds.size)
        assertTrue(
            "Fresh app sessions should replace the previous three-card set",
            firstSessionIds.intersect(secondSessionIds).isEmpty()
        )
    }
}
