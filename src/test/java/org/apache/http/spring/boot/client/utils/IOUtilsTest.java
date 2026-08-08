package org.apache.http.spring.boot.client.utils;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link IOUtils}.
 *
 * @since 3.0.0
 */
class IOUtilsTest {

    // ---- closeQuietly(InputStream) tests ----

    @Test
    void shouldCloseInputStreamQuietly() {
        boolean[] closed = {false};
        InputStream input = new InputStream() {
            @Override
            public int read() { return -1; }

            @Override
            public void close() throws IOException {
                closed[0] = true;
                super.close();
            }
        };
        IOUtils.closeQuietly(input);
        assertTrue(closed[0], "InputStream must be closed");
    }

    @Test
    void shouldHandleNullInputStream() {
        IOUtils.closeQuietly((InputStream) null);
        // No exception expected
    }

    @Test
    void shouldSwallowIOExceptionOnInputStreamClose() {
        InputStream throwing = new InputStream() {
            @Override
            public int read() { return -1; }

            @Override
            public void close() throws IOException {
                throw new IOException("close failed");
            }
        };
        IOUtils.closeQuietly(throwing);
        // No exception expected
    }

    // ---- closeQuietly(OutputStream) tests ----

    @Test
    void shouldCloseOutputStreamQuietly() {
        boolean[] closed = {false};
        OutputStream output = new OutputStream() {
            @Override
            public void write(int b) {}

            @Override
            public void close() throws IOException {
                closed[0] = true;
                super.close();
            }
        };
        IOUtils.closeQuietly(output);
        assertTrue(closed[0], "OutputStream must be closed");
    }

    @Test
    void shouldHandleNullOutputStream() {
        IOUtils.closeQuietly((OutputStream) null);
        // No exception expected
    }

    @Test
    void shouldSwallowIOExceptionOnOutputStreamClose() {
        OutputStream throwing = new OutputStream() {
            @Override
            public void write(int b) {}

            @Override
            public void close() throws IOException {
                throw new IOException("close failed");
            }
        };
        IOUtils.closeQuietly(throwing);
        // No exception expected
    }

    // ---- closeQuietly(Closeable) tests ----

    @Test
    void shouldCloseCloseableQuietly() {
        boolean[] closed = {false};
        Closeable closeable = () -> closed[0] = true;
        IOUtils.closeQuietly(closeable);
        assertTrue(closed[0], "Closeable must be closed");
    }

    @Test
    void shouldHandleNullCloseable() {
        IOUtils.closeQuietly((Closeable) null);
        // No exception expected
    }

    @Test
    void shouldSwallowIOExceptionOnCloseableClose() {
        Closeable throwing = () -> {
            throw new IOException("boom");
        };
        IOUtils.closeQuietly(throwing);
        // No exception expected
    }

    @Test
    void shouldCloseRealByteArrayInputStream() {
        ByteArrayInputStream bais = new ByteArrayInputStream("data".getBytes());
        IOUtils.closeQuietly(bais);
        // ByteArrayInputStream.close() is a no-op, but exercises the code path
    }

    @Test
    void shouldCloseRealByteArrayOutputStream() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IOUtils.closeQuietly(baos);
        // ByteArrayOutputStream.close() is a no-op, but exercises the code path
    }
}
