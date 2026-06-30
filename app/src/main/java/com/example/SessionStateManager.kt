package com.example

import android.content.Context
import android.content.SharedPreferences

object SessionStateManager {
    private var inMemoryExitCount = 0
    private var inMemoryPrefersTile = false
    private var inMemoryWorldHistory = mutableListOf<String>()
    
    private var context: Context? = null

    fun initialize(ctx: Context) {
        context = ctx.applicationContext
    }

    private fun getPrefs(): SharedPreferences? {
        return context?.getSharedPreferences("mood_board_prefs", Context.MODE_PRIVATE)
    }

    fun getExitCount(): Int {
        val prefs = getPrefs()
        return if (prefs != null) {
            prefs.getInt("consecutiveExits", 0)
        } else {
            inMemoryExitCount
        }
    }

    fun incrementExitCount() {
        val prefs = getPrefs()
        val currentCount = getExitCount()
        val newCount = currentCount + 1
        
        if (prefs != null) {
            prefs.edit().putInt("consecutiveExits", newCount).apply()
            if (newCount == 5) {
                prefs.edit().putBoolean("moodBoardPrefersTile", true).apply()
            }
        } else {
            inMemoryExitCount = newCount
            if (newCount == 5) {
                inMemoryPrefersTile = true
            }
        }
    }

    fun resetExitCount() {
        val prefs = getPrefs()
        if (prefs != null) {
            prefs.edit().putInt("consecutiveExits", 0).apply()
        } else {
            inMemoryExitCount = 0
        }
    }

    fun getPrefersTile(): Boolean {
        val prefs = getPrefs()
        return if (prefs != null) {
            prefs.getBoolean("moodBoardPrefersTile", false)
        } else {
            inMemoryPrefersTile
        }
    }

    fun resetPrefersTile() {
        val prefs = getPrefs()
        if (prefs != null) {
            prefs.edit().putBoolean("moodBoardPrefersTile", false).apply()
        } else {
            inMemoryPrefersTile = false
        }
        resetExitCount()
    }

    fun addWorldToHistory(worldId: String) {
        val currentHistory = getWorldHistory().toMutableList()
        if (currentHistory.size >= 10) {
            currentHistory.removeAt(0)
        }
        currentHistory.add(worldId)
        
        val serialized = currentHistory.joinToString(",")
        val prefs = getPrefs()
        if (prefs != null) {
            prefs.edit().putString("moodBoardWorldHistory", serialized).apply()
        } else {
            inMemoryWorldHistory = currentHistory
        }
    }

    fun getWorldHistory(): List<String> {
        val prefs = getPrefs()
        return if (prefs != null) {
            val serialized = prefs.getString("moodBoardWorldHistory", "") ?: ""
            if (serialized.isEmpty()) emptyList() else serialized.split(",")
        } else {
            inMemoryWorldHistory
        }
    }

    fun resetAllForTesting() {
        inMemoryExitCount = 0
        inMemoryPrefersTile = false
        inMemoryWorldHistory.clear()
        
        val prefs = getPrefs()
        prefs?.edit()?.clear()?.apply()
    }
}
