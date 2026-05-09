package com.mars.wxpusher.page.scan

import com.mars.wxpusher.base.common.IWxpBaseMvpPresenter
import com.mars.wxpusher.base.common.IWxpBaseMvpView

interface IWxpScanView : IWxpBaseMvpView<IWxpScanPresenter> {

    /***
     * 关闭扫描页面
     */
    fun onClosePage()

    /**
     * 打开一个web页面
     */
    fun onOpenWebPage(url: String)

    /**
     * 复制文本
     */
    fun onCopy(data: String)
}

interface IWxpScanPresenter :
    IWxpBaseMvpPresenter<IWxpScanView, IWxpScanPresenter> {
    /**
     * 当扫描到数据类型的时候
     */
    fun scan(data: String?)
}