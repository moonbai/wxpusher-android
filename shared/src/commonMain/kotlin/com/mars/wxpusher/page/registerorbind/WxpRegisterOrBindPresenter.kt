package com.mars.wxpusher.page.registerorbind

import com.mars.wxpusher.api.WxpApiService
import com.mars.wxpusher.base.biz.WxpAppDataService
import com.mars.wxpusher.base.biz.bean.WxpLoginInfo
import com.mars.wxpusher.base.common.WxpBaseInfoService
import com.mars.wxpusher.base.common.WxpBaseMvpPresenter
import com.mars.wxpusher.base.common.WxpLoadingUtils
import com.mars.wxpusher.base.common.WxpLogUtils
import com.mars.wxpusher.base.common.WxpToastUtils
import com.mars.wxpusher.base.common.runAtMainSuspend
import com.mars.wxpusher.page.login.WxpAppleLoginReq
import com.mars.wxpusher.page.login.WxpBindPageData
import com.mars.wxpusher.page.login.WxpLoginSendVerifyCodeReq
import com.mars.wxpusher.page.login.WxpWeixinLoginReq

class WxpRegisterOrBindPresenter(view: IWxpRegisterOrBindView) :
    WxpBaseMvpPresenter<IWxpRegisterOrBindView, IWxpRegisterOrBindPresenter>(view),
    IWxpRegisterOrBindPresenter {
    override fun weixinBind(code: String?, bindData: WxpBindPageData?) {
        if (bindData == null) {
            return
        }
        if (code.isNullOrEmpty()) {
            WxpToastUtils.showToast("微信授权码错误")
            return
        }
        val weixinLoginReq = WxpWeixinLoginReq(
            code,
            bindCode = bindData.phoneLogin?.phoneVerifyCode, //绑定手机号
            appleLoginJwtCode = bindData.appleLogin?.code, //绑定苹果账号
            appleName = bindData.appleLogin?.name, //苹果账号的名称
            deviceId = WxpAppDataService.getLoginInfo()?.deviceId,
            deviceName = WxpBaseInfoService.getDeviceName(),
            pushToken = WxpAppDataService.getPushToken()
        )

        runAtMainSuspend {
            WxpLoadingUtils.showLoading(msg = "绑定中...")
            val loginData = WxpApiService.weixinLogin(weixinLoginReq)
            WxpLoadingUtils.dismissLoading()
            loginData?.let {
                val loginInfo = WxpLoginInfo(it)
                WxpAppDataService.saveLoginInfo(loginInfo)
                WxpAppDataService.updateDeviceInfo()
                view?.onGoMain()
            }
        }
    }

    /**
     * 手机号或者苹果登录的时候，可以不绑定账号，直接创建新账号
     */
    override fun createAccount(bindData: WxpBindPageData?) {
        if (bindData == null) {
            return
        }
        val phoneLogin = bindData.phoneLogin
        if (phoneLogin != null) {
            val req = WxpLoginSendVerifyCodeReq(
                justCreateAccount = true,
                phone = bindData.phoneLogin.phone,
                code = bindData.phoneLogin.code,
                deviceId = WxpAppDataService.getLoginInfo()?.deviceId,
                deviceName = WxpBaseInfoService.getDeviceName(),
                pushToken = WxpAppDataService.getPushToken()
            )

            runAtMainSuspend {
                WxpLoadingUtils.showLoading(msg = "处理中...")
                val loginData = WxpApiService.verifyCodeLogin(req)
                WxpLoadingUtils.dismissLoading()
                loginData?.let {
                    val loginInfo = WxpLoginInfo(it)
                    WxpAppDataService.saveLoginInfo(loginInfo)
                    WxpAppDataService.updateDeviceInfo()
                    view?.onGoMain()
                }
            }
            return
        }

        val appleLogin = bindData.appleLogin
        if (appleLogin != null) {
            if (appleLogin.code.isNullOrEmpty()) {
                WxpToastUtils.showToast("苹果登录信息为空")
                return
            }
            val appleLoginReq = WxpAppleLoginReq(
                justCreateAccount = true,
                code = appleLogin.code,
                name = appleLogin.name,
                deviceId = WxpAppDataService.getLoginInfo()?.deviceId,
                deviceName = WxpBaseInfoService.getDeviceName(),
                pushToken = WxpAppDataService.getPushToken()
            )

            runAtMainSuspend {
                WxpLoadingUtils.showLoading(msg = "处理中...")
                val loginData = WxpApiService.appleLogin(appleLoginReq)
                WxpLoadingUtils.dismissLoading()
                loginData?.let {
                    WxpLogUtils.i(message = "登录直接注册苹果账号成功")
                    val loginInfo = WxpLoginInfo(it)
                    WxpAppDataService.saveLoginInfo(loginInfo)
                    WxpAppDataService.updateDeviceInfo()
                    view?.onGoMain()
                }
            }
            return
        }
    }
}