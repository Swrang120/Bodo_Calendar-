package com.example.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class SupabaseManager(context: Context) {

  private val prefs: SharedPreferences =
    context.getSharedPreferences("bodo_calendar_prefs", Context.MODE_PRIVATE)

  private val client = OkHttpClient.Builder()
    .connectTimeout(8, TimeUnit.SECONDS)
    .readTimeout(8, TimeUnit.SECONDS)
    .build()

  companion object {
    const val SUPABASE_URL = "https://rwqecmerqyxvgdewfsrv.supabase.co"
    const val SUPABASE_KEY = "sb_publishable_KzTjI1ao6jTf0MKNTSD6JQ_Iigfvhrx"
    private const val PREF_DELETED_IDS = "deleted_item_ids"
    private const val PREF_LOCAL_ADMIN_MESSAGES = "local_admin_messages"
  }

  /**
   * Checks if user has deleted / dismissed this message
   */
  fun isMessageDeleted(id: String): Boolean {
    val deletedSet = prefs.getStringSet(PREF_DELETED_IDS, emptySet()) ?: emptySet()
    return deletedSet.contains(id)
  }

  /**
   * Saves a message ID as deleted/dismissed by user
   */
  fun markMessageDeleted(id: String) {
    val currentSet = prefs.getStringSet(PREF_DELETED_IDS, emptySet())?.toMutableSet() ?: mutableSetOf()
    currentSet.add(id)
    prefs.edit().putStringSet(PREF_DELETED_IDS, currentSet).apply()
  }

  /**
   * Resets deleted messages (in case user wants to restore)
   */
  fun restoreAllMessages() {
    prefs.edit().remove(PREF_DELETED_IDS).apply()
  }

  /**
   * Retrieves locally stored custom admin messages
   */
  fun getLocalAdminMessages(): List<CulturalNewsItem> {
    val jsonStr = prefs.getString(PREF_LOCAL_ADMIN_MESSAGES, null) ?: return emptyList()
    val list = mutableListOf<CulturalNewsItem>()
    try {
      val jsonArray = JSONArray(jsonStr)
      for (i in 0 until jsonArray.length()) {
        val obj = jsonArray.getJSONObject(i)
        list.add(
          CulturalNewsItem(
            id = obj.optString("id", "admin_${System.currentTimeMillis()}_$i"),
            title = obj.optString("title", "Admin Announcement"),
            bodoTitle = obj.optString("bodoTitle", "Admin-ni Khobor"),
            category = obj.optString("category", "Admin"),
            description = obj.optString("description", ""),
            bodoDescription = obj.optString("bodoDescription", ""),
            dateLabel = obj.optString("dateLabel", "Live Admin Update"),
            fullDate = obj.optString("fullDate", "Today"),
            author = obj.optString("author", "Admin"),
            timestamp = obj.optLong("timestamp", System.currentTimeMillis()),
            isCustomAdmin = true
          )
        )
      }
    } catch (e: Exception) {
      Log.e("SupabaseManager", "Error parsing local admin messages", e)
    }
    return list
  }

  /**
   * Saves an admin message locally and attempts to sync with Supabase
   */
  suspend fun publishAdminMessage(
    title: String,
    bodoTitle: String,
    category: String,
    content: String,
    bodoContent: String,
    author: String
  ): Boolean = withContext(Dispatchers.IO) {
    val id = "admin_${System.currentTimeMillis()}"
    val newItem = CulturalNewsItem(
      id = id,
      title = title,
      bodoTitle = if (bodoTitle.isNotBlank()) bodoTitle else title,
      category = category,
      description = content,
      bodoDescription = if (bodoContent.isNotBlank()) bodoContent else content,
      dateLabel = "Live Admin Update",
      fullDate = "Broadcasting Live",
      author = author.ifBlank { "Admin" },
      timestamp = System.currentTimeMillis(),
      isCustomAdmin = true
    )

    // 1. Save locally first for guaranteed offline availability
    saveLocalAdminMessage(newItem)

    // 2. Post to Supabase REST API
    var supabaseSuccess = false
    try {
      val jsonBody = JSONObject().apply {
        put("id", id)
        put("title", title)
        put("bodo_title", bodoTitle)
        put("category", category)
        put("content", content)
        put("bodo_content", bodoContent)
        put("author", author)
        put("created_at", System.currentTimeMillis())
      }

      val body = jsonBody.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
      val request = Request.Builder()
        .url("$SUPABASE_URL/rest/v1/bodo_announcements")
        .addHeader("apikey", SUPABASE_KEY)
        .addHeader("Authorization", "Bearer $SUPABASE_KEY")
        .addHeader("Content-Type", "application/json")
        .addHeader("Prefer", "return=representation")
        .post(body)
        .build()

      val response = client.newCall(request).execute()
      supabaseSuccess = response.isSuccessful
      response.close()
    } catch (e: Exception) {
      Log.w("SupabaseManager", "Supabase sync notice: Stored locally. ${e.message}")
    }

    return@withContext true
  }

  private fun saveLocalAdminMessage(item: CulturalNewsItem) {
    val current = getLocalAdminMessages().toMutableList()
    current.add(0, item)
    val jsonArray = JSONArray()
    for (msg in current) {
      val obj = JSONObject().apply {
        put("id", msg.id)
        put("title", msg.title)
        put("bodoTitle", msg.bodoTitle)
        put("category", msg.category)
        put("description", msg.description)
        put("bodoDescription", msg.bodoDescription)
        put("dateLabel", msg.dateLabel)
        put("fullDate", msg.fullDate)
        put("author", msg.author)
        put("timestamp", msg.timestamp)
      }
      jsonArray.put(obj)
    }
    prefs.edit().putString(PREF_LOCAL_ADMIN_MESSAGES, jsonArray.toString()).apply()
  }

  /**
   * Fetches remote announcements from Supabase if table exists
   */
  suspend fun fetchSupabaseAnnouncements(): List<CulturalNewsItem> = withContext(Dispatchers.IO) {
    val remoteList = mutableListOf<CulturalNewsItem>()
    try {
      val request = Request.Builder()
        .url("$SUPABASE_URL/rest/v1/bodo_announcements?select=*&order=created_at.desc&limit=20")
        .addHeader("apikey", SUPABASE_KEY)
        .addHeader("Authorization", "Bearer $SUPABASE_KEY")
        .get()
        .build()

      val response = client.newCall(request).execute()
      if (response.isSuccessful) {
        val bodyStr = response.body?.string() ?: ""
        val jsonArray = JSONArray(bodyStr)
        for (i in 0 until jsonArray.length()) {
          val obj = jsonArray.getJSONObject(i)
          val id = obj.optString("id", "supa_$i")
          if (!isMessageDeleted(id)) {
            remoteList.add(
              CulturalNewsItem(
                id = id,
                title = obj.optString("title", "Official Announcement"),
                bodoTitle = obj.optString("bodo_title", "Gwnang Khobor"),
                category = obj.optString("category", "News"),
                description = obj.optString("content", ""),
                bodoDescription = obj.optString("bodo_content", ""),
                dateLabel = "Live Cloud Announcement",
                fullDate = "Live",
                author = obj.optString("author", "Bodoland Live"),
                timestamp = obj.optLong("created_at", System.currentTimeMillis()),
                isCustomAdmin = true
              )
            )
          }
        }
      }
      response.close()
    } catch (e: Exception) {
      Log.d("SupabaseManager", "Remote fetch notice: using local & curated messages.")
    }
    return@withContext remoteList
  }

  suspend fun fetchCloudAnnouncements(): List<CulturalNewsItem> = fetchSupabaseAnnouncements()
}
