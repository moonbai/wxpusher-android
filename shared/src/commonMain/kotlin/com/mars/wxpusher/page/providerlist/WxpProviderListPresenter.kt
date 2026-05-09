package com.mars.wxpusher.page.providerlist

import com.mars.wxpusher.WxpConfig
import com.mars.wxpusher.api.WxpApiService
import com.mars.wxpusher.base.common.WxpBaseMvpPresenter
import com.mars.wxpusher.base.common.WxpToastUtils
import com.mars.wxpusher.base.common.runAtMainSuspend
import com.mars.wxpusher.base.biz.WxpAppDataService

class WxpProviderListPresenter(view: IWxpProviderListView) :
    WxpBaseMvpPresenter<IWxpProviderListView, IWxpProviderListPresenter>(view),
    IWxpProviderListPresenter {
    override fun loadPage() {
        runAtMainSuspend {
            var openId = WxpAppDataService.getLoginInfo()?.openId
            if (openId.isNullOrEmpty()) {
                openId = WxpApiService.getOpenId()
                if (!openId.isNullOrEmpty()) {
                    WxpAppDataService.saveOpenId(openId)
                }
            }
            if (openId.isNullOrEmpty()) {
                WxpToastUtils.showToast("获取openId失败，请重试")
                return@runAtMainSuspend
            }
            view?.onLoadPage("${WxpConfig.appFeUrl}/app#/market")
        }
    }
}