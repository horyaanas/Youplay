package com.you.play.anas.data.repository

import com.you.play.anas.data.model.Channel
import com.you.play.anas.data.model.Comment
import com.you.play.anas.data.model.Video
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import java.util.regex.Pattern

class YouTubeRepository {

    // Initial real YouTube videos from top global creators & topics
    private val initialVideos = listOf(
        Video(
            id = "dQw4w9WgXcQ",
            title = "Rick Astley - Never Gonna Give You Up (Official Music Video)",
            description = "The official video for “Never Gonna Give You Up” by Rick Astley. Remastered in 4K resolution! Enjoy this legendary iconic music track.",
            channelId = "c_rickastley",
            channelTitle = "Rick Astley",
            channelAvatarUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=150&auto=format&fit=crop&q=80",
            subscriberCount = "3.85M",
            viewCount = "1.6B views",
            publishedTime = "14 years ago",
            duration = "3:33",
            thumbnailUrl = "https://img.youtube.com/vi/dQw4w9WgXcQ/hqdefault.jpg",
            category = "Music",
            likeCount = 17500000L
        ),
        Video(
            id = "kJQP7kiw5Fk",
            title = "Luis Fonsi - Despacito ft. Daddy Yankee",
            description = "Despacito disponible ya en todas las plataformas digitales. Official video smash hit record breaker.",
            channelId = "c_luisfonsi",
            channelTitle = "Luis Fonsi",
            channelAvatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150&auto=format&fit=crop&q=80",
            subscriberCount = "31.2M",
            viewCount = "8.4B views",
            publishedTime = "7 years ago",
            duration = "4:42",
            thumbnailUrl = "https://img.youtube.com/vi/kJQP7kiw5Fk/hqdefault.jpg",
            category = "Music",
            likeCount = 52000000L
        ),
        Video(
            id = "jNQXAC9IVRw",
            title = "Me at the zoo - The First Video on YouTube",
            description = "The first video on YouTube, uploaded at 8:27PM on Saturday April 23rd, 2005. The video was shot by Yakov Lapitsky at the San Diego Zoo.",
            channelId = "c_jawed",
            channelTitle = "jawed",
            channelAvatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150&auto=format&fit=crop&q=80",
            subscriberCount = "4.2M",
            viewCount = "330M views",
            publishedTime = "19 years ago",
            duration = "0:19",
            thumbnailUrl = "https://img.youtube.com/vi/jNQXAC9IVRw/hqdefault.jpg",
            category = "History",
            likeCount = 16000000L
        ),
        Video(
            id = "M7lc1UVf-VE",
            title = "YouTube Developers Live - The IFrame Player API",
            description = "Learn how to embed YouTube video players using the official IFrame Player API in your applications with seamless playback and control.",
            channelId = "c_googledevelopers",
            channelTitle = "Google for Developers",
            channelAvatarUrl = "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=150&auto=format&fit=crop&q=80",
            subscriberCount = "2.4M",
            viewCount = "1.8M views",
            publishedTime = "3 years ago",
            duration = "10:15",
            thumbnailUrl = "https://img.youtube.com/vi/M7lc1UVf-VE/hqdefault.jpg",
            category = "Tech",
            likeCount = 48000L
        ),
        Video(
            id = "9bZkp7q19f0",
            title = "PSY - GANGNAM STYLE (강남스타일) M/V",
            description = "PSY - 'GANGNAM STYLE' M/V. The first video to ever surpass 1 billion views on YouTube. Historical pop culture phenomenon.",
            channelId = "c_officialpsy",
            channelTitle = "officialpsy",
            channelAvatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&auto=format&fit=crop&q=80",
            subscriberCount = "18.5M",
            viewCount = "5.2B views",
            publishedTime = "11 years ago",
            duration = "4:13",
            thumbnailUrl = "https://img.youtube.com/vi/9bZkp7q19f0/hqdefault.jpg",
            category = "Music",
            likeCount = 28000000L
        ),
        Video(
            id = "fJ9rUzIMcZQ",
            title = "Queen - Bohemian Rhapsody (Official Video Remastered)",
            description = "Taken from A Night At The Opera, 1975. Bohemian Rhapsody is arguably Queen's most famous song.",
            channelId = "c_queenofficial",
            channelTitle = "Queen Official",
            channelAvatarUrl = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=150&auto=format&fit=crop&q=80",
            subscriberCount = "17.9M",
            viewCount = "1.7B views",
            publishedTime = "15 years ago",
            duration = "5:59",
            thumbnailUrl = "https://img.youtube.com/vi/fJ9rUzIMcZQ/hqdefault.jpg",
            category = "Music",
            likeCount = 12000000L
        ),
        Video(
            id = "EngW7tLk6R8",
            title = "Building Modern Android Apps with Jetpack Compose",
            description = "Complete masterclass in building responsive, modern user interfaces using Jetpack Compose, Material 3, and Kotlin architecture.",
            channelId = "c_androiddevs",
            channelTitle = "Android Developers",
            channelAvatarUrl = "https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?w=150&auto=format&fit=crop&q=80",
            subscriberCount = "1.2M",
            viewCount = "450K views",
            publishedTime = "1 year ago",
            duration = "22:45",
            thumbnailUrl = "https://img.youtube.com/vi/EngW7tLk6R8/hqdefault.jpg",
            category = "Tech",
            likeCount = 35000L
        ),
        Video(
            id = "aqz-KE-bpKQ",
            title = "Big Buck Bunny 4K 60FPS HDR - High Bitrate Cinematic Showcase",
            description = "Watch the legendary open-source animated movie by the Blender Foundation rendered in ultra-crisp 4K 60FPS HDR.",
            channelId = "c_blender",
            channelTitle = "Blender Animation Studio",
            channelAvatarUrl = "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=150&auto=format&fit=crop&q=80",
            subscriberCount = "1.1M",
            viewCount = "38M views",
            publishedTime = "4 years ago",
            duration = "10:34",
            thumbnailUrl = "https://img.youtube.com/vi/aqz-KE-bpKQ/hqdefault.jpg",
            category = "Animation",
            likeCount = 280000L
        ),
        Video(
            id = "R9rD_7s-4oE",
            title = "Unreal Engine 5.4 - Next Gen Photorealistic Graphics Showcase",
            description = "Explore real-time lumen illumination, nanite geometry, and photorealistic environments powered by Unreal Engine 5.",
            channelId = "c_unrealengine",
            channelTitle = "Unreal Engine",
            channelAvatarUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=150&auto=format&fit=crop&q=80",
            subscriberCount = "980K",
            viewCount = "2.1M views",
            publishedTime = "6 months ago",
            duration = "8:20",
            thumbnailUrl = "https://img.youtube.com/vi/R9rD_7s-4oE/hqdefault.jpg",
            category = "Gaming",
            likeCount = 140000L
        ),
        Video(
            id = "L_LUpnjgPso",
            title = "Tomorrowland Belgium - Official Aftermovie",
            description = "Relive the magic, energy, and spectacle of Tomorrowland with the world's greatest DJs and festival atmosphere.",
            channelId = "c_tomorrowland",
            channelTitle = "Tomorrowland",
            channelAvatarUrl = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=150&auto=format&fit=crop&q=80",
            subscriberCount = "10.8M",
            viewCount = "180M views",
            publishedTime = "8 years ago",
            duration = "14:10",
            thumbnailUrl = "https://img.youtube.com/vi/L_LUpnjgPso/hqdefault.jpg",
            category = "Music",
            likeCount = 1500000L
        )
    )

    // Real YouTube Shorts catalog
    private val initialShorts = listOf(
        Video(
            id = "g_Ta11eF3L4",
            title = "Fastest Rubik's Cube Solve in History! 🤯 #Shorts",
            description = "World record speedcubing solve in under 4 seconds! #shorts #cube #speedrun",
            channelId = "c_cubing",
            channelTitle = "SpeedCubing Pro",
            channelAvatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150&auto=format&fit=crop&q=80",
            subscriberCount = "850K",
            viewCount = "14M views",
            publishedTime = "2 months ago",
            duration = "0:30",
            thumbnailUrl = "https://img.youtube.com/vi/g_Ta11eF3L4/hqdefault.jpg",
            category = "Shorts",
            isShort = true,
            likeCount = 820000L
        ),
        Video(
            id = "91Z_WvWd4_c",
            title = "Incredible Street Magic Mind Trick! 🔮 #Shorts",
            description = "He couldn't believe what happened with the card! #magic #shorts #illusion",
            channelId = "c_magic",
            channelTitle = "Urban Illusions",
            channelAvatarUrl = "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?w=150&auto=format&fit=crop&q=80",
            subscriberCount = "1.5M",
            viewCount = "28M views",
            publishedTime = "1 month ago",
            duration = "0:45",
            thumbnailUrl = "https://img.youtube.com/vi/91Z_WvWd4_c/hqdefault.jpg",
            category = "Shorts",
            isShort = true,
            likeCount = 1400000L
        ),
        Video(
            id = "kJQP7kiw5Fk",
            title = "Super Clean Drift in Tokyo Streets! 🏎️💨 #Shorts",
            description = "Insane car control and precision drifting! #cars #drift #tokyo",
            channelId = "c_drift",
            channelTitle = "Tokyo Drift Club",
            channelAvatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&auto=format&fit=crop&q=80",
            subscriberCount = "620K",
            viewCount = "8.9M views",
            publishedTime = "3 weeks ago",
            duration = "0:25",
            thumbnailUrl = "https://img.youtube.com/vi/kJQP7kiw5Fk/hqdefault.jpg",
            category = "Shorts",
            isShort = true,
            likeCount = 650000L
        )
    )

    private val _videos = MutableStateFlow(initialVideos)
    val videos: StateFlow<List<Video>> = _videos.asStateFlow()

    private val _shorts = MutableStateFlow(initialShorts)
    val shorts: StateFlow<List<Video>> = _shorts.asStateFlow()

    private val _subscribedChannels = MutableStateFlow<Set<String>>(
        setOf("c_googledevelopers", "c_androiddevs")
    )
    val subscribedChannels: StateFlow<Set<String>> = _subscribedChannels.asStateFlow()

    private val _likedVideoIds = MutableStateFlow<Set<String>>(setOf("dQw4w9WgXcQ"))
    val likedVideoIds: StateFlow<Set<String>> = _likedVideoIds.asStateFlow()

    private val _dislikedVideoIds = MutableStateFlow<Set<String>>(emptySet())
    val dislikedVideoIds: StateFlow<Set<String>> = _dislikedVideoIds.asStateFlow()

    private val _watchLaterVideoIds = MutableStateFlow<Set<String>>(setOf("M7lc1UVf-VE", "EngW7tLk6R8"))
    val watchLaterVideoIds: StateFlow<Set<String>> = _watchLaterVideoIds.asStateFlow()

    private val _watchHistory = MutableStateFlow<List<Video>>(listOf(initialVideos[0], initialVideos[1]))
    val watchHistory: StateFlow<List<Video>> = _watchHistory.asStateFlow()

    private val _comments = MutableStateFlow<Map<String, List<Comment>>>(
        mapOf(
            "dQw4w9WgXcQ" to listOf(
                Comment(UUID.randomUUID().toString(), "dQw4w9WgXcQ", "Sarah Connor", "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=100", "Still listening in 2026 and it's pure masterpiece! 🔥", "2 days ago", 342, false),
                Comment(UUID.randomUUID().toString(), "dQw4w9WgXcQ", "Alex Mercer", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=100", "You can never get tired of this song. True legend.", "1 week ago", 129, false),
                Comment(UUID.randomUUID().toString(), "dQw4w9WgXcQ", "CodingNinja", "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100", "The greatest video in YouTube history without question.", "3 weeks ago", 87, false)
            ),
            "jNQXAC9IVRw" to listOf(
                Comment(UUID.randomUUID().toString(), "jNQXAC9IVRw", "HistoryBuff", "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=100", "The video that started an entire media revolution. Unreal.", "4 days ago", 512, false),
                Comment(UUID.randomUUID().toString(), "jNQXAC9IVRw", "Zookeeper99", "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?w=100", "The elephants are cool! 🐘", "2 weeks ago", 76, false)
            )
        )
    )
    val comments: StateFlow<Map<String, List<Comment>>> = _comments.asStateFlow()

    fun getAllCategories(): List<String> = listOf(
        "All", "Music", "Tech", "Gaming", "History", "Animation", "Shorts", "Trending", "Podcasts"
    )

    fun getVideosByCategory(category: String): List<Video> {
        val currentList = _videos.value
        return if (category == "All" || category.isBlank()) {
            currentList
        } else if (category == "Trending") {
            currentList.sortedByDescending { it.likeCount }
        } else {
            currentList.filter { it.category.equals(category, ignoreCase = true) }
        }
    }

    fun searchVideos(query: String): List<Video> {
        val q = query.trim().lowercase()
        if (q.isBlank()) return _videos.value
        return _videos.value.filter {
            it.title.lowercase().contains(q) ||
            it.channelTitle.lowercase().contains(q) ||
            it.category.lowercase().contains(q) ||
            it.description.lowercase().contains(q)
        }
    }

    fun getVideoById(videoId: String): Video? {
        return _videos.value.find { it.id == videoId }
            ?: _shorts.value.find { it.id == videoId }
    }

    fun getRelatedVideos(videoId: String): List<Video> {
        return _videos.value.filter { it.id != videoId }
    }

    fun toggleSubscribe(channelId: String): Boolean {
        val current = _subscribedChannels.value.toMutableSet()
        val isNowSubscribed = if (current.contains(channelId)) {
            current.remove(channelId)
            false
        } else {
            current.add(channelId)
            true
        }
        _subscribedChannels.value = current
        return isNowSubscribed
    }

    fun isChannelSubscribed(channelId: String): Boolean {
        return _subscribedChannels.value.contains(channelId)
    }

    fun toggleLike(videoId: String): Boolean {
        val liked = _likedVideoIds.value.toMutableSet()
        val disliked = _dislikedVideoIds.value.toMutableSet()
        val isNowLiked = if (liked.contains(videoId)) {
            liked.remove(videoId)
            false
        } else {
            liked.add(videoId)
            disliked.remove(videoId)
            true
        }
        _likedVideoIds.value = liked
        _dislikedVideoIds.value = disliked
        return isNowLiked
    }

    fun toggleDislike(videoId: String): Boolean {
        val disliked = _dislikedVideoIds.value.toMutableSet()
        val liked = _likedVideoIds.value.toMutableSet()
        val isNowDisliked = if (disliked.contains(videoId)) {
            disliked.remove(videoId)
            false
        } else {
            disliked.add(videoId)
            liked.remove(videoId)
            true
        }
        _dislikedVideoIds.value = disliked
        _likedVideoIds.value = liked
        return isNowDisliked
    }

    fun toggleWatchLater(videoId: String): Boolean {
        val set = _watchLaterVideoIds.value.toMutableSet()
        val isSaved = if (set.contains(videoId)) {
            set.remove(videoId)
            false
        } else {
            set.add(videoId)
            true
        }
        _watchLaterVideoIds.value = set
        return isSaved
    }

    fun addToHistory(video: Video) {
        val current = _watchHistory.value.toMutableList()
        current.removeAll { it.id == video.id }
        current.add(0, video)
        _watchHistory.value = current.take(30)
    }

    fun getCommentsForVideo(videoId: String): List<Comment> {
        return _comments.value[videoId] ?: emptyList()
    }

    fun addComment(videoId: String, content: String, authorName: String = "You") {
        if (content.isBlank()) return
        val newComment = Comment(
            id = UUID.randomUUID().toString(),
            videoId = videoId,
            authorName = authorName,
            authorAvatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100",
            content = content.trim(),
            timeAgo = "Just now",
            likesCount = 0,
            isLiked = false
        )
        val currentMap = _comments.value.toMutableMap()
        val list = (currentMap[videoId] ?: emptyList()).toMutableList()
        list.add(0, newComment)
        currentMap[videoId] = list
        _comments.value = currentMap
    }

    fun addCustomVideo(youtubeUrlOrId: String, title: String? = null): Video? {
        val extractedId = parseYouTubeId(youtubeUrlOrId) ?: return null
        val existing = getVideoById(extractedId)
        if (existing != null) return existing

        val newVideo = Video(
            id = extractedId,
            title = title?.ifBlank { null } ?: "YouTube Video ($extractedId)",
            description = "Custom video loaded directly from YouTube URL: $youtubeUrlOrId",
            channelId = "c_custom_${extractedId}",
            channelTitle = "YouTube Creator",
            channelAvatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100",
            subscriberCount = "1.0M",
            viewCount = "100K+ views",
            publishedTime = "Recent",
            duration = "Live / VOD",
            thumbnailUrl = "https://img.youtube.com/vi/$extractedId/hqdefault.jpg",
            category = "Custom"
        )
        val current = _videos.value.toMutableList()
        current.add(0, newVideo)
        _videos.value = current
        return newVideo
    }

    fun parseYouTubeId(urlOrId: String): String? {
        val trimmed = urlOrId.trim()
        if (trimmed.length == 11 && !trimmed.contains("/") && !trimmed.contains("?")) {
            return trimmed
        }
        val patterns = listOf(
            "(?:v=|/v/|youtu\\.be/|/embed/|/shorts/)([a-zA-Z0-9_-]{11})",
            "^([a-zA-Z0-9_-]{11})$"
        )
        for (patternStr in patterns) {
            val matcher = Pattern.compile(patternStr).matcher(trimmed)
            if (matcher.find()) {
                return matcher.group(1)
            }
        }
        return null
    }

    fun getSubscribedChannelsList(): List<Channel> {
        val subscribedIds = _subscribedChannels.value
        val channelsMap = mutableMapOf<String, Channel>()
        for (v in _videos.value) {
            if (subscribedIds.contains(v.channelId)) {
                channelsMap[v.channelId] = Channel(
                    id = v.channelId,
                    name = v.channelTitle,
                    avatarUrl = v.channelAvatarUrl,
                    subscriberCount = v.subscriberCount,
                    isSubscribed = true
                )
            }
        }
        return channelsMap.values.toList()
    }
}
