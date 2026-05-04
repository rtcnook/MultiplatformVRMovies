# MyVRMovies KMP 跨平台迁移项目文档

本文档记录了从 Android 原生项目 `MyVRMovies` 迁移至 Kotlin Multiplatform (KMP) 项目 `MyVRMoviesMultiplatform_` 的全套架构、技术细节及运行指南。

---

## 1. 项目架构与技术栈

本项目采用 **Compose Multiplatform (CMP)** 构建，实现了一套代码运行在 Android、iOS 和 Desktop。

### 1.1 技术映射表

| 功能模块 | Android 原生 | KMP / CMP 方案 | 状态 |
| :--- | :--- | :--- | :--- |
| **UI 框架** | Jetpack Compose | Compose Multiplatform | ✅ 已迁移 |
| **图片加载** | Coil 2.x | Coil 3.x (OkHttp/Ktor) | ✅ 已修复 |
| **状态管理** | LiveData | StateFlow / ViewModel | ✅ 已转换 |
| **后台数据** | Firebase Android SDK | GitLive Firebase (Multiplatform) | ✅ 已接入 |
| **序列化** | Gson | kotlinx.serialization | ✅ 已转换 |
| **导航** | Intent / Activity | Sealed Class + Back Stack | ✅ 已实现 |

---

## 2. 如何运行 (Startup Commands)

在项目根目录下执行以下命令：

### 2.1 Android 运行
```bash
# 运行 Android 应用
./gradlew :composeApp:installDebug
```
*注：也可在 Android Studio 中直接运行 `composeApp` 配置。*

### 2.2 Desktop (Windows/macOS/Linux) 运行
```bash
# 启动桌面端应用
./gradlew :composeApp:run
```
*注：桌面端已在 `main.kt` 中硬编码 Firebase 初始化参数，无需 `google-services.json` 插件。*

### 2.3 iOS 运行 (需要 macOS)
```bash
# 启动 iOS 模拟器运行
./gradlew :composeApp:iosSimulatorArm64Run
```
*注：建议使用 Xcode 打开 `iosApp/iosApp.xcworkspace` 运行，并确保已放入 `GoogleService-Info.plist`。*

---

## 3. 关键修复说明

### 3.1 Firebase 初始化
- **Android**: 依赖 `google-services` 插件读取 `composeApp/google-services.json`。
- **Desktop**: 由于桌面端不支持该插件，在 `jvmMain/main.kt` 中手动执行 `Firebase.initialize()`。
- **数据路径**: 修正了 Firebase 中 `Upcomming` (含拼写错误) 的路径映射。

### 3.2 导航与返回键
- **Back Stack**: 在 `App.kt` 中维护了一个 `mutableStateListOf<Screen>` 栈。
- **返回处理**: 通过 `AppNavigator` 接口，使 Android 端的系统返回键能触发 `backStack.removeLast()`。

### 3.3 UI 细节修复
- **输入框**: 解决了 `GradientTextField` 渐变边框在圆角处被裁剪的问题。
- **海报卡片**: 统一了海报尺寸（140x200dp），解决了在 `LazyRow` 中撑满全屏的布局错误。
- **详情页**: 优化了信息栏字体和图标大小，适配 IMDB/时长/年份在同一行显示。

---

## 4. 待办事项

- [ ] **iOS 配置**: 下载并放置 `GoogleService-Info.plist` 到 iOS 工程中。
- [ ] **搜索逻辑**: 在 `MainViewModel` 中实现 `SearchBar` 的实时过滤。
- [ ] **视频播放**: 详情页的 Trailer 链接需接入跨平台播放器（如 Compose-Video）或系统浏览器。
- [ ] **图片加载验证**: 验证各平台在大批量数据下的海报加载稳定性。

---

## 5. 项目结构索引
- `composeApp/src/commonMain/kotlin/org/example/project/`:
    - `App.kt`: 导航与页面组装。
    - `ui/`: 各功能页面 (Splash, Login, Main, Detail)。
    - `repository/`: Firebase 数据层。
    - `viewmodel/`: 业务逻辑与状态管理。
- `composeApp/src/androidMain/`: Android 特有配置 (MainActivity)。
- `composeApp/src/jvmMain/`: Desktop 特有配置 (main.kt)。
