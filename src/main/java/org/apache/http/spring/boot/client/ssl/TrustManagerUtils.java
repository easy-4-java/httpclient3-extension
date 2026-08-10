/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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

import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * Factory helpers that produce {@link X509TrustManager} instances for the
 * {@code httpclient3-extension}.
 *
 * <p>Three flavours of trust manager are exposed:</p>
 * <ul>
 *     <li>a permissive manager that accepts every certificate;</li>
 *     <li>a validating manager that still accepts every certificate but
 *         additionally checks that each one is within its validity period;</li>
 *     <li>the JVM's default trust manager, optionally initialised from a
 *         caller-supplied {@link KeyStore}.</li>
 * </ul>
 *
 * <p>The class is final and not instantiable.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see javax.net.ssl.X509TrustManager
 * @see javax.net.ssl.TrustManagerFactory
 */
public final class TrustManagerUtils
{
    /** Empty array reused for {@link X509TrustManager#getAcceptedIssuers()}. */
    private static final X509Certificate[] EMPTY_X509CERTIFICATE_ARRAY = new X509Certificate[]{};

    /**
     * Internal trust manager implementation backed by a configurable
     * server-validity check.
     */
    private static class TrustManager implements X509TrustManager {

        /** When {@code true}, validates every certificate chain entry for time validity. */
        private final boolean checkServerValidity;

        /**
         * Construct a new internal trust manager.
         *
         * @param checkServerValidity when {@code true} {@code checkServerTrusted}
         *                            additionally calls
         *                            {@link X509Certificate#checkValidity()} on
         *                            every supplied certificate
         */
        TrustManager(boolean checkServerValidity) {
            this.checkServerValidity = checkServerValidity;
        }

        /**
         * Accepts any client certificate chain without question.
         *
         * @param certificates the peer certificate chain (ignored)
         * @param authType     the key-exchange algorithm (ignored)
         */
        @Override
        public void checkClientTrusted(X509Certificate[] certificates, String authType)
        {
            return;
        }

        /**
         * Verifies the supplied server certificate chain. When the manager
         * was created with {@code checkServerValidity=true} every entry is
         * additionally checked for time validity.
         *
         * @param certificates the peer certificate chain; must not be {@code null}
         * @param authType     the key-exchange algorithm; currently ignored
         * @throws CertificateException if the chain contains an expired or
         *                              not-yet-valid certificate (only when
         *                              {@code checkServerValidity=true})
         */
        @Override
        public void checkServerTrusted(X509Certificate[] certificates, String authType)
            throws CertificateException
        {
            if (checkServerValidity) {
                for (X509Certificate certificate : certificates)
                {
                    certificate.checkValidity();
                }
            }
        }

        /**
         * @return an empty array of certificates; this implementation does
         *         not act as a certificate authority
         */
        @Override
        public X509Certificate[] getAcceptedIssuers()
        {
            return EMPTY_X509CERTIFICATE_ARRAY;
        }
    }

    /** Shared permissive trust manager. */
    private static final X509TrustManager ACCEPT_ALL=new TrustManager(false);

    /** Shared strict (time-validating) trust manager. */
    private static final X509TrustManager CHECK_SERVER_VALIDITY=new TrustManager(true);

    /**
     * Returns a trust manager that performs no certificate validation at
     * all. Intended for development and testing only.
     *
     * @return a singleton {@link X509TrustManager} that accepts every
     *         client and server certificate
     */
    public static X509TrustManager getAcceptAllTrustManager(){
        return ACCEPT_ALL;
    }

    /**
     * Returns a trust manager that accepts every certificate but verifies
     * that each certificate in the server chain is currently within its
     * validity window.
     *
     * @return a singleton {@link X509TrustManager} that checks server
     *         certificate validity and otherwise performs no further
     *         validation
     */
    public static X509TrustManager getValidateServerCertificateTrustManager(){
        return CHECK_SERVER_VALIDITY;
    }

    /**
     * Build the JVM's default {@link X509TrustManager} initialised against
     * the supplied keystore. When the keystore is {@code null} the JRE's
     * bundled {@code cacerts} file is used, mirroring the default behaviour
     * of {@link javax.net.ssl.SSLContext#init(javax.net.ssl.KeyManager[],
     * javax.net.ssl.TrustManager[], java.security.SecureRandom)} when its
     * trust manager parameter is {@code null}.
     *
     * @param keyStore the keystore to use as a source of trusted X.509
     *                 certificates; may be {@code null}
     * @return the first {@link X509TrustManager} returned by the default
     *         {@link TrustManagerFactory}
     * @throws GeneralSecurityException if the default trust-manager
     *                                 algorithm cannot be obtained, the
     *                                 factory cannot be initialised, or
     *                                 no trust managers are registered
     */
    public static X509TrustManager getDefaultTrustManager(KeyStore keyStore) throws GeneralSecurityException {
        String defaultAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(defaultAlgorithm);
        trustManagerFactory.init(keyStore);
		return (X509TrustManager) trustManagerFactory.getTrustManagers()[0];

    }

}