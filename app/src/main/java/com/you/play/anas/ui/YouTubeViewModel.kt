package com.you.play.anas.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.you.play.anas.data.model.Channel
import com.you.play.anas.data.model.Comment
import com.you.play.anas.data.model.Video
import com.you.play.anas.data.repository.YouTubeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class MiniPlayerState {
    EXPANDED,
    MINIMIZED,
    HIDDEN
}

enum class AppScreen {
    HOME,
    SHORTS,
    SUBSCRIPTIONS,
    YOU
}

data class UserProfile(
    val name: String = "Guest User",
    val handle: String = "@guest_user",
    val email: String = "guest@youplay.local",
    val avatarUrl: String = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150",
    val isSignedIn: Boolean = false
)

class YouTubeViewModel(
    val repository: YouTubeRepository = YouTubeRepository()
) : ViewModel() {

    private val _userProfile = MutableStateFlow(UserProfile(isSignedIn = false))
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _showSignInDialog = MutableStateFlow(false)
    val showSignInDialog: StateFlow<Boolean> = _showSignInDialog.asStateFlow()

    private val _currentScreen = MutableStateFlow(AppScreen.HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _activeVideo = MutableStateFlow<Video?>(null)
    val activeVideo: StateFlow<Video?> = _activeVideo.asStateFlow()

    private val _miniPlayerState = MutableStateFlow(MiniPlayerState.HIDDEN)
    val miniPlayerState: StateFlow<MiniPlayerState> = _miniPlayerState.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSearchActive = MutableStateFlow(false)
    val isSearchActive: StateFlow<Boolean> = _isSearchActive.asStateFlow()

    private val _isPlaying = MutableStateFlow(true)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _showCommentsSheet = MutableStateFlow(false)
    val showCommentsSheet: StateFlow<Boolean> = _showCommentsSheet.asStateFlow()

    private val _showQuickUrlDialog = MutableStateFlow(false)
    val showQuickUrlDialog: StateFlow<Boolean> = _showQuickUrlDialog.asStateFlow()

    private val _playbackProgress = MutableStateFlow(0f)
    val playbackProgress: StateFlow<Float> = _playbackProgress.asStateFlow()

    private val _currentTimeText = MutableStateFlow("0:00")
    val currentTimeText: StateFlow<String> = _currentTimeText.asStateFlow()

    private val _durationText = MutableStateFlow("0:00")
    val durationText: StateFlow<String> = _durationText.asStateFlow()

    val subscribedChannels: StateFlow<Set<String>> = repository.subscribedChannels
    val likedVideoIds: StateFlow<Set<String>> = repository.likedVideoIds
    val dislikedVideoIds: StateFlow<Set<String>> = repository.dislikedVideoIds
    val watchLaterVideoIds: StateFlow<Set<String>> = repository.watchLaterVideoIds
    val watchHistory: StateFlow<List<Video>> = repository.watchHistory
    val shortsList: StateFlow<List<Video>> = repository.shorts

    // Reactive filtered feed combining selected category and search query
    val feedVideos: StateFlow<List<Video>> = combine(
        repository.videos,
        _selectedCategory,
        _searchQuery
    ) { allVideos, category, query ->
        var list = allVideos
        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            list = list.filter {
                it.title.lowercase().contains(q) ||
                it.channelTitle.lowercase().contains(q) ||
                it.category.lowercase().contains(q)
            }
        } else if (category != "All") {
            if (category == "Trending") {
                list = list.sortedByDescending { it.likeCount }
            } else {
                list = list.filter { it.category.equals(category, ignoreCase = true) }
            }
        }
        list
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Subscriptions screen feed
    val subscriptionFeed: StateFlow<List<Video>> = combine(
        repository.videos,
        subscribedChannels
    ) { allVideos, subbedIds ->
        allVideos.filter { subbedIds.contains(it.channelId) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setScreen(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleSearchActive(active: Boolean) {
        _isSearchActive.value = active
        if (!active) {
            _searchQuery.value = ""
        }
    }

    fun playVideo(video: Video) {
        _activeVideo.value = video
        _miniPlayerState.value = MiniPlayerState.EXPANDED
        _isPlaying.value = true
        _durationText.value = video.duration
        _playbackProgress.value = 0f
        _currentTimeText.value = "0:00"
        repository.addToHistory(video)
    }

    fun minimizePlayer() {
        if (_activeVideo.value != null) {
            _miniPlayerState.value = MiniPlayerState.MINIMIZED
        }
    }

    fun expandPlayer() {
        if (_activeVideo.value != null) {
            _miniPlayerState.value = MiniPlayerState.EXPANDED
        }
    }

    fun closePlayer() {
        _miniPlayerState.value = MiniPlayerState.HIDDEN
        _isPlaying.value = false
        _activeVideo.value = null
    }

    fun togglePlayPause() {
        _isPlaying.value = !_isPlaying.value
    }

    fun setPlaying(playing: Boolean) {
        _isPlaying.value = playing
    }

    fun updateProgress(currentSeconds: Float, totalSeconds: Float) {
        if (totalSeconds > 0) {
            _playbackProgress.value = (currentSeconds / totalSeconds).coerceIn(0f, 1f)
            _currentTimeText.value = formatSeconds(currentSeconds.toInt())
            _durationText.value = formatSeconds(totalSeconds.toInt())
        }
    }

    private fun formatSeconds(totalSecs: Int): String {
        val m = totalSecs / 60
        val s = totalSecs % 60
        return "%d:%02d".format(m, s)
    }

    fun toggleLikeActiveVideo() {
        val v = _activeVideo.value ?: return
        repository.toggleLike(v.id)
    }

    fun toggleDislikeActiveVideo() {
        val v = _activeVideo.value ?: return
        repository.toggleDislike(v.id)
    }

    fun toggleSubscribeChannel(channelId: String) {
        repository.toggleSubscribe(channelId)
    }

    fun toggleWatchLaterActiveVideo() {
        val v = _activeVideo.value ?: return
        repository.toggleWatchLater(v.id)
    }

    fun toggleCommentsSheet(show: Boolean) {
        _showCommentsSheet.value = show
    }

    fun toggleQuickUrlDialog(show: Boolean) {
        _showQuickUrlDialog.value = show
    }

    fun postComment(content: String) {
        val v = _activeVideo.value ?: return
        repository.addComment(v.id, content)
    }

    fun getCommentsForActiveVideo(): List<Comment> {
        val v = _activeVideo.value ?: return emptyList()
        return repository.getCommentsForVideo(v.id)
    }

    fun playCustomUrl(url: String): Boolean {
        val created = repository.addCustomVideo(url)
        if (created != null) {
            playVideo(created)
            return true
        }
        return false
    }

    fun getSubscribedChannelsList(): List<Channel> {
        return repository.getSubscribedChannelsList()
    }

    fun toggleSignInDialog(show: Boolean) {
        _showSignInDialog.value = show
    }

    fun signIn(name: String, email: String) {
        val cleanName = if (name.isNotBlank()) name else "YouTube User"
        val handle = "@${cleanName.lowercase().replace(" ", "_")}"
        _userProfile.value = UserProfile(
            name = cleanName,
            handle = handle,
            email = if (email.isNotBlank()) email else "${cleanName.lowercase().replace(" ", "")}@gmail.com",
            avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150",
            isSignedIn = true
        )
        _showSignInDialog.value = false
    }

    fun signOut() {
        _userProfile.value = UserProfile(isSignedIn = false)
    }
}
