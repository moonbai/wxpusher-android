package com.smjcco.wxpusher.page.theme

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.smjcco.wxpusher.R
import com.smjcco.wxpusher.base.WxpBaseActivity

class ThemeSettingsActivity : WxpBaseActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var rvThemeColors: RecyclerView
    private lateinit var modeLight: View
    private lateinit var modeDark: View
    private lateinit var modeSystem: View
    private lateinit var rbLight: RadioButton
    private lateinit var rbDark: RadioButton
    private lateinit var rbSystem: RadioButton

    private var themeColorAdapter: ThemeColorAdapter? = null

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, ThemeSettingsActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_theme_settings)

        initViews()
        setupToolbar()
        setupThemeColors()
        setupDarkModeOptions()
        updateDarkModeSelection()
    }

    private fun initViews() {
        toolbar = findViewById(R.id.toolbar)
        rvThemeColors = findViewById(R.id.rvThemeColors)
        modeLight = findViewById(R.id.modeLight)
        modeDark = findViewById(R.id.modeDark)
        modeSystem = findViewById(R.id.modeSystem)
        rbLight = findViewById(R.id.rbLight)
        rbDark = findViewById(R.id.rbDark)
        rbSystem = findViewById(R.id.rbSystem)
    }

    private fun setupToolbar() {
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupThemeColors() {
        val colors = listOf(
            0xFF0088FF.toInt(),
            0xFF07C160.toInt(),
            0xFFFF9500.toInt(),
            0xFFFF3B30.toInt(),
            0xFF5856D6.toInt(),
            0xFFFF2D55.toInt(),
            0xFFAF52DE.toInt(),
            0xFF00C7BE.toInt()
        )

        themeColorAdapter = ThemeColorAdapter(colors) { color ->
            ThemeManager.setThemeColor(this, color)
            recreate()
        }

        rvThemeColors.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvThemeColors.adapter = themeColorAdapter
        themeColorAdapter?.setSelectedColor(ThemeManager.getThemeColor(this))
    }

    private fun setupDarkModeOptions() {
        modeLight.setOnClickListener { setDarkMode(ThemeMode.LIGHT) }
        modeDark.setOnClickListener { setDarkMode(ThemeMode.DARK) }
        modeSystem.setOnClickListener { setDarkMode(ThemeMode.SYSTEM) }
    }

    private fun updateDarkModeSelection() {
        when (ThemeManager.getDarkMode(this)) {
            ThemeMode.LIGHT -> {
                rbLight.isChecked = true
                rbDark.isChecked = false
                rbSystem.isChecked = false
            }
            ThemeMode.DARK -> {
                rbLight.isChecked = false
                rbDark.isChecked = true
                rbSystem.isChecked = false
            }
            ThemeMode.SYSTEM -> {
                rbLight.isChecked = false
                rbDark.isChecked = false
                rbSystem.isChecked = true
            }
        }
    }

    private fun setDarkMode(mode: ThemeMode) {
        ThemeManager.setDarkMode(this, mode)
        AppCompatDelegate.setDefaultNightMode(
            when (mode) {
                ThemeMode.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
                ThemeMode.DARK -> AppCompatDelegate.MODE_NIGHT_YES
                ThemeMode.SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
        )
        updateDarkModeSelection()
    }
}
