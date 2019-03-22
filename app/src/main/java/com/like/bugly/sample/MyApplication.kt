package com.like.bugly.sample

import android.app.Application
import android.content.Context
import android.support.multidex.MultiDex
import com.like.bugly.BuglyUtils

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        BuglyUtils.init(this, "92274f698a", true)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        // you must install multiDex whatever tinker is installed!
        MultiDex.install(base)

        BuglyUtils.initTinker()
    }
}