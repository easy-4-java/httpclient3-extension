# httpclient3-extension

[English](./README.md) | [简体中文](./README.zh-CN.md)

![Java](https://img.shields.io/badge/Java-21-orange) ![License](https://img.shields.io/badge/License-Apache%202.0-blue)

Apache HttpClient 3.x（`commons-httpclient` 3.1）的扩展工具 — URI 构建、SSL/密钥库工具、连接属性、响应处理抽象、multipart 数据源与 Content-Type / Header 常量。

<a id="1-project-overview"></a>
## 目录

- [1. 项目概览](#1-project-overview)
- [2. 功能与状态](#2-features--status)
- [3. 环境要求与兼容性](#3-requirements--compatibility)
- [4. 架构与模块](#4-architecture--modules)
- [5. 安装](#5-installation)
- [6. 快速开始](#6-quick-start)
- [7. 配置](#7-configuration)
- [8. 核心用法 / API](#8-core-usage--api)
- [9. 测试与构建](#9-testing--build)
- [10. 版本线与分支](#10-versioning--branches)
- [11. 参与贡献与许可协议](#11-contributing--license)

## 1. 项目概览

`httpclient3-extension` 是面向 Apache HttpClient 3.x（`commons-httpclient` 3.1）应用的实用工具层。提供带参数 Map 的 URI 构建、SSL socket/信任/密钥管理器工具、连接超时属性、响应处理器抽象、基于 `InputStream` 的 multipart 数据源与常用常量。

| 是什么 | 不是什么 |
|:---|:---|
| HttpClient 3.x 的实用扩展 | `commons-httpclient` 的替代品 |
| URI 构建、SSL/密钥库工具、连接属性、DTO | Spring Boot Starter（不含自动配置） |
| 纯 Java，无框架依赖 | Web 框架 |

典型使用场景：

| 场景 | 说明 |
|:---|:---|
| 带查询参数构建 URL | `HttpURIUtils.buildURL(baseURL, paramsMap, charset)` / `buildNameValuePairs(...)` |
| TLS 客户端配置（客户端证书） | `KeyManagerUtils.createClientKeyManager(...)`、`TrustManagerUtils`、`SSLSocketUtils` |
| 响应处理抽象 | `ResponseHandler<T>`，含 `handleClient(HttpClient)` 与按方法的回调 |
| 从流进行 multipart 上传 | `InputStreamPartSource` |
| 连接属性 | `HttpConnectionManagerProperties` / `HttpConnectionProperties`（超时间隔默认 5000 ms） |
| HTTP 常量 | `ContentType`、`HttpHeaders` |

**项目状态：** 稳定。

<a id="2-features--status"></a>
## 2. 功能与状态

| 能力 | 状态 | 说明 |
|:---|:---|:---|
| `HttpURIUtils` | 可用 | `buildURL(baseURL, paramsMap, charset)`、`buildNameValuePairs(...)` |
| `TrustManagerUtils` | 可用 | `getAcceptAllTrustManager()`、`getValidateServerCertificateTrustManager()`、`getDefaultTrustManager(KeyStore)` |
| `KeyManagerUtils` | 可用 | `createClientKeyManager(...)` 重载（KeyStore 或密钥库文件）、`closeQuietly(...)` |
| `SSLSocketUtils` | 可用 | `enableEndpointNameVerification(SSLSocket)` |
| `ResponseHandler<T>` | 可用 | 响应处理回调抽象 |
| `InputStreamPartSource` | 可用 | 基于 `InputStream` 的 `PartSource` 实现 |
| `HttpConnectionManagerProperties` / `HttpConnectionProperties` | 可用 | 超时属性（`http.timeout.interval`，默认 5000）与 `ManagerType` 枚举 |
| `HttpResponeUtils` | 可用 | `getContentType(HttpMethodBase)` |
| `IOUtils` | 可用 | `closeQuietly(...)` 重载 |
| `HttpResponseException` | 可用 | 携带 HTTP 状态码的 `IOException` 子类 |
| `ContentType` / `HttpHeaders` | 可用 | 常量集合（application/json、multipart/form-data、Accept、Authorization 等） |
| 单元测试 | 无 | 仓库中无测试源码 |
| CI 流水线 | 未配置 | 仓库中无 CI 工作流文件 |

<a id="3-requirements--compatibility"></a>
## 3. 环境要求与兼容性

| 依赖项 | 版本 |
|:---|:---|
| JDK | 8 |
| Maven | 3.0+ |
| commons-httpclient | 3.1 |
| commons-lang3 | 3.20.0 |
| dom4j | 2.2.0 |

### 版本线矩阵

| 分支 | JDK | 版本号模式 |
|:---|:---|:---|
| `feature/1.0.x` | JDK 8 | `1.0.x.*` |
| `feature/2.0.x` | JDK 17 | `2.0.x.*` |
| `feature/3.0.x` | JDK 21 | `3.0.x.*` |

<a id="4-architecture--modules"></a>
## 4. 架构与模块

```text
  业务代码（基于 commons-httpclient 3.1）
                     |
                     v
        httpclient3-extension（纯 Java）
   ----------------------------------------------------
   HttpURIUtils / HttpResponeUtils / IOUtils     ->  请求与响应辅助
   TrustManagerUtils / KeyManagerUtils / SSL...  ->  TLS 配置
   HttpConnectionManagerProperties /             ->  连接超时
   HttpConnectionProperties
   ResponseHandler<T>                            ->  响应抽象
   InputStreamPartSource                         ->  multipart 数据源
   ContentType / HttpHeaders / HttpResponseException -> 常量与异常
   ----------------------------------------------------
                     |
                     v
        Apache HttpClient 3.x（commons-httpclient）
```

单一模块，jar 打包（基础包 `org.apache.http.spring.boot.client`）：

| 子包 | 职责 |
|:---|:---|
| `org.apache.http.spring.boot.client` | `ContentType` 常量、`ResponseHandler<T>` |
| `...client.ssl` | `SSLSocketUtils`、`TrustManagerUtils`、`KeyManagerUtils` |
| `...client.property` | `HttpConnectionManagerProperties`、`HttpConnectionProperties` |
| `...client.utils` | `IOUtils`、`HttpResponeUtils`、`HttpURIUtils`、`HttpHeaders` |
| `...client.multipart` | `InputStreamPartSource` |
| `...client.exception` | `HttpResponseException` |

<a id="5-installation"></a>
## 5. 安装

### Maven

```xml
<dependency>
    <groupId>io.github.easy4j</groupId>
    <artifactId>httpclient3-extension</artifactId>
    <version>3.0.x.x.20260630-SNAPSHOT</version>
</dependency>
```

### Gradle

```groovy
implementation 'io.github.easy4j:httpclient3-extension:3.0.x.x.20260630-SNAPSHOT'
```

**可用性：** 构件发布至阿里云私有 Maven 仓库，并通过 GitHub Releases 分发；尚未发布到 Maven Central。

<a id="6-quick-start"></a>
## 6. 快速开始

```java
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.http.spring.boot.client.utils.HttpURIUtils;

import java.util.HashMap;
import java.util.Map;

Map<String, Object> params = new HashMap<>();
params.put("page", 1);
params.put("size", 20);

String url = HttpURIUtils.buildURL("http://example.com/api/search", params, "UTF-8");

HttpClient client = new HttpClient();
GetMethod method = new GetMethod(url);
int status = client.executeMethod(method);
```

预期结果：`url` 携带编码后的查询参数（`http://example.com/api/search?page=1&size=20`），GET 请求据此执行。

<a id="7-configuration"></a>
## 7. 配置

本模块为纯库：无配置文件。连接行为以编程方式配置：

- `HttpConnectionManagerProperties`：`setTimeoutInterval(int)`（属性名 `http.timeout.interval`，默认 5000）与 `ManagerType`（`valueOfIgnoreCase(String)`）
- `HttpConnectionProperties`：包装 `HttpConnectionParams`
- TLS：信任管理器与客户端密钥管理器由 `KeyStore` / 密钥库文件编程创建

<a id="8-core-usage--api"></a>
## 8. 核心用法 / API

### 8.1 URI 构建

```java
List<NameValuePair> pairs = HttpURIUtils.buildNameValuePairs("http://example.com/api", paramsMap);
```

### 8.2 TLS 配置

```java
import org.apache.http.spring.boot.client.ssl.KeyManagerUtils;
import org.apache.http.spring.boot.client.ssl.TrustManagerUtils;

X509TrustManager trustManager = TrustManagerUtils.getValidateServerCertificateTrustManager();
KeyManager keyManager = KeyManagerUtils.createClientKeyManager(keyStoreFile, "store-pass", "client-alias");
```

### 8.3 响应处理

```java
ResponseHandler<String> handler = new ResponseHandler<String>() {
    public void handleClient(HttpClient httpclient) { ... }
    // 接口中声明的按方法回调
};
```

### 8.4 Multipart

```java
InputStreamPartSource source = new InputStreamPartSource("report.pdf", fileInputStream);
```

<a id="9-testing--build"></a>
## 9. 测试与构建

```bash
./mvnw clean verify        # 编译、运行测试、生成覆盖率报告
./mvnw clean install       # 安装到本地仓库
```

- 仓库当前不含测试源码。
- 覆盖率由 JaCoCo Maven 插件度量（目标：90% 行覆盖率，`haltOnFailure=false`）。
- `release` profile 组装 GPG 签名 + 源码 + Javadoc + 部署（`./mvnw -Prelease clean deploy`）。

<a id="10-versioning--branches"></a>
## 10. 版本线与分支

仓库维护三条并行版本线：

| 分支 | JDK | 版本号模式 |
|:---|:---|:---|
| `feature/1.0.x` | JDK 8 | `1.0.x.*` |
| `feature/2.0.x` | JDK 17 | `2.0.x.*` |
| `feature/3.0.x` | JDK 21 | `3.0.x.*` |

维护策略：在 JDK 8 作为基线的同时，1.0.x 版本线接收缺陷修复；新功能开发主要面向 2.0.x / 3.0.x 版本线。

<a id="11-contributing--license"></a>
## 11. 参与贡献与许可协议

欢迎参与贡献——请通过 Issue 反馈问题，或向对应版本线分支提交 Pull Request（JDK 21 相关改动提交到 `feature/3.0.x`）。

本项目基于 [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0) 许可发布。详见仓库根目录的 `LICENSE` 文件。
