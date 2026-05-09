package com.mars.wxpusher.push.ws.keepalive

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.mars.wxpusher.base.common.WxpLogUtils

class KeepWsAliveStartReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        WxpLogUtils.d(message = "收到广播事件,action=${intent.action}")
        KeepWsAliveServiceStarter.start(context)
    }
}
