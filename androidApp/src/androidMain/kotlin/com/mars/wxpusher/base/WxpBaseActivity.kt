package com.mars.wxpusher.base

import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.appbar.MaterialToolbar
import com.mars.wxpusher.page.theme.ThemeManager

open class WxpBaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onContentChanged() {
        super.onContentChanged()
        applyThemeColors()
    }

    override fun onResume() {
        super.onResume()
        applyThemeColors()
    }

    private fun applyThemeColors() {
        val color = ThemeManager.getThemeColor(this)
        
        // 查找并设置所有可能的 Toolbar/ActionBar 颜色
        val decorView = window.decorView as ViewGroup
        
        // 遍历所有子视图来查找 Toolbar
        findAndSetToolbarColor(decorView, color)
        
        // 也尝试修改 ActionBar
        supportActionBar?.let {
            try {
                it.setBackgroundDrawable(android.graphics.drawable.ColorDrawable(color))
            } catch (e: Exception) {
                // 忽略异常
            }
        }
        
        // 尝试修改 status bar 颜色
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = color
        }
    }
    
    private fun findAndSetToolbarColor(view: ViewGroup, color: Int) {
        for (i in 0 until view.childCount) {
            val child = view.getChildAt(i)
            if (child is Toolbar || child is MaterialToolbar) {
                child.setBackgroundColor(color)
            } else if (child is ViewGroup) {
                findAndSetToolbarColor(child, color)
            }
        }
    }
}
