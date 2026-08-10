package org.apache.http.spring.boot.client.handler;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;

/**
 * Strategy that converts an Apache HttpClient 3.x execution result into a
 * caller-defined value type {@code T}.
 *
 * <p>The interface mirrors the well-known {@code ResponseHandler} pattern used
 * by Spring's {@code RestTemplate} but is adapted to the legacy
 * {@code commons-httpclient} 3.1 API. It exposes two hooks:</p>
 * <ul>
 *     <li>{@link #handleClient(HttpClient)} &mdash; invoked once per request
 *         to apply any pre-flight configuration to the shared
 *         {@link HttpClient}.</li>
 *     <li>{@link #handleResponse(HttpMethodBase)} &mdash; invoked after the
 *         HTTP method completes to convert the raw response into the
 *         caller-supplied return type.</li>
 * </ul>
 *
 * <p>Implementations are expected to be stateless and reusable across
 * threads.</p>
 *
 * @param <T> the type of value produced by {@link #handleResponse(HttpMethodBase)}
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see org.apache.commons.httpclient.HttpClient
 * @see org.apache.commons.httpclient.HttpMethodBase
 */
public interface ResponseHandler<T> {

    /**
     * Pre-process the supplied {@link HttpClient} before it is used to
     * execute the next request. Typical implementations tweak connection
     * timeouts, register authentication credentials, or attach stateful
     * interceptors.
     *
     * @param httpclient the live client that will perform the request;
     *                   never {@code null}
     */
    void handleClient(HttpClient httpclient);

    /**
     * Processes the result of an executed HTTP method and converts it into
     * a value of type {@code T}.
     *
     * @param httpMethod the completed HTTP method, never {@code null}
     * @return a value of type {@code T} derived from {@code httpMethod}
     * @throws IOException if the response cannot be read, the connection
     *                     was aborted, or any other I/O problem occurs
     */
    T handleResponse(HttpMethodBase httpMethod) throws IOException;

}