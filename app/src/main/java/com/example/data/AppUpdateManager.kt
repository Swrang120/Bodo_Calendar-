package com.example.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class AppUpdateInfo(
  val versionCode: Int,
  val versionName: String,
  val title: String,
  val releaseNotes: String,
  val apkUrl: String,
  val forceUpdate: Boolean = false
)

object AppUpdateManager {
  // Checks latest version.json on GitHub Pages
  private const val VERSION_CHECK_URL = "https://swrang120.github.io/Bodo_Calendar-/version.json"

  /**
   * Fetches latest version info from remote server without comparing.
   */
  suspend fun fetchLatestRemoteInfo(): AppUpdateInfo? = withContext(Dispatchers.IO) {
    try {
      val url = URL(VERSION_CHECK_URL)
      val connection = (url.openConnection() as HttpURLConnection).apply {
        connectTimeout = 6000
        readTimeout = 6000
        requestMethod = "GET"
        useCaches = false
      }

      if (connection.responseCode == HttpURLConnection.HTTP_OK) {
        val jsonStr = connection.inputStream.bufferedReader().use { it.readText() }
        val json = JSONObject(jsonStr)
        val latestVersionCode = json.optInt("versionCode", 1)

        return@withContext AppUpdateInfo(
          versionCode = latestVersionCode,
          versionName = json.optString("versionName", "2.0"),
          title = json.optString("title", "New Update Available!"),
          releaseNotes = json.optString("releaseNotes", "New features & performance improvements available."),
          apkUrl = json.optString("apkUrl", "https://swrang120.github.io/Bodo_Calendar-/app-debug.apk"),
          forceUpdate = json.optBoolean("forceUpdate", false)
        )
      }
    } catch (_: Exception) {
      // Network not available or error - fail gracefully
    }
    null
  }

  suspend fun checkForUpdates(currentVersionCode: Int): AppUpdateInfo? {
    val remote = fetchLatestRemoteInfo() ?: return null
    if (remote.versionCode > currentVersionCode) {
      return remote
    }
    return null
  }
}
