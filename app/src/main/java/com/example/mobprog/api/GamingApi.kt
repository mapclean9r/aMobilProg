package com.example.mobprog.api

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

class GamingApi {
    private val client = OkHttpClient()

    fun fetchGame(id: String) {
        val request = Request.Builder()
            .url("https://www.freetogame.com/api/game?id=$id")
            .build()

        CoroutineScope(Dispatchers.IO).launch {
            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                val gson = Gson()
                val responseData = response.body?.string()
                val person: GameData = gson.fromJson(responseData, GameData::class.java)
                println("game title: " + person.title)
                println("game id " + person.id)
            }
        }
    }

    fun fetchAllGames(callback: (List<GameData>?) -> Unit) {
        val client = OkHttpClient()
        CoroutineScope(Dispatchers.Default).launch {
            val request = Request.Builder()
                .url("https://www.freetogame.com/api/games").build()
            
            val response = client.newCall(request).execute()

            response.body?.string()?.let { responseData ->
                val gson = Gson()
                val listType = object : TypeToken<List<GameData>>() {}.type
                val games: List<GameData> = gson.fromJson(responseData, listType)
                withContext(Dispatchers.Main) {
                    callback(games)
                }
            } ?: withContext(Dispatchers.Main) {
                callback(null)
            }
        }
    }
}