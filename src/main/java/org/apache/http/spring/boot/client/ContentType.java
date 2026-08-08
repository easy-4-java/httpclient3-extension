package org.apache.http.spring.boot.client;

/**
 * Constants for common HTTP content types and character encodings.
 *
 * <p>This class gathers the MIME types most frequently used by the
 * {@code httpclient3-extension} into a single, easily referenced location.
 * The values follow the IANA Media Type registry as referenced by
 * {@code RFC 7231} (HTTP/1.1) and {@code RFC 2046} (MIME).</p>
 *
 * <p>The class is abstract and has no public constructor &mdash; it cannot be
 * instantiated. All members are static and treated as compile-time
 * constants.</p>
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 * @see org.apache.http.spring.boot.client.utils.HttpHeaders
 */
public abstract class ContentType {

    /** {@code application/atom+xml} &mdash; Atom syndication format. */
    public static final String APPLICATION_ATOM_XML = "application/atom+xml";

    /** {@code application/json} &mdash; JavaScript Object Notation. */
    public static final String APPLICATION_JSON = "application/json";

    /** {@code application/x-www-form-urlencoded} &mdash; HTML form post body. */
    public static final String APPLICATION_FORM_URLENCODED = "application/x-www-form-urlencoded";

    /** {@code application/octet-stream} &mdash; arbitrary binary content. */
    public static final String APPLICATION_OCTET_STREAM = "application/octet-stream";

    /** {@code application/svg+xml} &mdash; Scalable Vector Graphics. */
    public static final String APPLICATION_SVG_XML = "application/svg+xml";

    /** {@code application/xhtml+xml} &mdash; XHTML document. */
    public static final String APPLICATION_XHTML_XML = "application/xhtml+xml";

    /** {@code application/xml} &mdash; generic XML. */
    public static final String APPLICATION_XML = "application/xml";

    /** {@code multipart/form-data} &mdash; multipart HTML form upload. */
    public static final String MULTIPART_FORM_DATA = "multipart/form-data";

    /** {@code text/html} &mdash; HyperText Markup Language. */
    public static final String TEXT_HTML = "text/html";

    /** {@code text/plain} &mdash; plain, unformatted text. */
    public static final String TEXT_PLAIN = "text/plain";

    /** {@code text/json} &mdash; JSON (informal, non-standard). */
    public static final String TEXT_JSON = "text/json";

    /** {@code text/xml} &mdash; plain XML (informal). */
    public static final String TEXT_XML = "text/xml";

    /** {@code * / *} &mdash; wildcard match for any media type. */
    public static final String WILDCARD = "*/*";

    /** {@code UTF-8} &mdash; canonical 8-bit Unicode encoding name. */
    public static final String UTF_8 = "UTF-8";

    /** {@code US-ASCII} &mdash; 7-bit American Standard Code for Information Interchange. */
    public static final String ASCII = "US-ASCII";

    /** {@code ISO-8859-1} &mdash; Latin-1 character set encoding. */
    public static final String ISO_8859_1 = "ISO-8859-1";


}