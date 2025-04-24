// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.client.entity;

import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.io.InputStream;
import org.apache.http.HttpEntity;

public class GzipDecompressingEntity extends DecompressingEntity
{
    public GzipDecompressingEntity(final HttpEntity entity) {
        super(entity, new InputStreamFactory() {
            @Override
            public InputStream create(final InputStream instream) throws IOException {
                return new GZIPInputStream(instream);
            }
        });
    }
}
