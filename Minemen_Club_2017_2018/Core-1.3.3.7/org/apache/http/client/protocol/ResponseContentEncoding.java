// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.client.protocol;

import org.apache.http.client.entity.DeflateInputStream;
import java.util.zip.GZIPInputStream;
import java.io.InputStream;
import java.io.IOException;
import org.apache.http.HeaderElement;
import org.apache.http.Header;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.HttpException;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.DecompressingEntity;
import java.util.Locale;
import org.apache.http.protocol.HttpContext;
import org.apache.http.HttpResponse;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.Lookup;
import org.apache.http.client.entity.InputStreamFactory;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.annotation.Contract;
import org.apache.http.HttpResponseInterceptor;

@Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public class ResponseContentEncoding implements HttpResponseInterceptor
{
    public static final String UNCOMPRESSED = "http.client.response.uncompressed";
    private static final InputStreamFactory GZIP;
    private static final InputStreamFactory DEFLATE;
    private final Lookup<InputStreamFactory> decoderRegistry;
    private final boolean ignoreUnknown;
    
    public ResponseContentEncoding(final Lookup<InputStreamFactory> decoderRegistry, final boolean ignoreUnknown) {
        this.decoderRegistry = ((decoderRegistry != null) ? decoderRegistry : RegistryBuilder.create().register("gzip", ResponseContentEncoding.GZIP).register("x-gzip", ResponseContentEncoding.GZIP).register("deflate", ResponseContentEncoding.DEFLATE).build());
        this.ignoreUnknown = ignoreUnknown;
    }
    
    public ResponseContentEncoding(final boolean ignoreUnknown) {
        this(null, ignoreUnknown);
    }
    
    public ResponseContentEncoding(final Lookup<InputStreamFactory> decoderRegistry) {
        this(decoderRegistry, true);
    }
    
    public ResponseContentEncoding() {
        this(null);
    }
    
    @Override
    public void process(final HttpResponse response, final HttpContext context) throws HttpException, IOException {
        final HttpEntity entity = response.getEntity();
        final HttpClientContext clientContext = HttpClientContext.adapt(context);
        final RequestConfig requestConfig = clientContext.getRequestConfig();
        if (requestConfig.isContentCompressionEnabled() && entity != null && entity.getContentLength() != 0L) {
            final Header ceheader = entity.getContentEncoding();
            if (ceheader != null) {
                final HeaderElement[] arr$;
                final HeaderElement[] codecs = arr$ = ceheader.getElements();
                for (final HeaderElement codec : arr$) {
                    final String codecname = codec.getName().toLowerCase(Locale.ROOT);
                    final InputStreamFactory decoderFactory = this.decoderRegistry.lookup(codecname);
                    if (decoderFactory != null) {
                        response.setEntity(new DecompressingEntity(response.getEntity(), decoderFactory));
                        response.removeHeaders("Content-Length");
                        response.removeHeaders("Content-Encoding");
                        response.removeHeaders("Content-MD5");
                    }
                    else if (!"identity".equals(codecname) && !this.ignoreUnknown) {
                        throw new HttpException("Unsupported Content-Encoding: " + codec.getName());
                    }
                }
            }
        }
    }
    
    static {
        GZIP = new InputStreamFactory() {
            @Override
            public InputStream create(final InputStream instream) throws IOException {
                return new GZIPInputStream(instream);
            }
        };
        DEFLATE = new InputStreamFactory() {
            @Override
            public InputStream create(final InputStream instream) throws IOException {
                return new DeflateInputStream(instream);
            }
        };
    }
}
