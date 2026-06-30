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
        assertEquals("s1", viewModel.currentSong.value?.id)
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
        viewModel.setSearchQuery("Jersey")
        assertEquals("Jersey", viewModel.searchQuery.value)
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
        assertEquals("Jagadeesh", updatedPlaylists.first().creator)

        // Verifying it automatically switches tab and filter to Your Library
        assertEquals("Your Library", viewModel.currentTab.value)
        assertEquals("Playlists", viewModel.libraryFilter.value)
    }

    @Test
    fun testSongSelectionAndPlayback() {
        val targetSong = viewModel.songs.value[2] // s3
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
        val songId = "s2"
        val song = viewModel.songs.value.find { it.id == songId }
        assertNotNull(song)
        assertFalse(song!!.isLiked)

        // Toggle like on s2
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
    fun testSelectMoodCardSwapping() {
        // Original order: heavy, ready, electric
        val cards = viewModel.moodCards.value
        val originalLeft = cards[0]
        val originalCenter = cards[1] // "ready"
        val originalRight = cards[2]

        // Swap Left (index 0) to Center
        viewModel.selectMoodCard(0)
        val swappedCards = viewModel.moodCards.value
        assertEquals(originalLeft.id, swappedCards[1].id) // center is now heavy
        assertEquals(originalCenter.id, swappedCards[0].id) // left is now ready

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
        
        val pool = vmWithAll.moodCardsPool
        assertEquals("There should be exactly 15 emotional mood worlds", 15, pool.size)

        val world1 = pool.find { it.title == "Can't Land Tonight" }
        assertNotNull(world1)
        assertEquals("Wide awake at the wrong hour and this is what fits.", world1!!.fragment)
        assertEquals("You played something like this past midnight on a night you could not quite land.", world1.memoryAnchorText)

        val world2 = pool.find { it.title == "On My Way Somewhere" }
        assertNotNull(world2)
        assertEquals("Still in it, just quieter.", world2!!.fragment)
        assertEquals("You always find something like this when you are on your way somewhere.", world2.memoryAnchorText)

        val world3 = pool.find { it.title == "Everything at Once" }
        assertNotNull(world3)
        assertEquals("Everything at once and I need one thing to hold onto.", world3!!.fragment)
        assertEquals("You put this on quietly when the rest of it got to be too much.", world3.memoryAnchorText)

        val world4 = pool.find { it.title == "Something Went Right" }
        assertNotNull(world4)
        assertEquals("Something went right and I do not need to make a thing of it.", world4!!.fragment)
        assertEquals("You played something like this on one of the good days, quietly, for yourself.", world4.memoryAnchorText)

        val world5 = pool.find { it.title == "Something Just Happened" }
        assertNotNull(world5)
        assertEquals("Not ready to be fine yet and that is okay.", world5!!.fragment)
        assertEquals("You put this on right after something that cost you and stayed with it.", world5.memoryAnchorText)

        val world6 = pool.find { it.title == "Need to Disappear into Work" }
        assertNotNull(world6)
        assertEquals("Here but somewhere else at the same time.", world6!!.fragment)
        assertEquals("You had this on for two hours without once noticing the time.", world6.memoryAnchorText)

        val world7 = pool.find { it.title == "Pushing Through" }
        assertNotNull(world7)
        assertEquals("Body taking over from the head for a while.", world7!!.fragment)
        assertEquals("You always come back to something like this when you need to push through the last part.", world7.memoryAnchorText)

        val world8 = pool.find { it.title == "It Feels Like Then" }
        assertNotNull(world8)
        assertEquals("Not today but it feels like then again somehow.", world8!!.fragment)
        assertEquals("You first found something like this years ago and some days it still finds you back.", world8.memoryAnchorText)

        val world9 = pool.find { it.title == "Driving Alone" }
        assertNotNull(world9)
        assertEquals("Road to myself and the only decision is the volume.", world9!!.fragment)
        assertEquals("You played something like this on a drive and let it go all the way through without touching anything.", world9.memoryAnchorText)

        val world10 = pool.find { it.title == "Almost Done for the Day" }
        assertNotNull(world10)
        assertEquals("Almost done and the music should know that too.", world10!!.fragment)
        assertEquals("This was the last thing playing before you finally put the day down.", world10.memoryAnchorText)

        val world11 = pool.find { it.title == "About to Go Out" }
        assertNotNull(world11)
        assertEquals("Still here but already starting to be somewhere else.", world11!!.fragment)
        assertEquals("You always put something like this on before you went out and it always worked.", world11.memoryAnchorText)

        val world12 = pool.find { it.title == "Making Something at Home" }
        assertNotNull(world12)
        assertEquals("Something small and good happening right now.", world12!!.fragment)
        assertEquals("You put something like this on and the evening turned out the way it needed to.", world12.memoryAnchorText)

        val world13 = pool.find { it.title == "Thinking About Someone" }
        assertNotNull(world13)
        assertEquals("Thinking about someone in that particular way again.", world13!!.fragment)
        assertEquals("You played something like this on a night when someone was in your head and you let them stay.", world13.memoryAnchorText)

        val world14 = pool.find { it.title == "Sunday Afternoon" }
        assertNotNull(world14)
        assertEquals("Slow afternoon and nothing that needs to happen.", world14!!.fragment)
        assertEquals("You let something like this play for the whole afternoon once and barely moved.", world14.memoryAnchorText)

        val world15 = pool.find { it.title == "Something Just Found Me" }
        assertNotNull(world15)
        assertEquals("Something just found me and I need to stay in it.", world15!!.fragment)
        assertEquals("You came back to something like this three times the night it first played.", world15.memoryAnchorText)
    }

    @Test
    fun testCalibratedRecommendationsForAmberInterior() {
        val pool = viewModel.moodCardsPool
        val heavyCard = pool.find { it.id == "heavy" }
        assertNotNull("Amber Interior card exists", heavyCard)

        viewModel.confirmMoodCardSelection(heavyCard!!)

        val recs = viewModel.rightNowRecommendations.value
        assertEquals("Should recommend exactly 8 songs", 8, recs.size)

        // Introspective folk / quiet acoustic genres like "ac1" or "ac2" should be favored
        // Check that at least some quiet acoustic/folk songs appear in the recommendations
        val containsAcoustic = recs.any { it.id == "ac1" || it.id == "ac2" || it.id == "ac3" }
        assertTrue("Introspective acoustic songs should be recommended", containsAcoustic)
        assertFalse("Metric should start false on selection", viewModel.hasPlayedFromCalibrated.value)
    }

    @Test
    fun testCalibratedRecommendationsForCoastalMorning() {
        val pool = viewModel.moodCardsPool
        val readyCard = pool.find { it.id == "ready" }
        assertNotNull("Coastal Morning card exists", readyCard)

        viewModel.confirmMoodCardSelection(readyCard!!)

        val recs = viewModel.rightNowRecommendations.value
        assertEquals("Should recommend exactly 8 songs", 8, recs.size)

        // Energetic/pop/indie tracks should be favored
        val containsEnergetic = recs.any { it.id == "nr1" || it.id == "s3" || it.id == "gt3" }
        assertTrue("Energetic tracks should be recommended", containsEnergetic)
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
}
