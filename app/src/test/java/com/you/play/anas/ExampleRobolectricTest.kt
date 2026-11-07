package com.you.play.anas

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.you.play.anas.data.repository.YouTubeRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("YouPlay", appName)
    }

    @Test
    fun `parse real YouTube URLs correctly`() {
        val repo = YouTubeRepository()
        assertEquals("dQw4w9WgXcQ", repo.parseYouTubeId("https://www.youtube.com/watch?v=dQw4w9WgXcQ"))
        assertEquals("dQw4w9WgXcQ", repo.parseYouTubeId("https://youtu.be/dQw4w9WgXcQ"))
        assertEquals("dQw4w9WgXcQ", repo.parseYouTubeId("https://youtube.com/shorts/dQw4w9WgXcQ"))
        assertEquals("dQw4w9WgXcQ", repo.parseYouTubeId("dQw4w9WgXcQ"))
    }

    @Test
    fun `toggle subscribe and like functionality`() {
        val repo = YouTubeRepository()
        val isLiked = repo.toggleLike("kJQP7kiw5Fk")
        assertTrue(isLiked)

        val isSubbed = repo.toggleSubscribe("c_new_channel")
        assertTrue(isSubbed)
    }
}
