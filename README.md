# httpclient3-extension

[English](./README.md) | [简体中文](./README.zh-CN.md)

Extensions for Apache HttpClient 3.x (`commons-httpclient` 3.1) — URI builders, SSL/keystore utilities, connection properties, response handling, multipart sources and content-type / header constants.

## Table of Contents

- [1. Project Overview](#1-project-overview)
- [2. Features & Status](#2-features--status)
- [3. Requirements & Compatibility](#3-requirements--compatibility)
- [4. Architecture & Modules](#4-architecture--modules)
- [5. Installation](#5-installation)
- [6. Quick Start](#6-quick-start)
- [7. Configuration](#7-configuration)
- [8. Core Usage / API](#8-core-usage--api)
- [9. Testing & Build](#9-testing--build)
- [10. Versioning & Branches](#10-versioning--branches)
- [11. Contributing & License](#11-contributing--license)

## 1. Project Overview

`httpclient3-extension` is a utility layer for Apache HttpClient 3.x (`commons-httpclient` 3.1) applications. It provides URI building with parameter maps, SSL socket/trust/key-manager helpers, connection timeout properties, a response-handler abstraction, an `InputStream`-backed multipart part source, and common constants.

| What it is | What it is not |
|:---|:---|
| A utility extension for HttpClient 3.x | A replacement for `commons-httpclient` |
| URI builders, SSL/keystore utils, connection properties, DTOs | A Spring Boot starter (no auto-configuration) |
| Plain Java, no framework dependency | A web framework |

Typical use cases:

| Use case | Notes |
|:---|:---|
| Build URLs with query parameters | `HttpURIUtils.buildURL(baseURL, paramsMap, charset)` / `buildNameValuePairs(...)` |
| TLS client setup (client certificates) | `KeyManagerUtils.createClientKeyManager(...)`, `TrustManagerUtils`, `SSLSocketUtils` |
| Response handling abstraction | `ResponseHandler<T>` with `handleClient(HttpClient)` / method-handling callbacks |
| Multipart uploads from streams | `InputStreamPartSource` |
| Connection properties | `HttpConnectionManagerProperties` / `HttpConnectionProperties` (timeout interval default 5000 ms) |
| HTTP constants | `ContentType`, `HttpHeaders` |

**Project status:** stable.

## 2. Features & Status

| Feature | Status | Notes |
|:---|:---|:---|
| `HttpURIUtils` | Available | `buildURL(baseURL, paramsMap, charset)`, `buildNameValuePairs(...)` |
| `TrustManagerUtils` | Available | `getAcceptAllTrustManager()`, `getValidateServerCertificateTrustManager()`, `getDefaultTrustManager(KeyStore)` |
| `KeyManagerUtils` | Available | `createClientKeyManager(...)` overloads (KeyStore or store file), `closeQuietly(...)` |
| `SSLSocketUtils` | Available | `enableEndpointNameVerification(SSLSocket)` |
| `ResponseHandler<T>` | Available | Response-handling callback abstraction |
| `InputStreamPartSource` | Available | `PartSource` implementation backed by an `InputStream` |
| `HttpConnectionManagerProperties` / `HttpConnectionProperties` | Available | Timeout properties (`http.timeout.interval`, default 5000) and `ManagerType` enum |
| `HttpResponeUtils` | Available | `getContentType(HttpMethodBase)` |
| `IOUtils` | Available | `closeQuietly(...)` overloads |
| `HttpResponseException` | Available | `IOException` subclass carrying the HTTP status code |
| `ContentType` / `HttpHeaders` | Available | Constant collections (application/json, multipart/form-data, Accept, Authorization, ...) |
| Unit tests | Not present | No test sources in the repository |
| CI pipeline | Not configured | No CI workflow files in the repository |

## 3. Requirements & Compatibility

| Requirement | Version |
|:---|:---|
| JDK | 8 |
| Maven | 3.0+ |
| commons-httpclient | 3.1 |
| commons-lang3 | 3.20.0 |
| dom4j | 2.2.0 |

### Version lines

| Branch | JDK | Version pattern |
|:---|:---|:---|
| `feature/1.0.x` | JDK 8 | `1.0.x.*` |
| `feature/2.0.x` | JDK 17 | `2.0.x.*` |
| `feature/3.0.x` | JDK 21 | `3.0.x.*` |

## 4. Architecture & Modules

```text
  Your code (commons-httpclient 3.1 based)
                     |
                     v
        httpclient3-extension (pure Java)
   ----------------------------------------------------
   HttpURIUtils / HttpResponeUtils / IOUtils     ->  request & response helpers
   TrustManagerUtils / KeyManagerUtils / SSL...  ->  TLS setup
   HttpConnectionManagerProperties /             ->  connection timeouts
   HttpConnectionProperties
   ResponseHandler<T>                            ->  response abstraction
   InputStreamPartSource                         ->  multipart source
   ContentType / HttpHeaders / HttpResponseException -> constants & errors
   ----------------------------------------------------
                     |
                     v
        Apache HttpClient 3.x (commons-httpclient)
```

Single module, jar packaging (base package `org.apache.http.spring.boot.client`):

| Sub-package | Responsibility |
|:---|:---|
| `org.apache.http.spring.boot.client` | `ContentType` constants, `ResponseHandler<T>` |
| `...client.ssl` | `SSLSocketUtils`, `TrustManagerUtils`, `KeyManagerUtils` |
| `...client.property` | `HttpConnectionManagerProperties`, `HttpConnectionProperties` |
| `...client.utils` | `IOUtils`, `HttpResponeUtils`, `HttpURIUtils`, `HttpHeaders` |
| `...client.multipart` | `InputStreamPartSource` |
| `...client.exception` | `HttpResponseException` |

## 5. Installation

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

**Availability:** the artifact is published to the Aliyun private Maven repository and distributed through GitHub Releases; it has not yet been published to Maven Central.

## 6. Quick Start

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

Expected result: `url` carries the encoded query parameters (`http://example.com/api/search?page=1&size=20`), and the GET request executes against it.

## 7. Configuration

The module is a plain library: no configuration files. Connection behavior is configured programmatically:

- `HttpConnectionManagerProperties`: `setTimeoutInterval(int)` (property name `http.timeout.interval`, default 5000) and `ManagerType` (`valueOfIgnoreCase(String)`)
- `HttpConnectionProperties`: wraps `HttpConnectionParams`
- TLS: trust managers and client key managers are created from `KeyStore` / store files programmatically

## 8. Core Usage / API

### 8.1 URI building

```java
List<NameValuePair> pairs = HttpURIUtils.buildNameValuePairs("http://example.com/api", paramsMap);
```

### 8.2 TLS setup

```java
import org.apache.http.spring.boot.client.ssl.KeyManagerUtils;
import org.apache.http.spring.boot.client.ssl.TrustManagerUtils;

X509TrustManager trustManager = TrustManagerUtils.getValidateServerCertificateTrustManager();
KeyManager keyManager = KeyManagerUtils.createClientKeyManager(keyStoreFile, "store-pass", "client-alias");
```

### 8.3 Response handling

```java
ResponseHandler<String> handler = new ResponseHandler<String>() {
    public void handleClient(HttpClient httpclient) { ... }
    // method-level callbacks as declared in the interface
};
```

### 8.4 Multipart

```java
InputStreamPartSource source = new InputStreamPartSource("report.pdf", fileInputStream);
```

## 9. Testing & Build

```bash
./mvnw clean verify        # compile, run tests, generate coverage report
./mvnw clean install       # install into the local repository
```

- The repository currently contains no test sources.
- Coverage is measured with the JaCoCo Maven plugin (target: 90% line coverage, `haltOnFailure=false`).
- The `release` profile assembles GPG signing + sources + Javadoc + deployment (`./mvnw -Prelease clean deploy`).

## 10. Versioning & Branches

Three parallel version lines are maintained:

| Branch | JDK | Version pattern |
|:---|:---|:---|
| `feature/1.0.x` | JDK 8 | `1.0.x.*` |
| `feature/2.0.x` | JDK 17 | `2.0.x.*` |
| `feature/3.0.x` | JDK 21 | `3.0.x.*` |

Maintenance strategy: the 1.0.x line receives bug fixes while JDK 8 remains the baseline; feature development primarily targets the 2.0.x / 3.0.x lines.

## 11. Contributing & License

Contributions are welcome — open an issue or submit a pull request against the matching version-line branch (`feature/3.0.x` for JDK 21 changes).

This project is licensed under the [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0). See the `LICENSE` file in the repository root for details.
