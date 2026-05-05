# 项目信息说明

本文档只描述当前项目状态，不记录旧的修改历史。

## 项目定位

`MyVRMoviesMultiplatform` 是一个基于 Kotlin Multiplatform 和 Compose Multiplatform 的电影展示项目。项目由跨平台客户端、共享模块、Ktor 服务端和 iOS 宿主工程组成。

当前目标是让 Android、桌面端、Web 和 iOS 使用同一套 UI 与业务模型，并统一从服务端读取电影数据和图片资源。

## 总体架构

项目当前采用“客户端 + 本地服务端”的结构：

```text
server/src/main/resources/database.json
        ↓
Ktor GET /api/movies
        ↓
composeApp ServerMainRepository
        ↓
Compose UI 渲染电影列表和详情
        ↓
Coil 根据 /media/... 加载图片
```

当前核心状态：

- Android、桌面端、Web、iOS 都通过服务端接口读取电影数据。
- 电影数据来自 `server/src/main/resources/database.json`。
- 海报和演员图片来自 `server/src/main/resources/media`。
- 客户端不再依赖 Firebase 作为电影数据源。
- 客户端不再依赖 `composeApp/src/commonMain/composeResources/files` 中的旧电影资源。
- 服务端监听 `0.0.0.0:8080`，方便 Android 真机通过局域网访问。

## 模块职责

### composeApp

`composeApp` 是客户端主模块，包含 Compose Multiplatform UI、页面状态、仓库实现和平台差异代码。

主要职责：

- 展示电影列表、电影详情、演员信息等界面。
- 通过 `ServerMainRepository` 请求服务端电影数据。
- 使用 Coil 加载服务端返回的图片。
- 为 Android、桌面端、Web、iOS 提供平台实际实现。

关键目录：

```text
composeApp/src/commonMain      # 跨平台 UI 与通用业务逻辑
composeApp/src/androidMain     # Android 平台实现
composeApp/src/jvmMain         # 桌面端实现
composeApp/src/wasmJsMain      # Web/Wasm 实现
composeApp/src/iosMain         # iOS 实现
```

### server

`server` 是 Ktor 后端模块。它是当前电影数据和媒体资源的唯一来源。

主要职责：

- 从 classpath 读取 `database.json`。
- 通过 `/api/movies` 返回电影 JSON。
- 通过 `/media` 暴露海报和演员图片。
- 允许跨域请求，供 Web 客户端访问。

关键目录：

```text
server/src/main/kotlin/org/example/project/Application.kt
server/src/main/resources/database.json
server/src/main/resources/media/posters
server/src/main/resources/media/actors
```

### shared

`shared` 是共享模块，当前用于放置客户端和服务端都需要引用的通用内容。

当前关键内容：

```text
shared/src/commonMain/kotlin/org/example/project/Constants.kt
```

其中 `SERVER_PORT = 8080` 是服务端默认端口。

### iosApp

`iosApp` 是 iOS 宿主工程。Compose Multiplatform 的共享 UI 会被 iOS 工程承载运行。

## 服务端接口

服务端默认地址：

```text
http://127.0.0.1:8080
```

当前接口：

```text
GET /
GET /api/movies
GET /media/...
```

`/api/movies` 返回内容来自：

```text
server/src/main/resources/database.json
```

服务端读取 JSON 时会兼容 UTF-8 BOM，避免文件开头不可见字符导致客户端解析失败。不过仍建议将 `database.json` 保存为 UTF-8 without BOM。

静态资源接口：

```text
GET /media/posters/{fileName}
GET /media/actors/{fileName}
```

对应文件目录：

```text
server/src/main/resources/media/posters
server/src/main/resources/media/actors
```

示例图片地址：

```text
http://127.0.0.1:8080/media/posters/bad-boys.jpg
```

## 客户端数据读取

客户端通过 `ServerMainRepository` 请求：

```text
{serverBaseUrl}/api/movies
```

返回数据解析后，UI 使用电影对象中的海报和演员图片路径进行渲染。图片加载使用 Coil 3，并在 Android、桌面端和 Web 端使用 Ktor 网络 fetcher。

关键文件：

```text
composeApp/src/commonMain/kotlin/org/example/project/repository/ServerMainRepository.kt
composeApp/src/commonMain/kotlin/org/example/project/ImageLoader.kt
composeApp/src/androidMain/kotlin/org/example/project/ImageLoader.kt
composeApp/src/jvmMain/kotlin/org/example/project/ImageLoader.kt
composeApp/src/wasmJsMain/kotlin/org/example/project/ImageLoader.kt
```

## 平台服务端地址

客户端通过 `expect/actual` 为不同平台提供默认服务端地址。

```text
commonMain:
composeApp/src/commonMain/kotlin/org/example/project/repository/ServerMainRepository.kt

Android:
composeApp/src/androidMain/kotlin/org/example/project/repository/AndroidServerConfig.kt

Desktop:
composeApp/src/jvmMain/kotlin/org/example/project/repository/DesktopMainRepository.kt

Web:
composeApp/src/webMain/kotlin/org/example/project/repository/WebMainRepository.kt

iOS:
composeApp/src/iosMain/kotlin/org/example/project/repository/IosMainRepository.kt
```

当前默认值：

```text
Android -> http://192.168.2.12:8080
Desktop -> http://localhost:8080
Web     -> http://localhost:8080
iOS     -> http://localhost:8080
```

Android 真机需要访问电脑局域网 IP。如果换了网络、电脑 IP 改变，或者使用模拟器，需要修改 `AndroidServerConfig.kt`。Android 模拟器一般使用：

```text
http://10.0.2.2:8080
```

iOS 真机运行时，`localhost` 指的是手机本机，不是开发电脑。真机调试时也需要改成开发电脑的局域网 IP。

## 图片资源规范

所有服务端图片应放在：

```text
server/src/main/resources/media/posters
server/src/main/resources/media/actors
```

`database.json` 中推荐使用以 `/media/` 开头的相对 URL：

```text
"/media/posters/dune-part-two.jpg"
"/media/actors/timothee-chalamet.jpg"
```

这样客户端可以按当前平台服务端地址自动拼成完整 URL。

旧的客户端资源目录 `composeApp/src/commonMain/composeResources/files` 不再作为电影数据源使用。

## 运行方式

启动服务端：

```powershell
.\gradlew.bat :server:run
```

运行 Android：

```powershell
.\gradlew.bat :composeApp:installDebug
```

运行桌面端：

```powershell
.\gradlew.bat :composeApp:run
```

运行 Web 端：

```powershell
.\gradlew.bat :composeApp:wasmJsBrowserDevelopmentRun
```

iOS 端需要在 macOS 上通过 Xcode 打开 `iosApp` 运行。

## 常见问题

### 能显示电影列表，但不能显示图片

优先检查：

1. `database.json` 中图片路径是否以 `/media/` 开头。
2. 对应图片文件是否存在于 `server/src/main/resources/media`。
3. 浏览器能否直接打开图片 URL。
4. Android 或 iOS 真机是否使用了正确的电脑局域网 IP。
5. Logcat 中 `MyVRMovies` 输出的海报 URL 是否正确。

### Android 不能访问服务端

优先检查：

1. 服务端是否使用 `.\gradlew.bat :server:run` 启动。
2. 服务端日志是否显示 `Responding at http://127.0.0.1:8080`。
3. 电脑防火墙是否允许 8080 端口。
4. 手机和电脑是否连接同一个网络。
5. `AndroidServerConfig.kt` 中 IP 是否是当前电脑 IP。

### JSON 解析失败

如果日志出现类似 “Expected start of the object, but had BOM” 的错误，说明 JSON 文件开头可能带有 UTF-8 BOM。当前代码已经做兼容处理，但推荐继续保持 `database.json` 为 UTF-8 without BOM。

## 当前技术栈

- Kotlin Multiplatform
- Compose Multiplatform
- Ktor Server
- Ktor Client
- Kotlinx Serialization
- Coil 3
- Gradle Kotlin DSL

## 已知 Gradle 提示

Gradle 可能输出 Kotlin 默认层级模板未应用的警告，原因是项目里显式配置了部分 source set 的 `dependsOn` 关系。这个提示不影响服务端接口和图片资源的工作。

## 推荐检查命令

服务端测试：

```powershell
.\gradlew.bat :server:test
```

客户端编译：

```powershell
.\gradlew.bat :composeApp:compileDebugKotlinAndroid :composeApp:compileKotlinJvm :composeApp:compileKotlinWasmJs
```

启动服务端：

```powershell
.\gradlew.bat :server:run
```

## 维护建议

新增电影时，需要同时更新：

1. `server/src/main/resources/database.json`
2. `server/src/main/resources/media/posters`
3. `server/src/main/resources/media/actors`

更新后建议至少运行服务端测试。如果改动涉及客户端网络、模型或图片加载，还建议运行 Android、桌面端和 Web 的 Kotlin 编译检查。
