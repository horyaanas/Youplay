package com.you.play.anas.data.model

data class Video(
    val id: String,
    val title: String,
    val description: String,
    val channelId: String,
    val channelTitle: String,
    val channelAvatarUrl: String,
    val subscriberCount: String,
    val viewCount: String,
    val publishedTime: String,
    val duration: String,
    val thumbnailUrl: String,
    val category: String,
    val isShort: Boolean = false,
    val likeCount: Long = 145000L,
    val isLiked: Boolean = false,
    val isDisliked: Boolean = false,
    val isSavedToWatchLater: Boolean = false
)

data class Comment(
    val id: String,
    val videoId: String,
    val authorName: String,
    val authorAvatarUrl: String,
    val content: String,
    val timeAgo: String,
    val likesCount: Int = 0,
    val isLiked: Boolean = false
)

data class Channel(
    val id: String,
    val name: String,
    val avatarUrl: String,
    val subscriberCount: String,
    val isSubscribed: Boolean = false
)
