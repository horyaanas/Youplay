package com.you.play.anas.ui.player

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.you.play.anas.data.model.Video
import com.you.play.anas.ui.MiniPlayerState
import com.you.play.anas.ui.YouTubeViewModel
import com.you.play.anas.ui.components.VideoFeedCard
import com.you.play.anas.ui.theme.YouTubeRed

@Composable
fun YouTubeWatchOverlay(
    viewModel: YouTubeViewModel,
    modifier: Modifier = Modifier
) {
    val activeVideo by viewModel.activeVideo.collectAsState()
    val miniPlayerState by viewModel.miniPlayerState.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val likedVideoIds by viewModel.likedVideoIds.collectAsState()
    val dislikedVideoIds by viewModel.dislikedVideoIds.collectAsState()
    val watchLaterIds by viewModel.watchLaterVideoIds.collectAsState()
    val subscribedChannels by viewModel.subscribedChannels.collectAsState()
    val allVideos by viewModel.feedVideos.collectAsState()

    val video = activeVideo ?: return

    if (miniPlayerState == MiniPlayerState.HIDDEN) return

    val isLiked = likedVideoIds.contains(video.id)
    val isDisliked = dislikedVideoIds.contains(video.id)
    val isSaved = watchLaterIds.contains(video.id)
    val isSubscribed = subscribedChannels.contains(video.channelId)

    // Handle Android system back press to minimize instead of exiting app
    BackHandler(enabled = miniPlayerState == MiniPlayerState.EXPANDED) {
        viewModel.minimizePlayer()
    }

    when (miniPlayerState) {
        MiniPlayerState.EXPANDED -> {
            ExpandedPlayerView(
                video = video,
                isPlaying = isPlaying,
                isLiked = isLiked,
                isDisliked = isDisliked,
                isSaved = isSaved,
                isSubscribed = isSubscribed,
                relatedVideos = allVideos.filter { it.id != video.id },
                onMinimize = { viewModel.minimizePlayer() },
                onTogglePlayPause = { viewModel.togglePlayPause() },
                onToggleLike = { viewModel.toggleLikeActiveVideo() },
                onToggleDislike = { viewModel.toggleDislikeActiveVideo() },
                onToggleSubscribe = { viewModel.toggleSubscribeChannel(video.channelId) },
                onToggleWatchLater = { viewModel.toggleWatchLaterActiveVideo() },
                onOpenComments = { viewModel.toggleCommentsSheet(true) },
                onSelectRelatedVideo = { related -> viewModel.playVideo(related) },
                modifier = modifier
            )
        }
        MiniPlayerState.MINIMIZED -> {
            DockedMiniPlayerView(
                video = video,
                isPlaying = isPlaying,
                onExpand = { viewModel.expandPlayer() },
                onTogglePlayPause = { viewModel.togglePlayPause() },
                onClose = { viewModel.closePlayer() },
                modifier = modifier
            )
        }
        MiniPlayerState.HIDDEN -> {
            // Nothing rendered
        }
    }
}

@Composable
private fun ExpandedPlayerView(
    video: Video,
    isPlaying: Boolean,
    isLiked: Boolean,
    isDisliked: Boolean,
    isSaved: Boolean,
    isSubscribed: Boolean,
    relatedVideos: List<Video>,
    onMinimize: () -> Unit,
    onTogglePlayPause: () -> Unit,
    onToggleLike: () -> Unit,
    onToggleDislike: () -> Unit,
    onToggleSubscribe: () -> Unit,
    onToggleWatchLater: () -> Unit,
    onOpenComments: () -> Unit,
    onSelectRelatedVideo: (Video) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var dragOffsetY by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { _, dragAmount ->
                        dragOffsetY += dragAmount.y
                    },
                    onDragEnd = {
                        if (dragOffsetY > 120f) {
                            onMinimize()
                        }
                        dragOffsetY = 0f
                    },
                    onDragCancel = {
                        dragOffsetY = 0f
                    }
                )
            }
            .testTag("expanded_watch_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar with Minimize Chevron
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onMinimize,
                    modifier = Modifier.testTag("btn_minimize_player")
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Minimize player",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Text(
                    text = video.title,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                )

                IconButton(onClick = onTogglePlayPause) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            // Real YouTube Embedded Player View (16:9 ratio)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .background(Color.Black)
            ) {
                YouTubePlayerEmbed(
                    videoId = video.id,
                    isPlaying = isPlaying,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Scrollable Content Below Player
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                // Video Details Section
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = video.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                lineHeight = 22.sp
                            ),
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "${video.viewCount} • ${video.publishedTime}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Channel info & Subscribe button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = video.channelAvatarUrl,
                                contentDescription = video.channelTitle,
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = video.channelTitle,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.onBackground,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${video.subscriberCount} subscribers",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Subscribe Button (YouTube style)
                            Button(
                                onClick = onToggleSubscribe,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSubscribed) {
                                        MaterialTheme.colorScheme.surfaceVariant
                                    } else {
                                        MaterialTheme.colorScheme.onBackground
                                    },
                                    contentColor = if (isSubscribed) {
                                        MaterialTheme.colorScheme.onBackground
                                    } else {
                                        MaterialTheme.colorScheme.background
                                    }
                                ),
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier
                                    .height(38.dp)
                                    .testTag("subscribe_button")
                            ) {
                                Text(
                                    text = if (isSubscribed) "Subscribed" else "Subscribe",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Actions Row: Segmented Like/Dislike, Share, Watch Later, Open in YouTube
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Combined Like/Dislike Pill
                            item {
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier.height(36.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Row(
                                            modifier = Modifier
                                                .clickable(onClick = onToggleLike)
                                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                                .testTag("like_button"),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (isLiked) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                                                contentDescription = "Like",
                                                tint = if (isLiked) YouTubeRed else MaterialTheme.colorScheme.onBackground,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Text(
                                                text = if (isLiked) "145K+" else "145K",
                                                style = MaterialTheme.typography.labelMedium,
                                                color = MaterialTheme.colorScheme.onBackground
                                            )
                                        }

                                        Box(
                                            modifier = Modifier
                                                .width(1.dp)
                                                .height(20.dp)
                                                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                                        )

                                        Box(
                                            modifier = Modifier
                                                .clickable(onClick = onToggleDislike)
                                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                                .testTag("dislike_button"),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = if (isDisliked) Icons.Filled.ThumbDown else Icons.Outlined.ThumbDown,
                                                contentDescription = "Dislike",
                                                tint = if (isDisliked) YouTubeRed else MaterialTheme.colorScheme.onBackground,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            // Share Pill
                            item {
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier
                                        .height(36.dp)
                                        .clickable {
                                            try {
                                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                                    type = "text/plain"
                                                    putExtra(Intent.EXTRA_TEXT, "Watch '${video.title}' on YouTube: https://youtu.be/${video.id}")
                                                }
                                                context.startActivity(Intent.createChooser(shareIntent, "Share Video"))
                                            } catch (_: Exception) {}
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Share,
                                            contentDescription = "Share",
                                            tint = MaterialTheme.colorScheme.onBackground,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            text = "Share",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onBackground
                                        )
                                    }
                                }
                            }

                            // Watch Later Pill
                            item {
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier
                                        .height(36.dp)
                                        .clickable(onClick = onToggleWatchLater)
                                        .testTag("watch_later_button")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isSaved) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                                            contentDescription = "Save to Watch Later",
                                            tint = if (isSaved) YouTubeRed else MaterialTheme.colorScheme.onBackground,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            text = if (isSaved) "Saved" else "Save",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onBackground
                                        )
                                    }
                                }
                            }

                            // Open in YouTube Pill
                            item {
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier
                                        .height(36.dp)
                                        .clickable {
                                            try {
                                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=${video.id}"))
                                                context.startActivity(intent)
                                            } catch (_: Exception) {}
                                        }
                                        .testTag("btn_open_external_youtube")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.OpenInNew,
                                            contentDescription = "Open in YouTube",
                                            tint = YouTubeRed,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = "YouTube",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onBackground
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Video Description Card
                        var isDescExpanded by remember { mutableStateOf(false) }
                        Card(
                            onClick = { isDescExpanded = !isDescExpanded },
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = video.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    maxLines = if (isDescExpanded) Int.MAX_VALUE else 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (isDescExpanded) "Show less" else "...more",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Comments Preview Box (YouTube style)
                        Card(
                            onClick = onOpenComments,
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("comments_preview_card")
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Comments • 2.4K",
                                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    Text(
                                        text = "View all",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "A",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    Text(
                                        text = "Still watching this in 2026, absolute masterpiece!",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Up Next",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

                // Related Videos List
                items(relatedVideos, key = { it.id }) { related ->
                    VideoFeedCard(
                        video = related,
                        onClick = { onSelectRelatedVideo(related) }
                    )
                }
            }
        }
    }
}

@Composable
fun DockedMiniPlayerView(
    video: Video,
    isPlaying: Boolean,
    onExpand: () -> Unit,
    onTogglePlayPause: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 6.dp,
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .clickable(onClick = onExpand)
            .testTag("docked_miniplayer")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mini Thumbnail
                AsyncImage(
                    model = video.thumbnailUrl,
                    contentDescription = video.title,
                    modifier = Modifier
                        .width(96.dp)
                        .height(54.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(10.dp))

                // Title and Channel Name
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = video.title,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = video.channelTitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Play / Pause Button
                IconButton(
                    onClick = onTogglePlayPause,
                    modifier = Modifier.testTag("miniplayer_play_pause")
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                // Close Button
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.testTag("miniplayer_close")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            // Bottom subtle red progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.35f)
                        .height(2.dp)
                        .background(YouTubeRed)
                )
            }
        }
    }
}
