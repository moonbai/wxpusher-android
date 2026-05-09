package com.mars.wxpusher.page.web.bridge.handlers

import com.mars.wxpusher.base.common.WxpToastUtils
import com.mars.wxpusher.page.web.bridge.BridgeActionHandler
import com.mars.wxpusher.page.web.bridge.BridgeContext
import com.mars.wxpusher.page.web.bridge.BridgeRequest
import com.mars.wxpusher.page.web.bridge.WxpBridgeEmitter

object ShowToastBridgeHandler : BridgeActionHandler {
    override fun handle(request: BridgeRequest, context: BridgeContext, emitter: WxpBridgeEmitter) {
        val msg = request.data["msg"] as? String
        if (msg.isNullOrBlank()) {
            emitter.sendBridgeCallback(
                callbackId = request.callbackId,
                success = false,
                error = "msg is empty"
            )
            return
        }
        WxpToastUtils.showToast(msg)
        emitter.sendBridgeCallback(
            callbackId = request.callbackId,
            success = true
        )
    }
}
