package com.mars.wxpusher.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mars.wxpusher.page.theme.ThemeManager

open class WxpBaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        applyThemeColors()
    }

    protected open fun applyThemeColors() {
        val color = ThemeManager.getThemeColor(this)
        supportActionBar?.let {
            try {
                it.setBackgroundDrawable(android.graphics.drawable.ColorDrawable(color))
            } catch (e: Exception) {
                // 忽略异常
            }
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = color
        }
    }
}
