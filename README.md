# MyVRMoviesMultiplatform

这是一个 Kotlin Multiplatform / Compose Multiplatform 电影列表项目。当前项目由一个跨平台客户端和一个本地 Ktor 服务端组成：客户端负责 Android、桌面端、Web 和 iOS 的界面展示，服务端负责提供电影 JSON 数据和图片静态资源。

## 项目结构

```text
.
├── composeApp/   # Compose Multiplatform 客户端
├── server/       # Ktor 服务端，提供接口和图片资源
├── shared/       # 客户端和服务端共享的 Kotlin 常量与逻辑
├── iosApp/       # iOS 宿主工程
└── gradle/       # Gradle Wrapper 与版本配置
```

## 当前架构

客户端不再直接读取本地 JSON，也不再使用 Firebase 作为电影数据来源。所有平台都通过 `composeApp/src/commonMain/kotlin/org/example/project/repository/ServerMainRepository.kt` 请求服务端接口。

服务端位于 `server` 模块，启动后监听 `0.0.0.0:8080`。端口来自 `shared/src/commonMain/kotlin/org/example/project/Constants.kt` 中的 `SERVER_PORT`。

服务端提供两个主要访问入口：

```text
GET /api/movies          # 返回电影数据 JSON
GET /media/...           # 返回海报和演员图片
```

电影数据文件放在：

```text
server/src/main/resources/database.json
```

图片资源放在：

```text
server/src/main/resources/media/posters
server/src/main/resources/media/actors
```

`database.json` 中的图片字段应指向服务端静态资源路径，例如：

```text
/media/posters/bad-boys.jpg
/media/actors/will-smith.jpg
```

客户端拿到相对路径后，会拼接当前平台的服务端地址，最终加载完整图片 URL。

## 平台地址配置

不同平台的默认服务端地址在各自 source set 中配置：

```text
Android: http://192.168.2.12:8080
Desktop: http://localhost:8080
Web:     http://localhost:8080
iOS:     http://localhost:8080
```

Android 真机访问电脑上的服务端时，需要使用电脑在同一局域网中的 IP 地址，并确保手机和电脑处在同一个网络下。当前 Android 地址写在：

```text
composeApp/src/androidMain/kotlin/org/example/project/repository/AndroidServerConfig.kt
```

如果使用 Android 模拟器，通常需要把地址改成：

```text
http://10.0.2.2:8080
```

## 图片加载

项目使用 Coil 3 加载网络图片。Android、桌面端和 Web 端的 `ImageLoader` 都使用 `coil-network-ktor3` 的 `KtorNetworkFetcherFactory`，避免不同平台网络实现不一致导致图片加载失败。

相关文件：

```text
composeApp/src/commonMain/kotlin/org/example/project/ImageLoader.kt
composeApp/src/androidMain/kotlin/org/example/project/ImageLoader.kt
composeApp/src/jvmMain/kotlin/org/example/project/ImageLoader.kt
composeApp/src/wasmJsMain/kotlin/org/example/project/ImageLoader.kt
```

应用启动时会在 `App.kt` 中注册全局 Coil `ImageLoader`。

## 运行方式

先启动服务端：

```powershell
.\gradlew.bat :server:run
```

服务端启动后，可以访问：

```text
http://127.0.0.1:8080/api/movies
http://127.0.0.1:8080/media/posters/bad-boys.jpg
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

## 验证命令

常用编译与测试命令：

```powershell
.\gradlew.bat :server:test
.\gradlew.bat :composeApp:compileDebugKotlinAndroid
.\gradlew.bat :composeApp:compileKotlinJvm
.\gradlew.bat :composeApp:compileKotlinWasmJs
```

也可以一次执行：

```powershell
.\gradlew.bat :server:test :composeApp:compileDebugKotlinAndroid :composeApp:compileKotlinJvm :composeApp:compileKotlinWasmJs
```

## 排查提示

如果客户端能拿到电影列表但图片不显示，优先检查：

1. `server` 是否正在运行。
2. 图片 URL 是否能在浏览器中直接打开。
3. Android 真机是否能访问电脑的局域网 IP。
4. `database.json` 中的图片路径是否真实存在于 `server/src/main/resources/media`。
5. Logcat 中 `MyVRMovies` 标签输出的电影接口地址和海报地址。

如果 JSON 解析失败并提示开头存在不可见字符，通常是文件带有 UTF-8 BOM。当前服务端和客户端都做了 BOM 兼容处理，但建议 `database.json` 保存为 UTF-8 without BOM。

## 已知 Gradle 提示

当前 `composeApp` 和 `shared` 中显式配置了部分 source set 的 `dependsOn` 关系，因此 Gradle 可能提示 Kotlin 默认层级模板没有自动应用。这是构建配置提示，不代表服务端接口或图片加载失败。
