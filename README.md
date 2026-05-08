# wxpusher-android

wxpusher-android 是一个使用微信公众号作为通道的实时信息推送平台 Android 客户端。

📖 完整的平台文档请参阅：[WxPusher 官方文档](https://wxpusher.zjiecode.com/docs/) | 📥 [下载最新版本 APP](https://wxpusher.zjiecode.com/docs/download.html)

---

## 功能特性

- **消息推送**：接收来自 WxPusher 平台的实时消息推送
- **多通道推送**：支持华为、荣耀、小米、vivo、OPPO、魅族厂商推送通道及自建 WebSocket 长连接通道
- **微信登录**：通过微信 SDK 实现快捷登录
- **二维码扫描**：扫码关注 Topic、绑定账号等
- **账号管理**：登录、注册、绑定手机、换绑手机
- **消息列表**：浏览和管理推送消息，支持下拉刷新
- **订阅管理**：查看和管理已订阅的 Topic 列表
- **WebView**：内嵌网页查看消息详情

---

## 技术架构

本项目采用 **Kotlin Multiplatform (KMP)** 架构，业务逻辑在 `shared` 模块中跨平台共享。

```
wxpusher-android/
├── shared/                          # KMP 跨平台共享模块
│   └── src/
│       ├── commonMain/              # 通用业务逻辑（Kotlin）
│       │   └── kotlin/
│       │       ├── api/             # API 网络请求层
│       │       ├── base/            # 基础类和工具
│       │       └── page/            # 页面 MVP Contract & Presenter
│       └── androidMain/             # Android 平台特定实现
│
├── androidApp/                      # Android 原生应用
│   └── src/
│       ├── androidMain/             # 主代码
│       ├── androidOffline/          # 测试环境 Flavor
│       └── androidProd/             # 生产环境 Flavor
│
├── HiMiuix/                         # UI 组件库
├── gradle/                          # Gradle Wrapper & 版本目录
├── build.gradle.kts                 # 根构建脚本
└── settings.gradle.kts              # 项目配置
```

---

## 技术栈

### 跨平台共享（Shared）

| 技术 | 版本 | 用途 |
|------|------|------|
| Kotlin Multiplatform | 2.x | 跨平台业务逻辑共享 |
| Ktor Client | 3.2.1 | 网络请求 |
| Kotlinx Coroutines | 1.10.2 | 协程异步编程 |
| Kotlinx Serialization | - | JSON 序列化/反序列化 |
| Kotlinx DateTime | 0.6.1 | 日期时间处理 |

### Android

| 技术 | 版本 | 用途 |
|------|------|------|
| Kotlin | - | 开发语言 |
| AndroidX Core KTX | 1.15.0 | Android 核心扩展 |
| AndroidX AppCompat | 1.7.0 | 向后兼容 |
| Material Design | 1.12.0 | UI 组件库 |
| AndroidX Lifecycle | 2.8.3 | 生命周期管理 |
| WorkManager | 2.10.0 | 后台任务 |
| OkHttp | 4.12.0 | HTTP 客户端 |
| Gson | 2.10.1 | JSON 解析 |
| ZXing | 3.3.3 | 二维码扫描 |
| SmartRefreshLayout | 2.1.0 | 下拉刷新 |
| WeChat SDK | 6.8.34 | 微信登录 |
| 华为 Push SDK | 6.13.0.300 | 华为推送通道 |
| 荣耀 Push SDK | 8.0.12.307 | 荣耀推送通道 |

---

## 环境要求

- **JDK** 17+
- **Gradle** 8.5+（项目自带 Gradle Wrapper）
- **Android Studio** Ladybug (2024.2) 或更高版本
- **Android SDK**：compileSdk 35，minSdk 26，targetSdk 34

---

## 快速开始

### 克隆项目

```bash
git clone https://github.com/moonbai/wxpusher-android.git
cd wxpusher-android
```

### 构建

1. 使用 **Android Studio** 打开项目根目录
2. 等待 Gradle Sync 完成
3. 选择 Build Variant：
   - `androidOffline`：测试环境
   - `androidProd`：生产环境
4. 运行 `androidApp` 模块

```bash
# 或使用命令行构建
./gradlew :androidApp:assembleAndroidProdDebug
```

---

## 推送架构

Android 端集成了多家厂商系统推送通道，通过统一的推送管理层实现分发，可以让 APP 不保持后台运行，接收消息推送。

| 通道 | 适用设备 | 说明 |
|------|----------|------|
| 华为 Push | 华为/荣耀设备 | HMS Core 推送 |
| 荣耀 Push | 荣耀设备 | 荣耀独立推送 |
| 小米 Push | 小米/Redmi 设备 | MiPush |
| vivo Push | vivo/iQOO 设备 | vivo 推送 |
| OPPO Push | OPPO/Realme/一加 设备 | OPPO 推送 |
| 魅族 Push | 魅族设备 | Flyme 推送 |
| WebSocket | 所有设备 | 自建长连接通道（兜底方案） |

> 注意：部分厂商会绑定签名和包名，修改包名和签名后，可能无法接收推送通知。

---

## 关于作者

作者：Mars

源码仓库：https://github.com/moonbai/wxpusher-android

---

## 开源协议

本项目采用开源协议。
