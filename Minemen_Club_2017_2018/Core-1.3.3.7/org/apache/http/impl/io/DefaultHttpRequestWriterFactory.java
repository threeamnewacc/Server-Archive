// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.impl.io;

import org.apache.http.io.HttpMessageWriter;
import org.apache.http.io.SessionOutputBuffer;
import org.apache.http.message.BasicLineFormatter;
import org.apache.http.message.LineFormatter;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.annotation.Contract;
import org.apache.http.HttpRequest;
import org.apache.http.io.HttpMessageWriterFactory;

@Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public class DefaultHttpRequestWriterFactory implements HttpMessageWriterFactory<HttpRequest>
{
    public static final DefaultHttpRequestWriterFactory INSTANCE;
    private final LineFormatter lineFormatter;
    
    public DefaultHttpRequestWriterFactory(final LineFormatter lineFormatter) {
        this.lineFormatter = ((lineFormatter != null) ? lineFormatter : BasicLineFormatter.INSTANCE);
    }
    
    public DefaultHttpRequestWriterFactory() {
        this(null);
    }
    
    @Override
    public HttpMessageWriter<HttpRequest> create(final SessionOutputBuffer buffer) {
        return new DefaultHttpRequestWriter(buffer, this.lineFormatter);
    }
    
    static {
        INSTANCE = new DefaultHttpRequestWriterFactory();
    }
}
