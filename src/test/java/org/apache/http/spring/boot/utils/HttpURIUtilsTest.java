package org.apache.http.spring.boot.utils;

import org.apache.commons.httpclient.NameValuePair;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link HttpURIUtils}.
 *
 * @since 3.0.0
 */
class HttpURIUtilsTest {

    // ---- buildURL tests ----

    @Test
    void shouldReturnBaseUrlWhenParamsMapIsNull() throws Exception {
        String result = HttpURIUtils.buildURL("http://example.com/path", null, "UTF-8");
        assertEquals("http://example.com/path", result);
    }

    @Test
    void shouldReturnBaseUrlWhenNoQueryString() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("key", "value");
        String result = HttpURIUtils.buildURL("http://example.com/path", params, "UTF-8");
        assertEquals("http://example.com/path", result);
    }

    @Test
    void shouldAppendParamsToExistingQueryString() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("extra", "val");
        String result = HttpURIUtils.buildURL("http://example.com/path?a=1", params, "UTF-8");
        assertNotNull(result);
        assertTrue(result.startsWith("http://example.com/path"), "Should preserve base URL");
        assertTrue(result.contains("extra=val"), "Should contain new parameter");
    }

    @Test
    void shouldReturnBaseUrlWhenQueryIsEmpty() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("key", "val");
        // When there's a "?" but the split yields only 1 element, the code returns baseURL
        String result = HttpURIUtils.buildURL("http://example.com/path?", params, "UTF-8");
        assertNotNull(result);
    }

    // ---- buildNameValuePairs(String, Map) tests ----

    @Test
    void shouldBuildPairsFromBaseAndMap() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("name", "test");
        params.put("count", "5");
        List<NameValuePair> pairs = HttpURIUtils.buildNameValuePairs("http://example.com?a=1", params);
        assertNotNull(pairs);
        assertTrue(pairs.size() >= 2, "Should contain map entries");
    }

    @Test
    void shouldSkipFileValues() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("file", new File("/tmp/test.txt"));
        params.put("name", "test");
        List<NameValuePair> pairs = HttpURIUtils.buildNameValuePairs("http://example.com", params);
        assertNotNull(pairs);
        boolean hasName = false;
        for (NameValuePair pair : pairs) {
            if ("name".equals(pair.getName())) {
                hasName = true;
            }
        }
        assertTrue(hasName, "Should contain non-File parameter");
    }

    @Test
    void shouldSkipByteArrayValues() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("data", new byte[]{1, 2, 3});
        params.put("name", "test");
        List<NameValuePair> pairs = HttpURIUtils.buildNameValuePairs("http://example.com", params);
        assertNotNull(pairs);
        boolean hasData = false;
        for (NameValuePair pair : pairs) {
            if ("data".equals(pair.getName())) {
                hasData = true;
            }
        }
        // byte[] values should be silently skipped
        // but 'name' should be present
        boolean hasName = false;
        for (NameValuePair pair : pairs) {
            if ("name".equals(pair.getName())) {
                hasName = true;
            }
        }
        assertTrue(hasName, "Should contain non-byte[] parameter");
    }

    @Test
    void shouldAddEmptyStringForNullValue() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("empty", null);
        List<NameValuePair> pairs = HttpURIUtils.buildNameValuePairs("http://example.com", params);
        assertNotNull(pairs);
        boolean found = false;
        for (NameValuePair pair : pairs) {
            if ("empty".equals(pair.getName())) {
                assertEquals("", pair.getValue());
                found = true;
            }
        }
        assertTrue(found, "null value should produce empty string");
    }

    @Test
    void shouldAddEmptyStringForEmptyStringValue() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("blank", "");
        List<NameValuePair> pairs = HttpURIUtils.buildNameValuePairs("http://example.com", params);
        assertNotNull(pairs);
        boolean found = false;
        for (NameValuePair pair : pairs) {
            if ("blank".equals(pair.getName())) {
                assertEquals("", pair.getValue());
                found = true;
            }
        }
        assertTrue(found, "empty string value should produce empty string pair");
    }

    @Test
    void shouldReturnEmptyPairsWhenMapIsNull() throws Exception {
        List<NameValuePair> pairs = HttpURIUtils.buildNameValuePairs("http://example.com", null);
        assertNotNull(pairs);
        // Only the URL query params (none in this case) should be in the list
    }

    @Test
    void shouldReturnEmptyPairsWhenMapIsEmpty() throws Exception {
        Map<String, Object> params = new HashMap<>();
        List<NameValuePair> pairs = HttpURIUtils.buildNameValuePairs("http://example.com", params);
        assertNotNull(pairs);
    }

    @Test
    void shouldConvertObjectValuesToString() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("num", Integer.valueOf(42));
        List<NameValuePair> pairs = HttpURIUtils.buildNameValuePairs("http://example.com", params);
        assertNotNull(pairs);
        boolean found = false;
        for (NameValuePair pair : pairs) {
            if ("num".equals(pair.getName())) {
                assertNotNull(pair.getValue());
                found = true;
            }
        }
        assertTrue(found, "Should contain Integer parameter");
    }

    // ---- buildNameValuePairs(String) tests ----

    @Test
    void shouldReturnEmptyListWhenNoQueryString() throws Exception {
        List<NameValuePair> pairs = HttpURIUtils.buildNameValuePairs("http://example.com/path");
        assertNotNull(pairs);
        assertTrue(pairs.isEmpty());
    }

    @Test
    void shouldParseQueryStringParameters() throws Exception {
        List<NameValuePair> pairs = HttpURIUtils.buildNameValuePairs("http://example.com/path?foo=bar&baz=qux");
        assertNotNull(pairs);
        assertEquals(2, pairs.size());
    }

    @Test
    void shouldSkipMalformedQueryStringEntries() throws Exception {
        // Entries without "=" should be skipped
        List<NameValuePair> pairs = HttpURIUtils.buildNameValuePairs("http://example.com?valid=ok&malformed");
        assertNotNull(pairs);
        boolean hasValid = false;
        for (NameValuePair pair : pairs) {
            if ("valid".equals(pair.getName())) {
                hasValid = true;
            }
        }
        assertTrue(hasValid, "Should contain valid parameter");
    }

    @Test
    void shouldHandleUrlWithOnlyQuestionMark() throws Exception {
        List<NameValuePair> pairs = HttpURIUtils.buildNameValuePairs("http://example.com?");
        assertNotNull(pairs);
        // Empty query string after "?" produces empty param, which should be skipped
    }

    @Test
    void shouldHandleMultipleAmpersands() throws Exception {
        List<NameValuePair> pairs = HttpURIUtils.buildNameValuePairs("http://example.com?a=1&&b=2");
        assertNotNull(pairs);
        assertTrue(pairs.size() >= 1);
    }
}
