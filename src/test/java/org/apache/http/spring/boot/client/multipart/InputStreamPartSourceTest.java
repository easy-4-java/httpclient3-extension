package org.apache.http.spring.boot.client.multipart;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Unit tests for {@link InputStreamPartSource}.
 *
 * @since 3.0.0
 */
class InputStreamPartSourceTest {

    @Test
    void shouldReturnConfiguredFileName() {
        InputStream stream = new ByteArrayInputStream(new byte[0]);
        InputStreamPartSource source = new InputStreamPartSource("report.pdf", stream);
        assertEquals("report.pdf", source.getFileName());
    }

    @Test
    void shouldReturnNonameWhenFileNameIsNull() {
        InputStream stream = new ByteArrayInputStream(new byte[0]);
        InputStreamPartSource source = new InputStreamPartSource(null, stream);
        assertEquals("noname", source.getFileName());
    }

    @Test
    void shouldReturnZeroLengthWhenInputIsNull() {
        InputStreamPartSource source = new InputStreamPartSource("file.txt", null);
        assertEquals(0, source.getLength());
    }

    @Test
    void shouldReturnAvailableBytesAsLength() throws IOException {
        byte[] data = new byte[1024];
        InputStream stream = new ByteArrayInputStream(data);
        InputStreamPartSource source = new InputStreamPartSource("data.bin", stream);
        assertEquals(1024, source.getLength());
    }

    @Test
    void shouldReturnZeroLengthWhenAvailableThrows() {
        InputStream brokenStream = new InputStream() {
            @Override
            public int read() throws IOException {
                throw new IOException("broken");
            }

            @Override
            public int available() throws IOException {
                throw new IOException("cannot determine");
            }
        };
        InputStreamPartSource source = new InputStreamPartSource("broken.bin", brokenStream);
        assertEquals(0, source.getLength());
    }

    @Test
    void shouldReturnUnderlyingInputStream() throws IOException {
        InputStream stream = new ByteArrayInputStream("hello".getBytes());
        InputStreamPartSource source = new InputStreamPartSource("greeting.txt", stream);
        assertSame(stream, source.createInputStream());
    }

    @Test
    void shouldReturnNullInputStreamWhenConstructedWithNull() throws IOException {
        InputStreamPartSource source = new InputStreamPartSource("empty.txt", null);
        assertNull(source.createInputStream());
    }

    @Test
    void shouldReadContentFromCreatedInputStream() throws IOException {
        byte[] data = "test content".getBytes();
        InputStream stream = new ByteArrayInputStream(data);
        InputStreamPartSource source = new InputStreamPartSource("content.txt", stream);
        InputStream result = source.createInputStream();
        assertNotNull(result);
        byte[] buf = new byte[data.length];
        int read = result.read(buf);
        assertEquals(data.length, read);
        assertEquals("test content", new String(buf));
    }
}
