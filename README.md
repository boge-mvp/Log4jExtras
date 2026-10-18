# Log4jExtras

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![JitPack](https://jitpack.io/v/boge-mvp/Log4jExtras.svg)](https://jitpack.io/#boge-mvp/Log4jExtras)
[![GitHub Packages](https://img.shields.io/badge/Maven-GitHub%20Packages-red.svg)](https://github.com/boge-mvp/Log4jExtras)
[![JDK](https://img.shields.io/badge/JDK-23-orange.svg)](https://jdk.java.net/23/)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.0-purple.svg)](https://kotlinlang.org)
[![Log4j2](https://img.shields.io/badge/Log4j2-2.25.2-green.svg)](https://logging.apache.org/log4j/2.x/)

一个基于 **Kotlin 2.2.0** + **JDK 23** 的 Apache Log4j2 增强扩展库，为 JVM 应用提供更强大的 MDC 过滤能力与 YAML 配置锚点重放支持。

---

## 📋 项目概述

本项目为 **Apache Log4j2**（2.25.2+）提供针对性增强扩展：

- ✅ **MDC / ContextMap 精准过滤**：提供按键存在性判定（`ContextKeyExistsFilter`）与正则表达式正则匹配（`ContextMapRegexFilter`）；
- ✅ **YAML 锚点重放支持**：自定义 `YamlAnchorReplayConfigurationFactory`（`@Order(8)`），解决传统 YAML 配置文件在多次重载或引用锚点（`*anchor`）时的状态丢失问题；
- ✅ **编译期无感索引自动生成**：利用 Kapt 编译期注解处理器生成二进制插件索引文件 `Log4j2Plugins.dat`，Log4j2 运行期零配置自动加载识别；
- ✅ **高性能与安全兜底**：防递归上下文初始化机制，插件工厂参数校验失败优雅降级（记 Log 并返回 `null`，杜绝框架抛崩溃）。

---

## 🚀 快速开始

本项目支持 **JitPack（零凭据公网直连，推荐）** 和 **GitHub Packages** 两种分发方式。

### 方式一：通过 JitPack 引入（推荐，无需任何 GitHub Token）

#### Gradle (Kotlin DSL)

```kotlin
// 1. 在 settings.gradle.kts 中添加仓库
dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

// 2. 在 build.gradle.kts 中添加依赖
dependencies {
    implementation("com.github.boge-mvp:Log4jExtras:1.0.0")
}
```

#### Maven (`pom.xml`)

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.boge-mvp</groupId>
    <artifactId>Log4jExtras</artifactId>
    <version>1.0.0</version>
</dependency>
```

---

### 方式二：通过 GitHub Packages 引入

#### 选项 A：Gradle 全局初始化配置（零代码侵入且多项目通用）

无需在每个项目的 `settings.gradle.kts` 中散落个人 Token，一次配置即可对电脑上所有 Gradle 项目全局生效：

##### 第一步：在全局属性中配置 GitHub 凭证

打开或创建你电脑用户根目录下的 `~/.gradle/gradle.properties` 文件（Windows 用户为 `C:\Users\用户名\.gradle\gradle.properties`），在其中配置你的 GitHub 用户名和具有 `read:packages` 权限的 Personal Access Token (经典令牌)：

```properties
# ~/.gradle/gradle.properties
gpr.user=你的GitHub用户名
gpr.key=ghp_你的GitHub经典Token令牌
```

> 💡 **核心排查点：你的 `$GRADLE_USER_HOME` 在哪里？**  
> Gradle 官方指出，全局初始化脚本只会在以下几个位置按固定顺序被自动加载：  
> 1. `$GRADLE_USER_HOME/init.gradle`  
> 2. `$GRADLE_USER_HOME/init.d/` 目录下的任何以 `.gradle` 结尾的文件（按字母顺序执行）  
> 3. `$GRADLE_HOME/init.d/` 目录下的文件（属于本地安装的 Gradle 软件目录）  
> 
> 很多开发者以为用户根目录就是 `~/.gradle`（即 `C:\Users\用户名\.gradle`）。但如果在 Windows 系统中配置了环境变量（如 `GRADLE_USER_HOME` 指向了其他盘符），或者 Android Studio 内部使用了单独的隔离目录，那么它就永远不会去读默认的那个 `C:\Users` 目录。

##### 第二步：创建全局初始化脚本注入仓库

打开或创建你电脑用户根目录下的 `~/.gradle/init.gradle` 文件，复制并粘贴以下代码。这段脚本会自动为你电脑上所有 Gradle 项目的依赖解析和插件解析注入 GitHub 仓库：

```groovy
println("🔥 ====== Global init.gradle loading ======")
// 在 Gradle 设置加载完成后执行，确保能全局注入仓库
beforeSettings { settings ->

    // 1. 定义插件下载仓库（解决 plugins { ... } 找不到的问题）
    settings.pluginManagement {
        repositories {
            maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }
            mavenCentral()
            gradlePluginPortal()
        }
    }

    // 2. 定义项目依赖仓库（解决 implementation '...' 找不到的问题）
    settings.dependencyResolutionManagement {
        repositories {
            mavenLocal()
            maven { url = uri("https://maven.aliyun.com/repository/public") }
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/boge-mvp/Log4jExtras")
                credentials {
                    username = settings.providers.gradleProperty("gpr.user").orNull ?: ""
                    password = settings.providers.gradleProperty("gpr.key").orNull ?: ""
                }
            }
            mavenCentral()
        }
    }
}
```

---

#### 选项 B：单项目局部配置（备选）

若仅希望在当前单个项目中配置，可在该项目的 `settings.gradle.kts` 中添加：

```kotlin
dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/boge-mvp/repo")
            credentials {
                username = providers.environmentVariable("GITHUB_ACTOR").getOrElse("")
                password = providers.environmentVariable("GITHUB_TOKEN").getOrElse("")
            }
        }
    }
}
```

---

#### Maven (`pom.xml`)

```xml
<repositories>
    <repository>
        <id>github-log4j-extras</id>
        <url>https://maven.pkg.github.com/boge-mvp/Log4jExtras</url>
    </repository>
</repositories>
```

---

### 2. 添加项目依赖

#### Gradle (Kotlin DSL)

```kotlin
dependencies {
    implementation("com.github.boge-mvp:Log4jExtras:1.0.0")
}
```

#### Maven (`pom.xml`)

```xml
<dependency>
    <groupId>com.github.boge-mvp</groupId>
    <artifactId>Log4jExtras</artifactId>
    <version>1.0.0</version>
</dependency>
```

---

## 🧩 核心功能与配置示例

### 1. MDC Key 存在性过滤器 (`ContextKeyExistsFilter`)

检查 ThreadContext/MDC 中是否存在指定的 Key。

#### XML 配置示例 (`log4j2.xml`)

```xml
<Configuration status="WARN">
    <Appenders>
        <Console name="Console" target="SYSTEM_OUT">
            <PatternLayout pattern="%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"/>
            <!-- 仅允许包含 userId 的日志通过 -->
            <ContextKeyExistsFilter key="userId" onMatch="ACCEPT" onMismatch="DENY"/>
        </Console>
    </Appenders>
    <Loggers>
        <Root level="info">
            <AppenderRef ref="Console"/>
        </Root>
    </Loggers>
</Configuration>
```

#### YAML 配置示例 (`log4j2.yaml`)

```yaml
Configuration:
  status: WARN
  Appenders:
    Console:
      name: Console
      PatternLayout:
        pattern: "%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"
      ContextKeyExistsFilter:
        key: "userId"
        onMatch: "ACCEPT"
        onMismatch: "DENY"
```

---

### 2. MDC 正则表达式过滤器 (`ContextMapRegexFilter`)

针对 ThreadContext/MDC 中指定 Key 的 Value 进行正则表达式匹配。

#### XML 配置示例 (`log4j2.xml`)

```xml
<Configuration status="WARN">
    <Appenders>
        <Console name="Console" target="SYSTEM_OUT">
            <PatternLayout pattern="%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"/>
            <!-- 仅放行 traceId 符合标准的请求 -->
            <ContextMapRegexFilter key="traceId" regex="^TR-[0-9]{6}$" onMatch="ACCEPT" onMismatch="DENY"/>
        </Console>
    </Appenders>
</Configuration>
```

---

### 3. YAML 锚点重放配置工厂 (`YamlAnchorReplayConfigurationFactory`)

无需显式代码调用。只要将依赖加入 Classpath，Log4j2 启动读取 `.yml`/`.yaml` 配置文件时，会自动优先选择本扩展工厂，完美支持 YAML 锚点与别名重用。

```yaml
# 示例 log4j2.yaml（支持 YAML 锚点重放）
.common-layout: &default-layout
  pattern: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"

Configuration:
  Appenders:
    Console:
      name: STDOUT
      PatternLayout: *default-layout
    File:
      name: FILE
      fileName: "logs/app.log"
      PatternLayout: *default-layout
```

---

### 4. 上下文诊断 Key 常量 (`Keys`)

内置常用的 Log4j MDC 诊断跟踪上下文 Key：

```kotlin
import org.apache.logging.Keys

// 在业务代码中使用
ThreadContext.put(Keys.TRACE_ID, "TR-123456")
```

---

## 📦 技术栈

| 组件 | 版本 | 说明 |
|---|---|---|
| **JDK** | 23 | Java 运行时与 Toolchain 对齐版本 |
| **Kotlin** | 2.2.0 | 编程语言 |
| **Log4j Core** | 2.25.2 | 日志核心框架 |
| **Log4j API Kotlin** | 1.5.0 | Log4j Kotlin 语法扩展 |
| **Jackson YAML** | 2.19.1 | YAML 解析与重放引擎 |
| **Disruptor** | 3.4.4 | Log4j2 异步日志底层依赖 |
| **Gradle** | 9.1.0 | 构建工具（Kotlin DSL） |

---

## 🏗️ 构建与发布

### 本地编译

```bash
# 增量编译（跳过测试）
./gradlew classes --no-daemon
```

### 本地打包发布 (Maven Local)

```bash
# 发布到 ~/.m2/repository
./gradlew publishToMavenLocal -x test --no-daemon
```

### CI/CD 自动化发布

本项目配置了 GitHub Actions 自动化工作流 `.github/workflows/publish.yml`：

- **Tag 触发**：推送 `v*` 格式标签即可自动触发构建并发布至 GitHub Packages：
  ```bash
  git tag v1.0.0
  git push origin v1.0.0
  ```
- **手动触发**：在 GitHub 仓库操作界面面板中找到 Actions → "发布到 GitHub Packages" → "Run workflow" 填入版本号手动发布。

---

## ⚙️ 核心架构与设计原则

1. **Kapt 静态索引**：通过 `kapt(libs.log4j.core)` 挂载 Log4j2 的 `PluginProcessor`，在编译期于构建产物中生成 `META-INF/org/apache/logging/log4j/core/config/plugins/Log4j2Plugins.dat`，实现启动秒级索引识别。
2. **防递归设计**：`BaseContextMapFilter` 在初始化阶段优先激活 `ContextDataFactory`，防止框架在日志上下文提取时反向触发日志循环导致 StackOverflowError。
3. **安全降级**：所有 `@PluginFactory` 工厂方法均具备完善的参数检查，当配置缺失或非法时自动记 Log 并返回 `null`，确保应用启动平稳性。

---

## 📂 项目结构

```
Log4jExtras/
├── .github/workflows/
│   └── publish.yml                 # GitHub Actions 自动化发布脚本
├── gradle/
│   ├── libs.versions.toml          # Gradle Version Catalog 依赖统一声明
│   └── wrapper/                    # Gradle Wrapper (9.1.0)
├── src/
│   ├── main/
│   │   ├── kotlin/org/apache/logging/
│   │   │   ├── Keys.kt             # 上下文 Key 常量定义
│   │   │   ├── filter/             # 自定义 Log4j2 Filter 实现
│   │   │   │   ├── BaseContextMapFilter.kt
│   │   │   │   ├── ContextKeyExistsFilter.kt
│   │   │   │   └── ContextMapRegexFilter.kt
│   │   │   └── config/yaml/        # YAML 锚点重放配置工厂
│   │   │       ├── YamlAnchorReplayConfiguration.kt
│   │   │       └── YamlAnchorReplayConfigurationFactory.kt
│   │   └── resources/
│   └── test/                       # 单元测试与测试资源
├── build.gradle.kts                # 项目 Gradle 构建配置
├── settings.gradle.kts             # 项目 Gradle Settings 配置
├── AGENTS.md                       # AI 智能体协作与开发指南
└── README.md                       # 本说明文档
```

---

## 📝 版本历史

### v1.0 (当前版本)
- 支持 `ContextKeyExistsFilter` & `ContextMapRegexFilter`；
- 支持 `YamlAnchorReplayConfigurationFactory`（YAML 锚点重用与重载）；
- 支持 Kapt 自动构建 `Log4j2Plugins.dat` 插件索引；
- 提供 GitHub Actions 自动发布工作流与 GitHub Packages 分发。

---

## 🤝 贡献与反馈

欢迎提交 Issue 或 Pull Request！

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

---

## 📄 许可证

本项目采用 Apache License 2.0 许可证 - 详情参见 [LICENSE](LICENSE) 文件。

---

**Made with ❤️ using Kotlin & Apache Log4j2**
