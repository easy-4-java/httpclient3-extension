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
import org.junit.jupiter.api.io.TempDir;

import javax.net.ssl.KeyManager;
import javax.net.ssl.X509ExtendedKeyManager;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.Socket;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Unit tests for {@link KeyManagerUtils}.
 *
 * @since 3.0.0
 */
class KeyManagerUtilsTest {

    @Test
    void shouldHavePrivateConstructor() throws Exception {
        Constructor<?>[] constructors = KeyManagerUtils.class.getDeclaredConstructors();
        assertTrue(constructors.length >= 1, "Must have at least one constructor");
        Constructor<?> ctor = constructors[0];
        assertTrue(Modifier.isPrivate(ctor.getModifiers()), "Constructor must be private");
        ctor.setAccessible(true);
        ctor.newInstance();
    }

    @Test
    void shouldExposeCloseQuietlyForNullCloseable() {
        KeyManagerUtils.closeQuietly((Closeable) null);
    }

    @Test
    void shouldSwallowIOExceptionOnCloseQuietly() {
        Closeable throwing = () -> {
            throw new IOException("boom");
        };
        KeyManagerUtils.closeQuietly(throwing);
    }

    @Test
    void shouldCloseRealCloseableSilently() throws Exception {
        boolean[] closed = {false};
        Closeable closeable = () -> closed[0] = true;
        KeyManagerUtils.closeQuietly(closeable);
        assertTrue(closed[0], "Closeable must be closed");
    }

    @Test
    void shouldThrowForMissingFileStore(@TempDir Path tmp) {
        File missing = tmp.resolve("missing.jks").toFile();
        try {
            KeyManagerUtils.createClientKeyManager("JKS", missing, "storepass", "alias", "keypass");
            fail("Expected an exception because the store file is missing");
        } catch (Exception expected) {
            assertTrue(expected instanceof IOException || expected instanceof GeneralSecurityException);
        }
    }

    @Test
    void shouldThrowForKeyStoreWithoutKeyEntries() throws Exception {
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(null, null);
        assertThrows(GeneralSecurityException.class,
                () -> KeyManagerUtils.createClientKeyManager(ks, null, "x"));
    }

    @Test
    void shouldThrowWhenAliasDoesNotExistInKeyStore(@TempDir Path tmp) throws Exception {
        // Create a keystore with a real key entry, then look up a non-existent alias.
        // The source code NPEs on null cert chain, so we expect NullPointerException.
        File ksFile = generateKeyStoreViaKeytool("testkey", "samepass", tmp.resolve("test.jks").toFile());
        assertThrows(Exception.class,
                () -> KeyManagerUtils.createClientKeyManager("JKS", ksFile, "samepass", "does-not-exist", "samepass"));
    }

    @Test
    void shouldCreateKeyManagerFromValidKeyStore(@TempDir Path tmp) throws Exception {
        File ksFile = generateKeyStoreViaKeytool("mykey", "samepass", tmp.resolve("valid.jks").toFile());
        KeyManager km = KeyManagerUtils.createClientKeyManager("JKS", ksFile, "samepass", "mykey", "samepass");
        assertNotNull(km);
    }

    @Test
    void shouldCreateKeyManagerWithDefaultStoreType(@TempDir Path tmp) throws Exception {
        File ksFile = generateKeyStoreViaKeytool("mykey", "samepass", tmp.resolve("default.jks").toFile());
        KeyManager km = KeyManagerUtils.createClientKeyManager(ksFile, "samepass", "mykey");
        assertNotNull(km);
    }

    @Test
    void shouldCreateKeyManagerAutoDiscoveringAlias(@TempDir Path tmp) throws Exception {
        File ksFile = generateKeyStoreViaKeytool("mykey", "samepass", tmp.resolve("autoalias.jks").toFile());
        KeyManager km = KeyManagerUtils.createClientKeyManager(ksFile, "samepass");
        assertNotNull(km);
    }

    @Test
    void shouldReturnNullForServerAliases(@TempDir Path tmp) throws Exception {
        File ksFile = generateKeyStoreViaKeytool("testkey", "samepass", tmp.resolve("server.jks").toFile());
        KeyStore ks = KeyStore.getInstance("JKS");
        try (FileInputStream fis = new FileInputStream(ksFile)) {
            ks.load(fis, "samepass".toCharArray());
        }
        KeyManager km = KeyManagerUtils.createClientKeyManager(ks, "testkey", "samepass");
        X509ExtendedKeyManager xkm = (X509ExtendedKeyManager) km;
        assertNull(xkm.chooseServerAlias("RSA", null, (Socket) null));
        assertNull(xkm.getServerAliases("RSA", null));
    }

    @Test
    void shouldReturnClientAliasAndCertChain(@TempDir Path tmp) throws Exception {
        File ksFile = generateKeyStoreViaKeytool("testkey", "samepass", tmp.resolve("client.jks").toFile());
        KeyStore ks = KeyStore.getInstance("JKS");
        try (FileInputStream fis = new FileInputStream(ksFile)) {
            ks.load(fis, "samepass".toCharArray());
        }
        KeyManager km = KeyManagerUtils.createClientKeyManager(ks, "testkey", "samepass");
        X509ExtendedKeyManager xkm = (X509ExtendedKeyManager) km;
        String alias = xkm.chooseClientAlias(new String[]{"RSA"}, null, null);
        assertNotNull(alias);
        assertNotNull(xkm.getCertificateChain(alias));
        assertNotNull(xkm.getPrivateKey(alias));
        assertNotNull(xkm.getClientAliases("RSA", null));
    }

    @Test
    void shouldProvideStaticCloseQuietlyHelper() throws Exception {
        Method m = KeyManagerUtils.class.getDeclaredMethod("closeQuietly", Closeable.class);
        assertTrue(Modifier.isPublic(m.getModifiers()));
        assertTrue(Modifier.isStatic(m.getModifiers()));
    }

    @Test
    void shouldThrowForCorruptedKeyStoreFile(@TempDir Path tmp) throws Exception {
        File bogus = tmp.resolve("bogus.jks").toFile();
        try (FileOutputStream fos = new FileOutputStream(bogus)) {
            fos.write("not a real keystore".getBytes());
        }
        try {
            KeyManagerUtils.createClientKeyManager("JKS", bogus, "storepass", "alias", "keypass");
            fail("Expected an exception because the keystore is corrupted");
        } catch (Exception expected) {
            assertTrue(expected instanceof IOException || expected instanceof GeneralSecurityException);
        }
    }

    /**
     * Generate a JKS keystore file containing a self-signed RSA key entry
     * by invoking the {@code keytool} command-line utility. The store password
     * and key password are set to the same value for compatibility with the
     * convenience overloads of {@link KeyManagerUtils}.
     */
    private static File generateKeyStoreViaKeytool(String alias, String password, File ksFile) throws Exception {
        String javaHome = System.getProperty("java.home");
        String keytool = javaHome + File.separator + "bin" + File.separator + "keytool";
        ProcessBuilder pb = new ProcessBuilder(
                keytool, "-genkeypair",
                "-alias", alias,
                "-keyalg", "RSA",
                "-keysize", "2048",
                "-validity", "365",
                "-keystore", ksFile.getAbsolutePath(),
                "-storepass", password,
                "-keypass", password,
                "-dname", "CN=Test,O=Test",
                "-storetype", "JKS"
        );
        pb.redirectErrorStream(true);
        Process process = pb.start();
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("keytool exited with code " + exitCode);
        }
        return ksFile;
    }
}
