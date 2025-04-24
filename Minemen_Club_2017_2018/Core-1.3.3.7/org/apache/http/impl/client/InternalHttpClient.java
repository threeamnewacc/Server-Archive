// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.impl.client;

import org.apache.http.conn.scheme.SchemeRegistry;
import java.util.concurrent.TimeUnit;
import org.apache.http.conn.ManagedClientConnection;
import org.apache.http.conn.ClientConnectionRequest;
import org.apache.http.conn.ClientConnectionManager;
import java.util.Iterator;
import java.io.IOException;
import org.apache.http.params.HttpParams;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.params.HttpClientParamConfig;
import org.apache.http.params.HttpParamsNames;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.apache.http.client.methods.HttpExecutionAware;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.auth.AuthState;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.HttpException;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.protocol.HttpContext;
import org.apache.http.HttpRequest;
import org.apache.http.HttpHost;
import org.apache.http.util.Args;
import org.apache.commons.logging.LogFactory;
import java.io.Closeable;
import java.util.List;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.CookieStore;
import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.config.Lookup;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.execchain.ClientExecChain;
import org.apache.commons.logging.Log;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.annotation.Contract;
import org.apache.http.client.methods.Configurable;

@Contract(threading = ThreadingBehavior.SAFE_CONDITIONAL)
class InternalHttpClient extends CloseableHttpClient implements Configurable
{
    private final Log log;
    private final ClientExecChain execChain;
    private final HttpClientConnectionManager connManager;
    private final HttpRoutePlanner routePlanner;
    private final Lookup<CookieSpecProvider> cookieSpecRegistry;
    private final Lookup<AuthSchemeProvider> authSchemeRegistry;
    private final CookieStore cookieStore;
    private final CredentialsProvider credentialsProvider;
    private final RequestConfig defaultConfig;
    private final List<Closeable> closeables;
    
    public InternalHttpClient(final ClientExecChain execChain, final HttpClientConnectionManager connManager, final HttpRoutePlanner routePlanner, final Lookup<CookieSpecProvider> cookieSpecRegistry, final Lookup<AuthSchemeProvider> authSchemeRegistry, final CookieStore cookieStore, final CredentialsProvider credentialsProvider, final RequestConfig defaultConfig, final List<Closeable> closeables) {
        this.log = LogFactory.getLog(this.getClass());
        Args.notNull(execChain, "HTTP client exec chain");
        Args.notNull(connManager, "HTTP connection manager");
        Args.notNull(routePlanner, "HTTP route planner");
        this.execChain = execChain;
        this.connManager = connManager;
        this.routePlanner = routePlanner;
        this.cookieSpecRegistry = cookieSpecRegistry;
        this.authSchemeRegistry = authSchemeRegistry;
        this.cookieStore = cookieStore;
        this.credentialsProvider = credentialsProvider;
        this.defaultConfig = defaultConfig;
        this.closeables = closeables;
    }
    
    private HttpRoute determineRoute(final HttpHost target, final HttpRequest request, final HttpContext context) throws HttpException {
        HttpHost host = target;
        if (host == null) {
            host = (HttpHost)request.getParams().getParameter("http.default-host");
        }
        return this.routePlanner.determineRoute(host, request, context);
    }
    
    private void setupContext(final HttpClientContext context) {
        if (context.getAttribute("http.auth.target-scope") == null) {
            context.setAttribute("http.auth.target-scope", new AuthState());
        }
        if (context.getAttribute("http.auth.proxy-scope") == null) {
            context.setAttribute("http.auth.proxy-scope", new AuthState());
        }
        if (context.getAttribute("http.authscheme-registry") == null) {
            context.setAttribute("http.authscheme-registry", this.authSchemeRegistry);
        }
        if (context.getAttribute("http.cookiespec-registry") == null) {
            context.setAttribute("http.cookiespec-registry", this.cookieSpecRegistry);
        }
        if (context.getAttribute("http.cookie-store") == null) {
            context.setAttribute("http.cookie-store", this.cookieStore);
        }
        if (context.getAttribute("http.auth.credentials-provider") == null) {
            context.setAttribute("http.auth.credentials-provider", this.credentialsProvider);
        }
        if (context.getAttribute("http.request-config") == null) {
            context.setAttribute("http.request-config", this.defaultConfig);
        }
    }
    
    @Override
    protected CloseableHttpResponse doExecute(final HttpHost target, final HttpRequest request, final HttpContext context) throws IOException, ClientProtocolException {
        Args.notNull(request, "HTTP request");
        HttpExecutionAware execAware = null;
        if (request instanceof HttpExecutionAware) {
            execAware = (HttpExecutionAware)request;
        }
        try {
            final HttpRequestWrapper wrapper = HttpRequestWrapper.wrap(request, target);
            final HttpClientContext localcontext = HttpClientContext.adapt((context != null) ? context : new BasicHttpContext());
            RequestConfig config = null;
            if (request instanceof Configurable) {
                config = ((Configurable)request).getConfig();
            }
            if (config == null) {
                final HttpParams params = request.getParams();
                if (params instanceof HttpParamsNames) {
                    if (!((HttpParamsNames)params).getNames().isEmpty()) {
                        config = HttpClientParamConfig.getRequestConfig(params, this.defaultConfig);
                    }
                }
                else {
                    config = HttpClientParamConfig.getRequestConfig(params, this.defaultConfig);
                }
            }
            if (config != null) {
                localcontext.setRequestConfig(config);
            }
            this.setupContext(localcontext);
            final HttpRoute route = this.determineRoute(target, wrapper, localcontext);
            return this.execChain.execute(route, wrapper, localcontext, execAware);
        }
        catch (final HttpException httpException) {
            throw new ClientProtocolException(httpException);
        }
    }
    
    @Override
    public RequestConfig getConfig() {
        return this.defaultConfig;
    }
    
    @Override
    public void close() {
        if (this.closeables != null) {
            for (final Closeable closeable : this.closeables) {
                try {
                    closeable.close();
                }
                catch (final IOException ex) {
                    this.log.error(ex.getMessage(), ex);
                }
            }
        }
    }
    
    @Override
    public HttpParams getParams() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public ClientConnectionManager getConnectionManager() {
        return new ClientConnectionManager() {
            @Override
            public void shutdown() {
                InternalHttpClient.this.connManager.shutdown();
            }
            
            @Override
            public ClientConnectionRequest requestConnection(final HttpRoute route, final Object state) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public void releaseConnection(final ManagedClientConnection conn, final long validDuration, final TimeUnit timeUnit) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public SchemeRegistry getSchemeRegistry() {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public void closeIdleConnections(final long idletime, final TimeUnit tunit) {
                InternalHttpClient.this.connManager.closeIdleConnections(idletime, tunit);
            }
            
            @Override
            public void closeExpiredConnections() {
                InternalHttpClient.this.connManager.closeExpiredConnections();
            }
        };
    }
}
