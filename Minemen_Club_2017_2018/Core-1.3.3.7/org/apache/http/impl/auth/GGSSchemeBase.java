// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.impl.auth;

import java.net.InetAddress;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.HttpHost;
import org.apache.http.message.BufferedHeader;
import org.apache.http.util.CharArrayBuffer;
import org.apache.http.auth.InvalidCredentialsException;
import java.net.UnknownHostException;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.util.Args;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.protocol.HttpContext;
import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSCredential;
import org.apache.http.auth.KerberosCredentials;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.GSSException;
import org.apache.http.auth.Credentials;
import org.ietf.jgss.Oid;
import org.ietf.jgss.GSSManager;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;

public abstract class GGSSchemeBase extends AuthSchemeBase
{
    private final Log log;
    private final Base64 base64codec;
    private final boolean stripPort;
    private final boolean useCanonicalHostname;
    private State state;
    private byte[] token;
    
    GGSSchemeBase(final boolean stripPort, final boolean useCanonicalHostname) {
        this.log = LogFactory.getLog(this.getClass());
        this.base64codec = new Base64(0);
        this.stripPort = stripPort;
        this.useCanonicalHostname = useCanonicalHostname;
        this.state = State.UNINITIATED;
    }
    
    GGSSchemeBase(final boolean stripPort) {
        this(stripPort, true);
    }
    
    GGSSchemeBase() {
        this(true, true);
    }
    
    protected GSSManager getManager() {
        return GSSManager.getInstance();
    }
    
    protected byte[] generateGSSToken(final byte[] input, final Oid oid, final String authServer) throws GSSException {
        return this.generateGSSToken(input, oid, authServer, null);
    }
    
    protected byte[] generateGSSToken(final byte[] input, final Oid oid, final String authServer, final Credentials credentials) throws GSSException {
        final GSSManager manager = this.getManager();
        final GSSName serverName = manager.createName("HTTP@" + authServer, GSSName.NT_HOSTBASED_SERVICE);
        GSSCredential gssCredential;
        if (credentials instanceof KerberosCredentials) {
            gssCredential = ((KerberosCredentials)credentials).getGSSCredential();
        }
        else {
            gssCredential = null;
        }
        final GSSContext gssContext = this.createGSSContext(manager, oid, serverName, gssCredential);
        if (input != null) {
            return gssContext.initSecContext(input, 0, input.length);
        }
        return gssContext.initSecContext(new byte[0], 0, 0);
    }
    
    GSSContext createGSSContext(final GSSManager manager, final Oid oid, final GSSName serverName, final GSSCredential gssCredential) throws GSSException {
        final GSSContext gssContext = manager.createContext(serverName.canonicalize(oid), oid, gssCredential, 0);
        gssContext.requestMutualAuth(true);
        return gssContext;
    }
    
    @Deprecated
    protected byte[] generateToken(final byte[] input, final String authServer) throws GSSException {
        return null;
    }
    
    protected byte[] generateToken(final byte[] input, final String authServer, final Credentials credentials) throws GSSException {
        return this.generateToken(input, authServer);
    }
    
    @Override
    public boolean isComplete() {
        return this.state == State.TOKEN_GENERATED || this.state == State.FAILED;
    }
    
    @Deprecated
    @Override
    public Header authenticate(final Credentials credentials, final HttpRequest request) throws AuthenticationException {
        return this.authenticate(credentials, request, null);
    }
    
    @Override
    public Header authenticate(final Credentials credentials, final HttpRequest request, final HttpContext context) throws AuthenticationException {
        Args.notNull(request, "HTTP request");
        switch (this.state) {
            case UNINITIATED: {
                throw new AuthenticationException(this.getSchemeName() + " authentication has not been initiated");
            }
            case FAILED: {
                throw new AuthenticationException(this.getSchemeName() + " authentication has failed");
            }
            case CHALLENGE_RECEIVED: {
                try {
                    final HttpRoute route = (HttpRoute)context.getAttribute("http.route");
                    if (route == null) {
                        throw new AuthenticationException("Connection route is not available");
                    }
                    HttpHost host;
                    if (this.isProxy()) {
                        host = route.getProxyHost();
                        if (host == null) {
                            host = route.getTargetHost();
                        }
                    }
                    else {
                        host = route.getTargetHost();
                    }
                    String hostname = host.getHostName();
                    if (this.useCanonicalHostname) {
                        try {
                            hostname = this.resolveCanonicalHostname(hostname);
                        }
                        catch (final UnknownHostException ex) {}
                    }
                    String authServer;
                    if (this.stripPort) {
                        authServer = hostname;
                    }
                    else {
                        authServer = hostname + ":" + host.getPort();
                    }
                    if (this.log.isDebugEnabled()) {
                        this.log.debug("init " + authServer);
                    }
                    this.token = this.generateToken(this.token, authServer, credentials);
                    this.state = State.TOKEN_GENERATED;
                }
                catch (final GSSException gsse) {
                    this.state = State.FAILED;
                    if (gsse.getMajor() == 9 || gsse.getMajor() == 8) {
                        throw new InvalidCredentialsException(gsse.getMessage(), gsse);
                    }
                    if (gsse.getMajor() == 13) {
                        throw new InvalidCredentialsException(gsse.getMessage(), gsse);
                    }
                    if (gsse.getMajor() == 10 || gsse.getMajor() == 19 || gsse.getMajor() == 20) {
                        throw new AuthenticationException(gsse.getMessage(), gsse);
                    }
                    throw new AuthenticationException(gsse.getMessage());
                }
            }
            case TOKEN_GENERATED: {
                final String tokenstr = new String(this.base64codec.encode(this.token));
                if (this.log.isDebugEnabled()) {
                    this.log.debug("Sending response '" + tokenstr + "' back to the auth server");
                }
                final CharArrayBuffer buffer = new CharArrayBuffer(32);
                if (this.isProxy()) {
                    buffer.append("Proxy-Authorization");
                }
                else {
                    buffer.append("Authorization");
                }
                buffer.append(": Negotiate ");
                buffer.append(tokenstr);
                return new BufferedHeader(buffer);
            }
            default: {
                throw new IllegalStateException("Illegal state: " + this.state);
            }
        }
    }
    
    @Override
    protected void parseChallenge(final CharArrayBuffer buffer, final int beginIndex, final int endIndex) throws MalformedChallengeException {
        final String challenge = buffer.substringTrimmed(beginIndex, endIndex);
        if (this.log.isDebugEnabled()) {
            this.log.debug("Received challenge '" + challenge + "' from the auth server");
        }
        if (this.state == State.UNINITIATED) {
            this.token = Base64.decodeBase64(challenge.getBytes());
            this.state = State.CHALLENGE_RECEIVED;
        }
        else {
            this.log.debug("Authentication already attempted");
            this.state = State.FAILED;
        }
    }
    
    private String resolveCanonicalHostname(final String host) throws UnknownHostException {
        final InetAddress in = InetAddress.getByName(host);
        final String canonicalServer = in.getCanonicalHostName();
        if (in.getHostAddress().contentEquals(canonicalServer)) {
            return host;
        }
        return canonicalServer;
    }
    
    enum State
    {
        UNINITIATED, 
        CHALLENGE_RECEIVED, 
        TOKEN_GENERATED, 
        FAILED;
    }
}
