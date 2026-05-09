package com.mars.wxpusher.app

import com.mars.wxpusher.base.biz.IWxpAppPageService
import com.mars.wxpusher.utils.WxpJumpPageUtils

class WxpAppPageServiceImpl : IWxpAppPageService {

    override fun jumpToLogin() {
        WxpJumpPageUtils.jumpToLogin()
    }
}