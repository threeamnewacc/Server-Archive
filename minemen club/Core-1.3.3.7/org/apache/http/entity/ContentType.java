// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.entity;

import java.util.Collections;
import java.util.HashMap;
import org.apache.http.Consts;
import java.util.Iterator;
import java.util.List;
import org.apache.http.message.BasicNameValuePair;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.message.BasicHeaderValueParser;
import org.apache.http.message.ParserCursor;
import org.apache.http.HeaderElement;
import java.nio.charset.UnsupportedCharsetException;
import org.apache.http.util.TextUtils;
import java.util.Locale;
import org.apache.http.message.BasicHeaderValueFormatter;
import org.apache.http.util.CharArrayBuffer;
import org.apache.http.util.Args;
import org.apache.http.NameValuePair;
import java.nio.charset.Charset;
import java.util.Map;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.annotation.Contract;
import java.io.Serializable;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public final class ContentType implements Serializable
{
    private static final long serialVersionUID = -7768694718232371896L;
    public static final ContentType APPLICATION_ATOM_XML;
    public static final ContentType APPLICATION_FORM_URLENCODED;
    public static final ContentType APPLICATION_JSON;
    public static final ContentType APPLICATION_OCTET_STREAM;
    public static final ContentType APPLICATION_SVG_XML;
    public static final ContentType APPLICATION_XHTML_XML;
    public static final ContentType APPLICATION_XML;
    public static final ContentType MULTIPART_FORM_DATA;
    public static final ContentType TEXT_HTML;
    public static final ContentType TEXT_PLAIN;
    public static final ContentType TEXT_XML;
    public static final ContentType WILDCARD;
    private static final Map<String, ContentType> CONTENT_TYPE_MAP;
    public static final ContentType DEFAULT_TEXT;
    public static final ContentType DEFAULT_BINARY;
    private final String mimeType;
    private final Charset charset;
    private final NameValuePair[] params;
    
    ContentType(final String mimeType, final Charset charset) {
        this.mimeType = mimeType;
        this.charset = charset;
        this.params = null;
    }
    
    ContentType(final String mimeType, final Charset charset, final NameValuePair[] params) {
        this.mimeType = mimeType;
        this.charset = charset;
        this.params = params;
    }
    
    public String getMimeType() {
        return this.mimeType;
    }
    
    public Charset getCharset() {
        return this.charset;
    }
    
    public String getParameter(final String name) {
        Args.notEmpty(name, "Parameter name");
        if (this.params == null) {
            return null;
        }
        for (final NameValuePair param : this.params) {
            if (param.getName().equalsIgnoreCase(name)) {
                return param.getValue();
            }
        }
        return null;
    }
    
    @Override
    public String toString() {
        final CharArrayBuffer buf = new CharArrayBuffer(64);
        buf.append(this.mimeType);
        if (this.params != null) {
            buf.append("; ");
            BasicHeaderValueFormatter.INSTANCE.formatParameters(buf, this.params, false);
        }
        else if (this.charset != null) {
            buf.append("; charset=");
            buf.append(this.charset.name());
        }
        return buf.toString();
    }
    
    private static boolean valid(final String s) {
        for (int i = 0; i < s.length(); ++i) {
            final char ch = s.charAt(i);
            if (ch == '\"' || ch == ',' || ch == ';') {
                return false;
            }
        }
        return true;
    }
    
    public static ContentType create(final String mimeType, final Charset charset) {
        final String normalizedMimeType = Args.notBlank(mimeType, "MIME type").toLowerCase(Locale.ROOT);
        Args.check(valid(normalizedMimeType), "MIME type may not contain reserved characters");
        return new ContentType(normalizedMimeType, charset);
    }
    
    public static ContentType create(final String mimeType) {
        return create(mimeType, (Charset)null);
    }
    
    public static ContentType create(final String mimeType, final String charset) throws UnsupportedCharsetException {
        return create(mimeType, TextUtils.isBlank(charset) ? null : Charset.forName(charset));
    }
    
    private static ContentType create(final HeaderElement helem, final boolean strict) {
        return create(helem.getName(), helem.getParameters(), strict);
    }
    
    private static ContentType create(final String mimeType, final NameValuePair[] params, final boolean strict) {
        Charset charset = null;
        final NameValuePair[] arr$ = params;
        final int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            final NameValuePair param = arr$[i$];
            if (param.getName().equalsIgnoreCase("charset")) {
                final String s = param.getValue();
                if (!TextUtils.isBlank(s)) {
                    try {
                        charset = Charset.forName(s);
                    }
                    catch (final UnsupportedCharsetException ex) {
                        if (strict) {
                            throw ex;
                        }
                    }
                    break;
                }
                break;
            }
            else {
                ++i$;
            }
        }
        return new ContentType(mimeType, charset, (NameValuePair[])((params != null && params.length > 0) ? params : null));
    }
    
    public static ContentType create(final String mimeType, final NameValuePair... params) throws UnsupportedCharsetException {
        final String type = Args.notBlank(mimeType, "MIME type").toLowerCase(Locale.ROOT);
        Args.check(valid(type), "MIME type may not contain reserved characters");
        return create(mimeType, params, true);
    }
    
    public static ContentType parse(final String s) throws ParseException, UnsupportedCharsetException {
        Args.notNull(s, "Content type");
        final CharArrayBuffer buf = new CharArrayBuffer(s.length());
        buf.append(s);
        final ParserCursor cursor = new ParserCursor(0, s.length());
        final HeaderElement[] elements = BasicHeaderValueParser.INSTANCE.parseElements(buf, cursor);
        if (elements.length > 0) {
            return create(elements[0], true);
        }
        throw new ParseException("Invalid content type: " + s);
    }
    
    public static ContentType get(final HttpEntity entity) throws ParseException, UnsupportedCharsetException {
        if (entity == null) {
            return null;
        }
        final Header header = entity.getContentType();
        if (header != null) {
            final HeaderElement[] elements = header.getElements();
            if (elements.length > 0) {
                return create(elements[0], true);
            }
        }
        return null;
    }
    
    public static ContentType getLenient(final HttpEntity entity) {
        if (entity == null) {
            return null;
        }
        final Header header = entity.getContentType();
        if (header != null) {
            try {
                final HeaderElement[] elements = header.getElements();
                if (elements.length > 0) {
                    return create(elements[0], false);
                }
            }
            catch (final ParseException ex) {
                return null;
            }
        }
        return null;
    }
    
    public static ContentType getOrDefault(final HttpEntity entity) throws ParseException, UnsupportedCharsetException {
        final ContentType contentType = get(entity);
        return (contentType != null) ? contentType : ContentType.DEFAULT_TEXT;
    }
    
    public static ContentType getLenientOrDefault(final HttpEntity entity) throws ParseException, UnsupportedCharsetException {
        final ContentType contentType = get(entity);
        return (contentType != null) ? contentType : ContentType.DEFAULT_TEXT;
    }
    
    public static ContentType getByMimeType(final String mimeType) {
        if (mimeType == null) {
            return null;
        }
        return ContentType.CONTENT_TYPE_MAP.get(mimeType);
    }
    
    public ContentType withCharset(final Charset charset) {
        return create(this.getMimeType(), charset);
    }
    
    public ContentType withCharset(final String charset) {
        return create(this.getMimeType(), charset);
    }
    
    public ContentType withParameters(final NameValuePair... params) throws UnsupportedCharsetException {
        if (params.length == 0) {
            return this;
        }
        final Map<String, String> paramMap = new LinkedHashMap<String, String>();
        if (this.params != null) {
            for (final NameValuePair param : this.params) {
                paramMap.put(param.getName(), param.getValue());
            }
        }
        for (final NameValuePair param : params) {
            paramMap.put(param.getName(), param.getValue());
        }
        final List<NameValuePair> newParams = new ArrayList<NameValuePair>(paramMap.size() + 1);
        if (this.charset != null && !paramMap.containsKey("charset")) {
            newParams.add(new BasicNameValuePair("charset", this.charset.name()));
        }
        for (final Map.Entry<String, String> entry : paramMap.entrySet()) {
            newParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        return create(this.getMimeType(), newParams.toArray(new NameValuePair[newParams.size()]), true);
    }
    
    static {
        APPLICATION_ATOM_XML = create("application/atom+xml", Consts.ISO_8859_1);
        APPLICATION_FORM_URLENCODED = create("application/x-www-form-urlencoded", Consts.ISO_8859_1);
        APPLICATION_JSON = create("application/json", Consts.UTF_8);
        APPLICATION_OCTET_STREAM = create("application/octet-stream", (Charset)null);
        APPLICATION_SVG_XML = create("application/svg+xml", Consts.ISO_8859_1);
        APPLICATION_XHTML_XML = create("application/xhtml+xml", Consts.ISO_8859_1);
        APPLICATION_XML = create("application/xml", Consts.ISO_8859_1);
        MULTIPART_FORM_DATA = create("multipart/form-data", Consts.ISO_8859_1);
        TEXT_HTML = create("text/html", Consts.ISO_8859_1);
        TEXT_PLAIN = create("text/plain", Consts.ISO_8859_1);
        TEXT_XML = create("text/xml", Consts.ISO_8859_1);
        WILDCARD = create("*/*", (Charset)null);
        final ContentType[] contentTypes = { ContentType.APPLICATION_ATOM_XML, ContentType.APPLICATION_FORM_URLENCODED, ContentType.APPLICATION_JSON, ContentType.APPLICATION_SVG_XML, ContentType.APPLICATION_XHTML_XML, ContentType.APPLICATION_XML, ContentType.MULTIPART_FORM_DATA, ContentType.TEXT_HTML, ContentType.TEXT_PLAIN, ContentType.TEXT_XML };
        final HashMap<String, ContentType> map = new HashMap<String, ContentType>();
        for (final ContentType contentType : contentTypes) {
            map.put(contentType.getMimeType(), contentType);
        }
        CONTENT_TYPE_MAP = Collections.unmodifiableMap((Map<? extends String, ? extends ContentType>)map);
        DEFAULT_TEXT = ContentType.TEXT_PLAIN;
        DEFAULT_BINARY = ContentType.APPLICATION_OCTET_STREAM;
    }
}
