package com.mars.wxpusher.page.providerlist

import com.mars.wxpusher.base.common.IWxpBaseMvpPresenter
import com.mars.wxpusher.base.common.IWxpBaseMvpView

interface IWxpProviderListView : IWxpBaseMvpView<IWxpProviderListPresenter> {
    fun onLoadPage(url: String);
}

interface IWxpProviderListPresenter :
    IWxpBaseMvpPresenter<IWxpProviderListView, IWxpProviderListPresenter> {
    fun loadPage();
}