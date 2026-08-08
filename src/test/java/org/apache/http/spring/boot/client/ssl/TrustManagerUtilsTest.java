/*
 * Copyright (c) 2018-present, easy-4-java (https://github.com/easy-4-java).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.http.spring.boot.client.ssl;

import org.junit.jupiter.api.Test;

import javax.net.ssl.X509TrustManager;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link TrustManagerUtils}.
 *
 * @since 3.0.0
 */
class TrustManagerUtilsTest {

    @Test
    void shouldBeFinalUtilityClass() {
        assertTrue(Modifier.isFinal(TrustManagerUtils.class.getModifiers()),
                "TrustManagerUtils must be final");
    }

    @Test
    void shouldReturnSingletonAcceptAllTrustManager() {
        X509TrustManager first = TrustManagerUtils.getAcceptAllTrustManager();
        X509TrustManager second = TrustManagerUtils.getAcceptAllTrustManager();
        assertNotNull(first);
        assertSame(first, second, "Accept-all manager should be a singleton");
        assertEquals(0, first.getAcceptedIssuers().length);
    }

    @Test
    void shouldReturnSingletonValidateServerCertificateTrustManager() {
        X509TrustManager first = TrustManagerUtils.getValidateServerCertificateTrustManager();
        X509TrustManager second = TrustManagerUtils.getValidateServerCertificateTrustManager();
        assertNotNull(first);
        assertSame(first, second, "Strict manager should be a singleton");
        assertEquals(0, first.getAcceptedIssuers().length);
    }

    @Test
    void shouldNotThrowOnClientCheckForAcceptAll() throws CertificateException {
        TrustManagerUtils.getAcceptAllTrustManager().checkClientTrusted(null, "RSA");
    }

    @Test
    void shouldNotThrowOnServerCheckForAcceptAll() throws CertificateException {
        TrustManagerUtils.getAcceptAllTrustManager().checkServerTrusted(null, "RSA");
    }

    @Test
    void shouldNotThrowOnClientCheckForValidate() throws CertificateException {
        TrustManagerUtils.getValidateServerCertificateTrustManager().checkClientTrusted(null, "RSA");
    }

    @Test
    void shouldNotThrowOnServerCheckForValidateWhenChainIsEmpty() throws CertificateException {
        TrustManagerUtils.getValidateServerCertificateTrustManager()
                .checkServerTrusted(new X509Certificate[0], "RSA");
    }

    @Test
    void shouldDelegateToJvmDefaultTrustManager() throws Exception {
        X509TrustManager manager = TrustManagerUtils.getDefaultTrustManager((KeyStore) null);
        assertNotNull(manager, "Default trust manager should never be null");
    }

    @Test
    void shouldDelegateToJvmDefaultTrustManagerWithEmptyKeyStore() throws Exception {
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(null, null);
        X509TrustManager manager = TrustManagerUtils.getDefaultTrustManager(ks);
        assertNotNull(manager);
    }
}
