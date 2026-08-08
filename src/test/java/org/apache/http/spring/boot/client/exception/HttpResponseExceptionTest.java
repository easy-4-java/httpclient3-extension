package org.apache.http.spring.boot.client.exception;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link HttpResponseException}.
 *
 * @since 3.0.0
 */
class HttpResponseExceptionTest {

    @Test
    void shouldExtendIOException() {
        HttpResponseException ex = new HttpResponseException("test");
        assertTrue(ex instanceof IOException, "Must be an IOException subclass");
    }

    @Test
    void shouldDefaultStatusCodeTo200() {
        HttpResponseException ex = new HttpResponseException("msg");
        assertEquals(200, ex.getStatusCode());
    }

    @Test
    void shouldPreserveMessage() {
        HttpResponseException ex = new HttpResponseException("not found");
        assertEquals("not found", ex.getMessage());
    }

    @Test
    void shouldAcceptNullMessage() {
        HttpResponseException ex = new HttpResponseException((String) null);
        assertNull(ex.getMessage());
        assertEquals(200, ex.getStatusCode());
    }

    @Test
    void shouldPreserveCause() {
        IOException cause = new IOException("root cause");
        HttpResponseException ex = new HttpResponseException("wrapper", cause);
        assertEquals("wrapper", ex.getMessage());
        assertNotNull(ex.getCause());
        assertEquals("root cause", ex.getCause().getMessage());
    }

    @Test
    void shouldAcceptNullCause() {
        HttpResponseException ex = new HttpResponseException("msg", null);
        assertEquals("msg", ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    void shouldStoreStatusCode() {
        HttpResponseException ex = new HttpResponseException(404, "Not Found");
        assertEquals(404, ex.getStatusCode());
        assertEquals("Not Found", ex.getMessage());
    }

    @Test
    void shouldStoreServerErrorStatusCode() {
        HttpResponseException ex = new HttpResponseException(500, "Internal Server Error");
        assertEquals(500, ex.getStatusCode());
        assertEquals("Internal Server Error", ex.getMessage());
    }

    @Test
    void shouldAcceptNullMessageWithStatusCode() {
        HttpResponseException ex = new HttpResponseException(403, null);
        assertEquals(403, ex.getStatusCode());
        assertNull(ex.getMessage());
    }
}
