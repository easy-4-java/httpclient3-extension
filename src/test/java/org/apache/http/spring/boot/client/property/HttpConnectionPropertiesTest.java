package org.apache.http.spring.boot.client.property;

import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Unit tests for {@link HttpConnectionProperties}.
 *
 * @since 3.0.0
 */
class HttpConnectionPropertiesTest {

    @Test
    void shouldReturnSelfAsHttpConnectionParams() {
        HttpConnectionProperties props = new HttpConnectionProperties();
        HttpConnectionParams params = props.getHttpConnectionParams();
        assertNotNull(params);
        assertSame(props, params, "getHttpConnectionParams() should return this");
    }

    @Test
    void shouldExtendHttpConnectionParams() {
        HttpConnectionProperties props = new HttpConnectionProperties();
        assertTrue(props instanceof HttpConnectionParams);
    }

    private void assertTrue(boolean condition) {
        org.junit.jupiter.api.Assertions.assertTrue(condition);
    }
}
