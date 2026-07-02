package com.rollcall.android

import android.app.Application

class RollcallApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // initialize token storage
        com.rollcall.android.storage.TokenStorage.init(this)
    }
}
