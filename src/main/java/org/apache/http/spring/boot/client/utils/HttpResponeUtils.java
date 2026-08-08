package org.apache.http.spring.boot.utils;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethodBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Static helpers that read common response properties from an Apache
 * HttpClient 3.x {@link HttpMethodBase}.
 *
 * <p>The class is abstract and not instantiable.</p>
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 * @see HttpMethodBase
 * @see org.apache.http.spring.boot.client.utils.HttpHeaders
 */
public abstract class HttpResponeUtils{

	/** Logger for the class. */
	protected static Logger LOG = LoggerFactory.getLogger(HttpResponeUtils.class);

	/**
	 * Read the {@code Content-Type} header of the supplied response.
	 *
	 * <p>The value is returned verbatim, without any charset or boundary
	 * stripping. If the header is missing, the underlying
	 * {@code Header.getValue()} call returns {@code null}.</p>
	 *
	 * @param httpMethod the HTTP method whose response should be inspected;
	 *                   must not be {@code null}
	 * @return the raw {@code Content-Type} header value, or {@code null} if
	 *         the header is not set on the response
	 */
	public static String getContentType(HttpMethodBase httpMethod) {
		Header header = httpMethod.getResponseHeader(HttpHeaders.CONTENT_TYPE);
		/*HeaderElement[] elements = header.getElements();
		for (HeaderElement elem : elements) {
			LOG.debug(elem.getName() + " = " + elem.getValue());
			if ("gzip".equalsIgnoreCase(elem.getName())) {
				contentType = elem.getValue();
				break;
			}
		}*/
		return header.getValue();
	}
}