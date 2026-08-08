package org.apache.http.spring.boot.client.property;

import org.apache.http.spring.boot.client.property.HttpConnectionManagerProperties.ManagerType;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link HttpConnectionManagerProperties} and its
 * inner {@link ManagerType} enum.
 *
 * @since 3.0.0
 */
class HttpConnectionManagerPropertiesTest {

    // ---- ManagerType enum tests ----

    @Test
    void shouldExposeMultiThreadedType() {
        assertEquals("multi-threaded", ManagerType.MULTI_THREADED.get());
    }

    @Test
    void shouldExposeSimpleType() {
        assertEquals("simple", ManagerType.SIMPLE.get());
    }

    @Test
    void shouldMatchSameTypeViaEqualsEnum() {
        assertTrue(ManagerType.SIMPLE.equals(ManagerType.SIMPLE));
        assertTrue(ManagerType.MULTI_THREADED.equals(ManagerType.MULTI_THREADED));
    }

    @Test
    void shouldNotMatchDifferentTypeViaEqualsEnum() {
        assertFalse(ManagerType.SIMPLE.equals(ManagerType.MULTI_THREADED));
    }

    @Test
    void shouldMatchCaseInsensitiveString() {
        assertTrue(ManagerType.SIMPLE.equals("simple"));
        assertTrue(ManagerType.SIMPLE.equals("SIMPLE"));
        assertTrue(ManagerType.SIMPLE.equals("Simple"));
        assertTrue(ManagerType.MULTI_THREADED.equals("multi-threaded"));
        assertTrue(ManagerType.MULTI_THREADED.equals("MULTI-THREADED"));
    }

    @Test
    void shouldNotMatchDifferentString() {
        assertFalse(ManagerType.SIMPLE.equals("multi-threaded"));
    }

    @Test
    void shouldLookupByCaseInsensitiveKey() {
        assertEquals(ManagerType.SIMPLE, ManagerType.valueOfIgnoreCase("simple"));
        assertEquals(ManagerType.SIMPLE, ManagerType.valueOfIgnoreCase("SIMPLE"));
        assertEquals(ManagerType.MULTI_THREADED, ManagerType.valueOfIgnoreCase("multi-threaded"));
    }

    @Test
    void shouldThrowForUnknownManagerTypeKey() {
        assertThrows(NoSuchElementException.class, () -> ManagerType.valueOfIgnoreCase("unknown"));
    }

    @Test
    void shouldThrowForNullManagerTypeKey() {
        assertThrows(NoSuchElementException.class, () -> ManagerType.valueOfIgnoreCase(null));
    }

    @Test
    void shouldHaveTwoValues() {
        assertEquals(2, ManagerType.values().length);
    }

    // ---- Properties class tests ----

    @Test
    void shouldDefaultToSimpleManagerType() {
        HttpConnectionManagerProperties props = new HttpConnectionManagerProperties();
        assertEquals(ManagerType.SIMPLE, props.getType());
    }

    @Test
    void shouldAllowChangingManagerType() {
        HttpConnectionManagerProperties props = new HttpConnectionManagerProperties();
        props.setType(ManagerType.MULTI_THREADED);
        assertEquals(ManagerType.MULTI_THREADED, props.getType());
    }

    @Test
    void shouldDefaultAlwaysCloseToFalse() {
        HttpConnectionManagerProperties props = new HttpConnectionManagerProperties();
        assertFalse(props.isAlwaysClose());
    }

    @Test
    void shouldAllowSettingAlwaysClose() {
        HttpConnectionManagerProperties props = new HttpConnectionManagerProperties();
        props.setAlwaysClose(true);
        assertTrue(props.isAlwaysClose());
    }

    @Test
    void shouldReturnDefaultTimeoutInterval() {
        HttpConnectionManagerProperties props = new HttpConnectionManagerProperties();
        assertEquals(HttpConnectionManagerProperties.DEFAULT_TIMEOUT_INTERVAL,
                props.getTimeoutInterval());
    }

    @Test
    void shouldAllowSettingTimeoutInterval() {
        HttpConnectionManagerProperties props = new HttpConnectionManagerProperties();
        props.setTimeoutInterval(10000);
        assertEquals(10000, props.getTimeoutInterval());
    }

    @Test
    void shouldInitializeWithDefaults() {
        HttpConnectionManagerProperties props = new HttpConnectionManagerProperties();
        HttpConnectionManagerProperties result = props.getInitedParams();
        assertNotNull(result);
        assertEquals(30000, result.getConnectionTimeout());
        assertEquals(60000, result.getSoTimeout());
        assertEquals(20, result.getDefaultMaxConnectionsPerHost());
        assertEquals(60, result.getMaxTotalConnections());
        assertTrue(result.getTcpNoDelay());
    }

    @Test
    void shouldBeFluentFromGetInitedParams() {
        HttpConnectionManagerProperties props = new HttpConnectionManagerProperties();
        HttpConnectionManagerProperties returned = props.getInitedParams();
        assertTrue(returned == props, "getInitedParams() should return this");
    }

    @Test
    void shouldHaveTimeoutIntervalConstant() {
        assertEquals("http.timeout.interval", HttpConnectionManagerProperties.TIMEOUT_INTERVAL);
    }

    @Test
    void shouldHaveDefaultTimeoutConstant() {
        assertEquals(5000, HttpConnectionManagerProperties.DEFAULT_TIMEOUT_INTERVAL);
    }
}
