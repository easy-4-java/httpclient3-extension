/*
 * Copyright (c) 2018-present, easy-4-java (https://github.com/easy-4-java).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.http.spring.boot.client;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for {@link ContentType}.
 *
 * @since 3.0.0
 */
class ContentTypeTest {

    @Test
    void shouldExposeJsonContentType() {
        assertEquals("application/json", ContentType.APPLICATION_JSON);
    }

    @Test
    void shouldExposeXmlContentType() {
        assertEquals("application/xml", ContentType.APPLICATION_XML);
    }

    @Test
    void shouldExposeFormUrlencodedContentType() {
        assertEquals("application/x-www-form-urlencoded", ContentType.APPLICATION_FORM_URLENCODED);
    }

    @Test
    void shouldExposeOctetStreamContentType() {
        assertEquals("application/octet-stream", ContentType.APPLICATION_OCTET_STREAM);
    }

    @Test
    void shouldExposeAtomXmlContentType() {
        assertEquals("application/atom+xml", ContentType.APPLICATION_ATOM_XML);
    }

    @Test
    void shouldExposeSvgXmlContentType() {
        assertEquals("application/svg+xml", ContentType.APPLICATION_SVG_XML);
    }

    @Test
    void shouldExposeXhtmlXmlContentType() {
        assertEquals("application/xhtml+xml", ContentType.APPLICATION_XHTML_XML);
    }

    @Test
    void shouldExposeMultipartFormDataContentType() {
        assertEquals("multipart/form-data", ContentType.MULTIPART_FORM_DATA);
    }

    @Test
    void shouldExposeTextHtmlContentType() {
        assertEquals("text/html", ContentType.TEXT_HTML);
    }

    @Test
    void shouldExposeTextPlainContentType() {
        assertEquals("text/plain", ContentType.TEXT_PLAIN);
    }

    @Test
    void shouldExposeTextJsonContentType() {
        assertEquals("text/json", ContentType.TEXT_JSON);
    }

    @Test
    void shouldExposeTextXmlContentType() {
        assertEquals("text/xml", ContentType.TEXT_XML);
    }

    @Test
    void shouldExposeWildcardContentType() {
        assertEquals("*/*", ContentType.WILDCARD);
    }

    @Test
    void shouldExposeUtf8Charset() {
        assertEquals("UTF-8", ContentType.UTF_8);
    }

    @Test
    void shouldExposeAsciiCharset() {
        assertEquals("US-ASCII", ContentType.ASCII);
    }

    @Test
    void shouldExposeIso88591Charset() {
        assertEquals("ISO-8859-1", ContentType.ISO_8859_1);
    }
}