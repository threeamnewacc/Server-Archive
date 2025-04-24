// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.impl.client;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import org.apache.http.util.Args;
import org.apache.http.auth.AuthScheme;
import org.apache.http.conn.UnsupportedSchemeException;
import org.apache.http.impl.conn.DefaultSchemePortResolver;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.logging.LogFactory;
import org.apache.http.conn.SchemePortResolver;
import org.apache.http.HttpHost;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.annotation.Contract;
import org.apache.http.client.AuthCache;

@Contract(threading = ThreadingBehavior.SAFE)
public class BasicAuthCache implements AuthCache
{
    private final Log log;
    private final Map<HttpHost, byte[]> map;
    private final SchemePortResolver schemePortResolver;
    
    public BasicAuthCache(final SchemePortResolver schemePortResolver) {
        this.log = LogFactory.getLog(this.getClass());
        this.map = new ConcurrentHashMap<HttpHost, byte[]>();
        this.schemePortResolver = ((schemePortResolver != null) ? schemePortResolver : DefaultSchemePortResolver.INSTANCE);
    }
    
    public BasicAuthCache() {
        this(null);
    }
    
    protected HttpHost getKey(final HttpHost host) {
        if (host.getPort() <= 0) {
            int port;
            try {
                port = this.schemePortResolver.resolve(host);
            }
            catch (final UnsupportedSchemeException ignore) {
                return host;
            }
            return new HttpHost(host.getHostName(), port, host.getSchemeName());
        }
        return host;
    }
    
    @Override
    public void put(final HttpHost host, final AuthScheme authScheme) {
        Args.notNull(host, "HTTP host");
        if (authScheme == null) {
            return;
        }
        if (authScheme instanceof Serializable) {
            try {
                final ByteArrayOutputStream buf = new ByteArrayOutputStream();
                final ObjectOutputStream out = new ObjectOutputStream(buf);
                out.writeObject(authScheme);
                out.close();
                this.map.put(this.getKey(host), buf.toByteArray());
            }
            catch (final IOException ex) {
                if (this.log.isWarnEnabled()) {
                    this.log.warn("Unexpected I/O error while serializing auth scheme", ex);
                }
            }
        }
        else if (this.log.isDebugEnabled()) {
            this.log.debug("Auth scheme " + authScheme.getClass() + " is not serializable");
        }
    }
    
    @Override
    public AuthScheme get(final HttpHost host) {
        Args.notNull(host, "HTTP host");
        final byte[] bytes = this.map.get(this.getKey(host));
        if (bytes != null) {
            try {
                final ByteArrayInputStream buf = new ByteArrayInputStream(bytes);
                final ObjectInputStream in = new ObjectInputStream(buf);
                final AuthScheme authScheme = (AuthScheme)in.readObject();
                in.close();
                return authScheme;
            }
            catch (final IOException ex) {
                if (this.log.isWarnEnabled()) {
                    this.log.warn("Unexpected I/O error while de-serializing auth scheme", ex);
                }
                return null;
            }
            catch (final ClassNotFoundException ex2) {
                if (this.log.isWarnEnabled()) {
                    this.log.warn("Unexpected error while de-serializing auth scheme", ex2);
                }
                return null;
            }
        }
        return null;
    }
    
    @Override
    public void remove(final HttpHost host) {
        Args.notNull(host, "HTTP host");
        this.map.remove(this.getKey(host));
    }
    
    @Override
    public void clear() {
        this.map.clear();
    }
    
    @Override
    public String toString() {
        return this.map.toString();
    }
}
