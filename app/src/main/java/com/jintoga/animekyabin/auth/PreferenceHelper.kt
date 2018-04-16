package com.jintoga.animekyabin.auth

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class PreferenceHelper @Inject constructor(context: Context) {

    companion object {
        private const val PREF_FILE_NAME = "AnimeListSharedPreference"
        private const val AUTH_TOKEN = "AUTH_TOKEN"
    }

    private var preferences: SharedPreferences

    init {
        preferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)
    }

    fun clear() {
        preferences.edit().clear().apply()
    }

    fun getAuthToken(): String? = preferences.getString(AUTH_TOKEN, null)

    fun setAuthToken(authToken: String) {
        preferences.edit().putString(AUTH_TOKEN, authToken).apply()
    }


}