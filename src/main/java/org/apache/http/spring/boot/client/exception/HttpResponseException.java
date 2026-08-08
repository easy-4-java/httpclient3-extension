package org.apache.http.spring.boot.client.exception;

import java.io.IOException;

/**
 * Unchecked wrapper for an HTTP error response.
 *
 * <p>Subclasses of {@link IOException} are traditionally used by Java
 * network APIs to signal I/O problems, but they do not carry the HTTP
 * status code that triggered the failure. This exception adds a
 * {@link #getStatusCode()} accessor while still behaving like a regular
 * {@link IOException} &mdash; code that already catches
 * {@code IOException} does not need to be modified.</p>
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 * @see java.io.IOException
 */
@SuppressWarnings("serial")
public class HttpResponseException extends IOException {

	/** HTTP status code returned by the failing response. Defaults to {@code 200}. */
	private int statusCode = 200;

	/**
	 * Build an exception with the given message and no cause. The status
	 * code defaults to {@code 200}.
	 *
	 * @param message the human-readable failure message; may be {@code null}
	 */
	public HttpResponseException(String message) {
		super(message);
	}

	/**
	 * Build an exception with the given message and underlying cause.
	 * The status code defaults to {@code 200}.
	 *
	 * @param message the human-readable failure message; may be {@code null}
	 * @param cause   the original cause; may be {@code null}
	 */
	public HttpResponseException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Build an exception that carries both the HTTP status code and the
	 * message returned by the failing response.
	 *
	 * @param statusCode the HTTP status code returned by the server
	 * @param s          the human-readable failure message; may be {@code null}
	 */
	public HttpResponseException(final int statusCode, final String s) {
		super(s);
		this.statusCode = statusCode;
	}

	/**
	 * @return the HTTP status code carried by this exception, or
	 *         {@code 200} when the exception was constructed via the
	 *         message-only constructor
	 */
	public int getStatusCode() {
		return this.statusCode;
	}

}