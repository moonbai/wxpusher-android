package com.mars.wxpusher.app

import com.mars.wxpusher.base.common.IWxpBaseInfoServiceListener
import com.mars.wxpusher.utils.DeviceUtils

class WxpBaseInfoServiceImpl : IWxpBaseInfoServiceListener {
    override fun getPlatform(): String {
        return DeviceUtils.getPlatform().getPlatform()
    }
}