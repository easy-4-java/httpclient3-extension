package org.apache.http.spring.boot.utils;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HeaderGroup;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit tests for {@link HttpResponeUtils}.
 *
 * @since 3.0.0
 */
class HttpResponeUtilsTest {

    @Test
    void shouldReadContentTypeFromResponse() throws Exception {
        GetMethod method = new GetMethod("/test");
        addResponseHeader(method, "Content-Type", "application/json");
        String contentType = HttpResponeUtils.getContentType(method);
        assertEquals("application/json", contentType);
    }

    @Test
    void shouldReadHtmlContentType() throws Exception {
        GetMethod method = new GetMethod("/page");
        addResponseHeader(method, "Content-Type", "text/html; charset=UTF-8");
        String contentType = HttpResponeUtils.getContentType(method);
        assertEquals("text/html; charset=UTF-8", contentType);
    }

    @Test
    void shouldReadXmlContentType() throws Exception {
        GetMethod method = new GetMethod("/api");
        addResponseHeader(method, "Content-Type", "application/xml");
        String contentType = HttpResponeUtils.getContentType(method);
        assertEquals("application/xml", contentType);
    }

    @Test
    void shouldHaveLoggerField() throws Exception {
        assertNotNull(HttpResponeUtils.class.getDeclaredField("LOG"));
    }

    /**
     * Add a response header to an HttpMethodBase using the protected
     * {@code getResponseHeaderGroup()} method (accessed via reflection).
     */
    private static void addResponseHeader(HttpMethodBase method, String name, String value) throws Exception {
        Method getGroup = HttpMethodBase.class.getDeclaredMethod("getResponseHeaderGroup");
        getGroup.setAccessible(true);
        HeaderGroup group = (HeaderGroup) getGroup.invoke(method);
        group.addHeader(new Header(name, value));
    }
}
