package com.smjcco.wxpusher.page.about

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import com.google.android.material.card.MaterialCardView
import com.smjcco.wxpusher.BuildConfig
import com.smjcco.wxpusher.R
import com.smjcco.wxpusher.base.WxpBaseActivity
import com.smjcco.wxpusher.utils.WxpJumpPageUtils

class AboutActivity : WxpBaseActivity() {

    private lateinit var tvVersion: TextView
    private lateinit var cardSourceCode: MaterialCardView
    private lateinit var cardOfficialSite: MaterialCardView
    private lateinit var cardFeedback: MaterialCardView

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, AboutActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        supportActionBar?.hide()

        initViews()
        setupData()
        setupClickListeners()
    }

    private fun initViews() {
        tvVersion = findViewById(R.id.tvVersion)
        cardSourceCode = findViewById(R.id.cardSourceCode)
        cardOfficialSite = findViewById(R.id.cardOfficialSite)
        cardFeedback = findViewById(R.id.cardFeedback)
    }

    private fun setupData() {
        tvVersion.text = "版本 ${BuildConfig.VERSION_NAME}"
    }

    private fun setupClickListeners() {
        cardSourceCode.setOnClickListener {
            WxpJumpPageUtils.jumpToWebUrl(
                "https://github.com/moonbai/wxpusher-app",
                this
            )
        }

        cardOfficialSite.setOnClickListener {
            WxpJumpPageUtils.jumpToWebUrl(
                "https://wxpusher.zjiecode.com",
                this
            )
        }

        cardFeedback.setOnClickListener {
            WxpJumpPageUtils.jumpToWebUrl(
                "https://github.com/moonbai/wxpusher-app/issues",
                this
            )
        }
    }
}
