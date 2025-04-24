// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.impl.client;

import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.auth.NTCredentials;
import org.apache.http.util.Args;
import org.apache.http.HttpHost;
import java.net.URL;
import java.net.InetAddress;
import java.net.PasswordAuthentication;
import java.net.Authenticator;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.AuthScope;
import java.util.Map;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.annotation.Contract;
import org.apache.http.client.CredentialsProvider;

@Contract(threading = ThreadingBehavior.SAFE)
public class SystemDefaultCredentialsProvider implements CredentialsProvider
{
    private static final Map<String, String> SCHEME_MAP;
    private final BasicCredentialsProvider internal;
    
    private static String translateScheme(final String key) {
        if (key == null) {
            return null;
        }
        final String s = SystemDefaultCredentialsProvider.SCHEME_MAP.get(key);
        return (s != null) ? s : key;
    }
    
    public SystemDefaultCredentialsProvider() {
        this.internal = new BasicCredentialsProvider();
    }
    
    @Override
    public void setCredentials(final AuthScope authscope, final Credentials credentials) {
        this.internal.setCredentials(authscope, credentials);
    }
    
    private static PasswordAuthentication getSystemCreds(final AuthScope authscope, final Authenticator.RequestorType requestorType) {
        final String hostname = authscope.getHost();
        final int port = authscope.getPort();
        final HttpHost origin = authscope.getOrigin();
        final String protocol = (origin != null) ? origin.getSchemeName() : ((port == 443) ? "https" : "http");
        return Authenticator.requestPasswordAuthentication(hostname, null, port, protocol, null, translateScheme(authscope.getScheme()), null, requestorType);
    }
    
    @Override
    public Credentials getCredentials(final AuthScope authscope) {
        Args.notNull(authscope, "Auth scope");
        final Credentials localcreds = this.internal.getCredentials(authscope);
        if (localcreds != null) {
            return localcreds;
        }
        final String host = authscope.getHost();
        if (host != null) {
            PasswordAuthentication systemcreds = getSystemCreds(authscope, Authenticator.RequestorType.SERVER);
            if (systemcreds == null) {
                systemcreds = getSystemCreds(authscope, Authenticator.RequestorType.PROXY);
            }
            if (systemcreds == null) {
                final String proxyHost = System.getProperty("http.proxyHost");
                if (proxyHost != null) {
                    final String proxyPort = System.getProperty("http.proxyPort");
                    if (proxyPort != null) {
                        try {
                            final AuthScope systemScope = new AuthScope(proxyHost, Integer.parseInt(proxyPort));
                            if (authscope.match(systemScope) >= 0) {
                                final String proxyUser = System.getProperty("http.proxyUser");
                                if (proxyUser != null) {
                                    final String proxyPassword = System.getProperty("http.proxyPassword");
                                    systemcreds = new PasswordAuthentication(proxyUser, (proxyPassword != null) ? proxyPassword.toCharArray() : new char[0]);
                                }
                            }
                        }
                        catch (final NumberFormatException ex) {}
                    }
                }
            }
            if (systemcreds != null) {
                final String domain = System.getProperty("http.auth.ntlm.domain");
                if (domain != null) {
                    return new NTCredentials(systemcreds.getUserName(), new String(systemcreds.getPassword()), null, domain);
                }
                if ("NTLM".equalsIgnoreCase(authscope.getScheme())) {
                    return new NTCredentials(systemcreds.getUserName(), new String(systemcreds.getPassword()), null, null);
                }
                return new UsernamePasswordCredentials(systemcreds.getUserName(), new String(systemcreds.getPassword()));
            }
        }
        return null;
    }
    
    @Override
    public void clear() {
        this.internal.clear();
    }
    
    static {
        (SCHEME_MAP = new ConcurrentHashMap<String, String>()).put("Basic".toUpperCase(Locale.ROOT), "Basic");
        SystemDefaultCredentialsProvider.SCHEME_MAP.put("Digest".toUpperCase(Locale.ROOT), "Digest");
        SystemDefaultCredentialsProvider.SCHEME_MAP.put("NTLM".toUpperCase(Locale.ROOT), "NTLM");
        SystemDefaultCredentialsProvider.SCHEME_MAP.put("Negotiate".toUpperCase(Locale.ROOT), "SPNEGO");
        SystemDefaultCredentialsProvider.SCHEME_MAP.put("Kerberos".toUpperCase(Locale.ROOT), "Kerberos");
    }
}
