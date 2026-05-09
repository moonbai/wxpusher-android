package com.smjcco.wxpusher.page.login

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.hchen.himiuix.widget.MiuixCheckBox
import com.hchen.himiuix.widget.MiuixEditText
import com.smjcco.wxpusher.R
import com.smjcco.wxpusher.base.WxpBaseMvpActivity
import com.smjcco.wxpusher.base.common.WxpDialogParams
import com.smjcco.wxpusher.base.common.WxpDialogUtils
import com.smjcco.wxpusher.common.WxpConstants
import com.smjcco.wxpusher.utils.WxpJumpPageUtils

class WxpLoginActivity : WxpBaseMvpActivity<IWxpLoginPresenter>(), IWxpLoginView {

    private lateinit var phoneTextField: MiuixEditText
    private lateinit var codeTextField: MiuixEditText
    private lateinit var getCodeButton: MaterialButton
    private lateinit var loginButton: MaterialButton
    private lateinit var privacyCheckbox: MiuixCheckBox
    private lateinit var privacyLabel: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initViews()
        setupClickListeners()
        setupPrivacyLabel()

        presenter.init()
    }

    private fun initViews() {
        supportActionBar?.hide()
        phoneTextField = findViewById(R.id.phone_text_field)
        codeTextField = findViewById(R.id.code_text_field)
        getCodeButton = findViewById(R.id.get_code_button)
        loginButton = findViewById(R.id.login_button)
        privacyCheckbox = findViewById(R.id.privacy_checkbox)
        privacyLabel = findViewById(R.id.privacy_label)
    }

    private fun setupClickListeners() {
        getCodeButton.setOnClickListener {
            val phone = phoneTextField.getText()?.toString()
            presenter.sendVerifyCode(phone = phone)
        }

        loginButton.setOnClickListener {
            if (!privacyCheckbox.isChecked) {
                checkPrivacyAgree {
                    val phone = phoneTextField.getText()?.toString()
                    val code = codeTextField.getText()?.toString()
                    presenter.verifyCodeLogin(phone = phone, verifyCode = code)
                }
                return@setOnClickListener
            }

            val phone = phoneTextField.getText()?.toString()
            val code = codeTextField.getText()?.toString()
            presenter.verifyCodeLogin(phone = phone, verifyCode = code)
        }
    }

    private fun setupPrivacyLabel() {
        val text = getString(R.string.login_privacy_text)
        val spannableString = SpannableString(text)

        // 设置"隐私协议和用户协议"部分为可点击的蓝色文字
        val titleText = "隐私协议和用户协议"
        val startIndex = text.indexOf(titleText)
        val endIndex = startIndex + titleText.length

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                // 跳转到隐私协议页面
                jumpToPrivacy()
            }
        }

        spannableString.setSpan(
            clickableSpan,
            startIndex,
            endIndex,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorPrimary)),
            startIndex,
            endIndex,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        privacyLabel.text = spannableString
        privacyLabel.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun jumpToPrivacy() {
        WxpJumpPageUtils.jumpToWebUrl(WxpConstants.PrivacyUrl, this)
    }

    /**
     * 检查隐私协议是否同意，如果未同意则显示对话框
     * 参考iOS的实现：checkPrivacyAgree方法
     * @param run 同意后执行的逻辑
     */
    private fun checkPrivacyAgree(run: () -> Unit) {
        val params = WxpDialogParams()
        params.title = "请同意用户和隐私协议"
        params.message = "我已经阅读并且同意《用户和隐私协议》"
        params.leftText = "取消"
        params.rightText = "同意协议"
        params.rightBlock = {
            privacyCheckbox.isChecked = true
            run()
        }
        WxpDialogUtils.showDialog(params)
    }

    override fun createPresenter(): IWxpLoginPresenter {
        return WxpLoginPresenter(this)
    }

    override fun onSendButtonText(msg: String, loading: Boolean) {
        runOnUiThread {
            if (loading) {
                getCodeButton.isEnabled = false
                getCodeButton.text = getString(R.string.login_sending)
            } else {
                getCodeButton.isEnabled = true
                getCodeButton.text = msg
            }
        }
    }

    override fun onGoBindOrCreateAccount(data: WxpBindPageData) {
        WxpJumpPageUtils.jumpToRegisterOrBind(data, this)
        finish()
    }


    override fun onGoMain() {
        WxpJumpPageUtils.jumpToMain(this)
        finish()
    }
}