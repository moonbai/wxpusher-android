package com.mars.wxpusher.page.main.fragment

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mars.wxpusher.R
import com.mars.wxpusher.WxpConfig
import com.mars.wxpusher.base.WxpBaseFragment
import com.mars.wxpusher.base.biz.WxpAppDataService
import com.mars.wxpusher.base.common.WxpToastUtils
import com.mars.wxpusher.common.WxpConstants
import com.mars.wxpusher.utils.PermissionUtils
import com.mars.wxpusher.utils.WxpJumpPageUtils

class ProfileFragment : WxpBaseFragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProfileAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI(view)
        setupData()
    }

    private fun setupUI(view: View) {
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = ProfileAdapter { item ->
            item.action?.invoke()
        }
        recyclerView.adapter = adapter
    }

    private fun setupData() {
        val sectionData = mutableListOf<ProfileSection>()
        val loginInfo = WxpAppDataService.getLoginInfo()
        val uid = loginInfo?.uid ?: ""
        val spt = loginInfo?.spt ?: ""
        val deviceId = loginInfo?.deviceId ?: ""

        sectionData.add(
            ProfileSection(
                title = "设备和账号",
                isExpandable = true,
                isExpanded = true,
                items = listOf(
                    ProfileItem(title = "UID", subtitle = if (uid.isEmpty()) "未登录" else uid, hasArrow = true) {
                        copyToClipboard(uid, "UID复制成功")
                    },
                    ProfileItem(title = "SPT", subtitle = if (spt.isEmpty()) "未登录" else spt, hasArrow = true) {
                        copyToClipboard(spt, "SPT复制成功")
                    },
                    ProfileItem(title = "设备ID", subtitle = deviceId, hasArrow = true) {
                        copyToClipboard(deviceId, "设备ID复制成功")
                    },
                    ProfileItem(title = "账号信息", subtitle = "管理账号", hasArrow = true) {
                        WxpJumpPageUtils.jumpToAccountDetail(requireActivity())
                    },
                    ProfileItem(title = "推送渠道", subtitle = "管理消息接收渠道", hasArrow = true) {
                        WxpJumpPageUtils.jumpToWebUrl("${WxpConfig.appFeUrl}/app/#/push-channel", activity)
                    }
                )
            ))

        sectionData.add(
            ProfileSection(
                title = "通知管理",
                isExpandable = true,
                isExpanded = true,
                items = listOf(
                    ProfileItem(title = "通知设置", subtitle = "检查通知权限", hasArrow = true) {
                        checkNotificationPermission()
                    },
                    ProfileItem(title = "推送检查", subtitle = "收不到消息的异常排查", hasArrow = true) {
                        openPushCheckUrl()
                    }
                )
            ))

        sectionData.add(
            ProfileSection(
                title = "通用",
                isExpandable = false,
                isExpanded = true,
                items = listOf(
                    ProfileItem(title = "主题设置", subtitle = "颜色和深色模式", hasArrow = true) {
                        WxpJumpPageUtils.jumpToThemeSettings(requireActivity())
                    },
                    ProfileItem(title = "关于", subtitle = "作者: Mars", hasArrow = true) {
                        WxpJumpPageUtils.jumpToAbout(requireActivity())
                    }
                )
            ))

        adapter.setData(sectionData)
        adapter.notifyDataSetChanged()
    }

    private fun copyToClipboard(text: String, successMessage: String) {
        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("WxPusher", text)
        clipboard.setPrimaryClip(clip)
        WxpToastUtils.showToast(successMessage)
    }

    private fun checkNotificationPermission() {
        val hasPermission = PermissionUtils.hasNotificationPermission(requireActivity())
        if (hasPermission) {
            WxpToastUtils.showToast("你已经打开通知权限")
        } else {
            PermissionUtils.gotoNotificationSettingPage()
        }
    }

    private fun openPushCheckUrl() {
        val url = "https://wxpusher.zjiecode.com/docs/open-app-note/index.html?brand=Android"
        WxpJumpPageUtils.jumpToWebUrl(url, requireActivity())
    }

    data class ProfileSection(
        val title: String,
        val isExpandable: Boolean = false,
        val isExpanded: Boolean = true,
        val items: List<ProfileItem>
    )

    data class ProfileItem(
        val title: String,
        val subtitle: String,
        val hasArrow: Boolean = false,
        val isEnabled: Boolean = true,
        val action: (() -> Unit)? = null
    )

    private class ProfileAdapter(
        private val onItemClick: (ProfileItem) -> Unit
    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        companion object {
            private const val TYPE_HEADER = 0
            private const val TYPE_ITEM = 1
        }

        private val items = mutableListOf<Any>()
        private val expandedStates = mutableMapOf<String, Boolean>()

        fun setData(sections: List<ProfileSection>) {
            expandedStates.clear()
            sections.forEach { section ->
                expandedStates[section.title] = section.isExpanded
            }
            rebuildItems(sections)
        }

        private fun rebuildItems(sections: List<ProfileSection>) {
            items.clear()
            sections.forEach { section ->
                items.add(section)
                if (expandedStates[section.title] == true) {
                    items.addAll(section.items)
                }
            }
        }

        override fun getItemViewType(position: Int): Int {
            return when (items[position]) {
                is ProfileSection -> TYPE_HEADER
                is ProfileItem -> TYPE_ITEM
                else -> TYPE_ITEM
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            return when (viewType) {
                TYPE_HEADER -> {
                    val view = inflater.inflate(R.layout.item_profile_section_header, parent, false)
                    SectionHeaderViewHolder(view)
                }
                else -> {
                    val view = inflater.inflate(R.layout.item_profile, parent, false)
                    ItemViewHolder(view)
                }
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            when (holder) {
                is SectionHeaderViewHolder -> {
                    val section = items[position] as ProfileSection
                    holder.bind(section, position)
                }
                is ItemViewHolder -> {
                    val item = items[position] as ProfileItem
                    val isLastInSection = isLastItemInSection(position)
                    holder.bind(item, onItemClick, isLastInSection)
                }
            }
        }

        private fun isLastItemInSection(position: Int): Boolean {
            if (position == items.size - 1) return true
            if (position + 1 < items.size && items[position + 1] is ProfileSection) return true
            return false
        }

        override fun getItemCount(): Int = items.size

        private fun rotateExpandIcon(imageView: ImageView, isExpanded: Boolean) {
            val fromAngle = if (isExpanded) 0f else 90f
            val toAngle = if (isExpanded) 90f else 0f
            val rotateAnimation = RotateAnimation(fromAngle, toAngle, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f)
            rotateAnimation.duration = 200
            rotateAnimation.fillAfter = true
            imageView.startAnimation(rotateAnimation)
        }

        private inner class SectionHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val titleTextView: TextView = itemView.findViewById(R.id.tv_section_title)
            private val expandIcon: ImageView = itemView.findViewById(R.id.iv_expand_icon)

            fun bind(section: ProfileSection, position: Int) {
                titleTextView.text = section.title

                if (section.isExpandable) {
                    expandIcon.visibility = View.VISIBLE
                    val isExpanded = expandedStates[section.title] == true
                    expandIcon.rotation = if (isExpanded) 90f else 0f

                    itemView.setOnClickListener {
                        val currentState = expandedStates[section.title] ?: true
                        val newState = !currentState
                        expandedStates[section.title] = newState

                        val sections = mutableListOf<ProfileSection>()
                        items.forEach { item ->
                            if (item is ProfileSection) {
                                sections.add(item)
                            }
                        }
                        rebuildItems(sections)
                        notifyDataSetChanged()
                    }
                } else {
                    expandIcon.visibility = View.GONE
                    itemView.setOnClickListener(null)
                }
            }
        }

        private class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val titleTextView: TextView = itemView.findViewById(R.id.tv_title)
            private val subtitleTextView: TextView = itemView.findViewById(R.id.tv_subtitle)
            private val arrowImageView: ImageView = itemView.findViewById(R.id.iv_arrow)
            private val dividerView: View = itemView.findViewById(R.id.divider)

            fun bind(item: ProfileItem, onItemClick: (ProfileItem) -> Unit, isLastInSection: Boolean) {
                titleTextView.text = item.title
                subtitleTextView.text = item.subtitle
                arrowImageView.visibility = if (item.hasArrow) View.VISIBLE else View.GONE
                dividerView.visibility = if (isLastInSection) View.GONE else View.VISIBLE
                itemView.isEnabled = item.isEnabled
                itemView.alpha = if (item.isEnabled) 1.0f else 0.5f

                if (item.isEnabled) {
                    itemView.setOnClickListener { onItemClick(item) }
                } else {
                    itemView.setOnClickListener(null)
                    itemView.isClickable = false
                }
            }
        }
    }
}
