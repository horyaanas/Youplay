package com.you.play.anas

import com.you.play.anas.data.repository.YouTubeRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Test

/**
 * Fast, pure JVM unit tests for YouPlay repository and utility logic.
 * Executes instantly without any Android/Robolectric shadow dependencies.
 */
class ExampleUnitTest {

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun parse_real_YouTube_URLs_correctly() {
        val repo = YouTubeRepository()
        assertEquals("dQw4w9WgXcQ", repo.parseYouTubeId("https://www.youtube.com/watch?v=dQw4w9WgXcQ"))
        assertEquals("dQw4w9WgXcQ", repo.parseYouTubeId("https://youtu.be/dQw4w9WgXcQ"))
        assertEquals("dQw4w9WgXcQ", repo.parseYouTubeId("https://youtube.com/shorts/dQw4w9WgXcQ"))
        assertEquals("dQw4w9WgXcQ", repo.parseYouTubeId("dQw4w9WgXcQ"))
        assertEquals("kJQP7kiw5Fk", repo.parseYouTubeId("https://www.youtube.com/embed/kJQP7kiw5Fk"))
    }

    @Test
    fun toggle_subscribe_and_like_functionality() {
        val repo = YouTubeRepository()
        
        // Like toggle
        val isLiked = repo.toggleLike("kJQP7kiw5Fk")
        assertTrue(isLiked)
        assertTrue(repo.likedVideoIds.value.contains("kJQP7kiw5Fk"))
        
        val isUnliked = repo.toggleLike("kJQP7kiw5Fk")
        assertFalse(isUnliked)
        assertFalse(repo.likedVideoIds.value.contains("kJQP7kiw5Fk"))

        // Subscribe toggle
        val isSubbed = repo.toggleSubscribe("c_new_test_channel")
        assertTrue(isSubbed)
        assertTrue(repo.isChannelSubscribed("c_new_test_channel"))

        val isUnsubbed = repo.toggleSubscribe("c_new_test_channel")
        assertFalse(isUnsubbed)
        assertFalse(repo.isChannelSubscribed("c_new_test_channel"))
    }

    @Test
    fun category_and_search_filtering_works() {
        val repo = YouTubeRepository()
        val musicVideos = repo.getVideosByCategory("Music")
        assertTrue(musicVideos.isNotEmpty())
        assertTrue(musicVideos.all { it.category == "Music" })

        val searchResults = repo.searchVideos("Never Gonna Give You Up")
        assertTrue(searchResults.isNotEmpty())
        assertEquals("dQw4w9WgXcQ", searchResults[0].id)
    }

    @Test
    fun add_custom_video_and_comments_work() {
        val repo = YouTubeRepository()
        val customVideo = repo.addCustomVideo("https://www.youtube.com/watch?v=M7lc1UVf-VE", "IFrame API Live")
        assertNotNull(customVideo)
        assertEquals("M7lc1UVf-VE", customVideo?.id)

        // Comments
        repo.addComment("M7lc1UVf-VE", "Amazing tutorial!", "Anas")
        val comments = repo.getCommentsForVideo("M7lc1UVf-VE")
        assertTrue(comments.isNotEmpty())
        assertEquals("Amazing tutorial!", comments[0].content)
        assertEquals("Anas", comments[0].authorName)
    }
}
