// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.impl.bootstrap;

import java.io.IOException;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.ExceptionLogger;
import org.apache.http.HttpServerConnection;
import org.apache.http.protocol.HttpService;

class Worker implements Runnable
{
    private final HttpService httpservice;
    private final HttpServerConnection conn;
    private final ExceptionLogger exceptionLogger;
    
    Worker(final HttpService httpservice, final HttpServerConnection conn, final ExceptionLogger exceptionLogger) {
        this.httpservice = httpservice;
        this.conn = conn;
        this.exceptionLogger = exceptionLogger;
    }
    
    public HttpServerConnection getConnection() {
        return this.conn;
    }
    
    @Override
    public void run() {
        try {
            final BasicHttpContext localContext = new BasicHttpContext();
            final HttpCoreContext context = HttpCoreContext.adapt(localContext);
            while (!Thread.interrupted() && this.conn.isOpen()) {
                this.httpservice.handleRequest(this.conn, context);
                localContext.clear();
            }
            this.conn.close();
        }
        catch (final Exception ex) {
            this.exceptionLogger.log(ex);
            try {
                this.conn.shutdown();
            }
            catch (final IOException ex2) {
                this.exceptionLogger.log(ex2);
            }
        }
        finally {
            try {
                this.conn.shutdown();
            }
            catch (final IOException ex3) {
                this.exceptionLogger.log(ex3);
            }
        }
    }
}
