package org.apache.http.spring.boot.client.multipart;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.httpclient.methods.multipart.PartSource;

/**
 * {@link PartSource} implementation backed by an in-memory
 * {@link InputStream}.
 *
 * <p>This is useful when the data to upload already exists as a stream
 * &mdash; for example the body of another HTTP response, an in-memory
 * buffer, or a decoded message &mdash; and there is no on-disk file to
 * pass to the multipart machinery. The stream is consumed lazily and is
 * not closed by this class; the caller remains responsible for the
 * stream's lifecycle.</p>
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 * @see org.apache.commons.httpclient.methods.multipart.PartSource
 */
public class InputStreamPartSource implements PartSource {


    /** Stream part input. */
    private InputStream input = null;

    /** File part file name. */
    private String fileName = null;

    /**
     * Build a new {@code InputStreamPartSource}.
     *
     * @param fileName the file name to advertise to the receiving peer; if
     *                 {@code null} the literal string {@code "noname"} is
     *                 used instead
     * @param input    the source stream, may be {@code null} for a
     *                 zero-length part
     */
	public InputStreamPartSource( String fileName , InputStream input) {
		super();
		this.fileName = fileName;
		this.input = input;
	}

	/**
	 * @return the number of bytes currently available for reading without
	 *         blocking, or {@code 0} when no input stream is attached or
	 *         the underlying {@link InputStream#available()} call throws
	 */
	@Override
	public long getLength() {
		if (this.input != null) {
            try {
				return this.input.available();
			} catch (IOException e) {
				//	e.printStackTrace();
				return 0;
			}
        } else {
            return 0;
        }
	}

	/**
	 * @return the configured file name, or the literal string
	 *         {@code "noname"} when no file name was supplied
	 */
	@Override
	public String getFileName() {
		 return (fileName == null) ? "noname" : fileName;
	}

	/**
	 * Return the underlying {@link InputStream} unchanged. The stream is
	 * <em>not</em> closed by this method.
	 *
	 * @return the source stream, possibly {@code null}
	 * @throws IOException never thrown by this implementation, but
	 *                     declared by the {@link PartSource} contract
	 */
	@Override
	public InputStream createInputStream() throws IOException {
		return input;
	}

}