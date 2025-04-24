// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.client.methods;

import org.apache.http.message.BasicRequestLine;
import org.apache.http.RequestLine;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.client.config.RequestConfig;
import java.net.URI;
import org.apache.http.ProtocolVersion;

public abstract class HttpRequestBase extends AbstractExecutionAwareRequest implements HttpUriRequest, Configurable
{
    private ProtocolVersion version;
    private URI uri;
    private RequestConfig config;
    
    @Override
    public abstract String getMethod();
    
    public void setProtocolVersion(final ProtocolVersion version) {
        this.version = version;
    }
    
    @Override
    public ProtocolVersion getProtocolVersion() {
        return (this.version != null) ? this.version : HttpProtocolParams.getVersion(this.getParams());
    }
    
    @Override
    public URI getURI() {
        return this.uri;
    }
    
    @Override
    public RequestLine getRequestLine() {
        final String method = this.getMethod();
        final ProtocolVersion ver = this.getProtocolVersion();
        final URI uriCopy = this.getURI();
        String uritext = null;
        if (uriCopy != null) {
            uritext = uriCopy.toASCIIString();
        }
        if (uritext == null || uritext.isEmpty()) {
            uritext = "/";
        }
        return new BasicRequestLine(method, uritext, ver);
    }
    
    @Override
    public RequestConfig getConfig() {
        return this.config;
    }
    
    public void setConfig(final RequestConfig config) {
        this.config = config;
    }
    
    public void setURI(final URI uri) {
        this.uri = uri;
    }
    
    public void started() {
    }
    
    public void releaseConnection() {
        this.reset();
    }
    
    @Override
    public String toString() {
        return this.getMethod() + " " + this.getURI() + " " + this.getProtocolVersion();
    }
}
