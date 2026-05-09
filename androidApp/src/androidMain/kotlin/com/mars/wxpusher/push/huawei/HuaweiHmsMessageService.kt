package com.mars.wxpusher.push.huawei

import android.os.Build
import com.huawei.hms.push.HmsMessageService
import com.mars.wxpusher.base.common.WxpLogUtils
import com.mars.wxpusher.bean.DevicePlatform
import com.mars.wxpusher.push.PushManager
import com.mars.wxpusher.utils.DeviceUtils


class HuaweiHmsMessageService : HmsMessageService() {
    private val TAG = "Huawei"
    override fun onNewToken(s: String?) {
        if (s.isNullOrEmpty()) {
            WxpLogUtils.w(TAG, "华为推送-onNewToken=null")
            PushManager.onGetPushTokenFail(DevicePlatform.Android_HONOR)
            return
        }
        WxpLogUtils.i(TAG, "华为推送-通过HuaweiHmsMessageService获取token=" + s)
        if (DeviceUtils.getPlatform() == DevicePlatform.Android_HUAWEI) {
            PushManager.onGetPushToken(s, DevicePlatform.Android_HUAWEI)
        } else {
            //安装有HCM的时候 ，可能会自动回调token，所以不进行回调，避免被覆盖
            WxpLogUtils.i(TAG, "华为推送-但是是[" + Build.MANUFACTURER + "]设备，忽略华为token=" + s)
        }
    }
}