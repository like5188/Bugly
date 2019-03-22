package com.like.bugly

import android.content.Context
import android.util.Log
import com.meituan.android.walle.WalleChannelReader
import com.tencent.bugly.Bugly
import com.tencent.bugly.beta.Beta

object BuglyUtils {

    /**
     * 初始化异常上报、应用升级功能。建议在Application的onCreate()进行
     */
    fun init(context: Context, appId: String, isDebug: Boolean = false) {
        val app = context.applicationContext
        // 这样我们就可以按渠道维度来统计你们app的Crash数据了。
        val channel = WalleChannelReader.getChannel(app)
        Bugly.setAppChannel(app, channel)
        Log.d("BuglyUtils", "channel = $channel")
        Bugly.init(context.applicationContext, appId, isDebug)
    }

    /**
     * 初始化热更新功能。建议在Application的attachBaseContext()进行
     */
    fun initTinker() {
        // 安装tinker
        Beta.installTinker()
    }

    /**
     * 检查更新
     *
     * @param isManual      用户手动点击检查，非用户点击操作请传false
     * @param isSilence     是否显示弹窗等交互，[true:没有弹窗和toast] [false:有弹窗或toast]
     */
    fun checkUpgrade(isManual: Boolean, isSilence: Boolean) {
        Beta.checkUpgrade(isManual, isSilence)
    }
}