package com.phivantuan.sigmatest

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences

@SuppressLint("StaticFieldLeak")
object UserData {
    private const val PREF_NAME = "user_data_manager"
    private const val KEY_LOCATION = "location"
    private const val KEY_BATTERY = "battery"
    private lateinit var pref: SharedPreferences
    private lateinit var mContext: Context

    fun init(context: Context) {
        pref = getPrefs(context)
        val edit = pref.edit()
        edit.apply()
        mContext = context
    }

    var location: String
        get() = pref.getString(KEY_LOCATION, "") ?: ""
        set(value) = pref.edit().putString(KEY_BATTERY, value).apply()

    var battery: String
        get() = pref.getString(KEY_BATTERY, "") ?: ""
        set(value) = pref.edit().putString(KEY_BATTERY, value).apply()

    private fun getPrefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
}