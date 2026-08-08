package org.apache.http.spring.boot.client.handler;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link ResponseHandler}.
 *
 * @since 3.0.0
 */
class ResponseHandlerTest {

    @Test
    void shouldBeAnInterface() {
        assertTrue(ResponseHandler.class.isInterface(),
                "ResponseHandler must be an interface");
    }

    @Test
    void shouldDeclareHandleClientMethod() throws NoSuchMethodException {
        Method m = ResponseHandler.class.getDeclaredMethod("handleClient", HttpClient.class);
        assertNotNull(m);
        assertEquals(void.class, m.getReturnType());
    }

    @Test
    void shouldDeclareHandleResponseMethod() throws NoSuchMethodException {
        Method m = ResponseHandler.class.getDeclaredMethod("handleResponse", HttpMethodBase.class);
        assertNotNull(m);
        assertTrue(m.getExceptionTypes().length > 0, "handleResponse must declare throws IOException");
    }

    @Test
    void shouldBeImplementable() throws Exception {
        ResponseHandler<String> handler = new ResponseHandler<>() {
            @Override
            public void handleClient(HttpClient httpclient) {
                // no-op
            }

            @Override
            public String handleResponse(HttpMethodBase httpMethod) throws IOException {
                return "ok";
            }
        };
        assertNotNull(handler);
        handler.handleClient(new HttpClient());
        assertEquals("ok", handler.handleResponse(null));
    }
}
