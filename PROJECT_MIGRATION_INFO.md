# MyVRMovies KMP 跨平台迁移项目文档

本文档记录了从 Android 原生项目 `MyVRMovies` 迁移至 Kotlin Multiplatform (KMP) 项目 `MyVRMoviesMultiplatform_` 的全套架构、技术细节及运行指南。

---

## 1. 项目架构与技术栈

本项目采用 **Compose Multiplatform (CMP)** 构建，实现了一套代码运行在 Android、iOS、Desktop 和 Web (WasmJS)。

### 1.1 技术映射表

| 功能模块 | Android 原生 | KMP / CMP 方案 | 状态 |
| :--- | :--- | :--- | :--- |
| **UI 框架** | Jetpack Compose | Compose Multiplatform | ✅ 已迁移 |
| **图片加载** | Coil 2.x | Coil 3.x (OkHttp/Ktor) | ✅ 已修复 |
| **状态管理** | LiveData | StateFlow / ViewModel | ✅ 已转换 |
| **后台数据** | Firebase Android SDK | GitLive Firebase / Local JSON | ✅ 已接入 |
| **序列化** | kotlinx.serialization | kotlinx.serialization | ✅ 已转换 |
| **导航** | Intent / Activity | Sealed Class + Back Stack | ✅ 已实现 |

### 1.2 版本依赖

| 组件 | 版本 |
| :--- | :--- |
| Kotlin | 2.3.20 |
| Compose Multiplatform | 1.10.3 |
| Gradle | 8.14.3 |
| AGP | 8.11.2 |
| Ktor | 3.4.1 |
| Coil 3 | 3.0.4 |
| GitLive Firebase | 1.13.0 |

---

## 2. 如何运行 (Startup Commands)

### 2.1 Android 运行
```bash
./gradlew :composeApp:installDebug
```

### 2.2 Desktop (Windows/macOS/Linux) 运行
```bash
./gradlew :composeApp:run
```
> 默认窗口大小：420×900dp（可手动调整）

### 2.3 iOS 运行 (需要 macOS)
```bash
./gradlew :composeApp:iosSimulatorArm64Run
```

### 2.4 Web (WasmJS) 运行
```bash
./gradlew :composeApp:wasmJsBrowserDevelopmentRun
```
> ⚠️ Web 端目前因部分依赖（Coil/Firebase）尚未完全支持 WasmJS，构建可能存在问题。

---

## 3. 核心机制说明

### 3.1 混合数据源策略 (Data Source Strategy)
为了保证各平台的稳定性和演示效果，采用了以下策略：
- **Android**: 继续使用 **Firebase Realtime Database**（`FirebaseMainRepository`）。
- **Desktop / iOS / Web**: 使用 **本地 JSON 文件** (`composeResources/files/database.json`)。
- **工厂模式**: 通过 `expect/actual fun createMainRepository()` 自动根据平台切换实现，UI 层完全解耦。

### 3.2 图片加载优化 (Coil 3)
- **网络引擎**: Android 端使用 `OkHttp`，Desktop 端通过 `Ktor (OkHttp)` 引擎加载。
- **配置**: 在 `App.kt` 中通过 `setSingletonImageLoaderFactory` 统一配置了 `KtorNetworkFetcherFactory`。
- **依赖隔离**: Coil 依赖已从 `commonMain` 移至 `androidMain` 和 `jvmMain`，避免 Web 端编译冲突。

### 3.3 导航系统
- **统一返回逻辑**: 在 `commonMain` 中维护导航栈，通过 `AppNavigator` 对象与 Android 原生返回键挂钩，实现了完美的跨平台返回体验。

### 3.4 Desktop 端滚动优化
- **鼠标滚轮横向滚动**: 通过自定义 `ScrollableLazyRow` 组件，将鼠标滚轮的垂直事件转为水平滚动，解决了 Desktop 端 `LazyRow` 无法用鼠标滚轮浏览的问题。

---

## 4. 项目结构

```
MyVRMoviesMultiplatform_/
├── composeApp/
│   └── src/
│       ├── commonMain/       # 共享 UI、ViewModel、Repository 接口
│       ├── androidMain/      # Android 入口、Firebase Repository
│       ├── jvmMain/          # Desktop 入口（窗口配置 420×900dp）
│       ├── iosMain/          # iOS 入口
│       └── webMain/          # Web 入口（WasmJS）
├── shared/                   # 共享业务逻辑模块
├── server/                   # Ktor 后端服务
├── iosApp/                   # iOS 壳工程
└── gradle/                   # 版本目录 & Wrapper
```

---

## 5. 迁移期间修复的关键问题

1.  **Firebase Desktop 崩溃**: 移除了在 JVM 上无法自动初始化的 Firebase 代码，改用本地 JSON 方案。
2.  **资源路径规范**: 修复了 `database.json` 位置错误导致无法生成 Resource Accessors 的问题（必须放在 `files/` 目录下）。
3.  **类型推导错误**: 修复了 `MainViewModel` 丢失导入导致的 `collectAsState` 和 `LazyRow` 编译错误。
4.  **UI 布局偏差**: 修正了海报在 `LazyRow` 中填满全屏的问题，固定为 `140×200dp`。
5.  **Desktop 窗口高度不足**: 设置默认窗口为 `420×900dp`，充分展示电影列表。
6.  **Desktop 横向滚动失效**: 新增 `ScrollableLazyRow` 组件，支持鼠标滚轮横向浏览海报。
7.  **Web 目标配置**: 添加了 WasmJS 目标及 `webMain` 源码集的基础代码框架。

---

## 6. 项目状态

目前项目已达到 **MVP (最小可行性产品)** 阶段：
- ✅ **Android**: 正常运行，Firebase 数据加载，海报显示，详情页导航。
- ✅ **Desktop**: 正常运行，本地 JSON 数据，鼠标滚轮横向滚动，窗口尺寸优化。
- ⏳ **iOS**: 代码就绪，需 macOS 构建验证。
- ⏳ **Web (WasmJS)**: 基础代码就绪，待部分依赖（Coil/Firebase）完全适配后可用。

---

## 7. Git 提交记录参考
- 初始迁移：完成 UI 转换。
- 导航增强：实现跨平台返回栈。
- 稳定性修复：解决 Firebase 桌面端兼容性与资源路径问题。
- 图片增强：配置 Coil 3 全平台网络加载。
- Desktop UX 优化：窗口尺寸 + 鼠标滚轮横向滚动。
- Web 支持：添加 WasmJS 目标及 webMain 基础代码。
