package com.mars.wxpusher.push

import android.app.Activity
import android.app.Application
import com.mars.wxpusher.base.biz.WxpAppDataService
import com.mars.wxpusher.base.common.ApplicationUtils
import com.mars.wxpusher.base.common.WxpLogUtils
import com.mars.wxpusher.bean.DevicePlatform
import com.mars.wxpusher.push.honor.HonorPushUtils
import com.mars.wxpusher.push.huawei.HuaweiPushUtils
import com.mars.wxpusher.push.meizu.MeizuPushUtils
import com.mars.wxpusher.push.oppo.OppoPushUtils
import com.mars.wxpusher.push.vivo.VIVOPushUtils
import com.mars.wxpusher.push.ws.WxpNotificationManager
import com.mars.wxpusher.push.ws.connect.WsManager
import com.mars.wxpusher.push.ws.keepalive.KeepWsAliveServiceStarter
import com.mars.wxpusher.push.xiaomi.XiaomiUtils
import com.mars.wxpusher.utils.DeviceUtils
import com.mars.wxpusher.utils.PermissionUtils
import com.mars.wxpusher.utils.ThreadUtils

interface IPushTokenChangedListener {
    fun onPushToken(platform: DevicePlatform, pushToken: String)
}

/**
 * 管理push的一堆事儿，对厂商和通道做抽象
 */
object PushManager : Runnable {
    private val TAG = "PushManager"

    private val pushTokenChangedListenerList: MutableList<IPushTokenChangedListener> =
        mutableListOf()

    /**
     * 初始化推送
     */
    fun init(application: Application = ApplicationUtils.getApplication()) {
        if (!ApplicationUtils.isMainProcess()) {
            WxpLogUtils.i(TAG, "非主进程，不初始化")
            return
        }

        val platform = DeviceUtils.getPlatform()
        if (platform == DevicePlatform.Android_XIAOMI) {
            WxpLogUtils.i(TAG, "初始化小米推送")
            XiaomiUtils.init(application)
        } else if (platform == DevicePlatform.Android_VIVO) {
            WxpLogUtils.i(TAG, "初始化VIVO推送")
            VIVOPushUtils.init(ApplicationUtils.getApplication())
        } else if (platform == DevicePlatform.Android_HONOR) {
            WxpLogUtils.i(TAG, "初始化荣耀推送")
            HonorPushUtils.init(application)
        } else if (platform == DevicePlatform.Android_HUAWEI) {
            WxpLogUtils.i(TAG, "初始化华为推送")
            HuaweiPushUtils.init(application)
        } else if (platform == DevicePlatform.Android_OPPO) {
            WxpLogUtils.i(TAG, "初始化OPPO推送")
            OppoPushUtils.init(application)
        } else if (platform == DevicePlatform.Android_MEIZU) {
            WxpLogUtils.i(TAG, "初始化魅族推送")
            MeizuPushUtils.init(application)
        } else {
            WxpLogUtils.i(TAG, "初始化自建长链接")
            WxpNotificationManager.init()
            WsManager.init()
            //启动保活，必须在最后
            KeepWsAliveServiceStarter.start(application)
        }

        //如果不是安卓，厂商通道设置token注册超时，10秒超时以后，走自建ws推送通道
        if (platform != DevicePlatform.Android) {
            ThreadUtils.runOnMainThread(this, 10 * 1000)
        }
    }


    override fun run() {
        val platform = DeviceUtils.getPlatform()
        WxpLogUtils.i(
            TAG,
            "获取厂商pushToken超时，platform=【" + platform.getPlatform() + "】，初始化自建长链接"
        )
        onGetPushTokenFail(platform)
    }

    /**
     * 当获取pushtoken失败的时候回调
     */
    fun onGetPushTokenFail(platform: DevicePlatform) {
        if (platform != DevicePlatform.Android) {
            WxpLogUtils.i(
                TAG,
                "获取厂商pushToken失败【" + platform.getPlatform() + "】，初始化自建长链接"
            )
            ThreadUtils.getMainThreadHandler().removeCallbacks(this)
            //厂商推送注册失败了，设备为安卓，默认走ws通道
            DeviceUtils.setPlatform(DevicePlatform.Android)
            init()
        }
    }

    /**
     * 当获取到推动token的时候，管理token的上报，更新
     */
    fun onGetPushToken(token: String, platform: DevicePlatform) {
        WxpLogUtils.i(TAG, "收到设备token，platform=${platform}, token=${token}")
        ThreadUtils.getMainThreadHandler().removeCallbacks(this)
        WxpAppDataService.savePushToken(token)
        WxpAppDataService.updateDeviceInfo(platform.getPlatform())

        // 发送pushToken变更的通知
        ThreadUtils.runOnMainThread {
            for (listener in pushTokenChangedListenerList) {
                listener.onPushToken(platform, token)
            }
        }
    }

    fun addPushTokenChangedListener(listener: IPushTokenChangedListener) {
        pushTokenChangedListenerList.add(listener)
    }

    fun removePushTokenChangedListener(listener: IPushTokenChangedListener) {
        pushTokenChangedListenerList.remove(listener)
    }

    /**
     * 显示打开通知提醒的弹窗
     */
    fun showOpenNoteRemindSettingDialog(activity: Activity) {
        //没登录不提醒
        if (WxpAppDataService.getLoginInfo()?.deviceToken.isNullOrEmpty()) {
            return
        }
        //没有推送id，不提醒
        if (WxpAppDataService.getPushToken().isNullOrEmpty()) {
            return
        }
        //没有推送权限不提醒
        if (!PermissionUtils.hasNotificationPermission(activity)) {
            return
        }
        val platform = DeviceUtils.getPlatform()
        if (platform == DevicePlatform.Android_XIAOMI) {
            XiaomiUtils.showSettingGuide(activity)
        } else if (platform == DevicePlatform.Android_VIVO) {
            CommonUtils.showSettingGuide(activity)
        } else if (platform == DevicePlatform.Android_HONOR) {
            CommonUtils.showSettingGuide(activity)
        } else if (platform == DevicePlatform.Android_HUAWEI) {
            //华为打开app的时候，不让弹窗，因此先注释调
//            CommonUtils.showSettingGuide(activity)
        } else if (platform == DevicePlatform.Android_OPPO) {
            CommonUtils.showSettingGuide(activity)
        } else if (platform == DevicePlatform.Android_MEIZU) {
            MeizuPushUtils.showSettingGuide(activity)
        }
    }

    fun getGuidePageUrl(): String {
        val platform = DeviceUtils.getPlatform()
        return "https://wxpusher.zjiecode.com/docs/open-app-note/index.html?brand=%s".format(
            platform.getPlatform()
        )
    }


}