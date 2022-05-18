package com.phivantuan.sigmatest

import android.app.Application

class SigmaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        UserData.init(this)
    }
}