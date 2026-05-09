package com.mars.wxpusher.page.bind

import com.mars.wxpusher.api.WxpApiService
import com.mars.wxpusher.base.common.WxpBaseInfoService
import com.mars.wxpusher.base.common.WxpBaseMvpPresenter
import com.mars.wxpusher.base.common.WxpToastUtils
import com.mars.wxpusher.base.common.runAtMainSuspend
import com.mars.wxpusher.base.biz.bean.WxpLoginInfo
import com.mars.wxpusher.base.biz.WxpAppDataService
import com.mars.wxpusher.page.login.WxpLoginSendVerifyCodeReq

class WxpBindPresenter(view: IWxpBindView) :
    WxpBaseMvpPresenter<IWxpBindView, IWxpBindPresenter>(view),
    IWxpBindPresenter {

    override fun queryBindStatus(phone: String?, verifyCode: String?) {
        if (phone.isNullOrEmpty()) {
            WxpToastUtils.showToast("手机号为空，请重新登录")
            return
        }
        if (verifyCode.isNullOrEmpty()) {
            WxpToastUtils.showToast("验证码为空，请重新登录")
            return
        }

        val req = WxpLoginSendVerifyCodeReq(
            justCreateAccount = false,
            phone = phone,
            code = verifyCode,
            deviceId = WxpAppDataService.getLoginInfo()?.deviceId,
            deviceName = WxpBaseInfoService.getDeviceName(),
            pushToken = WxpAppDataService.getPushToken()
        )

        runAtMainSuspend {
            view?.showLoading(true)
            val loginData = WxpApiService.verifyCodeLogin(req)
            view?.showLoading(false)
            loginData?.let {
                if (it.phoneHasRegister == true) {
                    val loginInfo = WxpLoginInfo(it)
                    WxpAppDataService.saveLoginInfo(loginInfo)
                    WxpAppDataService.updateDeviceInfo()
                    view?.onGoMain()
                } else {
                    WxpToastUtils.showToast("绑定未完成，请先按步骤绑定 ")
                }
            }
        }
    }
}