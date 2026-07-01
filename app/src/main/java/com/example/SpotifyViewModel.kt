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

    // The full library has 15 worlds; each user-facing session narrows it to 3.
    var allMoodCards: List<MoodCardData> = emptyList()
    var moodCardsPool: List<MoodCardData> = emptyList()
    private var lastSessionCardIds: Set<String> = emptySet()
    private var moodSongIdsByMoodId: Map<String, List<String>> = emptyMap()

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

    private val _trendingTracks = MutableStateFlow<List<Song>>(emptyList())
    val trendingTracks: StateFlow<List<Song>> = _trendingTracks.asStateFlow()

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

    private fun song(
        id: String,
        title: String,
        artist: String,
        duration: Int,
        color: Color,
        liked: Boolean = false
    ) = Song(
        id = id,
        title = title,
        artist = artist,
        duration = duration,
        isLiked = liked,
        gradientColors = listOf(color, Color(0xFF121212))
    )

    private fun loadMockData() {
        val homeSongs = listOf(
            song("h01", "As It Was", "Harry Styles", 167, Color(0xFF5E35B1), liked = true),
            song("h02", "Shape of You", "Ed Sheeran", 233, Color(0xFF304FFE)),
            song("h03", "Watermelon Sugar", "Harry Styles", 174, Color(0xFFE91E63), liked = true),
            song("h04", "Heat Waves", "Glass Animals", 239, Color(0xFFFF7043)),
            song("h05", "Sunflower", "Post Malone, Swae Lee", 158, Color(0xFFFFB300)),
            song("h06", "Stay", "The Kid LAROI, Justin Bieber", 141, Color(0xFF2962FF), liked = true),
            song("h07", "Dance The Night", "Dua Lipa", 176, Color(0xFFC51162)),
            song("h08", "Anti-Hero", "Taylor Swift", 201, Color(0xFF7E57C2)),
            song("h09", "Kill Bill", "SZA", 153, Color(0xFF00897B), liked = true),
            song("h10", "APT.", "ROSE, Bruno Mars", 169, Color(0xFFFF4081)),
            song("h11", "Ordinary", "Alex Warren", 185, Color(0xFF26A69A)),
            song("h12", "Lose Control", "Teddy Swims", 210, Color(0xFF795548)),
            song("h13", "Greedy", "Tate McRae", 131, Color(0xFFFF6D00)),
            song("h14", "Cruel Summer", "Taylor Swift", 178, Color(0xFFE040FB)),
            song("h15", "Bad Guy", "Billie Eilish", 194, Color(0xFF689F38)),
            song("h16", "Viva La Vida", "Coldplay", 242, Color(0xFFFFC107)),
            song("h17", "Counting Stars", "OneRepublic", 257, Color(0xFF00ACC1)),
            song("h18", "Stressed Out", "Twenty One Pilots", 202, Color(0xFF455A64)),
            song("h19", "Circles", "Post Malone", 215, Color(0xFF8D6E63)),
            song("h20", "Good 4 U", "Olivia Rodrigo", 178, Color(0xFFD81B60))
        )

        val moodSongsByMoodId = mapOf(
            "heavy" to listOf(
                song("m01_01", "The Night We Met", "Lord Huron", 208, Color(0xFF6D4C41)),
                song("m01_02", "Apocalypse", "Cigarettes After Sex", 290, Color(0xFF5D4037)),
                song("m01_03", "Liability", "Lorde", 171, Color(0xFF795548)),
                song("m01_04", "I Know The End", "Phoebe Bridgers", 344, Color(0xFF8D6E63)),
                song("m01_05", "Skinny Love", "Bon Iver", 238, Color(0xFFA1887F)),
                song("m01_06", "Holocene", "Bon Iver", 336, Color(0xFF6D4C41)),
                song("m01_07", "Youth", "Daughter", 252, Color(0xFF4E342E)),
                song("m01_08", "All I Want", "Kodaline", 305, Color(0xFF795548)),
                song("m01_09", "Another Love", "Tom Odell", 244, Color(0xFF8D6E63)),
                song("m01_10", "Slow Dancing in the Dark", "Joji", 209, Color(0xFF5D4037))
            ),
            "ready" to listOf(
                song("m02_01", "Walking on a Dream", "Empire of the Sun", 198, Color(0xFF00ACC1)),
                song("m02_02", "Dog Days Are Over", "Florence + The Machine", 252, Color(0xFF26A69A)),
                song("m02_03", "Send Me On My Way", "Rusted Root", 263, Color(0xFF29B6F6)),
                song("m02_04", "Good Life", "OneRepublic", 253, Color(0xFF039BE5)),
                song("m02_05", "Sweet Disposition", "The Temper Trap", 231, Color(0xFF4FC3F7)),
                song("m02_06", "Home", "Edward Sharpe & The Magnetic Zeros", 303, Color(0xFF00BCD4)),
                song("m02_07", "Electric Feel", "MGMT", 229, Color(0xFF009688)),
                song("m02_08", "Best Day of My Life", "American Authors", 194, Color(0xFF03A9F4)),
                song("m02_09", "On Top of the World", "Imagine Dragons", 192, Color(0xFF26C6DA)),
                song("m02_10", "Riptide", "Vance Joy", 204, Color(0xFF00ACC1))
            ),
            "electric" to listOf(
                song("m03_01", "Blinding Lights", "The Weeknd", 200, Color(0xFFC51162)),
                song("m03_02", "Starboy", "The Weeknd, Daft Punk", 230, Color(0xFFAD1457)),
                song("m03_03", "Midnight City", "M83", 244, Color(0xFF5E35B1)),
                song("m03_04", "After Dark", "Mr.Kitty", 257, Color(0xFF512DA8)),
                song("m03_05", "Take on Me", "a-ha", 225, Color(0xFF3949AB)),
                song("m03_06", "Sweet Dreams (Are Made of This)", "Eurythmics", 216, Color(0xFF6A1B9A)),
                song("m03_07", "The Less I Know The Better", "Tame Impala", 216, Color(0xFF8E24AA)),
                song("m03_08", "Bad Habits", "Ed Sheeran", 231, Color(0xFFD81B60)),
                song("m03_09", "Levitating", "Dua Lipa", 203, Color(0xFFE040FB)),
                song("m03_10", "I Feel It Coming", "The Weeknd, Daft Punk", 269, Color(0xFFAB47BC))
            ),
            "joyful" to listOf(
                song("m04_01", "Flowers", "Miley Cyrus", 200, Color(0xFFFFB300)),
                song("m04_02", "Happy", "Pharrell Williams", 233, Color(0xFFFFCA28)),
                song("m04_03", "Can't Stop the Feeling!", "Justin Timberlake", 236, Color(0xFFFFD54F)),
                song("m04_04", "Shut Up and Dance", "WALK THE MOON", 199, Color(0xFFFFA000)),
                song("m04_05", "Good as Hell", "Lizzo", 159, Color(0xFFFFC107)),
                song("m04_06", "Golden", "Harry Styles", 209, Color(0xFFFFB74D)),
                song("m04_07", "Put Your Records On", "Corinne Bailey Rae", 215, Color(0xFFFFD54F)),
                song("m04_08", "Sunday Best", "Surfaces", 158, Color(0xFFFFCA28)),
                song("m04_09", "Adore You", "Harry Styles", 207, Color(0xFFFFB300)),
                song("m04_10", "Love On Top", "Beyonce", 267, Color(0xFFFFA726))
            ),
            "melancholic" to listOf(
                song("m05_01", "Someone You Loved", "Lewis Capaldi", 182, Color(0xFF2E7D32)),
                song("m05_02", "Drivers License", "Olivia Rodrigo", 242, Color(0xFF33691E)),
                song("m05_03", "When the Party's Over", "Billie Eilish", 196, Color(0xFF558B2F)),
                song("m05_04", "Fix You", "Coldplay", 296, Color(0xFF1B5E20)),
                song("m05_05", "Say You Love Me", "Jessie Ware", 258, Color(0xFF4CAF50)),
                song("m05_06", "Let Her Go", "Passenger", 253, Color(0xFF388E3C)),
                song("m05_07", "Bruises", "Lewis Capaldi", 220, Color(0xFF689F38)),
                song("m05_08", "Dancing On My Own", "Calum Scott", 260, Color(0xFF2E7D32)),
                song("m05_09", "Exile", "Taylor Swift, Bon Iver", 285, Color(0xFF33691E)),
                song("m05_10", "The Scientist", "Coldplay", 309, Color(0xFF4CAF50))
            ),
            "vast" to listOf(
                song("m06_01", "Intro", "The xx", 128, Color(0xFF00BCD4)),
                song("m06_02", "Sunset Lover", "Petit Biscuit", 237, Color(0xFF26C6DA)),
                song("m06_03", "Awake", "Tycho", 283, Color(0xFF00ACC1)),
                song("m06_04", "Dayvan Cowboy", "Boards of Canada", 300, Color(0xFF00838F)),
                song("m06_05", "Near Light", "Olafur Arnalds", 213, Color(0xFF4DD0E1)),
                song("m06_06", "Says", "Nils Frahm", 500, Color(0xFF00B8D4)),
                song("m06_07", "Weightless", "Marconi Union", 485, Color(0xFF0097A7)),
                song("m06_08", "Opus 55", "Dustin O'Halloran", 207, Color(0xFF26A69A)),
                song("m06_09", "An Ending (Ascent)", "Brian Eno", 261, Color(0xFF4DB6AC)),
                song("m06_10", "First Breath After Coma", "Explosions in the Sky", 555, Color(0xFF00ACC1))
            ),
            "world7" to listOf(
                song("m07_01", "Believer", "Imagine Dragons", 204, Color(0xFF8D6E63)),
                song("m07_02", "Radioactive", "Imagine Dragons", 187, Color(0xFF6D4C41)),
                song("m07_03", "Seven Nation Army", "The White Stripes", 232, Color(0xFF795548)),
                song("m07_04", "Uprising", "Muse", 304, Color(0xFF5D4037)),
                song("m07_05", "The Pretender", "Foo Fighters", 269, Color(0xFF4E342E)),
                song("m07_06", "Eye of the Tiger", "Survivor", 244, Color(0xFF8D6E63)),
                song("m07_07", "Stronger", "Kanye West", 312, Color(0xFF6D4C41)),
                song("m07_08", "Till I Collapse", "Eminem, Nate Dogg", 297, Color(0xFF795548)),
                song("m07_09", "Lose Yourself", "Eminem", 326, Color(0xFF5D4037)),
                song("m07_10", "Can't Hold Us", "Macklemore & Ryan Lewis", 258, Color(0xFF8D6E63))
            ),
            "world8" to listOf(
                song("m08_01", "Yellow", "Coldplay", 267, Color(0xFFFFB300)),
                song("m08_02", "Dreams", "Fleetwood Mac", 257, Color(0xFFFFA000)),
                song("m08_03", "Fast Car", "Tracy Chapman", 296, Color(0xFFFFC107)),
                song("m08_04", "Vienna", "Billy Joel", 215, Color(0xFFFFD54F)),
                song("m08_05", "Landslide", "Fleetwood Mac", 199, Color(0xFFFFB74D)),
                song("m08_06", "Everybody Wants To Rule The World", "Tears for Fears", 251, Color(0xFFFFCA28)),
                song("m08_07", "Wonderwall", "Oasis", 258, Color(0xFFFFB300)),
                song("m08_08", "There She Goes", "The La's", 163, Color(0xFFFFA726)),
                song("m08_09", "Iris", "Goo Goo Dolls", 289, Color(0xFFFFC107)),
                song("m08_10", "Time After Time", "Cyndi Lauper", 240, Color(0xFFFFD54F))
            ),
            "world9" to listOf(
                song("m09_01", "Life is a Highway", "Rascal Flatts", 276, Color(0xFFE65100)),
                song("m09_02", "Sweet Home Alabama", "Lynyrd Skynyrd", 283, Color(0xFFF57C00)),
                song("m09_03", "Take It Easy", "Eagles", 211, Color(0xFFFF7043)),
                song("m09_04", "Go Your Own Way", "Fleetwood Mac", 218, Color(0xFFFF6D00)),
                song("m09_05", "Born to Run", "Bruce Springsteen", 270, Color(0xFFE64A19)),
                song("m09_06", "Shut Up and Drive", "Rihanna", 212, Color(0xFFFF8A65)),
                song("m09_07", "Drive", "Incubus", 232, Color(0xFFFF7043)),
                song("m09_08", "Adventure of a Lifetime", "Coldplay", 263, Color(0xFFF57C00)),
                song("m09_09", "Highway Tune", "Greta Van Fleet", 181, Color(0xFFE65100)),
                song("m09_10", "Mr. Brightside", "The Killers", 222, Color(0xFFFF6D00))
            ),
            "world10" to listOf(
                song("m10_01", "Ocean Eyes", "Billie Eilish", 200, Color(0xFF4DD0E1)),
                song("m10_02", "Bloom", "The Paper Kites", 209, Color(0xFF80DEEA)),
                song("m10_03", "Better Together", "Jack Johnson", 207, Color(0xFF26C6DA)),
                song("m10_04", "Banana Pancakes", "Jack Johnson", 191, Color(0xFF00ACC1)),
                song("m10_05", "To Build a Home", "The Cinematic Orchestra", 370, Color(0xFF4FC3F7)),
                song("m10_06", "Anchor", "Novo Amor", 258, Color(0xFF00BCD4)),
                song("m10_07", "Saturn", "Sleeping At Last", 291, Color(0xFF80DEEA)),
                song("m10_08", "Mystery of Love", "Sufjan Stevens", 248, Color(0xFF26C6DA)),
                song("m10_09", "Angels", "The xx", 172, Color(0xFF4DD0E1)),
                song("m10_10", "Heartbeats", "Jose Gonzalez", 161, Color(0xFF00ACC1))
            ),
            "world11" to listOf(
                song("m11_01", "Espresso", "Sabrina Carpenter", 175, Color(0xFF304FFE)),
                song("m11_02", "Don't Start Now", "Dua Lipa", 183, Color(0xFF2962FF)),
                song("m11_03", "Uptown Funk", "Mark Ronson, Bruno Mars", 270, Color(0xFF1E88E5)),
                song("m11_04", "24K Magic", "Bruno Mars", 226, Color(0xFF3949AB)),
                song("m11_05", "One Kiss", "Calvin Harris, Dua Lipa", 215, Color(0xFF536DFE)),
                song("m11_06", "Titanium", "David Guetta, Sia", 245, Color(0xFF3D5AFE)),
                song("m11_07", "We Found Love", "Rihanna, Calvin Harris", 215, Color(0xFF2979FF)),
                song("m11_08", "Rather Be", "Clean Bandit, Jess Glynne", 227, Color(0xFF448AFF)),
                song("m11_09", "Closer", "The Chainsmokers, Halsey", 244, Color(0xFF536DFE)),
                song("m11_10", "Wake Me Up", "Avicii", 247, Color(0xFF2962FF))
            ),
            "world12" to listOf(
                song("m12_01", "Sunday Morning", "Maroon 5", 242, Color(0xFFD84315)),
                song("m12_02", "Come Away With Me", "Norah Jones", 198, Color(0xFFE64A19)),
                song("m12_03", "Lovely Day", "Bill Withers", 255, Color(0xFFFF7043)),
                song("m12_04", "Dreams Tonite", "Alvvays", 197, Color(0xFFFF8A65)),
                song("m12_05", "Real Love Baby", "Father John Misty", 189, Color(0xFFD84315)),
                song("m12_06", "Sweet Creature", "Harry Styles", 225, Color(0xFFE64A19)),
                song("m12_07", "Put It All On Me", "Ed Sheeran, Ella Mai", 197, Color(0xFFFF7043)),
                song("m12_08", "You and I", "Ingrid Michaelson", 148, Color(0xFFFF8A65)),
                song("m12_09", "Home Again", "Michael Kiwanuka", 209, Color(0xFFD84315)),
                song("m12_10", "Kiss Me", "Sixpence None the Richer", 208, Color(0xFFE64A19))
            ),
            "world13" to listOf(
                song("m13_01", "Lovely", "Billie Eilish, Khalid", 200, Color(0xFF4DB6AC)),
                song("m13_02", "Say You Won't Let Go", "James Arthur", 211, Color(0xFF26A69A)),
                song("m13_03", "All of Me", "John Legend", 269, Color(0xFF00897B)),
                song("m13_04", "Like I'm Gonna Lose You", "Meghan Trainor, John Legend", 225, Color(0xFF00796B)),
                song("m13_05", "Stay With Me", "Sam Smith", 172, Color(0xFF80CBC4)),
                song("m13_06", "I Will Follow You into the Dark", "Death Cab for Cutie", 189, Color(0xFF4DB6AC)),
                song("m13_07", "Cardigan", "Taylor Swift", 239, Color(0xFF26A69A)),
                song("m13_08", "The A Team", "Ed Sheeran", 258, Color(0xFF00897B)),
                song("m13_09", "I Miss You, I'm Sorry", "Gracie Abrams", 167, Color(0xFF00796B)),
                song("m13_10", "If the World Was Ending", "JP Saxe, Julia Michaels", 209, Color(0xFF80CBC4))
            ),
            "world14" to listOf(
                song("m14_01", "Here Comes the Sun", "The Beatles", 185, Color(0xFFFF7043)),
                song("m14_02", "Pink + White", "Frank Ocean", 184, Color(0xFFFF8A65)),
                song("m14_03", "Sunset", "The xx", 219, Color(0xFFD84315)),
                song("m14_04", "Sweet Nothing", "Taylor Swift", 188, Color(0xFFE64A19)),
                song("m14_05", "Malibu", "Miley Cyrus", 231, Color(0xFFFF7043)),
                song("m14_06", "Ophelia", "The Lumineers", 160, Color(0xFFFF8A65)),
                song("m14_07", "Ho Hey", "The Lumineers", 163, Color(0xFFD84315)),
                song("m14_08", "Budapest", "George Ezra", 201, Color(0xFFE64A19)),
                song("m14_09", "Georgia", "Vance Joy", 230, Color(0xFFFF7043)),
                song("m14_10", "Shake the Frost", "Tyler Childers", 224, Color(0xFFFF8A65))
            ),
            "world15" to listOf(
                song("m15_01", "Die With A Smile", "Lady Gaga, Bruno Mars", 251, Color(0xFF9FA8DA)),
                song("m15_02", "Birds of a Feather", "Billie Eilish", 210, Color(0xFF7986CB)),
                song("m15_03", "Beautiful Things", "Benson Boone", 180, Color(0xFF5C6BC0)),
                song("m15_04", "Chasing Cars", "Snow Patrol", 267, Color(0xFF3F51B5)),
                song("m15_05", "My Tears Ricochet", "Taylor Swift", 255, Color(0xFF9FA8DA)),
                song("m15_06", "Runaway", "Aurora", 249, Color(0xFF7986CB)),
                song("m15_07", "Everything I Wanted", "Billie Eilish", 245, Color(0xFF5C6BC0)),
                song("m15_08", "Take Me To Church", "Hozier", 241, Color(0xFF3F51B5)),
                song("m15_09", "Unsteady", "X Ambassadors", 193, Color(0xFF9FA8DA)),
                song("m15_10", "Sign of the Times", "Harry Styles", 341, Color(0xFF7986CB))
            )
        )

        val podcasts = listOf(
            song("p_ep1", "Song Exploder: How Pop Hooks Work", "Song Exploder", 1980, Color(0xFF311B92)),
            song("p_ep2", "Switched on Pop: The Anatomy of a Hit", "Vox Media", 2400, Color(0xFF1B5E20)),
            song("p_ep3", "The Journal: Streaming and Music Discovery", "The Wall Street Journal", 1800, Color(0xFF0D47A1)),
            song("p_ep4", "Decoder: The Future of Audio Platforms", "The Verge", 2640, Color(0xFF4A148C)),
            song("p_ep5", "How I Built This: Creative Companies", "NPR", 3020, Color(0xFFE65100)),
            song("p_ep6", "Twenty Thousand Hertz: Sound Design Stories", "Defacto Sound", 2100, Color(0xFF263238))
        )

        val allMoodSongs = moodSongsByMoodId.values.flatten()
        moodSongIdsByMoodId = moodSongsByMoodId.mapValues { entry -> entry.value.map { it.id } }

        _podcasts.value = podcasts
        _followedArtists.value = listOf(
            Artist("fa1", "Billie Eilish"),
            Artist("fa2", "Harry Styles"),
            Artist("fa3", "Taylor Swift"),
            Artist("fa4", "The Weeknd"),
            Artist("fa5", "Coldplay")
        )
        _newReleases.value = homeSongs.slice(9..12)
        _trendingTracks.value = homeSongs.slice(13..16)
        _acousticChill.value = moodSongsByMoodId.getValue("world12").take(4)
        _globalTop50.value = homeSongs.take(8)
        _songs.value = homeSongs + allMoodSongs + podcasts
        _currentSong.value = homeSongs.first()

        _playlists.value = listOf(
            Playlist("p1", "Liked Songs", "You", isLikedSongs = true, songs = _songs.value.filter { it.isLiked }, gradientColors = listOf(Color(0xFF512DA8), Color(0xFF121212))),
            Playlist("p2", "Friend Mix", "Spotify", songs = listOf(homeSongs[0], homeSongs[5], homeSongs[8], homeSongs[13]), gradientColors = listOf(Color(0xFF00796B), Color(0xFF121212))),
            Playlist("p3", "Weekend Favorites", "You", songs = listOf(homeSongs[2], homeSongs[6], homeSongs[12], homeSongs[19]), gradientColors = listOf(Color(0xFFD32F2F), Color(0xFF121212))),
            Playlist("p4", "Daily Mix 1", "Spotify", songs = listOf(homeSongs[1], homeSongs[4], homeSongs[10], homeSongs[16]), gradientColors = listOf(Color(0xFFE64A19), Color(0xFF121212)))
        )

        _artists.value = listOf(
            Artist("a1", "Billie Eilish"),
            Artist("a2", "Dua Lipa"),
            Artist("a3", "Bruno Mars"),
            Artist("a4", "Taylor Swift")
        )

        _categories.value = listOf(
            BrowseCategory("c1", "Music", listOf(Color(0xFFEC407A), Color(0xFF880E4F))),
            BrowseCategory("c2", "Podcasts", listOf(Color(0xFF009688), Color(0xFF004D40))),
            BrowseCategory("c3", "Live Events", listOf(Color(0xFFAB47BC), Color(0xFF4A148C))),
            BrowseCategory("c4", "Pop", listOf(Color(0xFF1E88E5), Color(0xFF0D47A1))),
            BrowseCategory("c5", "Trending", listOf(Color(0xFFFF7043), Color(0xFFBF360C))),
            BrowseCategory("c6", "Made For You", listOf(Color(0xFF5C6BC0), Color(0xFF1A237E))),
            BrowseCategory("c7", "New Releases", listOf(Color(0xFF26A69A), Color(0xFF004D40))),
            BrowseCategory("c8", "Indie", listOf(Color(0xFF9CCC65), Color(0xFF33691E)))
        )

        initMoodCards(shuffle = true)
    }

    fun initMoodCards(shuffle: Boolean = true) {
        val allMoods = listOf(
            MoodCardData(
                id = "heavy",
                title = "Amber Interior",
                fragment = "Still up, still thinking.",
                accentColor = Color(0xFFC4762A), // Clear amber
                backgroundColors = listOf(Color(0xFF100804), Color(0xFF3D1A08), Color(0xFF6B3010)),
                memoryAnchorSongId = "m01_01",
                memoryAnchorText = "You come back to this one on the late nights.",
                communityCount = 1847,
                styleType = "heavy"
            ),
            MoodCardData(
                id = "ready",
                title = "Coastal Morning",
                fragment = "Out the door, finally.",
                accentColor = Color(0xFF4AA8C4), // Mid-teal
                backgroundColors = listOf(Color(0xFF04101C), Color(0xFF0E2D4A), Color(0xFF1A4D6E)),
                memoryAnchorSongId = "m02_01",
                memoryAnchorText = "You reach for this one on your way out.",
                communityCount = 2451,
                styleType = "ready"
            ),
            MoodCardData(
                id = "electric",
                title = "Urban Night",
                fragment = "Wired, and it's late.",
                accentColor = Color(0xFFC4762A), // Clear amber
                backgroundColors = listOf(Color(0xFF100804), Color(0xFF3D1A08), Color(0xFF6B3010)),
                memoryAnchorSongId = "m03_01",
                memoryAnchorText = "You had this going last time the night ran long.",
                communityCount = 3109,
                styleType = "heavy"
            ),
            MoodCardData(
                id = "joyful",
                title = "Sunlit Meadow",
                fragment = "Today's just been good.",
                accentColor = Color(0xFFE8B030), // Warm golden yellow
                backgroundColors = listOf(Color(0xFF0C0A04), Color(0xFF3A2C08), Color(0xFF5A4010)),
                memoryAnchorSongId = "m04_01",
                memoryAnchorText = "You put this on when things are going your way.",
                communityCount = 1928,
                styleType = "joyful"
            ),
            MoodCardData(
                id = "melancholic",
                title = "Deep Ocean",
                fragment = "Not ready to be okay yet.",
                accentColor = Color(0xFF4A9A6A), // Muted forest green
                backgroundColors = listOf(Color(0xFF060A08), Color(0xFF0E1A14), Color(0xFF162418)),
                memoryAnchorSongId = "m05_01",
                memoryAnchorText = "You stayed with this one right to the end, more than once.",
                communityCount = 1205,
                styleType = "melancholic"
            ),
            MoodCardData(
                id = "vast",
                title = "Foggy Forest",
                fragment = "Head down, tuning it out.",
                accentColor = Color(0xFF2AAEBC), // Clear cool teal-cyan
                backgroundColors = listOf(Color(0xFF02050F), Color(0xFF040E20), Color(0xFF0A2E46)),
                memoryAnchorSongId = "m06_01",
                memoryAnchorText = "This is your one for disappearing into something.",
                communityCount = 2780,
                styleType = "vast"
            ),
            MoodCardData(
                id = "world7",
                title = "Storm Approaching",
                fragment = "One more push left.",
                accentColor = Color(0xFF9C7030), // Muted storm-ochre
                backgroundColors = listOf(Color(0xFF060508), Color(0xFF1A1520), Color(0xFF241C10)),
                memoryAnchorSongId = "m07_01",
                memoryAnchorText = "You turn this one up when you need to push through.",
                communityCount = 1420,
                styleType = "storm"
            ),
            MoodCardData(
                id = "world8",
                title = "Late Afternoon Gold",
                fragment = "Feels like a while ago.",
                accentColor = Color(0xFFE8A820), // Vivid warm gold
                backgroundColors = listOf(Color(0xFF0C0804), Color(0xFF4A2C08), Color(0xFF6E4010)),
                memoryAnchorSongId = "m08_01",
                memoryAnchorText = "You've had this one for years and it still gets you.",
                communityCount = 1650,
                styleType = "gold"
            ),
            MoodCardData(
                id = "world9",
                title = "Open Highway",
                fragment = "Just me and the road.",
                accentColor = Color(0xFFE86A1A), // Warm orange-amber
                backgroundColors = listOf(Color(0xFF0C0804), Color(0xFF5A2A08), Color(0xFF3A1A04)),
                memoryAnchorSongId = "m09_01",
                memoryAnchorText = "You let this play the whole drive, start to finish.",
                communityCount = 2150,
                styleType = "highway"
            ),
            MoodCardData(
                id = "world10",
                title = "Still Water",
                fragment = "Almost done for the day.",
                accentColor = Color(0xFF5ABCC4), // Clear mid-blue cyan
                backgroundColors = listOf(Color(0xFF04080C), Color(0xFF0C1E2C), Color(0xFF1A3040)),
                memoryAnchorSongId = "m10_01",
                memoryAnchorText = "This is usually the last one before you call it a night.",
                communityCount = 980,
                styleType = "still"
            ),
            MoodCardData(
                id = "world11",
                title = "Electric Blue",
                fragment = "About to head out.",
                accentColor = Color(0xFF4040FF), // Vivid blue
                backgroundColors = listOf(Color(0xFF030410), Color(0xFF0A0E40), Color(0xFF1A1A60)),
                memoryAnchorSongId = "m11_01",
                memoryAnchorText = "You always end up playing this before you go out.",
                communityCount = 3420,
                styleType = "electric_blue"
            ),
            MoodCardData(
                id = "world12",
                title = "Warm Kitchen",
                fragment = "Home, in no hurry.",
                accentColor = Color(0xFFD47828), // Terracotta-amber
                backgroundColors = listOf(Color(0xFF0A0604), Color(0xFF3A1C08), Color(0xFF5A2C10)),
                memoryAnchorSongId = "m12_01",
                memoryAnchorText = "This one's on a lot while you're moving around the house.",
                communityCount = 1130,
                styleType = "kitchen"
            ),
            MoodCardData(
                id = "world13",
                title = "Late Winter",
                fragment = "Someone's on my mind.",
                accentColor = Color(0xFF6ABAAA), // Cold muted teal
                backgroundColors = listOf(Color(0xFF060808), Color(0xFF0E1618), Color(0xFF0A1010)),
                memoryAnchorSongId = "m13_01",
                memoryAnchorText = "You come back to this one when someone's on your mind.",
                communityCount = 870,
                styleType = "winter"
            ),
            MoodCardData(
                id = "world14",
                title = "Golden Dusk",
                fragment = "Nowhere I need to be.",
                accentColor = Color(0xFFE87028), // Sunset orange
                backgroundColors = listOf(Color(0xFF080508), Color(0xFF5A2A08), Color(0xFF2A1040)),
                memoryAnchorSongId = "m14_01",
                memoryAnchorText = "You let this play through on the slow afternoons.",
                communityCount = 2090,
                styleType = "dusk"
            ),
            MoodCardData(
                id = "world15",
                title = "Breaking Open",
                fragment = "This one just got me.",
                accentColor = Color(0xFFA0A0FF), // Cool blue-white
                backgroundColors = listOf(Color(0xFF040408), Color(0xFF0C0C20), Color(0xFF060614)),
                memoryAnchorSongId = "m15_01",
                memoryAnchorText = "You played this over and over the day you found it.",
                communityCount = 1560,
                styleType = "break"
            )
        )

        allMoodCards = allMoods
        if (shuffle) {
            startNewMoodCardSession()
        } else {
            // Test mode: only use first 3, disable sliding cycle logic for compatibility
            moodCardsPool = allMoods.take(3)
            lastSessionCardIds = moodCardsPool.map { it.id }.toSet()
            _moodCards.value = moodCardsPool
        }
    }

    private fun startNewMoodCardSession() {
        if (allMoodCards.isEmpty()) return
        val sessionIndex = SessionStateManager.beginMoodSession()
        val previousSessionIds = SessionStateManager.getLastMoodSessionCardIds().toSet()
        val personalizedCards = choosePersonalizedMoodCards(allMoodCards, sessionIndex, previousSessionIds)
        moodCardsPool = personalizedCards
        lastSessionCardIds = personalizedCards.map { it.id }.toSet()
        SessionStateManager.setLastMoodSessionCardIds(personalizedCards.map { it.id })
        _moodCards.value = personalizedCards
    }

    private fun choosePersonalizedMoodCards(
        allMoods: List<MoodCardData>,
        sessionIndex: Int,
        persistedPreviousIds: Set<String>
    ): List<MoodCardData> {
        val history = SessionStateManager.getWorldHistory()
        val previousIds = persistedPreviousIds
            .ifEmpty { lastSessionCardIds }
            .ifEmpty { _moodCards.value.map { it.id }.toSet() }
        val candidates = allMoods
            .filterNot { it.id in previousIds }
            .takeIf { it.size >= 3 }
            ?: allMoods
        val hour = java.time.LocalTime.now().hour
        val timeBucket = when (hour) {
            in 5..10 -> "morning"
            in 11..16 -> "day"
            in 17..20 -> "evening"
            else -> "late"
        }
        val seed = 31L * sessionIndex + 17L * history.joinToString("|").hashCode() + timeBucket.hashCode()
        val random = java.util.Random(seed)
        val scored = candidates.associateWith { mood ->
            moodSessionScore(
                mood = mood,
                history = history,
                timeBucket = timeBucket,
                sessionIndex = sessionIndex,
                randomJitter = random.nextFloat() * 0.12f
            )
        }

        val center = scored.maxByOrNull { it.value }?.key ?: candidates[1]
        val second = candidates
            .filterNot { it.id == center.id }
            .maxByOrNull { mood ->
                (scored[mood] ?: 0f) + moodDistance(center, mood) * 0.72f
            }
            ?: candidates.first { it.id != center.id }
        val third = candidates
            .filterNot { it.id == center.id || it.id == second.id }
            .maxByOrNull { mood ->
                (scored[mood] ?: 0f) +
                    moodDistance(center, mood) * 0.46f +
                    moodDistance(second, mood) * 0.46f
            }
            ?: candidates.first { it.id != center.id && it.id != second.id }
        val flanks = if ((sessionIndex + timeBucket.hashCode()) % 2 == 0) {
            listOf(second, third)
        } else {
            listOf(third, second)
        }
        return listOf(flanks[0], center, flanks[1])
    }

    private fun moodSessionScore(
        mood: MoodCardData,
        history: List<String>,
        timeBucket: String,
        sessionIndex: Int,
        randomJitter: Float
    ): Float {
        val recentHistory = history.takeLast(5)
        val totalUses = history.count { it == mood.id }
        val recentUses = recentHistory.count { it == mood.id }
        val timeFit = when (timeBucket) {
            "morning" -> setOf("ready", "joyful", "vast", "world12")
            "day" -> setOf("ready", "vast", "world7", "world9", "joyful")
            "evening" -> setOf("world10", "world12", "world14", "world8", "melancholic")
            else -> setOf("heavy", "electric", "world11", "world13", "world15")
        }
        val noveltyBoost = if (mood.id !in history) 0.28f else 0f
        val timeBoost = if (mood.id in timeFit) 1.1f else 0f
        val historyBoost = (totalUses * 0.24f + recentUses * 0.34f).coerceAtMost(1.15f)
        val exitCount = SessionStateManager.getExitCount()
        val retreatBoost = if (exitCount >= 2 && mood.id in setOf("world10", "world12", "world14", "vast")) 0.25f else 0f
        val sessionDrift = ((sessionIndex + mood.id.hashCode()).mod(7)) * 0.025f
        return 1f + timeBoost + historyBoost + noveltyBoost + retreatBoost + sessionDrift + randomJitter
    }

    private fun moodDistance(a: MoodCardData, b: MoodCardData): Float {
        val pa = moodWorldPoint(a.id)
        val pb = moodWorldPoint(b.id)
        val dx = pa.first - pb.first
        val dy = pa.second - pb.second
        return kotlin.math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()
    }

    private fun moodWorldPoint(id: String): Pair<Float, Float> = when (id) {
        "heavy" -> 0.20f to 0.25f
        "ready" -> 0.78f to 0.78f
        "electric" -> 0.42f to 0.88f
        "joyful" -> 0.86f to 0.58f
        "melancholic" -> 0.16f to 0.34f
        "vast" -> 0.48f to 0.30f
        "world7" -> 0.30f to 0.86f
        "world8" -> 0.44f to 0.46f
        "world9" -> 0.64f to 0.70f
        "world10" -> 0.62f to 0.18f
        "world11" -> 0.74f to 0.92f
        "world12" -> 0.72f to 0.36f
        "world13" -> 0.22f to 0.22f
        "world14" -> 0.58f to 0.32f
        "world15" -> 0.36f to 0.62f
        else -> 0.50f to 0.50f
    }

    private fun updateMoodCardsFromPool() {
        _moodCards.value = moodCardsPool.take(3)
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
        val currentList = _moodCards.value.toMutableList()
        if (currentList.size == 3 && index in currentList.indices) {
            val cycledList = if (index == 2) {
                // Right swipe/tap: 1,2,3 -> 2,3,1 -> 3,1,2
                listOf(currentList[1], currentList[2], currentList[0])
            } else {
                // Left swipe/tap: 1,2,3 -> 3,1,2 -> 2,3,1
                listOf(currentList[2], currentList[0], currentList[1])
            }
            _moodCards.value = cycledList
            moodCardsPool = cycledList
        }
    }

    fun confirmMoodCardSelection(card: MoodCardData, autoplaySong: Song? = null) {
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

        autoplaySong?.let { selectSongFromCalibrated(it) }
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
        moodSongIdsByMoodId[mood.id]?.let { ids ->
            val byId = _songs.value.associateBy { it.id }
            return ids.mapNotNull { byId[it] }.take(10)
        }

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
        .take(10)
    }

    fun createPlaylist(name: String) {
        if (name.isBlank()) return
        val newPlaylist = Playlist(
            id = "p_" + System.currentTimeMillis(),
            name = name,
            creator = "You",
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
    "h01" to SongFeatures(0.72f, 0.70f, "pop"),
    "h02" to SongFeatures(0.85f, 0.75f, "pop"),
    "h03" to SongFeatures(0.86f, 0.68f, "sunny pop"),
    "h04" to SongFeatures(0.66f, 0.70f, "indie pop"),
    "h05" to SongFeatures(0.84f, 0.66f, "pop rap"),
    "h06" to SongFeatures(0.80f, 0.80f, "energized pop"),
    "h07" to SongFeatures(0.88f, 0.84f, "dance pop"),
    "h08" to SongFeatures(0.58f, 0.62f, "pop"),
    "h09" to SongFeatures(0.44f, 0.58f, "r&b"),
    "h10" to SongFeatures(0.90f, 0.84f, "pop"),
    "h11" to SongFeatures(0.62f, 0.60f, "pop"),
    "h12" to SongFeatures(0.38f, 0.62f, "soul pop"),
    "h13" to SongFeatures(0.82f, 0.78f, "dance pop"),
    "h14" to SongFeatures(0.82f, 0.76f, "pop"),
    "h15" to SongFeatures(0.44f, 0.62f, "electropop"),
    "h16" to SongFeatures(0.74f, 0.70f, "alternative pop"),
    "h17" to SongFeatures(0.78f, 0.78f, "pop rock"),
    "h18" to SongFeatures(0.42f, 0.66f, "alternative"),
    "h19" to SongFeatures(0.70f, 0.58f, "pop"),
    "h20" to SongFeatures(0.72f, 0.92f, "pop punk")
)
