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

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link SSLSocketUtils}.
 *
 * @since 3.0.0
 */
class SSLSocketUtilsTest {

    @Test
    void shouldHavePrivateConstructor() throws Exception {
        Constructor<SSLSocketUtils> constructor = SSLSocketUtils.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()),
                "Constructor must be private");
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test
    void shouldHaveStaticEnableMethod() throws Exception {
        Method method = SSLSocketUtils.class.getDeclaredMethod("enableEndpointNameVerification", SSLSocket.class);
        assertTrue(Modifier.isStatic(method.getModifiers()), "Method must be static");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method must be public");
        assertEquals(boolean.class, method.getReturnType(), "Method must return boolean");
    }

    @Test
    void shouldReturnTrueForRealSslSocket() throws Exception {
        SSLContext ctx = SSLContext.getDefault();
        SSLSocket socket = (SSLSocket) ctx.getSocketFactory().createSocket();
        try {
            boolean result = SSLSocketUtils.enableEndpointNameVerification(socket);
            assertTrue(result, "Should successfully enable endpoint verification on modern JVM");
        } finally {
            socket.close();
        }
    }

    @Test
    void shouldVerifySslParametersClassExists() {
        // Confirm the reflective target class is available on this JVM
        try {
            Class<?> cls = Class.forName("javax.net.ssl.SSLParameters");
            assertNotNull(cls);
            assertEquals("javax.net.ssl.SSLParameters", cls.getName());
        } catch (ClassNotFoundException e) {
            // Very old JVM - should not happen on Java 21
            assertFalse(true, "SSLParameters must be available on this JVM");
        }
    }
}
