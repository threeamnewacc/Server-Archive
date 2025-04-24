// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.impl.client;

import java.util.List;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.Header;
import java.util.Map;
import org.apache.http.util.Args;
import org.apache.http.protocol.HttpContext;
import org.apache.http.HttpResponse;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.annotation.Contract;

@Deprecated
@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class DefaultTargetAuthenticationHandler extends AbstractAuthenticationHandler
{
    @Override
    public boolean isAuthenticationRequested(final HttpResponse response, final HttpContext context) {
        Args.notNull(response, "HTTP response");
        final int status = response.getStatusLine().getStatusCode();
        return status == 401;
    }
    
    @Override
    public Map<String, Header> getChallenges(final HttpResponse response, final HttpContext context) throws MalformedChallengeException {
        Args.notNull(response, "HTTP response");
        final Header[] headers = response.getHeaders("WWW-Authenticate");
        return this.parseChallenges(headers);
    }
    
    @Override
    protected List<String> getAuthPreferences(final HttpResponse response, final HttpContext context) {
        final List<String> authpref = (List<String>)response.getParams().getParameter("http.auth.target-scheme-pref");
        if (authpref != null) {
            return authpref;
        }
        return super.getAuthPreferences(response, context);
    }
}
