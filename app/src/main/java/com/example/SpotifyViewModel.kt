package com.example

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SpotifyViewModel : ViewModel() {

    // Bottom Navigation Tab
    private val _currentTab = MutableStateFlow("Home")
    val currentTab: StateFlow<String> = _currentTab.asStateFlow()

    // Mood Board States
    private val _isMoodBoardActive = MutableStateFlow(true)
    val isMoodBoardActive: StateFlow<Boolean> = _isMoodBoardActive.asStateFlow()

    private val _prefersTile = MutableStateFlow(false)
    val prefersTile: StateFlow<Boolean> = _prefersTile.asStateFlow()

    private val _moodCards = MutableStateFlow<List<MoodCardData>>(emptyList())
    val moodCards: StateFlow<List<MoodCardData>> = _moodCards.asStateFlow()

    private val _selectedMood = MutableStateFlow<MoodCardData?>(null)
    val selectedMood: StateFlow<MoodCardData?> = _selectedMood.asStateFlow()

    // Phase 2: Calibrated recommendations for the active mood
    private val _rightNowRecommendations = MutableStateFlow<List<Song>>(emptyList())
    val rightNowRecommendations: StateFlow<List<Song>> = _rightNowRecommendations.asStateFlow()

    // Consequential session metric tracker
    private val _hasPlayedFromCalibrated = MutableStateFlow(false)
    val hasPlayedFromCalibrated: StateFlow<Boolean> = _hasPlayedFromCalibrated.asStateFlow()

    // Pool of all 15 mood cards (supports fanned 15-world scrolling)
    var moodCardsPool: List<MoodCardData> = emptyList()
    private var poolCenterIndex: Int = 1
    private var isCyclingEnabled: Boolean = true

    // Home Sub-filters
    private val _homeFilter = MutableStateFlow("All")
    val homeFilter: StateFlow<String> = _homeFilter.asStateFlow()

    // Library Sub-filters
    private val _libraryFilter = MutableStateFlow("Playlists")
    val libraryFilter: StateFlow<String> = _libraryFilter.asStateFlow()

    // Search Query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Create Modal / Bottom Sheet visibility
    private val _isCreateSheetOpen = MutableStateFlow(false)
    val isCreateSheetOpen: StateFlow<Boolean> = _isCreateSheetOpen.asStateFlow()

    // Create Playlist Dialog visibility
    private val _isCreatePlaylistDialogOpen = MutableStateFlow(false)
    val isCreatePlaylistDialogOpen: StateFlow<Boolean> = _isCreatePlaylistDialogOpen.asStateFlow()

    // Profile Section visibility
    private val _isProfileOpen = MutableStateFlow(false)
    val isProfileOpen: StateFlow<Boolean> = _isProfileOpen.asStateFlow()

    // Player State
    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong: StateFlow<Song?> = _currentSong.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _playbackProgress = MutableStateFlow(0) // in seconds
    val playbackProgress: StateFlow<Int> = _playbackProgress.asStateFlow()

    // Is Full Player Expanded
    private val _isPlayerExpanded = MutableStateFlow(false)
    val isPlayerExpanded: StateFlow<Boolean> = _isPlayerExpanded.asStateFlow()

    // Dynamic Lists (State)
    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs: StateFlow<List<Song>> = _songs.asStateFlow()

    private val _playlists = MutableStateFlow<List<Playlist>>(emptyList())
    val playlists: StateFlow<List<Playlist>> = _playlists.asStateFlow()

    private val _artists = MutableStateFlow<List<Artist>>(emptyList())
    val artists: StateFlow<List<Artist>> = _artists.asStateFlow()

    private val _categories = MutableStateFlow<List<BrowseCategory>>(emptyList())
    val categories: StateFlow<List<BrowseCategory>> = _categories.asStateFlow()

    // Sub-sections lists for real-app rich content
    private val _podcasts = MutableStateFlow<List<Song>>(emptyList())
    val podcasts: StateFlow<List<Song>> = _podcasts.asStateFlow()

    private val _followedArtists = MutableStateFlow<List<Artist>>(emptyList())
    val followedArtists: StateFlow<List<Artist>> = _followedArtists.asStateFlow()

    private val _newReleases = MutableStateFlow<List<Song>>(emptyList())
    val newReleases: StateFlow<List<Song>> = _newReleases.asStateFlow()

    private val _topIndianTracks = MutableStateFlow<List<Song>>(emptyList())
    val topIndianTracks: StateFlow<List<Song>> = _topIndianTracks.asStateFlow()

    private val _acousticChill = MutableStateFlow<List<Song>>(emptyList())
    val acousticChill: StateFlow<List<Song>> = _acousticChill.asStateFlow()

    private val _globalTop50 = MutableStateFlow<List<Song>>(emptyList())
    val globalTop50: StateFlow<List<Song>> = _globalTop50.asStateFlow()

    private var playbackJob: Job? = null

    init {
        val currentPrefers = SessionStateManager.getPrefersTile()
        _prefersTile.value = currentPrefers
        _isMoodBoardActive.value = !currentPrefers
        loadMockData()
        startPlaybackTicker()
    }

    private fun loadMockData() {
        val s1 = Song("s1", "Aarambhame Le", "Srinidhi Venkatesh", 215, true, gradientColors = listOf(Color(0xFF381460), Color(0xFF121212)))
        val s2 = Song("s2", "Jersey Theme", "Anirudh Ravichander", 184, false, gradientColors = listOf(Color(0xFF0D47A1), Color(0xFF121212)))
        val s3 = Song("s3", "Singari", "Sai Abhyankkar, Ramaj...", 242, true, gradientColors = listOf(Color(0xFF004D40), Color(0xFF121212)))
        val s4 = Song("s4", "Dacoit Kannepettaro Mix", "Raj-Koti, Gyaani, S. P. B...", 310, false, gradientColors = listOf(Color(0xFF3E2723), Color(0xFF121212)))
        val s5 = Song("s5", "Maa Inti Bangaaram", "Santhosh Narayanan", 258, true, gradientColors = listOf(Color(0xFF880E4F), Color(0xFF121212)))
        val s6 = Song("s6", "Peddi (TELUGU)", "Sai Kartheek", 210, false, gradientColors = listOf(Color(0xFFBF360C), Color(0xFF121212)))
        val s7 = Song("s7", "Aparichithudu", "Harris Jayaraj", 295, true, gradientColors = listOf(Color(0xFF1A237E), Color(0xFF121212)))
        val s8 = Song("s8", "OG Telugu songs", "Devi Sri Prasad", 320, false, gradientColors = listOf(Color(0xFF006064), Color(0xFF121212)))
        val s9 = Song("s9", "Spirit Of Jersey (Jersey)", "Anirudh Ravichander", 205, true, gradientColors = listOf(Color(0xFF1B5E20), Color(0xFF121212)))
        val s10 = Song("s10", "Daily Mix 1", "Spotify", 4200, false, gradientColors = listOf(Color(0xFF3E2723), Color(0xFF121212)))

        // Podcast Episodes (representing as Songs for easy integration/playback)
        val p1_ep = Song("p_ep1", "The Joe Rogan Experience #2160", "Joe Rogan", 7200, false, gradientColors = listOf(Color(0xFF311B92), Color(0xFF121212)))
        val p2_ep = Song("p_ep2", "The Ranveer Show (Telugu) - Allu Arjun", "Ranveer Allahbadia", 3600, false, gradientColors = listOf(Color(0xFF1B5E20), Color(0xFF121212)))
        val p3_ep = Song("p_ep3", "Telugu Tech Podcast - Episode 42", "Prasadtechintelugu", 1800, false, gradientColors = listOf(Color(0xFF0D47A1), Color(0xFF121212)))
        val p4_ep = Song("p_ep4", "Business Secrets - Zero To One", "Startup Telugu", 2400, false, gradientColors = listOf(Color(0xFF4A148C), Color(0xFF121212)))
        val p5_ep = Song("p_ep5", "Geetha Govindam Pod - Director Special", "Parasuram", 3200, false, gradientColors = listOf(Color(0xFFE65100), Color(0xFF121212)))
        val p6_ep = Song("p_ep6", "K.G.F Chapter 2 - Behind The Scenes", "Prashanth Neel", 4100, false, gradientColors = listOf(Color(0xFF263238), Color(0xFF121212)))
        _podcasts.value = listOf(p1_ep, p2_ep, p3_ep, p4_ep, p5_ep, p6_ep)

        // Followed Artists
        val fa1 = Artist("fa1", "Anirudh Ravichander")
        val fa2 = Artist("fa2", "Devi Sri Prasad")
        val fa3 = Artist("fa3", "Srinidhi Venkatesh")
        val fa4 = Artist("fa4", "Harris Jayaraj")
        val fa5 = Artist("fa5", "Sai Kartheek")
        _followedArtists.value = listOf(fa1, fa2, fa3, fa4, fa5)

        // New Releases
        val nr1 = Song("nr1", "Kurchi Madathapetti", "Thaman S, Sri Krishna", 212, false, gradientColors = listOf(Color(0xFFD50000), Color(0xFF121212)))
        val nr2 = Song("nr2", "Fear Song (Devara)", "Anirudh Ravichander", 195, false, gradientColors = listOf(Color(0xFF212121), Color(0xFF121212)))
        val nr3 = Song("nr3", "Pushpa Pushpa (Pushpa 2)", "Devi Sri Prasad", 220, false, gradientColors = listOf(Color(0xFFFFD600), Color(0xFF121212)))
        val nr4 = Song("nr4", "Ticket Eh Konakunda", "Ram Miriyala", 180, false, gradientColors = listOf(Color(0xFF00C853), Color(0xFF121212)))
        _newReleases.value = listOf(nr1, nr2, nr3, nr4)

        // Top Indian Tracks
        val tit1 = Song("tit1", "Nandamuri Kalyan Ram Mix", "Sai Kartheek", 240, false, gradientColors = listOf(Color(0xFF0091EA), Color(0xFF121212)))
        val tit2 = Song("tit2", "Khaidi No 150 Theme", "Devi Sri Prasad", 190, false, gradientColors = listOf(Color(0xFFAA00FF), Color(0xFF121212)))
        val tit3 = Song("tit3", "Oo Antava Oo Oo Antava", "Devi Sri Prasad", 223, false, gradientColors = listOf(Color(0xFFFF6D00), Color(0xFF121212)))
        val tit4 = Song("tit4", "Ramuloo Ramulaa", "Thaman S", 234, false, gradientColors = listOf(Color(0xFF00B0FF), Color(0xFF121212)))
        _topIndianTracks.value = listOf(tit1, tit2, tit3, tit4)

        // Acoustic Chill
        val ac1 = Song("ac1", "Pranam (Slowed & Reverb)", "Srinidhi Venkatesh", 280, false, gradientColors = listOf(Color(0xFF00E5FF), Color(0xFF121212)))
        val ac2 = Song("ac2", "Chila Chila (Acoustic)", "Sai Abhyankkar", 195, false, gradientColors = listOf(Color(0xFF00E676), Color(0xFF121212)))
        val ac3 = Song("ac3", "Samajavaragamana (Unplugged)", "Thaman S", 210, false, gradientColors = listOf(Color(0xFFFF3D00), Color(0xFF121212)))
        _acousticChill.value = listOf(ac1, ac2, ac3)

        // Global Top 50
        val gt1 = Song("gt1", "Blinding Lights", "The Weeknd", 200, false, gradientColors = listOf(Color(0xFFC51162), Color(0xFF121212)))
        val gt2 = Song("gt2", "Shape of You", "Ed Sheeran", 233, false, gradientColors = listOf(Color(0xFF304FFE), Color(0xFF121212)))
        val gt3 = Song("gt3", "Stay", "The Kid LAROI, Justin Bieber", 141, false, gradientColors = listOf(Color(0xFF2962FF), Color(0xFF121212)))
        _globalTop50.value = listOf(gt1, gt2, gt3)

        // Set combined songs
        _songs.value = listOf(
            s1, s2, s3, s4, s5, s6, s7, s8, s9, s10,
            p1_ep, p2_ep, p3_ep, p4_ep, p5_ep, p6_ep,
            nr1, nr2, nr3, nr4,
            tit1, tit2, tit3, tit4,
            ac1, ac2, ac3,
            gt1, gt2, gt3
        )

        // Default playing song is s1
        _currentSong.value = s1

        _playlists.value = listOf(
            Playlist("p1", "Liked Songs", "Jagadeesh", isLikedSongs = true, songs = listOf(s1, s3, s5, s7, s9), gradientColors = listOf(Color(0xFF512DA8), Color(0xFF121212))),
            Playlist("p2", "Saisowjanya + Jagadeesh", "Spotify", songs = listOf(s1, s2, s6, s9), gradientColors = listOf(Color(0xFF00796B), Color(0xFF121212))),
            Playlist("p3", "Jags", "Jagadeesh", songs = listOf(s5, s7, s8), gradientColors = listOf(Color(0xFFD32F2F), Color(0xFF121212))),
            Playlist("p4", "Daily Mix 1", "Spotify", songs = listOf(s1, s3, s10), gradientColors = listOf(Color(0xFFE64A19), Color(0xFF121212)))
        )

        _artists.value = listOf(
            Artist("a1", "Justin Bieber"),
            Artist("a2", "Devi Sri Prasad"),
            Artist("a3", "Srinidhi Venkatesh"),
            Artist("a4", "Anirudh Ravichander")
        )

        _categories.value = listOf(
            BrowseCategory("c1", "Music", listOf(Color(0xFFEC407A), Color(0xFF880E4F))),
            BrowseCategory("c2", "Podcasts", listOf(Color(0xFF009688), Color(0xFF004D40))),
            BrowseCategory("c3", "Live Events", listOf(Color(0xFFAB47BC), Color(0xFF4A148C))),
            BrowseCategory("c4", "Home of I-Pop", listOf(Color(0xFF1E88E5), Color(0xFF0D47A1))),
            BrowseCategory("c5", "Trending", listOf(Color(0xFFFF7043), Color(0xFFBF360C))),
            BrowseCategory("c6", "Made For You", listOf(Color(0xFF5C6BC0), Color(0xFF1A237E))),
            BrowseCategory("c7", "New Releases", listOf(Color(0xFF26A69A), Color(0xFF004D40))),
            BrowseCategory("c8", "Tollywood", listOf(Color(0xFF9CCC65), Color(0xFF33691E)))
        )

        // Initialize Mood Cards (maximally emotionally divergent)
        initMoodCards(shuffle = true)
    }

    fun initMoodCards(shuffle: Boolean = true) {
        val allMoods = listOf(
            MoodCardData(
                id = "heavy",
                title = "Can't Land Tonight",
                fragment = "Wide awake at the wrong hour and this is what fits.",
                accentColor = Color(0xFFC4762A), // Clear amber
                backgroundColors = listOf(Color(0xFF100804), Color(0xFF3D1A08), Color(0xFF6B3010)),
                memoryAnchorSongId = "s9", // Spirit Of Jersey
                memoryAnchorText = "You played something like this past midnight on a night you could not quite land.",
                communityCount = 1847,
                styleType = "heavy"
            ),
            MoodCardData(
                id = "ready",
                title = "On My Way Somewhere",
                fragment = "Still in it, just quieter.",
                accentColor = Color(0xFF4AA8C4), // Mid-teal
                backgroundColors = listOf(Color(0xFF04101C), Color(0xFF0E2D4A), Color(0xFF1A4D6E)),
                memoryAnchorSongId = "nr1", // Kurchi Madathapetti
                memoryAnchorText = "You always find something like this when you are on your way somewhere.",
                communityCount = 2451,
                styleType = "ready"
            ),
            MoodCardData(
                id = "electric",
                title = "Everything at Once",
                fragment = "Everything at once and I need one thing to hold onto.",
                accentColor = Color(0xFFC4762A), // Clear amber
                backgroundColors = listOf(Color(0xFF100804), Color(0xFF3D1A08), Color(0xFF6B3010)),
                memoryAnchorSongId = "gt1", // Blinding Lights
                memoryAnchorText = "You put this on quietly when the rest of it got to be too much.",
                communityCount = 3109,
                styleType = "heavy"
            ),
            MoodCardData(
                id = "joyful",
                title = "Something Went Right",
                fragment = "Something went right and I do not need to make a thing of it.",
                accentColor = Color(0xFFE8B030), // Warm golden yellow
                backgroundColors = listOf(Color(0xFF0C0A04), Color(0xFF3A2C08), Color(0xFF5A4010)),
                memoryAnchorSongId = "nr3", // Pushpa Pushpa
                memoryAnchorText = "You played something like this on one of the good days, quietly, for yourself.",
                communityCount = 1928,
                styleType = "joyful"
            ),
            MoodCardData(
                id = "melancholic",
                title = "Something Just Happened",
                fragment = "Not ready to be fine yet and that is okay.",
                accentColor = Color(0xFF4A9A6A), // Muted forest green
                backgroundColors = listOf(Color(0xFF060A08), Color(0xFF0E1A14), Color(0xFF162418)),
                memoryAnchorSongId = "ac1", // Pranam
                memoryAnchorText = "You put this on right after something that cost you and stayed with it.",
                communityCount = 1205,
                styleType = "melancholic"
            ),
            MoodCardData(
                id = "vast",
                title = "Need to Disappear into Work",
                fragment = "Here but somewhere else at the same time.",
                accentColor = Color(0xFF2AAEBC), // Clear cool teal-cyan
                backgroundColors = listOf(Color(0xFF02050F), Color(0xFF040E20), Color(0xFF0A2E46)),
                memoryAnchorSongId = "ac2", // Chila Chila
                memoryAnchorText = "You had this on for two hours without once noticing the time.",
                communityCount = 2780,
                styleType = "vast"
            ),
            MoodCardData(
                id = "world7",
                title = "Pushing Through",
                fragment = "Body taking over from the head for a while.",
                accentColor = Color(0xFF9C7030), // Muted storm-ochre
                backgroundColors = listOf(Color(0xFF060508), Color(0xFF1A1520), Color(0xFF241C10)),
                memoryAnchorSongId = "s4", // Dacoit Kannepettaro Mix
                memoryAnchorText = "You always come back to something like this when you need to push through the last part.",
                communityCount = 1420,
                styleType = "storm"
            ),
            MoodCardData(
                id = "world8",
                title = "It Feels Like Then",
                fragment = "Not today but it feels like then again somehow.",
                accentColor = Color(0xFFE8A820), // Vivid warm gold
                backgroundColors = listOf(Color(0xFF0C0804), Color(0xFF4A2C08), Color(0xFF6E4010)),
                memoryAnchorSongId = "s5", // Maa Inti Bangaaram
                memoryAnchorText = "You first found something like this years ago and some days it still finds you back.",
                communityCount = 1650,
                styleType = "gold"
            ),
            MoodCardData(
                id = "world9",
                title = "Driving Alone",
                fragment = "Road to myself and the only decision is the volume.",
                accentColor = Color(0xFFE86A1A), // Warm orange-amber
                backgroundColors = listOf(Color(0xFF0C0804), Color(0xFF5A2A08), Color(0xFF3A1A04)),
                memoryAnchorSongId = "s8", // OG Telugu songs
                memoryAnchorText = "You played something like this on a drive and let it go all the way through without touching anything.",
                communityCount = 2150,
                styleType = "highway"
            ),
            MoodCardData(
                id = "world10",
                title = "Almost Done for the Day",
                fragment = "Almost done and the music should know that too.",
                accentColor = Color(0xFF5ABCC4), // Clear mid-blue cyan
                backgroundColors = listOf(Color(0xFF04080C), Color(0xFF0C1E2C), Color(0xFF1A3040)),
                memoryAnchorSongId = "ac3", // Samajavaragamana
                memoryAnchorText = "This was the last thing playing before you finally put the day down.",
                communityCount = 980,
                styleType = "still"
            ),
            MoodCardData(
                id = "world11",
                title = "About to Go Out",
                fragment = "Still here but already starting to be somewhere else.",
                accentColor = Color(0xFF4040FF), // Vivid blue
                backgroundColors = listOf(Color(0xFF030410), Color(0xFF0A0E40), Color(0xFF1A1A60)),
                memoryAnchorSongId = "gt1", // Blinding Lights
                memoryAnchorText = "You always put something like this on before you went out and it always worked.",
                communityCount = 3420,
                styleType = "electric_blue"
            ),
            MoodCardData(
                id = "world12",
                title = "Making Something at Home",
                fragment = "Something small and good happening right now.",
                accentColor = Color(0xFFD47828), // Terracotta-amber
                backgroundColors = listOf(Color(0xFF0A0604), Color(0xFF3A1C08), Color(0xFF5A2C10)),
                memoryAnchorSongId = "ac1", // Pranam
                memoryAnchorText = "You put something like this on and the evening turned out the way it needed to.",
                communityCount = 1130,
                styleType = "kitchen"
            ),
            MoodCardData(
                id = "world13",
                title = "Thinking About Someone",
                fragment = "Thinking about someone in that particular way again.",
                accentColor = Color(0xFF6ABAAA), // Cold muted teal
                backgroundColors = listOf(Color(0xFF060808), Color(0xFF0E1618), Color(0xFF0A1010)),
                memoryAnchorSongId = "s10", // Daily Mix 1
                memoryAnchorText = "You played something like this on a night when someone was in your head and you let them stay.",
                communityCount = 870,
                styleType = "winter"
            ),
            MoodCardData(
                id = "world14",
                title = "Sunday Afternoon",
                fragment = "Slow afternoon and nothing that needs to happen.",
                accentColor = Color(0xFFE87028), // Sunset orange
                backgroundColors = listOf(Color(0xFF080508), Color(0xFF5A2A08), Color(0xFF2A1040)),
                memoryAnchorSongId = "s1", // Aarambhame Le
                memoryAnchorText = "You let something like this play for the whole afternoon once and barely moved.",
                communityCount = 2090,
                styleType = "dusk"
            ),
            MoodCardData(
                id = "world15",
                title = "Something Just Found Me",
                fragment = "Something just found me and I need to stay in it.",
                accentColor = Color(0xFFA0A0FF), // Cool blue-white
                backgroundColors = listOf(Color(0xFF040408), Color(0xFF0C0C20), Color(0xFF060614)),
                memoryAnchorSongId = "s2", // Jersey Theme
                memoryAnchorText = "You came back to something like this three times the night it first played.",
                communityCount = 1560,
                styleType = "break"
            )
        )

        isCyclingEnabled = shuffle
        if (shuffle) {
            val history = SessionStateManager.getWorldHistory()
            val freqMap = history.groupingBy { it }.eachCount()
            val weightedIds = freqMap.filter { it.value >= 2 }.keys.toList()

            val shuffledList = allMoods.shuffled().toMutableList()
            if (weightedIds.isNotEmpty()) {
                // With 75% probability, select one of the weightedIds to be the center card (index 1)
                if (java.util.Random().nextDouble() < 0.75) {
                    val targetId = weightedIds.random()
                    val targetIndex = shuffledList.indexOfFirst { it.id == targetId }
                    if (targetIndex != -1 && targetIndex != 1) {
                        val temp = shuffledList[1]
                        shuffledList[1] = shuffledList[targetIndex]
                        shuffledList[targetIndex] = temp
                    }
                }
            }
            moodCardsPool = shuffledList
            poolCenterIndex = 1

            updateMoodCardsFromPool()
        } else {
            // Test mode: only use first 3, disable sliding cycle logic for compatibility
            moodCardsPool = allMoods.take(3)
            poolCenterIndex = 1
            _moodCards.value = moodCardsPool
        }
    }

    private fun updateMoodCardsFromPool() {
        val size = moodCardsPool.size
        if (size >= 3) {
            val left = moodCardsPool[(poolCenterIndex - 1 + size) % size]
            val center = moodCardsPool[poolCenterIndex]
            val right = moodCardsPool[(poolCenterIndex + 1) % size]
            _moodCards.value = listOf(left, center, right)
        }
    }

    private fun startPlaybackTicker() {
        playbackJob?.cancel()
        playbackJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                if (_isPlaying.value) {
                    val current = _playbackProgress.value
                    val max = _currentSong.value?.duration ?: 300
                    if (current >= max) {
                        // Play next song or loop
                        _playbackProgress.value = 0
                        playNextSong()
                    } else {
                        _playbackProgress.value = current + 1
                    }
                }
            }
        }
    }

    fun selectTab(tab: String) {
        if (tab == "Create") {
            _isCreateSheetOpen.value = true
        } else {
            _currentTab.value = tab
        }
    }

    fun setHomeFilter(filter: String) {
        _homeFilter.value = filter
    }

    fun setLibraryFilter(filter: String) {
        _libraryFilter.value = filter
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun closeCreateSheet() {
        _isCreateSheetOpen.value = false
    }

    fun openCreatePlaylistDialog() {
        _isCreateSheetOpen.value = false
        _isCreatePlaylistDialogOpen.value = true
    }

    fun closeCreatePlaylistDialog() {
        _isCreatePlaylistDialogOpen.value = false
    }

    fun openProfile() {
        _isProfileOpen.value = true
    }

    fun closeProfile() {
        _isProfileOpen.value = false
    }

    fun selectMoodCard(index: Int) {
        if (index == 1) return // already in center
        if (isCyclingEnabled && moodCardsPool.size > 3) {
            val size = moodCardsPool.size
            if (index == 0) {
                // cycle left: decrement poolCenterIndex
                poolCenterIndex = (poolCenterIndex - 1 + size) % size
            } else if (index == 2) {
                // cycle right: increment poolCenterIndex
                poolCenterIndex = (poolCenterIndex + 1) % size
            }
            updateMoodCardsFromPool()
        } else {
            val currentList = _moodCards.value.toMutableList()
            if (currentList.size == 3) {
                val temp = currentList[1]
                currentList[1] = currentList[index]
                currentList[index] = temp
                _moodCards.value = currentList
            }
        }
    }

    fun confirmMoodCardSelection(card: MoodCardData) {
        SessionStateManager.resetExitCount()
        SessionStateManager.resetPrefersTile()
        SessionStateManager.addWorldToHistory(card.id)

        _prefersTile.value = false
        _selectedMood.value = card
        _isMoodBoardActive.value = false
        _hasPlayedFromCalibrated.value = false
        _rightNowRecommendations.value = getCalibratedRecommendations(card)

        // Navigate to Discover: switch tab and filter/state
        _currentTab.value = "Home"
        _homeFilter.value = "Music"
    }

    fun openMoodBoard() {
        _isMoodBoardActive.value = true
    }

    fun exitMoodBoard() {
        SessionStateManager.incrementExitCount()
        _prefersTile.value = SessionStateManager.getPrefersTile()
        _isMoodBoardActive.value = false
    }

    fun resetExitCounter() {
        SessionStateManager.resetPrefersTile()
        _prefersTile.value = false
        _isMoodBoardActive.value = true
        _selectedMood.value = null
        _rightNowRecommendations.value = emptyList()
        _hasPlayedFromCalibrated.value = false
        initMoodCards(shuffle = true)
    }

    fun clearMood() {
        _selectedMood.value = null
        _rightNowRecommendations.value = emptyList()
        _hasPlayedFromCalibrated.value = false
    }

    fun selectSongFromCalibrated(song: Song) {
        if (!_hasPlayedFromCalibrated.value) {
            _hasPlayedFromCalibrated.value = true
            try {
                android.util.Log.d("MoodBoard", "Session success! User initiated discovery session via Right Now For You: ${song.title}")
            } catch (e: Throwable) {
                println("MoodBoard: Session success! User initiated discovery session via Right Now For You: ${song.title}")
            }
        }
        selectSong(song)
    }

    fun getCalibratedRecommendations(mood: MoodCardData): List<Song> {
        val features = moodFeaturesMap[mood.styleType] ?: MoodFeatures(0.5f, 0.5f, listOf("indie"))
        val allAvailableSongs = _songs.value.filter { !it.id.startsWith("p_ep") }

        return allAvailableSongs.map { song ->
            val songFeat = songFeaturesMap[song.id] ?: SongFeatures(0.5f, 0.5f, "indie")
            val dv = songFeat.valence - features.targetValence
            val de = songFeat.energy - features.targetEnergy
            val distance = kotlin.math.sqrt((dv * dv + de * de).toDouble()).toFloat()
            
            val genreMatch = features.seedGenres.any { seed ->
                songFeat.genre.contains(seed, ignoreCase = true) || 
                seed.contains(songFeat.genre, ignoreCase = true)
            }
            
            val score = (1.0f / (1.0f + distance)) + (if (genreMatch) 0.6f else 0.0f)
            song to score
        }
        .sortedByDescending { it.second }
        .map { it.first }
        .take(8)
    }

    fun createPlaylist(name: String) {
        if (name.isBlank()) return
        val newPlaylist = Playlist(
            id = "p_" + System.currentTimeMillis(),
            name = name,
            creator = "Jagadeesh",
            gradientColors = listOf(
                Color((0..255).random(), (0..255).random(), (0..255).random()),
                Color(0xFF121212)
            )
        )
        _playlists.update { listOf(newPlaylist) + it }
        _isCreatePlaylistDialogOpen.value = false
        // Automatically switch to Your Library and filter by playlists to see it immediately!
        _currentTab.value = "Your Library"
        _libraryFilter.value = "Playlists"
    }

    fun selectSong(song: Song) {
        _currentSong.value = song
        _playbackProgress.value = 0
        _isPlaying.value = true
    }

    fun togglePlayback() {
        _isPlaying.value = !_isPlaying.value
    }

    fun seekTo(seconds: Int) {
        val max = _currentSong.value?.duration ?: 300
        _playbackProgress.value = seconds.coerceIn(0, max)
    }

    fun toggleLikeSong(songId: String) {
        _songs.update { currentSongs ->
            currentSongs.map { song ->
                if (song.id == songId) song.copy(isLiked = !song.isLiked) else song
            }
        }
        val updatedSong = _songs.value.find { it.id == songId }
        if (_currentSong.value?.id == songId && updatedSong != null) {
            _currentSong.value = updatedSong
        }

        // Update Liked Songs playlist automatically
        _playlists.update { currentPlaylists ->
            currentPlaylists.map { playlist ->
                if (playlist.isLikedSongs) {
                    val updatedSongsList = _songs.value.filter { it.isLiked }
                    playlist.copy(songs = updatedSongsList)
                } else {
                    playlist
                }
            }
        }
    }

    fun playNextSong() {
        val currentList = _songs.value
        val currentIndex = currentList.indexOfFirst { it.id == _currentSong.value?.id }
        if (currentIndex != -1) {
            val nextIndex = (currentIndex + 1) % currentList.size
            selectSong(currentList[nextIndex])
        }
    }

    fun playPreviousSong() {
        val currentList = _songs.value
        val currentIndex = currentList.indexOfFirst { it.id == _currentSong.value?.id }
        if (currentIndex != -1) {
            val prevIndex = if (currentIndex - 1 < 0) currentList.size - 1 else currentIndex - 1
            selectSong(currentList[prevIndex])
        }
    }

    fun setPlayerExpanded(expanded: Boolean) {
        _isPlayerExpanded.value = expanded
    }
}

// Helper structures & maps for Phase 2 pre-calibrated emotional matching
data class MoodFeatures(
    val targetValence: Float,
    val targetEnergy: Float,
    val seedGenres: List<String>
)

data class SongFeatures(
    val valence: Float,
    val energy: Float,
    val genre: String
)

val moodFeaturesMap = mapOf(
    "heavy" to MoodFeatures(0.15f, 0.25f, listOf("introspective folk", "melancholic indie", "quiet acoustic", "acoustic")),
    "ready" to MoodFeatures(0.85f, 0.80f, listOf("anthemic indie", "energized pop", "outward-facing alternative", "pop")),
    "electric" to MoodFeatures(0.40f, 0.90f, listOf("synthpop", "electro", "darkwave", "rock")),
    "joyful" to MoodFeatures(0.95f, 0.65f, listOf("uplifting pop", "acoustic", "sunny alternative")),
    "melancholic" to MoodFeatures(0.10f, 0.20f, listOf("ambient", "cinematic", "melancholic indie", "slowcore")),
    "vast" to MoodFeatures(0.50f, 0.35f, listOf("atmospheric lofi", "post-rock", "indie folk")),
    "storm" to MoodFeatures(0.30f, 0.85f, listOf("heavy alternative", "grunge", "intense rock")),
    "gold" to MoodFeatures(0.70f, 0.45f, listOf("warm soul", "vintage folk", "acoustic")),
    "highway" to MoodFeatures(0.80f, 0.85f, listOf("roadtrip rock", "driving synthpop", "indie pop")),
    "still" to MoodFeatures(0.60f, 0.15f, listOf("minimalist ambient", "peaceful acoustic", "lofi")),
    "electric_blue" to MoodFeatures(0.50f, 0.95f, listOf("club beat", "hyperpop", "electronic")),
    "kitchen" to MoodFeatures(0.85f, 0.40f, listOf("cozy acoustic", "home indie", "sweet folk")),
    "winter" to MoodFeatures(0.25f, 0.30f, listOf("cold indie", "isolated acoustic", "sad synth")),
    "dusk" to MoodFeatures(0.60f, 0.45f, listOf("sunset chill", "rhythm and blues", "slow lofi")),
    "break" to MoodFeatures(0.75f, 0.75f, listOf("soaring pop", "triumphant alternative", "indie"))
)

val songFeaturesMap = mapOf(
    "s1" to SongFeatures(0.75f, 0.80f, "indie pop"),
    "s2" to SongFeatures(0.50f, 0.90f, "driving synthpop"),
    "s3" to SongFeatures(0.85f, 0.85f, "energized pop"),
    "s4" to SongFeatures(0.60f, 0.75f, "retro pop"),
    "s5" to SongFeatures(0.80f, 0.60f, "warm soul"),
    "s6" to SongFeatures(0.40f, 0.85f, "intense rock"),
    "s7" to SongFeatures(0.30f, 0.90f, "heavy alternative"),
    "s8" to SongFeatures(0.70f, 0.75f, "indie"),
    "s9" to SongFeatures(0.90f, 0.90f, "triumphant alternative"),
    "s10" to SongFeatures(0.50f, 0.50f, "lofi"),
    "nr1" to SongFeatures(0.90f, 0.95f, "club beat"),
    "nr2" to SongFeatures(0.35f, 0.85f, "intense rock"),
    "nr3" to SongFeatures(0.80f, 0.90f, "energized pop"),
    "nr4" to SongFeatures(0.85f, 0.75f, "indie pop"),
    "tit1" to SongFeatures(0.45f, 0.80f, "synthpop"),
    "tit2" to SongFeatures(0.65f, 0.85f, "electro"),
    "tit3" to SongFeatures(0.50f, 0.80f, "electro"),
    "tit4" to SongFeatures(0.75f, 0.80f, "pop"),
    "ac1" to SongFeatures(0.30f, 0.35f, "quiet acoustic"),
    "ac2" to SongFeatures(0.65f, 0.45f, "cozy acoustic"),
    "ac3" to SongFeatures(0.70f, 0.40f, "home indie"),
    "gt1" to SongFeatures(0.75f, 0.85f, "driving synthpop"),
    "gt2" to SongFeatures(0.85f, 0.75f, "pop"),
    "gt3" to SongFeatures(0.80f, 0.80f, "energized pop")
)
