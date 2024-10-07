package com.example.mobprog.api

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

class GamingApi {
    private val client = OkHttpClient()

    fun fetchAllGames() {
        val request = Request.Builder()
            .url("https://www.freetogame.com/api/games")
            .build()

        CoroutineScope(Dispatchers.IO).launch {
            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                val responseData = response.body?.string()

                withContext(Dispatchers.Main) {
                    Log.d("Response Data", responseData ?: "No response data")
                }
            }
        }
    }
}