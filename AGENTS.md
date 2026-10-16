# Log4jExtras 开发者与 AI 智能体协作指南 (AGENTS.md)

本项目为 **Apache Log4j2** 提供增强特性的 Kotlin JVM 库（基于 JDK 23、Kotlin 2.2.0、Log4j 2.25.2、Gradle 9.1.0）。
为保障后续开发与智能体协作的高效与安全性，请严格遵循本指南。

---

## 1. 核心架构与功能组件

- **自定义过滤器 (Filters)** (`org.apache.logging.filter.*`)：
  - `BaseContextMapFilter`：抽象基类，继承自 Log4j2 `MapFilter`，提供对 `ContextDataInjector`、MDC/ThreadContextMap 的单键/多键值匹配与生命周期防护（防止 `ContextDataFactory` 递归触发）。
  - `ContextKeyExistsFilter`：检查 MDC/ContextMap 中是否存在指定的 Key（支持单个 key 或 key 列表，AND/OR 逻辑）。
  - `ContextMapRegexFilter`：针对 MDC/ContextMap 中的值进行正则表达式匹配（支持单键与多键正则）。
- **YAML 锚点重放配置工厂 (ConfigurationFactory)** (`org.apache.logging.config.yaml.*`)：
  - `YamlAnchorReplayConfigurationFactory`（`@Order(8)`，高于 Log4j2 默认的 5）：支持 `.yml`/`.yaml` 配置文件，集成 Jackson 的 `YAMLAnchorReplayingFactory`，解决 YAML 锚点在多次重放解析时的状态丢失问题。
- **公共常量定义** (`org.apache.logging.Keys`)：
  - 统一定义常用的 MDC 诊断跟踪上下文 Key。

---

## 2. 核心编译与关键机制 (Critical Gotchas)

### 2.1 Kapt 与 Log4j 插件索引生成
- **编译时注解处理器**：项目通过 `kapt(libs.log4j.core)` 挂载 `org.apache.logging.log4j.core.config.plugins.processor.PluginProcessor`。
- **生成的关键资产**：Kapt 编译时会在产物中生成二进制索引文件：
  `META-INF/org/apache/logging/log4j/core/config/plugins/Log4j2Plugins.dat`。
- **排查与校验**：若 Log4j2 运行时无法识别 `@Plugin` 注册的组件（如配置报无法识别的元素），必须首先检查 `build/classes/kotlin/main/META-INF/.../Log4j2Plugins.dat` 是否存在。可通过 `Log4jPluginsDiscoveryTest` 反射探测类加载器中的插件注册表。

### 2.2 插件工厂 (@PluginFactory) 编码范式
- **返回 Nullable**：Log4j2 插件工厂方法（`@PluginFactory fun createFilter(...)`）在参数校验失败时，**绝对禁止直接抛出未捕获异常**；
- **容错规范**：参数缺失或格式非法时，应通过内置 `LOGGER.error(...)` 输出错误原因，并返回 `null`。这是 Log4j2 框架优雅降级与报错的标准惯例。

### 2.3 JDK 与 Toolchain 约束
- 构建使用 Gradle Toolchain 强制声明为 **JDK 23** (`jvmToolchain(23)`)。
- 编译生成 Java 字节码与 Kotlin 目标统一对齐 JDK 23。

---

## 3. 构建、发布与验证命令规范

### 3.1 核心禁令 (最高优先级)
- 🚨 **严禁自动运行测试**：在任何情况下，**绝对禁止**擅自执行 `./gradlew test`、`gradle test` 或包含 `test` task 的命令。只有在用户 Prompt 中**显式授权**（例如：“允许运行测试”）后方可执行。
- 🚨 **优先使用 JetBrains 原生 MCP 编译**：日常验证代码改动时，必须首先调用 `tools.idea.build_project` 进行毫秒级增量编译，禁止频繁全量手敲命令行。

### 3.2 常用开发指令
- **仅编译主代码（无测试）**：
  ```bash
  ./gradlew classes --no-daemon
  ```
- **发布到本地 Maven 仓库（验证打包）**：
  ```bash
  ./gradlew publishToMavenLocal -x test --no-daemon
  ```
- **带版本号动态发布（CI/CD 使用）**：
  ```bash
  ./gradlew publish -Pversion=1.0.0 --no-daemon
  ```

---

## 4. CI/CD 与多渠道发布

- **GitHub Actions** (`.github/workflows/publish.yml`)：
  - 触发机制：推送 `v*` tag 或在 Actions 面板手动触发（`workflow_dispatch`，可自定义输入版本号）。
  - 发布目标：GitHub Packages (`https://maven.pkg.github.com/boge-mvp/Log4jExtras`)。
  - 构件坐标：`com.github.boge-mvp:Log4jExtras:<version>`，包含主 Jar、`-sources.jar`、`-javadoc.jar` 以及生成的 POM 文件。
  - 凭据说明：通过内置 `GITHUB_TOKEN` 自动鉴权，无需额外配置 Secret。

---

## 5. 测试与资源约定

- **测试资源**：测试配置位于 `src/test/resources/log4j2-test.yaml`，测试运行时 Log4j2 会自动优先加载该文件。
- **断言与验证**：编写单元测试请使用 Kotlin 标准测试库 `kotlin.test` 与 JUnit 5 (`org.junit.jupiter.api.Test`)。
