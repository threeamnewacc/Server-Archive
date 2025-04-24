// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.client.entity;

import org.apache.http.Header;
import org.apache.http.util.Args;
import java.io.OutputStream;
import java.io.IOException;
import org.apache.http.HttpEntity;
import java.io.InputStream;
import org.apache.http.entity.HttpEntityWrapper;

public class DecompressingEntity extends HttpEntityWrapper
{
    private static final int BUFFER_SIZE = 2048;
    private final InputStreamFactory inputStreamFactory;
    private InputStream content;
    
    public DecompressingEntity(final HttpEntity wrapped, final InputStreamFactory inputStreamFactory) {
        super(wrapped);
        this.inputStreamFactory = inputStreamFactory;
    }
    
    private InputStream getDecompressingStream() throws IOException {
        final InputStream in = this.wrappedEntity.getContent();
        return new LazyDecompressingInputStream(in, this.inputStreamFactory);
    }
    
    @Override
    public InputStream getContent() throws IOException {
        if (this.wrappedEntity.isStreaming()) {
            if (this.content == null) {
                this.content = this.getDecompressingStream();
            }
            return this.content;
        }
        return this.getDecompressingStream();
    }
    
    @Override
    public void writeTo(final OutputStream outstream) throws IOException {
        Args.notNull(outstream, "Output stream");
        final InputStream instream = this.getContent();
        try {
            final byte[] buffer = new byte[2048];
            int l;
            while ((l = instream.read(buffer)) != -1) {
                outstream.write(buffer, 0, l);
            }
        }
        finally {
            instream.close();
        }
    }
    
    @Override
    public Header getContentEncoding() {
        return null;
    }
    
    @Override
    public long getContentLength() {
        return -1L;
    }
}
