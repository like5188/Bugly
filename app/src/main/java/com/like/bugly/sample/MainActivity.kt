package com.like.bugly.sample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import com.like.bugly.BuglyUtils

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val tv = findViewById<TextView>(R.id.tv)
        tv.text = BuildConfig.VERSION_NAME + "base-4.0 patch-4.0"
    }

    fun checkUpdate(view: View) {
        BuglyUtils.checkUpgrade(true, false)
    }

    fun crash(view: View) {
        Log.d("MainActivity", "${2 / 0}")
    }
}
