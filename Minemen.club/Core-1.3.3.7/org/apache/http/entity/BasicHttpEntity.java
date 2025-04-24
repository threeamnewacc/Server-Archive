// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.entity;

import org.apache.http.impl.io.EmptyInputStream;
import java.io.IOException;
import org.apache.http.util.Args;
import java.io.OutputStream;
import org.apache.http.util.Asserts;
import java.io.InputStream;

public class BasicHttpEntity extends AbstractHttpEntity
{
    private InputStream content;
    private long length;
    
    public BasicHttpEntity() {
        this.length = -1L;
    }
    
    @Override
    public long getContentLength() {
        return this.length;
    }
    
    @Override
    public InputStream getContent() throws IllegalStateException {
        Asserts.check(this.content != null, "Content has not been provided");
        return this.content;
    }
    
    @Override
    public boolean isRepeatable() {
        return false;
    }
    
    public void setContentLength(final long len) {
        this.length = len;
    }
    
    public void setContent(final InputStream instream) {
        this.content = instream;
    }
    
    @Override
    public void writeTo(final OutputStream outstream) throws IOException {
        Args.notNull(outstream, "Output stream");
        final InputStream instream = this.getContent();
        try {
            final byte[] tmp = new byte[4096];
            int l;
            while ((l = instream.read(tmp)) != -1) {
                outstream.write(tmp, 0, l);
            }
        }
        finally {
            instream.close();
        }
    }
    
    @Override
    public boolean isStreaming() {
        return this.content != null && this.content != EmptyInputStream.INSTANCE;
    }
}
