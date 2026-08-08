package org.apache.http.spring.boot.utils;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link HttpHeaders}.
 *
 * @since 3.0.0
 */
class HttpHeadersTest {

    @Test
    void shouldHavePrivateConstructor() throws Exception {
        Constructor<?> ctor = HttpHeaders.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(ctor.getModifiers()), "Constructor must be private");
        ctor.setAccessible(true);
        ctor.newInstance();
    }

    @Test
    void shouldBeFinal() {
        assertTrue(Modifier.isFinal(HttpHeaders.class.getModifiers()));
    }

    @Test
    void shouldExposeAcceptHeader() {
        assertEquals("Accept", HttpHeaders.ACCEPT);
    }

    @Test
    void shouldExposeAcceptCharsetHeader() {
        assertEquals("Accept-Charset", HttpHeaders.ACCEPT_CHARSET);
    }

    @Test
    void shouldExposeAcceptEncodingHeader() {
        assertEquals("Accept-Encoding", HttpHeaders.ACCEPT_ENCODING);
    }

    @Test
    void shouldExposeAcceptLanguageHeader() {
        assertEquals("Accept-Language", HttpHeaders.ACCEPT_LANGUAGE);
    }

    @Test
    void shouldExposeAcceptRangesHeader() {
        assertEquals("Accept-Ranges", HttpHeaders.ACCEPT_RANGES);
    }

    @Test
    void shouldExposeAgeHeader() {
        assertEquals("Age", HttpHeaders.AGE);
    }

    @Test
    void shouldExposeAllowHeader() {
        assertEquals("Allow", HttpHeaders.ALLOW);
    }

    @Test
    void shouldExposeAuthorizationHeader() {
        assertEquals("Authorization", HttpHeaders.AUTHORIZATION);
    }

    @Test
    void shouldExposeCacheControlHeader() {
        assertEquals("Cache-Control", HttpHeaders.CACHE_CONTROL);
    }

    @Test
    void shouldExposeConnectionHeader() {
        assertEquals("Connection", HttpHeaders.CONNECTION);
    }

    @Test
    void shouldExposeContentEncodingHeader() {
        assertEquals("Content-Encoding", HttpHeaders.CONTENT_ENCODING);
    }

    @Test
    void shouldExposeContentLanguageHeader() {
        assertEquals("Content-Language", HttpHeaders.CONTENT_LANGUAGE);
    }

    @Test
    void shouldExposeContentLengthHeader() {
        assertEquals("Content-Length", HttpHeaders.CONTENT_LENGTH);
    }

    @Test
    void shouldExposeContentLocationHeader() {
        assertEquals("Content-Location", HttpHeaders.CONTENT_LOCATION);
    }

    @Test
    void shouldExposeContentMd5Header() {
        assertEquals("Content-MD5", HttpHeaders.CONTENT_MD5);
    }

    @Test
    void shouldExposeContentRangeHeader() {
        assertEquals("Content-Range", HttpHeaders.CONTENT_RANGE);
    }

    @Test
    void shouldExposeContentTypeHeader() {
        assertEquals("Content-Type", HttpHeaders.CONTENT_TYPE);
    }

    @Test
    void shouldExposeDateHeader() {
        assertEquals("Date", HttpHeaders.DATE);
    }

    @Test
    void shouldExposeEtagHeader() {
        assertEquals("ETag", HttpHeaders.ETAG);
    }

    @Test
    void shouldExpiresHeader() {
        assertEquals("Expires", HttpHeaders.EXPIRES);
    }

    @Test
    void shouldExposeHostHeader() {
        assertEquals("Host", HttpHeaders.HOST);
    }

    @Test
    void shouldExposeLastModifiedHeader() {
        assertEquals("Last-Modified", HttpHeaders.LAST_MODIFIED);
    }

    @Test
    void shouldExposeLocationHeader() {
        assertEquals("Location", HttpHeaders.LOCATION);
    }

    @Test
    void shouldExposeUserAgentHeader() {
        assertEquals("User-Agent", HttpHeaders.USER_AGENT);
    }

    @Test
    void shouldExposeWwwAuthenticateHeader() {
        assertEquals("WWW-Authenticate", HttpHeaders.WWW_AUTHENTICATE);
    }

    @Test
    void shouldExposeXForwardedForHeader() {
        assertEquals("x-forwarded-for", HttpHeaders.X_FORWARDED_FOR);
    }

    @Test
    void shouldExposeXRequestedWithHeader() {
        assertEquals("X-Requested-With", HttpHeaders.X_REQUESTED_WITH);
    }
}
