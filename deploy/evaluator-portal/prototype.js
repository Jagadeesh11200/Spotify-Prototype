const homeSongs = [
  song("h01", "As It Was", "Harry Styles", 167, "#5E35B1", true),
  song("h02", "Shape of You", "Ed Sheeran", 233, "#304FFE"),
  song("h03", "Watermelon Sugar", "Harry Styles", 174, "#E91E63", true),
  song("h04", "Heat Waves", "Glass Animals", 239, "#FF7043"),
  song("h05", "Sunflower", "Post Malone, Swae Lee", 158, "#FFB300"),
  song("h06", "Stay", "The Kid LAROI, Justin Bieber", 141, "#2962FF", true),
  song("h07", "Dance The Night", "Dua Lipa", 176, "#C51162"),
  song("h08", "Anti-Hero", "Taylor Swift", 201, "#7E57C2"),
  song("h09", "Kill Bill", "SZA", 153, "#00897B", true),
  song("h10", "APT.", "ROSE, Bruno Mars", 169, "#FF4081"),
  song("h11", "Ordinary", "Alex Warren", 185, "#26A69A"),
  song("h12", "Lose Control", "Teddy Swims", 210, "#795548"),
  song("h13", "Greedy", "Tate McRae", 131, "#FF6D00"),
  song("h14", "Cruel Summer", "Taylor Swift", 178, "#E040FB"),
  song("h15", "Bad Guy", "Billie Eilish", 194, "#689F38"),
  song("h16", "Viva La Vida", "Coldplay", 242, "#FFC107"),
  song("h17", "Counting Stars", "OneRepublic", 257, "#00ACC1"),
  song("h18", "Stressed Out", "Twenty One Pilots", 202, "#455A64"),
  song("h19", "Circles", "Post Malone", 215, "#8D6E63"),
  song("h20", "Good 4 U", "Olivia Rodrigo", 178, "#D81B60")
];

const moodSongs = {
  heavy: [
    song("m01_01", "The Night We Met", "Lord Huron", 208, "#6D4C41"),
    song("m01_02", "Apocalypse", "Cigarettes After Sex", 290, "#5D4037"),
    song("m01_03", "Liability", "Lorde", 171, "#795548"),
    song("m01_04", "I Know The End", "Phoebe Bridgers", 344, "#8D6E63"),
    song("m01_05", "Skinny Love", "Bon Iver", 238, "#A1887F"),
    song("m01_06", "Holocene", "Bon Iver", 336, "#6D4C41"),
    song("m01_07", "Youth", "Daughter", 252, "#4E342E"),
    song("m01_08", "All I Want", "Kodaline", 305, "#795548"),
    song("m01_09", "Another Love", "Tom Odell", 244, "#8D6E63"),
    song("m01_10", "Slow Dancing in the Dark", "Joji", 209, "#5D4037")
  ],
  ready: [
    song("m02_01", "Walking on a Dream", "Empire of the Sun", 198, "#00ACC1"),
    song("m02_02", "Dog Days Are Over", "Florence + The Machine", 252, "#26A69A"),
    song("m02_03", "Send Me On My Way", "Rusted Root", 263, "#29B6F6"),
    song("m02_04", "Good Life", "OneRepublic", 253, "#039BE5"),
    song("m02_05", "Sweet Disposition", "The Temper Trap", 231, "#4FC3F7"),
    song("m02_06", "Home", "Edward Sharpe & The Magnetic Zeros", 303, "#00BCD4"),
    song("m02_07", "Electric Feel", "MGMT", 229, "#009688"),
    song("m02_08", "Best Day of My Life", "American Authors", 194, "#03A9F4"),
    song("m02_09", "On Top of the World", "Imagine Dragons", 192, "#26C6DA"),
    song("m02_10", "Riptide", "Vance Joy", 204, "#00ACC1")
  ],
  electric: [
    song("m03_01", "Blinding Lights", "The Weeknd", 200, "#C51162"),
    song("m03_02", "Starboy", "The Weeknd, Daft Punk", 230, "#AD1457"),
    song("m03_03", "Midnight City", "M83", 244, "#5E35B1"),
    song("m03_04", "After Dark", "Mr.Kitty", 257, "#512DA8"),
    song("m03_05", "Take on Me", "a-ha", 225, "#3949AB"),
    song("m03_06", "Sweet Dreams (Are Made of This)", "Eurythmics", 216, "#6A1B9A"),
    song("m03_07", "The Less I Know The Better", "Tame Impala", 216, "#8E24AA"),
    song("m03_08", "Bad Habits", "Ed Sheeran", 231, "#D81B60"),
    song("m03_09", "Levitating", "Dua Lipa", 203, "#E040FB"),
    song("m03_10", "I Feel It Coming", "The Weeknd, Daft Punk", 269, "#AB47BC")
  ],
  joyful: [
    song("m04_01", "Flowers", "Miley Cyrus", 200, "#FFB300"),
    song("m04_02", "Happy", "Pharrell Williams", 233, "#FFCA28"),
    song("m04_03", "Can't Stop the Feeling!", "Justin Timberlake", 236, "#FFD54F"),
    song("m04_04", "Shut Up and Dance", "WALK THE MOON", 199, "#FFA000"),
    song("m04_05", "Good as Hell", "Lizzo", 159, "#FFC107"),
    song("m04_06", "Golden", "Harry Styles", 209, "#FFB74D"),
    song("m04_07", "Put Your Records On", "Corinne Bailey Rae", 215, "#FFD54F"),
    song("m04_08", "Sunday Best", "Surfaces", 158, "#FFCA28"),
    song("m04_09", "Adore You", "Harry Styles", 207, "#FFB300"),
    song("m04_10", "Love On Top", "Beyonce", 267, "#FFA726")
  ],
  melancholic: [
    song("m05_01", "Someone You Loved", "Lewis Capaldi", 182, "#2E7D32"),
    song("m05_02", "Drivers License", "Olivia Rodrigo", 242, "#33691E"),
    song("m05_03", "When the Party's Over", "Billie Eilish", 196, "#558B2F"),
    song("m05_04", "Fix You", "Coldplay", 296, "#1B5E20"),
    song("m05_05", "Say You Love Me", "Jessie Ware", 258, "#4CAF50"),
    song("m05_06", "Let Her Go", "Passenger", 253, "#388E3C"),
    song("m05_07", "Bruises", "Lewis Capaldi", 220, "#689F38"),
    song("m05_08", "Dancing On My Own", "Calum Scott", 260, "#2E7D32"),
    song("m05_09", "Exile", "Taylor Swift, Bon Iver", 285, "#33691E"),
    song("m05_10", "The Scientist", "Coldplay", 309, "#4CAF50")
  ],
  vast: [
    song("m06_01", "Intro", "The xx", 128, "#00BCD4"),
    song("m06_02", "Sunset Lover", "Petit Biscuit", 237, "#26C6DA"),
    song("m06_03", "Awake", "Tycho", 283, "#00ACC1"),
    song("m06_04", "Dayvan Cowboy", "Boards of Canada", 300, "#00838F"),
    song("m06_05", "Near Light", "Olafur Arnalds", 213, "#4DD0E1"),
    song("m06_06", "Says", "Nils Frahm", 500, "#00B8D4"),
    song("m06_07", "Weightless", "Marconi Union", 485, "#0097A7"),
    song("m06_08", "Opus 55", "Dustin O'Halloran", 207, "#26A69A"),
    song("m06_09", "An Ending (Ascent)", "Brian Eno", 261, "#4DB6AC"),
    song("m06_10", "First Breath After Coma", "Explosions in the Sky", 555, "#00ACC1")
  ],
  world7: [
    song("m07_01", "Believer", "Imagine Dragons", 204, "#8D6E63"),
    song("m07_02", "Radioactive", "Imagine Dragons", 187, "#6D4C41"),
    song("m07_03", "Seven Nation Army", "The White Stripes", 232, "#795548"),
    song("m07_04", "Uprising", "Muse", 304, "#5D4037"),
    song("m07_05", "The Pretender", "Foo Fighters", 269, "#4E342E"),
    song("m07_06", "Eye of the Tiger", "Survivor", 244, "#8D6E63"),
    song("m07_07", "Stronger", "Kanye West", 312, "#6D4C41"),
    song("m07_08", "Till I Collapse", "Eminem, Nate Dogg", 297, "#795548"),
    song("m07_09", "Lose Yourself", "Eminem", 326, "#5D4037"),
    song("m07_10", "Can't Hold Us", "Macklemore & Ryan Lewis", 258, "#8D6E63")
  ],
  world8: [
    song("m08_01", "Yellow", "Coldplay", 267, "#FFB300"),
    song("m08_02", "Dreams", "Fleetwood Mac", 257, "#FFA000"),
    song("m08_03", "Fast Car", "Tracy Chapman", 296, "#FFC107"),
    song("m08_04", "Vienna", "Billy Joel", 215, "#FFD54F"),
    song("m08_05", "Landslide", "Fleetwood Mac", 199, "#FFB74D"),
    song("m08_06", "Everybody Wants To Rule The World", "Tears for Fears", 251, "#FFCA28"),
    song("m08_07", "Wonderwall", "Oasis", 258, "#FFB300"),
    song("m08_08", "There She Goes", "The La's", 163, "#FFA726"),
    song("m08_09", "Iris", "Goo Goo Dolls", 289, "#FFC107"),
    song("m08_10", "Time After Time", "Cyndi Lauper", 240, "#FFD54F")
  ],
  world9: [
    song("m09_01", "Life is a Highway", "Rascal Flatts", 276, "#E65100"),
    song("m09_02", "Sweet Home Alabama", "Lynyrd Skynyrd", 283, "#F57C00"),
    song("m09_03", "Take It Easy", "Eagles", 211, "#FF7043"),
    song("m09_04", "Go Your Own Way", "Fleetwood Mac", 218, "#FF6D00"),
    song("m09_05", "Born to Run", "Bruce Springsteen", 270, "#E64A19"),
    song("m09_06", "Shut Up and Drive", "Rihanna", 212, "#FF8A65"),
    song("m09_07", "Drive", "Incubus", 232, "#FF7043"),
    song("m09_08", "Adventure of a Lifetime", "Coldplay", 263, "#F57C00"),
    song("m09_09", "Highway Tune", "Greta Van Fleet", 181, "#E65100"),
    song("m09_10", "Mr. Brightside", "The Killers", 222, "#FF6D00")
  ],
  world10: [
    song("m10_01", "Ocean Eyes", "Billie Eilish", 200, "#4DD0E1"),
    song("m10_02", "Bloom", "The Paper Kites", 209, "#80DEEA"),
    song("m10_03", "Better Together", "Jack Johnson", 207, "#26C6DA"),
    song("m10_04", "Banana Pancakes", "Jack Johnson", 191, "#00ACC1"),
    song("m10_05", "To Build a Home", "The Cinematic Orchestra", 370, "#4FC3F7"),
    song("m10_06", "Anchor", "Novo Amor", 258, "#00BCD4"),
    song("m10_07", "Saturn", "Sleeping At Last", 291, "#80DEEA"),
    song("m10_08", "Mystery of Love", "Sufjan Stevens", 248, "#26C6DA"),
    song("m10_09", "Angels", "The xx", 172, "#4DD0E1"),
    song("m10_10", "Heartbeats", "Jose Gonzalez", 161, "#00ACC1")
  ],
  world11: [
    song("m11_01", "Espresso", "Sabrina Carpenter", 175, "#304FFE"),
    song("m11_02", "Don't Start Now", "Dua Lipa", 183, "#2962FF"),
    song("m11_03", "Uptown Funk", "Mark Ronson, Bruno Mars", 270, "#1E88E5"),
    song("m11_04", "24K Magic", "Bruno Mars", 226, "#3949AB"),
    song("m11_05", "One Kiss", "Calvin Harris, Dua Lipa", 215, "#536DFE"),
    song("m11_06", "Titanium", "David Guetta, Sia", 245, "#3D5AFE"),
    song("m11_07", "We Found Love", "Rihanna, Calvin Harris", 215, "#2979FF"),
    song("m11_08", "Rather Be", "Clean Bandit, Jess Glynne", 227, "#448AFF"),
    song("m11_09", "Closer", "The Chainsmokers, Halsey", 244, "#536DFE"),
    song("m11_10", "Wake Me Up", "Avicii", 247, "#2962FF")
  ],
  world12: [
    song("m12_01", "Sunday Morning", "Maroon 5", 242, "#D84315"),
    song("m12_02", "Come Away With Me", "Norah Jones", 198, "#E64A19"),
    song("m12_03", "Lovely Day", "Bill Withers", 255, "#FF7043"),
    song("m12_04", "Dreams Tonite", "Alvvays", 197, "#FF8A65"),
    song("m12_05", "Real Love Baby", "Father John Misty", 189, "#D84315"),
    song("m12_06", "Sweet Creature", "Harry Styles", 225, "#E64A19"),
    song("m12_07", "Put It All On Me", "Ed Sheeran, Ella Mai", 197, "#FF7043"),
    song("m12_08", "You and I", "Ingrid Michaelson", 148, "#FF8A65"),
    song("m12_09", "Home Again", "Michael Kiwanuka", 209, "#D84315"),
    song("m12_10", "Kiss Me", "Sixpence None the Richer", 208, "#E64A19")
  ],
  world13: [
    song("m13_01", "Lovely", "Billie Eilish, Khalid", 200, "#4DB6AC"),
    song("m13_02", "Say You Won't Let Go", "James Arthur", 211, "#26A69A"),
    song("m13_03", "All of Me", "John Legend", 269, "#00897B"),
    song("m13_04", "Like I'm Gonna Lose You", "Meghan Trainor, John Legend", 225, "#00796B"),
    song("m13_05", "Stay With Me", "Sam Smith", 172, "#80CBC4"),
    song("m13_06", "I Will Follow You into the Dark", "Death Cab for Cutie", 189, "#4DB6AC"),
    song("m13_07", "Cardigan", "Taylor Swift", 239, "#26A69A"),
    song("m13_08", "The A Team", "Ed Sheeran", 258, "#00897B"),
    song("m13_09", "I Miss You, I'm Sorry", "Gracie Abrams", 167, "#00796B"),
    song("m13_10", "If the World Was Ending", "JP Saxe, Julia Michaels", 209, "#80CBC4")
  ],
  world14: [
    song("m14_01", "Here Comes the Sun", "The Beatles", 185, "#FF7043"),
    song("m14_02", "Pink + White", "Frank Ocean", 184, "#FF8A65"),
    song("m14_03", "Sunset", "The xx", 219, "#D84315"),
    song("m14_04", "Sweet Nothing", "Taylor Swift", 188, "#E64A19"),
    song("m14_05", "Malibu", "Miley Cyrus", 231, "#FF7043"),
    song("m14_06", "Ophelia", "The Lumineers", 160, "#FF8A65"),
    song("m14_07", "Ho Hey", "The Lumineers", 163, "#D84315"),
    song("m14_08", "Budapest", "George Ezra", 201, "#E64A19"),
    song("m14_09", "Georgia", "Vance Joy", 230, "#FF7043"),
    song("m14_10", "Shake the Frost", "Tyler Childers", 224, "#FF8A65")
  ],
  world15: [
    song("m15_01", "Die With A Smile", "Lady Gaga, Bruno Mars", 251, "#9FA8DA"),
    song("m15_02", "Birds of a Feather", "Billie Eilish", 210, "#7986CB"),
    song("m15_03", "Beautiful Things", "Benson Boone", 180, "#5C6BC0"),
    song("m15_04", "Chasing Cars", "Snow Patrol", 267, "#3F51B5"),
    song("m15_05", "My Tears Ricochet", "Taylor Swift", 255, "#9FA8DA"),
    song("m15_06", "Runaway", "Aurora", 249, "#7986CB"),
    song("m15_07", "Everything I Wanted", "Billie Eilish", 245, "#5C6BC0"),
    song("m15_08", "Take Me To Church", "Hozier", 241, "#3F51B5"),
    song("m15_09", "Unsteady", "X Ambassadors", 193, "#9FA8DA"),
    song("m15_10", "Sign of the Times", "Harry Styles", 341, "#7986CB")
  ]
};

const moods = [
  mood("heavy", "Amber Interior", "Still up, still thinking.", "#C4762A", ["#100804", "#3D1A08", "#6B3010"], "You come back to this one on the late nights.", 1847, "heavy"),
  mood("ready", "Coastal Morning", "Out the door, finally.", "#4AA8C4", ["#04101C", "#0E2D4A", "#1A4D6E"], "You reach for this one on your way out.", 2451, "ready"),
  mood("electric", "Urban Night", "Wired, and it's late.", "#C4762A", ["#100804", "#3D1A08", "#6B3010"], "You had this going last time the night ran long.", 3109, "heavy"),
  mood("joyful", "Sunlit Meadow", "Today's just been good.", "#E8B030", ["#0C0A04", "#3A2C08", "#5A4010"], "You put this on when things are going your way.", 1928, "joyful"),
  mood("melancholic", "Deep Ocean", "Not ready to be okay yet.", "#4A9A6A", ["#060A08", "#0E1A14", "#162418"], "You stayed with this one right to the end, more than once.", 1205, "melancholic"),
  mood("vast", "Foggy Forest", "Head down, tuning it out.", "#2AAEBC", ["#02050F", "#040E20", "#0A2E46"], "This is your one for disappearing into something.", 2780, "vast"),
  mood("world7", "Storm Approaching", "One more push left.", "#9C7030", ["#060508", "#1A1520", "#241C10"], "You turn this one up when you need to push through.", 1420, "storm"),
  mood("world8", "Late Afternoon Gold", "Feels like a while ago.", "#E8A820", ["#0C0804", "#4A2C08", "#6E4010"], "You've had this one for years and it still gets you.", 1650, "gold"),
  mood("world9", "Open Highway", "Just me and the road.", "#E86A1A", ["#0C0804", "#5A2A08", "#3A1A04"], "You let this play the whole drive, start to finish.", 2150, "highway"),
  mood("world10", "Still Water", "Almost done for the day.", "#5ABCC4", ["#04080C", "#0C1E2C", "#1A3040"], "This is usually the last one before you call it a night.", 980, "still"),
  mood("world11", "Electric Blue", "About to head out.", "#4040FF", ["#030410", "#0A0E40", "#1A1A60"], "You always end up playing this before you go out.", 3420, "electric_blue"),
  mood("world12", "Warm Kitchen", "Home, in no hurry.", "#D47828", ["#0A0604", "#3A1C08", "#5A2C10"], "This one's on a lot while you're moving around the house.", 1130, "kitchen"),
  mood("world13", "Late Winter", "Someone's on my mind.", "#6ABAAA", ["#060808", "#0E1618", "#0A1010"], "You come back to this one when someone's on your mind.", 870, "winter"),
  mood("world14", "Golden Dusk", "Nowhere I need to be.", "#E87028", ["#080508", "#5A2A08", "#2A1040"], "You let this play through on the slow afternoons.", 2090, "dusk"),
  mood("world15", "Breaking Open", "This one just got me.", "#A0A0FF", ["#040408", "#0C0C20", "#060614"], "You played this over and over the day you found it.", 1560, "break")
];

const categories = [
  category("c1", "Music", ["#EC407A", "#880E4F"]),
  category("c2", "Podcasts", ["#009688", "#004D40"]),
  category("c3", "Live Events", ["#AB47BC", "#4A148C"]),
  category("c4", "Pop", ["#1E88E5", "#0D47A1"]),
  category("c5", "Trending", ["#FF7043", "#BF360C"]),
  category("c6", "Made For You", ["#5C6BC0", "#1A237E"]),
  category("c7", "New Releases", ["#26A69A", "#004D40"]),
  category("c8", "Indie", ["#9CCC65", "#33691E"])
];

const artists = [
  artist("a1", "Billie Eilish", "#455A64"),
  artist("a2", "Dua Lipa", "#C51162"),
  artist("a3", "Bruno Mars", "#FF7043"),
  artist("a4", "Taylor Swift", "#7E57C2")
];

const followedArtists = [
  artist("fa1", "Billie Eilish", "#455A64"),
  artist("fa2", "Harry Styles", "#5E35B1"),
  artist("fa3", "Taylor Swift", "#7E57C2"),
  artist("fa4", "The Weeknd", "#C51162"),
  artist("fa5", "Coldplay", "#00ACC1")
];

const podcastSongs = [
  song("p_ep1", "Song Exploder: How Pop Hooks Work", "Song Exploder", 1980, "#311B92"),
  song("p_ep2", "Switched on Pop: The Anatomy of a Hit", "Vox Media", 2400, "#1B5E20"),
  song("p_ep3", "The Journal: Streaming and Music Discovery", "The Wall Street Journal", 1800, "#263238"),
  song("p_ep4", "Decoder: The Future of Audio Platforms", "The Verge", 2640, "#4A148C"),
  song("p_ep5", "How I Built This: Creative Companies", "NPR", 3020, "#E65100"),
  song("p_ep6", "Twenty Thousand Hertz: Sound Design Stories", "Defacto Sound", 2100, "#00695C")
];

const basePlaylists = [
  playlist("p1", "Liked Songs", "You", true, "#512DA8", homeSongs.filter((item) => item.liked)),
  playlist("p2", "Friend Mix", "Spotify", false, "#00796B", [homeSongs[0], homeSongs[5], homeSongs[8], homeSongs[13]]),
  playlist("p3", "Weekend Favorites", "You", false, "#D32F2F", [homeSongs[2], homeSongs[6], homeSongs[12], homeSongs[19]]),
  playlist("p4", "Daily Mix 1", "Spotify", false, "#E64A19", [homeSongs[1], homeSongs[4], homeSongs[10], homeSongs[16]])
];

const state = {
  cards: chooseSessionCards(),
  selectedMood: null,
  currentSong: homeSongs[0],
  isPlaying: false,
  playbackProgress: 0,
  currentTab: "Home",
  homeFilter: "All",
  libraryFilter: "Playlists",
  searchQuery: "",
  playlists: [...basePlaylists],
  isCreateSheetOpen: false,
  isCreatePlaylistDialogOpen: false,
  isProfileOpen: false,
  isPlayerExpanded: false,
  hasPlayedFromCalibrated: false,
  exitCount: Number(sessionStorage.getItem("spotifyReplicaExitCount") || 0),
  prefersTile: sessionStorage.getItem("spotifyReplicaPrefersTile") === "true"
};

function song(id, title, artist, duration, color, liked = false) {
  return { id, title, artist, duration, color, liked };
}

function mood(id, title, fragment, accent, colors, memory, count, style) {
  return { id, title, fragment, accent, colors, memory, count, style };
}

function category(id, title, colors) {
  return { id, title, colors };
}

function artist(id, name, color) {
  return { id, name, color };
}

function playlist(id, name, creator, isLikedSongs, color, songs = []) {
  return { id, name, creator, isLikedSongs, color, songs };
}

function chooseSessionCards() {
  const sessionIndex = Number(localStorage.getItem("spotifyReplicaSessionIndex") || 0) + 1;
  localStorage.setItem("spotifyReplicaSessionIndex", String(sessionIndex));
  const previousIds = safeJson(localStorage.getItem("spotifyReplicaLastMoodCards"), []);
  const history = safeJson(localStorage.getItem("spotifyReplicaMoodHistory"), []);
  const validIds = new Set(moods.map((item) => item.id));
  let deck = safeJson(localStorage.getItem("spotifyReplicaMoodDeck"), []).filter((id) => validIds.has(id));

  if (deck.length < 3) {
    const remainingIds = new Set(deck);
    const refill = shuffledMoodIds(`${sessionIndex}:${Date.now()}:${Math.random()}`);
    const nonRepeatingRefill = refill.filter((id) => !previousIds.includes(id) && !remainingIds.has(id));
    const fallbackRefill = refill.filter((id) => !remainingIds.has(id));
    deck = [...deck, ...(nonRepeatingRefill.length + deck.length >= 3 ? nonRepeatingRefill : fallbackRefill)];
  }

  const selectedIds = deck.slice(0, 3);
  deck = deck.slice(3);
  localStorage.setItem("spotifyReplicaMoodDeck", JSON.stringify(deck));
  const pool = selectedIds.map((id) => moods.find((item) => item.id === id)).filter(Boolean);
  const hour = new Date().getHours();
  const timeBucket = hour >= 5 && hour <= 10 ? "morning"
    : hour >= 11 && hour <= 16 ? "day"
    : hour >= 17 && hour <= 20 ? "evening"
    : "late";
  const timeFit = {
    morning: ["ready", "joyful", "vast", "world12"],
    day: ["ready", "vast", "world7", "world9", "joyful"],
    evening: ["world10", "world12", "world14", "world8", "melancholic"],
    late: ["heavy", "electric", "world11", "world13", "world15"]
  }[timeBucket];
  const scored = pool.map((moodItem) => {
    const totalUses = history.filter((id) => id === moodItem.id).length;
    const timeBoost = timeFit.includes(moodItem.id) ? 1.1 : 0;
    const novelty = history.includes(moodItem.id) ? 0 : 0.28;
    const drift = Math.abs(hashCode(`${moodItem.id}:${sessionIndex}:${timeBucket}`) % 17) / 100;
    return { mood: moodItem, score: 1 + timeBoost + totalUses * 0.24 + novelty + drift };
  }).sort((a, b) => b.score - a.score);
  const center = scored[0].mood;
  const remaining = pool.filter((item) => item.id !== center.id);
  const second = remaining.sort((a, b) => moodDistance(center, b) - moodDistance(center, a))[0] || center;
  const third = remaining
    .filter((item) => item.id !== second.id)
    .sort((a, b) => (moodDistance(center, b) + moodDistance(second, b)) - (moodDistance(center, a) + moodDistance(second, a)))[0] || remaining[0] || center;
  const trio = sessionIndex % 2 === 0 ? [second, center, third] : [third, center, second];
  localStorage.setItem("spotifyReplicaLastMoodCards", JSON.stringify(trio.map((item) => item.id)));
  return trio;
}

function shuffledMoodIds(seed) {
  const list = moods.map((item) => item.id);
  for (let index = list.length - 1; index > 0; index -= 1) {
    const random = seededRandom(`${seed}:${index}`);
    const swapIndex = Math.floor(random * (index + 1));
    [list[index], list[swapIndex]] = [list[swapIndex], list[index]];
  }
  return list;
}

function seededRandom(seed) {
  let value = Math.abs(hashCode(seed)) || 1;
  value = (value ^ 0x6d2b79f5) >>> 0;
  value = Math.imul(value ^ (value >>> 15), value | 1);
  value ^= value + Math.imul(value ^ (value >>> 7), value | 61);
  return ((value ^ (value >>> 14)) >>> 0) / 4294967296;
}

function safeJson(value, fallback) {
  try {
    return value ? JSON.parse(value) : fallback;
  } catch {
    return fallback;
  }
}

function hashCode(value) {
  return String(value).split("").reduce((acc, char) => ((acc << 5) - acc + char.charCodeAt(0)) | 0, 0);
}

function moodDistance(a, b) {
  const pa = moodPoint(a.id);
  const pb = moodPoint(b.id);
  return Math.hypot(pa[0] - pb[0], pa[1] - pb[1]);
}

function moodPoint(id) {
  return ({
    heavy: [0.20, 0.25],
    ready: [0.78, 0.78],
    electric: [0.42, 0.88],
    joyful: [0.86, 0.58],
    melancholic: [0.16, 0.34],
    vast: [0.48, 0.30],
    world7: [0.30, 0.86],
    world8: [0.44, 0.46],
    world9: [0.64, 0.70],
    world10: [0.62, 0.18],
    world11: [0.74, 0.92],
    world12: [0.72, 0.36],
    world13: [0.22, 0.22],
    world14: [0.58, 0.32],
    world15: [0.36, 0.62]
  })[id] || [0.5, 0.5];
}

function render() {
  renderMoodCards();
  renderHome();
  renderSearch();
  renderLibrary();
  renderNav();
  renderMiniPlayer();
  renderOverlays();
}

function renderMoodCards() {
  const carousel = document.querySelector("#carousel");
  carousel.innerHTML = "";
  state.cards.forEach((card, index) => {
    const position = ["left", "center", "right"][index];
    const element = document.createElement("article");
    element.className = `mood-card ${position} style-${card.style}`;
    element.style.setProperty("--accent", card.accent);
    element.style.setProperty("--accent-soft", `${card.accent}55`);
    element.style.background = `linear-gradient(180deg, ${card.colors.join(", ")})`;
    element.innerHTML = moodCardTemplate(card);
    element.addEventListener("click", () => {
      if (suppressCardClick) return;
      if (index === 1) {
        enterMood(card);
      } else {
        cycleCards(index === 0 ? "left" : "right");
      }
    });
    element.querySelectorAll("[data-preview-song]").forEach((button) => {
      button.addEventListener("click", (event) => {
        event.stopPropagation();
        const selected = recommendations(card).find((item) => item.id === button.dataset.previewSong);
        enterMood(card, selected);
      });
    });
    carousel.appendChild(element);
  });
}

function moodCardTemplate(card) {
  const previews = recommendations(card).slice(0, 2).map((item) => `
    <button class="preview-song" type="button" data-preview-song="${item.id}">
      <span class="tiny-art" data-letter="${initialFor(item)}" style="--art:${item.color}"></span>
      <span>
        <strong>${escapeHtml(item.title)}</strong>
        <span>${escapeHtml(item.artist)}</span>
      </span>
    </button>
  `).join("");

  return `
    <div class="atmosphere" aria-hidden="true"></div>
    <div class="mood-top">
      <span class="mood-icon">${iconFor(card.id)}</span>
      <div class="preview-songs">${previews}</div>
    </div>
    <button class="tap-enter" type="button">Tap to enter</button>
    <div class="mood-bottom">
      <p class="fragment">${escapeHtml(card.fragment)}</p>
      <div class="accent-line"></div>
      <div class="memory-card">
        <span class="music-dot">&#9834;</span>
        <p>${escapeHtml(card.memory)}</p>
      </div>
      <p class="pulse">${card.count} people who hear music like you are here right now.</p>
    </div>
  `;
}

function renderHome() {
  const active = document.querySelector("#active-vibe");
  const moodFilterRow = document.querySelector(".mood-filter-row");
  const banner = document.querySelector("#resonance-banner");
  const compactTile = document.querySelector("#compact-tile");
  const rightNow = document.querySelector("#right-now");
  const homeFilters = document.querySelector(".home-filters");
  const filterNames = state.homeFilter === "Music" || state.homeFilter === "Following"
    ? ["All", "Music", "Following", "Podcasts"]
    : ["All", "Music", "Podcasts"];
  homeFilters.innerHTML = filterNames.map((filter) => `
    <button class="filter ${filter === state.homeFilter ? "is-active" : ""}" type="button" data-home-filter="${filter}">${filter}</button>
  `).join("");
  homeFilters.querySelectorAll("[data-home-filter]").forEach((button) => {
    button.addEventListener("click", () => {
      state.homeFilter = button.dataset.homeFilter;
      renderHome();
    });
  });

  if (state.selectedMood) {
    const card = state.selectedMood;
    moodFilterRow.classList.remove("is-hidden");
    active.classList.remove("is-hidden");
    active.style.setProperty("--vibe-accent", card.accent);
    active.innerHTML = `<span data-change-vibe>Vibe: ${escapeHtml(card.title)}</span> <button type="button" aria-label="Clear vibe" data-clear-vibe>x</button>`;
    active.querySelector("[data-clear-vibe]").addEventListener("click", clearMood);
    active.querySelector("[data-change-vibe]").addEventListener("click", () => showScreen("mood"));
    banner.classList.add("is-hidden");
    compactTile.classList.add("is-hidden");
    const showRightNow = state.homeFilter === "All" || state.homeFilter === "Music";
    rightNow.classList.toggle("is-hidden", !showRightNow);
    if (showRightNow) {
      const mark = document.querySelector("#right-now-icon");
      mark.style.color = card.accent;
      mark.innerHTML = iconFor(card.id);
      renderSongRow("#right-now-list", recommendations(card), true);
    }
  } else {
    moodFilterRow.classList.add("is-hidden");
    active.classList.add("is-hidden");
    active.style.removeProperty("--vibe-accent");
    rightNow.classList.add("is-hidden");
    banner.classList.toggle("is-hidden", state.prefersTile);
    compactTile.classList.toggle("is-hidden", !state.prefersTile);
  }

  renderHomeContentForFilter();
}

function renderHomeContentForFilter() {
  const primary = document.querySelector("#row-section-primary");
  const secondary = document.querySelector("#row-section-secondary");
  const tertiary = document.querySelector("#row-section-tertiary");
  const artistsSection = document.querySelector("#artist-section");
  [primary, secondary, tertiary].forEach((section) => section.classList.remove("is-hidden"));
  artistsSection.classList.add("is-hidden");

  if (state.homeFilter === "Podcasts") {
    renderRecentGrid(podcastSongs.slice(0, 6));
    setSection("#row-title-primary", "Recommended Podcasts");
    renderSongRow("#recommended-row", podcastSongs, false);
    setSection("#row-title-secondary", "Trending Shows");
    renderSongRow("#trending-row", [...podcastSongs].reverse(), false);
    setSection("#row-title-tertiary", "Tech & Business Stories");
    renderSongRow("#tertiary-row", podcastSongs.filter((item) => /Tech|Business|Startup|Streaming|Platforms/i.test(item.title + item.artist)), false);
    return;
  }

  if (state.homeFilter === "Following") {
    renderRecentGrid(homeSongs.slice(0, 8));
    artistsSection.classList.remove("is-hidden");
    document.querySelector("#artist-title").textContent = "Artists you follow";
    renderArtistCards("#artist-row", followedArtists);
    setSection("#row-title-primary", "Latest Releases from followed artists");
    renderSongRow("#recommended-row", homeSongs.slice(9, 13), false);
    setSection("#row-title-secondary", "Trending among other fans");
    renderSongRow("#trending-row", homeSongs.slice(0, 8), false);
    setSection("#row-title-tertiary", "Concerts & Showcases");
    renderSongRow("#tertiary-row", homeSongs.slice(13, 17), false);
    return;
  }

  renderRecentGrid(homeSongs.slice(0, 8));
  if (state.homeFilter === "All") {
    setSection("#row-title-primary", "Recommended for today");
    renderSongRow("#recommended-row", homeSongs.slice(0, 8), false);
    setSection("#row-title-secondary", "Popular Albums");
    renderSongRow("#trending-row", homeSongs.slice(3, 11), false);
    setSection("#row-title-tertiary", "Hot Hits");
    renderSongRow("#tertiary-row", homeSongs.slice(0, 8), false);
    artistsSection.classList.remove("is-hidden");
    document.querySelector("#artist-title").textContent = "Popular Artists";
    renderArtistCards("#artist-row", artists);
    return;
  }

  setSection("#row-title-primary", "New Releases");
  renderSongRow("#recommended-row", homeSongs.slice(9, 13), false);
  setSection("#row-title-secondary", "Trending Tracks");
  renderSongRow("#trending-row", homeSongs.slice(13, 17), false);
  setSection("#row-title-tertiary", "Acoustic & Chill");
  renderSongRow("#tertiary-row", moodSongs.world12.slice(0, 4), false);
  artistsSection.classList.remove("is-hidden");
  document.querySelector("#artist-title").textContent = "Featured Artists";
  renderArtistCards("#artist-row", artists);
}

function setSection(selector, text) {
  document.querySelector(selector).textContent = text;
}

function renderSearch() {
  const input = document.querySelector("#search-input");
  const clear = document.querySelector("#clear-search");
  const content = document.querySelector("#search-content");
  if (input.value !== state.searchQuery) input.value = state.searchQuery;
  clear.classList.toggle("is-hidden", !state.searchQuery);

  const query = state.searchQuery.trim().toLowerCase();
  if (query) {
    const results = allSongs().filter((item) =>
      item.title.toLowerCase().includes(query) || item.artist.toLowerCase().includes(query)
    );
    if (results.length === 0) {
      content.innerHTML = `
        <div class="empty-state">
          <b>No results found for "${escapeHtml(state.searchQuery)}"</b>
          <span>Please check spelling or try a different search.</span>
        </div>
      `;
      return;
    }
    content.innerHTML = `
      <h2 class="search-title">Songs and Artists</h2>
      <div class="result-list">
        ${results.slice(0, 12).map((item) => listSongRow(item, "Song")).join("")}
      </div>
    `;
    wireSongButtons(content, results);
    return;
  }

  const featured = homeSongs[4] || homeSongs[0];
  content.innerHTML = `
    <h2 class="search-title">Start browsing</h2>
    <div class="category-grid">
      ${categories.map((item) => `
        <button class="category-card" type="button" data-category="${item.title}" style="background:linear-gradient(135deg, ${item.colors.join(", ")})">
          ${escapeHtml(item.title)}
        </button>
      `).join("")}
    </div>
    <h2 class="search-title">Picked for you</h2>
    <article class="picked-card">
      <span class="album-art" data-letter="${initialFor(featured)}" style="--art:${featured.color}"></span>
      <div>
        <small>Album</small>
        <strong>${escapeHtml(featured.title)}</strong>
        <span>${escapeHtml(featured.artist)}</span>
        <div class="picked-actions">
          <button type="button" data-like-song="${featured.id}" aria-label="Like song">${featured.liked ? "&#9829;" : "&#9825;"}</button>
          <button type="button" data-song="${featured.id}" aria-label="Play">&#9654;</button>
        </div>
      </div>
    </article>
  `;
  content.querySelectorAll("[data-category]").forEach((button) => {
    button.addEventListener("click", () => {
      state.searchQuery = button.dataset.category || "";
      renderSearch();
    });
  });
  wireSongButtons(content, [featured]);
  wireLikeButtons(content);
}

function renderLibrary() {
  const filters = document.querySelector("#library-filters");
  const list = document.querySelector("#library-list");
  const libraryFilters = ["Playlists", "Podcasts", "Albums", "Artists"];
  filters.innerHTML = libraryFilters.map((filter) => `
    <button class="filter ${filter === state.libraryFilter ? "is-active" : ""}" type="button" data-library-filter="${filter}">
      ${filter}
    </button>
  `).join("");
  filters.querySelectorAll("[data-library-filter]").forEach((button) => {
    button.addEventListener("click", () => {
      state.libraryFilter = button.dataset.libraryFilter;
      renderLibrary();
    });
  });

  if (state.libraryFilter === "Playlists") {
    const likedCount = homeSongs.filter((item) => item.liked).length + allMoodSongs().filter((item) => item.liked).length;
    const custom = state.playlists.filter((item) => !item.isLikedSongs);
    list.innerHTML = `
      <div class="library-list">
        <button class="list-row" type="button" data-play-liked>
          <span class="library-art-liked">&#9829;</span>
          <span><strong>Liked Songs</strong><span>Playlist - ${likedCount} songs</span></span>
          <em>pin</em>
        </button>
        ${custom.map((item) => playlistRow(item, "Playlist")).join("")}
      </div>
    `;
    list.querySelector("[data-play-liked]")?.addEventListener("click", () => {
      const liked = allSongs().find((item) => item.liked);
      if (liked) playSong(liked);
    });
    list.querySelectorAll("[data-playlist]").forEach((button) => {
      button.addEventListener("click", () => {
        const found = state.playlists.find((item) => item.id === button.dataset.playlist);
        if (found?.songs?.length) playSong(found.songs[0]);
      });
    });
    return;
  }

  if (state.libraryFilter === "Podcasts") {
    list.innerHTML = `
      <div class="library-list">
        <button class="list-row" type="button">
          <span class="library-art-episodes">+</span>
          <span><strong>New Episodes</strong><span>Updated today</span></span>
          <em></em>
        </button>
      </div>
    `;
    return;
  }

  if (state.libraryFilter === "Albums") {
    list.innerHTML = `<div class="library-list">${state.playlists.slice(1, 3).map((item) => playlistRow(item, "Album")).join("")}</div>`;
    return;
  }

  list.innerHTML = `<div class="library-list">${artists.map((item) => artistRow(item)).join("")}</div>`;
}

function renderNav() {
  document.querySelectorAll("[data-tab]").forEach((button) => {
    const tab = button.dataset.tab;
    button.classList.toggle("is-active", tab === state.currentTab);
  });
}

function listSongRow(item, type) {
  return `
    <button class="list-row" type="button" data-song="${item.id}">
      <span class="album-art" data-letter="${initialFor(item)}" style="--art:${item.color}"></span>
      <span><strong>${escapeHtml(item.title)}</strong><span>${type} - ${escapeHtml(item.artist)}</span></span>
      <em>⋮</em>
    </button>
  `;
}

function playlistRow(item, type) {
  return `
    <button class="list-row" type="button" data-playlist="${item.id}">
      <span class="album-art" data-letter="${initialFor({ title: item.name })}" style="--art:${item.color}"></span>
      <span><strong>${escapeHtml(item.name)}</strong><span>${type} - ${escapeHtml(item.creator)}</span></span>
      <em></em>
    </button>
  `;
}

function artistRow(item) {
  return `
    <button class="list-row" type="button">
      <span class="album-art circle" data-letter="${initialFor({ title: item.name })}" style="--art:${item.color}"></span>
      <span><strong>${escapeHtml(item.name)}</strong><span>Artist</span></span>
      <em></em>
    </button>
  `;
}

function renderRecentGrid(items = homeSongs.slice(0, 8)) {
  const grid = document.querySelector("#recent-grid");
  grid.innerHTML = items.map((item) => `
    <button class="recent-card" type="button" data-song="${item.id}">
      <span class="album-art" data-letter="${initialFor(item)}" style="--art:${item.color}"></span>
      <strong>${escapeHtml(item.title)}</strong>
    </button>
  `).join("");
  wireSongButtons(grid, items);
}

function renderSongRow(selector, list, calibrated) {
  const row = document.querySelector(selector);
  row.innerHTML = list.map((item) => `
    <button class="song-card" type="button" data-song="${item.id}" data-calibrated="${calibrated}">
      <span class="album-art" data-letter="${initialFor(item)}" style="--art:${item.color}"></span>
      <strong>${escapeHtml(item.title)}</strong>
      <span>${escapeHtml(item.artist)}</span>
    </button>
  `).join("");
  wireSongButtons(row, list);
}

function wireSongButtons(root, list) {
  root.querySelectorAll("[data-song]").forEach((button) => {
    button.addEventListener("click", () => {
      const item = list.find((candidate) => candidate.id === button.dataset.song);
      if (item) {
        if (button.dataset.calibrated === "true") {
          state.hasPlayedFromCalibrated = true;
        }
        playSong(item);
      }
    });
  });
}

function renderArtistCards(selector, list) {
  const row = document.querySelector(selector);
  row.innerHTML = list.map((item) => `
    <button class="artist-card" type="button">
      <span class="album-art circle" data-letter="${initialFor({ title: item.name })}" style="--art:${item.color}"></span>
      <strong>${escapeHtml(item.name)}</strong>
      <span>Artist</span>
    </button>
  `).join("");
}

function wireLikeButtons(root) {
  root.querySelectorAll("[data-like-song]").forEach((button) => {
    button.addEventListener("click", (event) => {
      event.stopPropagation();
      toggleLikeSong(button.dataset.likeSong);
    });
  });
}

function enterMood(card, autoplaySong = null) {
  state.selectedMood = card;
  state.exitCount = 0;
  state.prefersTile = false;
  state.currentTab = "Home";
  state.homeFilter = "Music";
  state.hasPlayedFromCalibrated = false;
  const history = safeJson(localStorage.getItem("spotifyReplicaMoodHistory"), []);
  history.push(card.id);
  localStorage.setItem("spotifyReplicaMoodHistory", JSON.stringify(history.slice(-20)));
  persistExitState();
  renderHome();
  renderNav();
  showScreen("home");
  if (autoplaySong) {
    state.hasPlayedFromCalibrated = true;
    playSong(autoplaySong);
  }
}

function clearMood() {
  state.selectedMood = null;
  state.hasPlayedFromCalibrated = false;
  renderHome();
}

function exitMoodBoard() {
  state.exitCount += 1;
  if (state.exitCount >= 5) {
    state.prefersTile = true;
  }
  persistExitState();
  showScreen("home");
  renderHome();
}

function persistExitState() {
  sessionStorage.setItem("spotifyReplicaExitCount", String(state.exitCount));
  sessionStorage.setItem("spotifyReplicaPrefersTile", String(state.prefersTile));
}

function playSong(item) {
  state.currentSong = item;
  state.isPlaying = true;
  state.playbackProgress = 0;
  renderMiniPlayer();
  renderOverlays();
}

function renderMiniPlayer() {
  document.querySelector("#mini-title").textContent = state.currentSong.title;
  document.querySelector("#mini-artist").textContent = `ZLRWIN-JXV7FG3 - ${state.currentSong.artist}`;
  document.querySelector("#mini-art").style.setProperty("--art", state.currentSong.color);
  document.querySelector("#mini-art").dataset.letter = initialFor(state.currentSong);
  document.querySelector("#play-toggle").innerHTML = state.isPlaying ? "&#10074;&#10074;" : "&#9654;";
  const likeButton = document.querySelector(".mini-action[aria-label='Like song']");
  if (likeButton) likeButton.innerHTML = state.currentSong.liked ? "&#9829;" : "&#9825;";
}

function renderOverlays() {
  renderFullPlayer();
  renderCreateSheet();
  renderCreateDialog();
  renderProfileDrawer();
}

function renderFullPlayer() {
  const player = document.querySelector("#full-player");
  player.classList.toggle("is-hidden", !state.isPlayerExpanded);
  if (!state.isPlayerExpanded) return;
  const song = state.currentSong;
  const progress = song.duration > 0 ? Math.round((state.playbackProgress / song.duration) * 100) : 0;
  player.style.setProperty("--art", song.color);
  player.innerHTML = `
    <div class="player-top">
      <button type="button" data-close-player aria-label="Collapse Player">v</button>
      <div class="player-source"><small>PLAYING FROM PLAYLIST</small><b>Liked Songs</b></div>
      <button type="button" aria-label="Options">⋮</button>
    </div>
    <div class="album-art full-art" data-letter="${initialFor(song)}" style="--art:${song.color}"></div>
    <div class="player-meta">
      <span><strong>${escapeHtml(song.title)}</strong><span>${escapeHtml(song.artist)}</span></span>
      <button type="button" data-like-song="${song.id}" aria-label="Like Song">${song.liked ? "&#9829;" : "&#9825;"}</button>
    </div>
    <div class="player-progress" style="--progress:${progress}%"><span></span></div>
    <div class="player-times"><span>${formatTime(state.playbackProgress)}</span><span>${formatTime(song.duration)}</span></div>
    <div class="player-controls">
      <button type="button" aria-label="Shuffle">&#8644;</button>
      <button type="button" data-prev-song aria-label="Previous Song">&#9198;</button>
      <button class="primary" type="button" data-toggle-player aria-label="Play or pause">${state.isPlaying ? "&#10074;&#10074;" : "&#9654;"}</button>
      <button type="button" data-next-song aria-label="Next Song">&#9197;</button>
      <button type="button" aria-label="Repeat">&#8635;</button>
    </div>
    <div class="player-utils">
      <button type="button" aria-label="Available Devices">&#9637;</button>
      <button type="button" aria-label="Share">&#8599;</button>
      <button type="button" aria-label="Sleep Timer">&#9719;</button>
      <button type="button" aria-label="Lyrics">&#8801;</button>
    </div>
    <section class="lyrics-card">
      <h2>Lyrics <span>MORE</span></h2>
      <p>And I feel like playing this track forever...<br>Yeah, the rhythm goes down my spine...</p>
    </section>
  `;
  player.querySelector("[data-close-player]").addEventListener("click", () => {
    state.isPlayerExpanded = false;
    renderOverlays();
  });
  player.querySelector("[data-toggle-player]").addEventListener("click", togglePlayback);
  player.querySelector("[data-next-song]").addEventListener("click", playNextSong);
  player.querySelector("[data-prev-song]").addEventListener("click", playPreviousSong);
  wireLikeButtons(player);
}

function renderCreateSheet() {
  const sheet = document.querySelector("#create-sheet");
  sheet.classList.toggle("is-hidden", !state.isCreateSheetOpen);
  if (!state.isCreateSheetOpen) return;
  sheet.innerHTML = `
    <div class="create-panel">
      <h2>Create</h2>
      ${createOption("♪", "Playlist", "Create a playlist with songs or episodes", "data-open-create-dialog")}
      ${createOption("++", "Collaborative playlist", "Create a playlist together with friends", "data-close-create")}
      ${createOption("◌", "Blend", "Combine your friends' tastes into a playlist", "data-close-create")}
      ${createOption("▰", "Jam", "Listen together from anywhere", "data-close-create")}
      <button class="sheet-close" type="button" data-close-create aria-label="Close">x</button>
    </div>
  `;
  sheet.addEventListener("click", closeSheetFromBackdrop, { once: true });
  sheet.querySelectorAll("[data-close-create]").forEach((button) => button.addEventListener("click", closeCreateSheet));
  sheet.querySelector("[data-open-create-dialog]").addEventListener("click", () => {
    state.isCreateSheetOpen = false;
    state.isCreatePlaylistDialogOpen = true;
    renderOverlays();
  });
}

function createOption(icon, title, description, attr) {
  return `
    <button class="create-option" type="button" ${attr}>
      <span class="create-icon">${icon}</span>
      <span><strong>${title}</strong><span>${description}</span></span>
    </button>
  `;
}

function renderCreateDialog() {
  const dialog = document.querySelector("#create-dialog");
  dialog.classList.toggle("is-hidden", !state.isCreatePlaylistDialogOpen);
  if (!state.isCreatePlaylistDialogOpen) return;
  dialog.innerHTML = `
    <div class="dialog-card">
      <h2>Give your playlist a name</h2>
      <input id="playlist-name" type="text" placeholder="My Playlist #1" autocomplete="off" />
      <div class="dialog-actions">
        <button type="button" data-cancel-playlist>CANCEL</button>
        <button class="confirm" type="button" data-confirm-playlist disabled>CREATE</button>
      </div>
    </div>
  `;
  const input = dialog.querySelector("#playlist-name");
  const confirm = dialog.querySelector("[data-confirm-playlist]");
  input.addEventListener("input", () => {
    confirm.disabled = !input.value.trim();
  });
  dialog.querySelector("[data-cancel-playlist]").addEventListener("click", closeCreateDialog);
  confirm.addEventListener("click", () => createPlaylist(input.value.trim()));
}

function renderProfileDrawer() {
  const layer = document.querySelector("#profile-drawer");
  layer.classList.toggle("is-hidden", !state.isProfileOpen);
  if (!state.isProfileOpen) return;
  layer.innerHTML = `
    <aside class="profile-panel">
      <div class="profile-head">
        <span class="avatar">U</span>
        <span><strong>User</strong><span>View profile</span></span>
      </div>
      <div class="profile-menu">
        <button type="button" data-close-profile><span>+</span>Add account</button>
        <button type="button" data-close-profile><span>&#9679;</span>Your Premium <span class="profile-badge">Standard</span></button>
        <button type="button" data-close-profile><span>&#9637;</span>Your Sound Capsule</button>
        <button type="button" data-close-profile><span>&#9719;</span>Recents</button>
        <button type="button" data-close-profile><span>&#9834;</span>Your Updates</button>
        <button type="button" data-close-profile><span>&#9881;</span>Settings and privacy</button>
      </div>
    </aside>
  `;
  layer.addEventListener("click", (event) => {
    if (event.target === layer) closeProfile();
  }, { once: true });
  layer.querySelectorAll("[data-close-profile]").forEach((button) => button.addEventListener("click", closeProfile));
}

function cycleCards(direction) {
  state.cards = direction === "right"
    ? [state.cards[1], state.cards[2], state.cards[0]]
    : [state.cards[2], state.cards[0], state.cards[1]];
  renderMoodCards();
}

function showScreen(name) {
  const tabName = name === "home" ? state.currentTab : name;
  document.querySelector("#mood-board").classList.toggle("is-active", name === "mood");
  document.querySelector("#home-screen").classList.toggle("is-active", name !== "mood" && tabName === "Home");
  document.querySelector("#search-screen").classList.toggle("is-active", name !== "mood" && tabName === "Search");
  document.querySelector("#library-screen").classList.toggle("is-active", name !== "mood" && tabName === "Your Library");
  document.querySelector(".phone-shell").classList.toggle("is-mood", name === "mood");
  renderNav();
}

function recommendations(card) {
  return moodSongs[card.id] || homeSongs;
}

function allMoodSongs() {
  return Object.values(moodSongs).flat();
}

function allSongs() {
  return [...homeSongs, ...allMoodSongs(), ...podcastSongs];
}

function togglePlayback() {
  state.isPlaying = !state.isPlaying;
  renderMiniPlayer();
  renderOverlays();
}

function toggleLikeSong(songId) {
  const song = allSongs().find((item) => item.id === songId);
  if (!song) return;
  song.liked = !song.liked;
  const liked = allSongs().filter((item) => item.liked);
  const likedPlaylist = state.playlists.find((item) => item.isLikedSongs);
  if (likedPlaylist) likedPlaylist.songs = liked;
  renderHome();
  renderSearch();
  renderLibrary();
  renderMiniPlayer();
  renderOverlays();
}

function playNextSong() {
  const list = allSongs();
  const index = list.findIndex((item) => item.id === state.currentSong.id);
  playSong(list[(index + 1 + list.length) % list.length]);
}

function playPreviousSong() {
  const list = allSongs();
  const index = list.findIndex((item) => item.id === state.currentSong.id);
  playSong(list[(index - 1 + list.length) % list.length]);
}

function closeCreateSheet() {
  state.isCreateSheetOpen = false;
  renderOverlays();
}

function closeCreateDialog() {
  state.isCreatePlaylistDialogOpen = false;
  renderOverlays();
}

function closeProfile() {
  state.isProfileOpen = false;
  renderOverlays();
}

function closeSheetFromBackdrop(event) {
  if (event.target?.id === "create-sheet") closeCreateSheet();
}

function createPlaylist(name) {
  if (!name) return;
  const created = playlist(`p_${Date.now()}`, name, "You", false, randomColor(), []);
  state.playlists = [created, ...state.playlists];
  state.isCreatePlaylistDialogOpen = false;
  state.currentTab = "Your Library";
  state.libraryFilter = "Playlists";
  renderLibrary();
  renderNav();
  renderOverlays();
  showScreen("home");
}

function randomColor() {
  const colors = ["#1E88E5", "#7E57C2", "#D81B60", "#00897B", "#FF7043", "#5E35B1"];
  return colors[Math.floor(Math.random() * colors.length)];
}

function formatTime(seconds) {
  const safe = Math.max(0, Math.floor(seconds));
  const mins = Math.floor(safe / 60);
  const secs = String(safe % 60).padStart(2, "0");
  return `${mins}:${secs}`;
}

function iconFor(style) {
  const aliases = {
    heavy: "heavy",
    ready: "ready",
    electric: "electric",
    joyful: "joyful",
    melancholic: "melancholic",
    vast: "vast",
    storm: "world7",
    gold: "world8",
    highway: "world9",
    still: "world10",
    electric_blue: "world11",
    kitchen: "world12",
    winter: "world13",
    dusk: "world14",
    break: "world15"
  };
  const paths = {
    heavy: ["M29,12 a11,11 0,1 0,0 20 a8.5,8.5 0,1 1,0 -20"],
    ready: ["M22,21 m-5,0 a5,5 0,1 0,10 0 a5,5 0,1 0,-10 0", "M22,11 V8", "M13.5,17 L11.5,15", "M30.5,17 L32.5,15", "M9,31 q4,-3 8,0 t8,0 t8,0"],
    electric: ["M10,22 H15 V33 H10 Z", "M16,16 H21 V33 H16 Z", "M22,26 H27 V33 H22 Z", "M28,20 H33 V33 H28 Z", "M32,12 m-1.6,0 a1.6,1.6 0,1 0,3.2 0 a1.6,1.6 0,1 0,-3.2 0"],
    joyful: ["M22,33 V19", "M22,24 C17,24 14,21 13,16 C18.5,16 22,19 22,24 Z", "M22,21 C27,21 30,18 31,13 C25.5,13 22,16 22,21 Z"],
    melancholic: ["M9,18 q4,-3 8,0 t8,0 t8,0", "M9,24 q4,-3 8,0 t8,0 t8,0", "M9,30 q4,-3 8,0 t8,0 t8,0"],
    vast: ["M16,12 L10.5,23 L21.5,23 Z", "M16,23 V26", "M28,15 L23.5,24 L32.5,24 Z", "M28,24 V27", "M10,30 H20", "M25,32 H34"],
    world7: ["M13,21 a4,4 0,0 1,4 -5 a5.5,5.5 0,0 1,10 1 a3.5,3.5 0,0 1,0 7 H16 a3.5,3.5 0,0 1,-3 -3 z", "M22,25 L18.5,31 H21.5 L19,35"],
    world8: ["M14,11 H30", "M14,33 H30", "M16,11 L22,22 L16,33", "M28,11 L22,22 L28,33"],
    world9: ["M13,33 L20.5,15", "M31,33 L23.5,15", "M22,32 v-3", "M22,26 v-3", "M22,20 v-2"],
    world10: ["M9,23 H35", "M22,17 m-4,0 a4,4 0,1 0,8 0 a4,4 0,1 0,-8 0", "M19,27 H25", "M20,30 H24"],
    world11: ["M14,32 V25", "M19,32 V16", "M24,32 V21", "M29,32 V14"],
    world12: ["M14,22 H25 V29 a3,3 0,0 1,-3 3 H17 a3,3 0,0 1,-3 -3 Z", "M25,24 a3.5,3.5 0,0 1,0 6", "M18,18 q2,-2 0,-4", "M22,18 q2,-2 0,-4"],
    world13: ["M13,32 L29,14", "M21,23 L25,22", "M19,25 L16,22", "M24,20 L27,21", "M22.5,21.5 L21.5,17.5"],
    world14: ["M8,28 H36", "M14,28 A8,8 0,0 1,30 28", "M22,15 V12", "M15,19 L13,17", "M29,19 L31,17"],
    world15: ["M22,22 m-3,0 a3,3 0,1 0,6 0 a3,3 0,1 0,-6 0", "M22,15 V11", "M22,29 V33", "M15,22 H11", "M29,22 H33", "M17,17 L14.5,14.5", "M27,17 L29.5,14.5", "M17,27 L14.5,29.5", "M27,27 L29.5,29.5"]
  };
  const key = aliases[style] || style;
  const iconPaths = paths[key] || paths.ready;
  return `<svg viewBox="0 0 44 44" aria-hidden="true">${iconPaths.map((path) => `<path d="${path}"/>`).join("")}</svg>`;
}

function initialFor(item) {
  return item.title.trim().charAt(0).toUpperCase();
}

function escapeHtml(value) {
  return String(value).replace(/[&<>"']/g, (char) => ({
    "&": "&amp;",
    "<": "&lt;",
    ">": "&gt;",
    '"': "&quot;",
    "'": "&#39;"
  })[char]);
}

document.querySelector("[data-cycle-left]").addEventListener("click", () => cycleCards("left"));
document.querySelector("[data-cycle-right]").addEventListener("click", () => cycleCards("right"));
document.querySelector("[data-go-home]").addEventListener("click", exitMoodBoard);
document.querySelectorAll("[data-open-mood]").forEach((button) => {
  button.addEventListener("click", () => showScreen("mood"));
});
document.querySelectorAll("[data-tab]").forEach((button) => {
  button.addEventListener("click", () => {
    const tab = button.dataset.tab;
    if (tab === "Create") {
      state.isCreateSheetOpen = true;
      renderOverlays();
      return;
    }
    state.currentTab = tab;
    showScreen("home");
  });
});
document.querySelectorAll("[data-tab-shortcut]").forEach((button) => {
  button.addEventListener("click", () => {
    state.currentTab = button.dataset.tabShortcut;
    showScreen("home");
  });
});
document.querySelectorAll("[data-open-profile]").forEach((button) => {
  button.addEventListener("click", () => {
    state.isProfileOpen = true;
    renderOverlays();
  });
});
document.querySelectorAll("[data-open-create-dialog]").forEach((button) => {
  button.addEventListener("click", () => {
    state.isCreatePlaylistDialogOpen = true;
    renderOverlays();
  });
});
document.querySelector("#search-input").addEventListener("input", (event) => {
  state.searchQuery = event.target.value;
  renderSearch();
});
document.querySelector("#clear-search").addEventListener("click", () => {
  state.searchQuery = "";
  renderSearch();
});
document.querySelectorAll(".home-filters .filter").forEach((button) => {
  button.addEventListener("click", () => {
    state.homeFilter = button.textContent.trim();
    renderHome();
  });
});
document.querySelector("#mini-player").addEventListener("click", (event) => {
  if (event.target.closest("button")) return;
  state.isPlayerExpanded = true;
  renderOverlays();
});
document.querySelector(".mini-action[aria-label='Like song']").addEventListener("click", (event) => {
  event.stopPropagation();
  toggleLikeSong(state.currentSong.id);
});
document.querySelector("#play-toggle").addEventListener("click", (event) => {
  event.stopPropagation();
  togglePlayback();
});

let startX = 0;
let startY = 0;
let dragX = 0;
let suppressCardClick = false;
document.querySelector("#carousel").addEventListener("pointerdown", (event) => {
  startX = event.clientX;
  startY = event.clientY;
  dragX = 0;
});
document.querySelector("#carousel").addEventListener("pointermove", (event) => {
  dragX = event.clientX - startX;
});
document.querySelector("#carousel").addEventListener("pointerup", (event) => {
  const delta = event.clientX - startX;
  const vertical = Math.abs(event.clientY - startY);
  if (Math.abs(delta) > 56 && Math.abs(delta) > vertical) {
    suppressCardClick = true;
    cycleCards(delta > 0 ? "right" : "left");
    setTimeout(() => {
      suppressCardClick = false;
    }, 80);
  }
});

render();
showScreen("mood");
