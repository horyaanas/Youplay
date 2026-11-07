package com.you.play.anas.ui.player

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.RenderProcessGoneDetail
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun YouTubePlayerEmbed(
    videoId: String,
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var hasRendererCrashed by remember { mutableStateOf(false) }

    val webView = remember(context, videoId) {
        WebView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(Color.BLACK)

            // Hardware layer for smooth video decoding
            setLayerType(View.LAYER_TYPE_HARDWARE, null)

            // Cookie permissions for YouTube player session
            try {
                val cookieManager = CookieManager.getInstance()
                cookieManager.setAcceptCookie(true)
                cookieManager.setAcceptThirdPartyCookies(this, true)
            } catch (_: Exception) {}

            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                databaseEnabled = true
                mediaPlaybackRequiresUserGesture = false
                allowFileAccess = false
                allowContentAccess = true
                loadWithOverviewMode = true
                useWideViewPort = true
                cacheMode = WebSettings.LOAD_DEFAULT
                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                // Set standard modern Mobile Chrome User-Agent so YouTube allows embedding and playback
                userAgentString = "Mozilla/5.0 (Linux; Android 14; Pixel 7 Build/UQ1A.240105.004) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Mobile Safari/537.36"
            }

            webChromeClient = WebChromeClient()
            webViewClient = object : WebViewClient() {
                // Safeguard against Chromium renderer crashes in cloud emulators
                override fun onRenderProcessGone(view: WebView?, detail: RenderProcessGoneDetail?): Boolean {
                    hasRendererCrashed = true
                    try {
                        view?.destroy()
                    } catch (_: Exception) {}
                    return true
                }
            }
        }
    }

    LaunchedEffect(videoId, hasRendererCrashed) {
        if (videoId.isNotBlank() && !hasRendererCrashed) {
            val htmlData = generateYouTubeHtml(videoId)
            // Use https://www.google.com as base URL so YouTube accepts cross-origin iframe with origin parameter
            webView.loadDataWithBaseURL("https://www.google.com", htmlData, "text/html", "UTF-8", null)
        }
    }

    LaunchedEffect(isPlaying) {
        if (!hasRendererCrashed) {
            val command = if (isPlaying) "playVideo" else "pauseVideo"
            webView.evaluateJavascript(
                "var p = document.getElementById('ytplayer'); if (p && p.contentWindow) p.contentWindow.postMessage('{\"event\":\"command\",\"func\":\"$command\",\"args\":\"\"}', '*');",
                null
            )
        }
    }

    DisposableEffect(videoId) {
        onDispose {
            try {
                webView.stopLoading()
                webView.destroy()
            } catch (_: Exception) {}
        }
    }

    Box(
        modifier = modifier.background(androidx.compose.ui.graphics.Color.Black)
    ) {
        if (!hasRendererCrashed) {
            AndroidView(
                factory = { webView },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

private fun generateYouTubeHtml(videoId: String): String {
    return """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
            <style>
                * { margin: 0; padding: 0; box-sizing: border-box; background: #000; }
                html, body { width: 100%; height: 100%; overflow: hidden; }
                .container { position: relative; width: 100%; height: 100%; }
                iframe { position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: 0; }
            </style>
        </head>
        <body>
            <div class="container">
                <iframe id="ytplayer"
                    src="https://www.youtube.com/embed/$videoId?autoplay=1&playsinline=1&controls=1&enablejsapi=1&origin=https://www.google.com&rel=0&modestbranding=1&fs=1&iv_load_policy=3"
                    allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share"
                    allowfullscreen>
                </iframe>
            </div>
        </body>
        </html>
    """.trimIndent()
}
