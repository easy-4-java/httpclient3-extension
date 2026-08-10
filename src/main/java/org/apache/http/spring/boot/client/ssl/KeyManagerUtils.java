/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.http.spring.boot.client.ssl;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import javax.net.ssl.KeyManager;
import javax.net.ssl.X509ExtendedKeyManager;


/**
 * General KeyManager utilities.
 *
 * <p>How to use with a client certificate:</p>
 * <pre>
 * KeyManager km = KeyManagerUtils.createClientKeyManager("JKS",
 *     "/path/to/privatekeystore.jks","storepassword",
 *     "privatekeyalias", "keypassword");
 * FTPSClient cl = new FTPSClient();
 * cl.setKeyManager(km);
 * cl.connect(...);
 * </pre>
 *
 * <p>If using the default store type and the key password is the same as
 * the store password, these parameters can be omitted. If the desired key
 * is the first or only key in the keystore, the keyAlias parameter can be
 * omitted, in which case the code becomes:</p>
 * <pre>
 * KeyManager km = KeyManagerUtils.createClientKeyManager(
 *     "/path/to/privatekeystore.jks","storepassword");
 * FTPSClient cl = new FTPSClient();
 * cl.setKeyManager(km);
 * cl.connect(...);
 * </pre>
 *
 * <p>The class is final and not instantiable.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see javax.net.ssl.KeyManager
 * @see javax.net.ssl.X509ExtendedKeyManager
 */
public final class KeyManagerUtils {

    /** Cached default {@link KeyStore} type for the current JVM. */
    private static final String DEFAULT_STORE_TYPE = KeyStore.getDefaultType();

    /**
     * Prevent instantiation: this class provides only static helpers.
     */
    private KeyManagerUtils(){
        // Not instantiable
    }

    /**
     * Create a client key manager which returns a particular key. Does not
     * handle server keys.
     *
     * @param ks       the keystore to use, must not be {@code null}
     * @param keyAlias the alias of the key to use; may be {@code null} in
     *                 which case the first key-entry alias found in the
     *                 keystore is used
     * @param keyPass  the password of the key to use, must not be {@code null}
     * @return the customised {@link KeyManager}
     * @throws GeneralSecurityException if the keystore cannot be inspected,
     *                                 the requested alias cannot be
     *                                 resolved, or the supplied password is
     *                                 incorrect
     */
    public static KeyManager createClientKeyManager(KeyStore ks, String keyAlias, String keyPass)
        throws GeneralSecurityException
    {
        ClientKeyStore cks = new ClientKeyStore(ks, keyAlias != null ? keyAlias : findAlias(ks), keyPass);
        return new X509KeyManager(cks);
    }

    /**
     * Create a client key manager which returns a particular key. Does not
     * handle server keys.
     *
     * @param storeType the type of the keyStore, e.g. {@code "JKS"}
     * @param storePath the path to the keyStore file, must not be {@code null}
     * @param storePass the keystore password, must not be {@code null}
     * @param keyAlias  the alias of the key to use; may be {@code null}
     * @param keyPass   the password of the key to use, must not be {@code null}
     * @return the customised {@link KeyManager}
     * @throws GeneralSecurityException if the keystore cannot be loaded or
     *                                 inspected
     * @throws IOException              if the keystore file cannot be read
     */
    public static KeyManager createClientKeyManager(
            String storeType, File storePath, String storePass, String keyAlias, String keyPass)
        throws IOException, GeneralSecurityException
    {
        KeyStore ks = loadStore(storeType, storePath, storePass);
        return createClientKeyManager(ks, keyAlias, keyPass);
    }

    /**
     * Create a client key manager which returns a particular key. Does not
     * handle server keys. Uses the default store type and assumes the key
     * password is the same as the store password.
     *
     * @param storePath the path to the keyStore file
     * @param storePass the keystore and key password
     * @param keyAlias  the alias of the key to use; may be {@code null}
     * @return the customised {@link KeyManager}
     * @throws IOException              if the keystore file cannot be read
     * @throws GeneralSecurityException if the keystore cannot be loaded or
     *                                 inspected
     */
    public static KeyManager createClientKeyManager(File storePath, String storePass, String keyAlias)
        throws IOException, GeneralSecurityException
    {
        return createClientKeyManager(DEFAULT_STORE_TYPE, storePath, storePass, keyAlias, storePass);
    }

    /**
     * Create a client key manager which returns a particular key. Does not
     * handle server keys. Uses the default store type, assumes the key
     * password is the same as the store password, and discovers the alias
     * by selecting the first private-key entry in the keystore.
     *
     * @param storePath the path to the keyStore file
     * @param storePass the keystore and key password
     * @return the customised {@link KeyManager}
     * @throws IOException              if the keystore file cannot be read
     * @throws GeneralSecurityException if the keystore cannot be loaded or
     *                                 no private-key entry can be located
     */
    public static KeyManager createClientKeyManager(File storePath, String storePass)
        throws IOException, GeneralSecurityException
    {
        return createClientKeyManager(DEFAULT_STORE_TYPE, storePath, storePass, null, storePass);
    }

    /**
     * Read a {@link KeyStore} from disk.
     *
     * @param storeType the type of the keyStore, e.g. {@code "JKS"}
     * @param storePath the path to the keyStore file
     * @param storePass the keystore password
     * @return the loaded keystore
     * @throws KeyStoreException       if the keystore cannot be instantiated
     * @throws IOException              if the keystore file cannot be read
     * @throws GeneralSecurityException if the keystore cannot be loaded
     */
    private static KeyStore loadStore(String storeType, File storePath, String storePass)
        throws KeyStoreException,  IOException, GeneralSecurityException {
        KeyStore ks = KeyStore.getInstance(storeType);
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(storePath);
            ks.load(stream, storePass.toCharArray());
        } finally {
            closeQuietly(stream);
        }
        return ks;
    }

    /**
     * Locate the first private-key alias in the supplied keystore.
     *
     * @param ks the keystore to inspect, must not be {@code null}
     * @return the alias of the first private-key entry
     * @throws KeyStoreException if no private-key entry can be found
     */
    private static String findAlias(KeyStore ks) throws KeyStoreException {
        Enumeration<String> e = ks.aliases();
        while(e.hasMoreElements()) {
            String entry = e.nextElement();
            if (ks.isKeyEntry(entry)) {
                return entry;
            }
        }
        throw new KeyStoreException("Cannot find a private key entry");
    }

    /**
     * Internal value object holding a single client key together with its
     * certificate chain.
     */
    private static class ClientKeyStore {

        /** The certificate chain associated with the selected key. */
        private final X509Certificate[] certChain;

        /** The private key itself. */
        private final PrivateKey key;

        /** The alias under which the key is stored. */
        private final String keyAlias;

        /**
         * Construct a new client keystore value object.
         *
         * @param ks       the source keystore
         * @param keyAlias the alias of the key to load
         * @param keyPass  the password of the key
         * @throws GeneralSecurityException if the key or its chain cannot
         *                                  be retrieved
         */
        ClientKeyStore(KeyStore ks, String keyAlias, String keyPass) throws GeneralSecurityException
        {
            this.keyAlias = keyAlias;
            this.key = (PrivateKey) ks.getKey(this.keyAlias, keyPass.toCharArray());
            Certificate[] certs = ks.getCertificateChain(this.keyAlias);
            X509Certificate[] X509certs = new X509Certificate[certs.length];
            for (int i=0; i < certs.length; i++) {
                X509certs[i] = (X509Certificate) certs[i];
            }
            this.certChain = X509certs;
        }

        /**
         * @return the certificate chain for the loaded key
         */
        final X509Certificate[] getCertificateChain() {
            return this.certChain;
        }

        /**
         * @return the loaded private key
         */
        final PrivateKey getPrivateKey() {
            return this.key;
        }

        /**
         * @return the alias under which the key is stored
         */
        final String getAlias() {
            return this.keyAlias;
        }
    }

    /**
     * Internal {@link X509ExtendedKeyManager} implementation that always
     * returns the same client key regardless of the issuer filter or
     * socket supplied.
     */
    private static class X509KeyManager extends X509ExtendedKeyManager  {

        /** Source of the single key returned by this manager. */
        private final ClientKeyStore keyStore;

        /**
         * Construct a new key manager backed by the supplied client keystore.
         *
         * @param keyStore the source keystore; must not be {@code null}
         */
        X509KeyManager(final ClientKeyStore keyStore) {
            this.keyStore = keyStore;
        }

        // Call sequence: 1
        @Override
        public String chooseClientAlias(String[] keyType, Principal[] issuers,
                Socket socket) {
            return keyStore.getAlias();
        }

        // Call sequence: 2
        @Override
        public X509Certificate[] getCertificateChain(String alias) {
            return keyStore.getCertificateChain();
        }

        @Override
        public String[] getClientAliases(String keyType, Principal[] issuers) {
            return new String[]{ keyStore.getAlias()};
        }

        // Call sequence: 3
        @Override
        public PrivateKey getPrivateKey(String alias) {
            return keyStore.getPrivateKey();
        }

        @Override
        public String[] getServerAliases(String keyType, Principal[] issuers) {
            return null;
        }

        @Override
        public String chooseServerAlias(String keyType, Principal[] issuers, Socket socket) {
            return null;
        }

    }

    /**
     * Closes the object quietly, catching rather than throwing any
     * {@link IOException}. Intended for use from {@code finally} blocks.
     *
     * @param closeable the object to close; may be {@code null}
     * @author <a href="https://github.com/loong10k">Loong Wan</a>
     * @since 3.0.0
     */
    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                // Ignored
            }
        }
    }

}