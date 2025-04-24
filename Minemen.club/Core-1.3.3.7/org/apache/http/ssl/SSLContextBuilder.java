// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.ssl;

import javax.net.ssl.SSLEngine;
import java.security.PrivateKey;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.security.Principal;
import java.security.cert.X509Certificate;
import java.security.KeyManagementException;
import java.util.Collection;
import javax.net.ssl.SSLContext;
import java.security.UnrecoverableKeyException;
import javax.net.ssl.X509ExtendedKeyManager;
import javax.net.ssl.KeyManagerFactory;
import java.net.URL;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.io.InputStream;
import java.io.FileInputStream;
import org.apache.http.util.Args;
import java.io.File;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.TrustManagerFactory;
import java.security.KeyStore;
import java.util.LinkedHashSet;
import java.security.SecureRandom;
import javax.net.ssl.TrustManager;
import javax.net.ssl.KeyManager;
import java.util.Set;

public class SSLContextBuilder
{
    static final String TLS = "TLS";
    private String protocol;
    private final Set<KeyManager> keymanagers;
    private final Set<TrustManager> trustmanagers;
    private SecureRandom secureRandom;
    
    public static SSLContextBuilder create() {
        return new SSLContextBuilder();
    }
    
    public SSLContextBuilder() {
        this.keymanagers = new LinkedHashSet<KeyManager>();
        this.trustmanagers = new LinkedHashSet<TrustManager>();
    }
    
    public SSLContextBuilder useProtocol(final String protocol) {
        this.protocol = protocol;
        return this;
    }
    
    public SSLContextBuilder setSecureRandom(final SecureRandom secureRandom) {
        this.secureRandom = secureRandom;
        return this;
    }
    
    public SSLContextBuilder loadTrustMaterial(final KeyStore truststore, final TrustStrategy trustStrategy) throws NoSuchAlgorithmException, KeyStoreException {
        final TrustManagerFactory tmfactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmfactory.init(truststore);
        final TrustManager[] tms = tmfactory.getTrustManagers();
        if (tms != null) {
            if (trustStrategy != null) {
                for (int i = 0; i < tms.length; ++i) {
                    final TrustManager tm = tms[i];
                    if (tm instanceof X509TrustManager) {
                        tms[i] = new TrustManagerDelegate((X509TrustManager)tm, trustStrategy);
                    }
                }
            }
            for (final TrustManager tm2 : tms) {
                this.trustmanagers.add(tm2);
            }
        }
        return this;
    }
    
    public SSLContextBuilder loadTrustMaterial(final TrustStrategy trustStrategy) throws NoSuchAlgorithmException, KeyStoreException {
        return this.loadTrustMaterial(null, trustStrategy);
    }
    
    public SSLContextBuilder loadTrustMaterial(final File file, final char[] storePassword, final TrustStrategy trustStrategy) throws NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException {
        Args.notNull(file, "Truststore file");
        final KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        final FileInputStream instream = new FileInputStream(file);
        try {
            trustStore.load(instream, storePassword);
        }
        finally {
            instream.close();
        }
        return this.loadTrustMaterial(trustStore, trustStrategy);
    }
    
    public SSLContextBuilder loadTrustMaterial(final File file, final char[] storePassword) throws NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException {
        return this.loadTrustMaterial(file, storePassword, null);
    }
    
    public SSLContextBuilder loadTrustMaterial(final File file) throws NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException {
        return this.loadTrustMaterial(file, null);
    }
    
    public SSLContextBuilder loadTrustMaterial(final URL url, final char[] storePassword, final TrustStrategy trustStrategy) throws NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException {
        Args.notNull(url, "Truststore URL");
        final KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        final InputStream instream = url.openStream();
        try {
            trustStore.load(instream, storePassword);
        }
        finally {
            instream.close();
        }
        return this.loadTrustMaterial(trustStore, trustStrategy);
    }
    
    public SSLContextBuilder loadTrustMaterial(final URL url, final char[] storePassword) throws NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException {
        return this.loadTrustMaterial(url, storePassword, null);
    }
    
    public SSLContextBuilder loadKeyMaterial(final KeyStore keystore, final char[] keyPassword, final PrivateKeyStrategy aliasStrategy) throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException {
        final KeyManagerFactory kmfactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmfactory.init(keystore, keyPassword);
        final KeyManager[] kms = kmfactory.getKeyManagers();
        if (kms != null) {
            if (aliasStrategy != null) {
                for (int i = 0; i < kms.length; ++i) {
                    final KeyManager km = kms[i];
                    if (km instanceof X509ExtendedKeyManager) {
                        kms[i] = new KeyManagerDelegate((X509ExtendedKeyManager)km, aliasStrategy);
                    }
                }
            }
            for (final KeyManager km2 : kms) {
                this.keymanagers.add(km2);
            }
        }
        return this;
    }
    
    public SSLContextBuilder loadKeyMaterial(final KeyStore keystore, final char[] keyPassword) throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException {
        return this.loadKeyMaterial(keystore, keyPassword, null);
    }
    
    public SSLContextBuilder loadKeyMaterial(final File file, final char[] storePassword, final char[] keyPassword, final PrivateKeyStrategy aliasStrategy) throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException, CertificateException, IOException {
        Args.notNull(file, "Keystore file");
        final KeyStore identityStore = KeyStore.getInstance(KeyStore.getDefaultType());
        final FileInputStream instream = new FileInputStream(file);
        try {
            identityStore.load(instream, storePassword);
        }
        finally {
            instream.close();
        }
        return this.loadKeyMaterial(identityStore, keyPassword, aliasStrategy);
    }
    
    public SSLContextBuilder loadKeyMaterial(final File file, final char[] storePassword, final char[] keyPassword) throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException, CertificateException, IOException {
        return this.loadKeyMaterial(file, storePassword, keyPassword, null);
    }
    
    public SSLContextBuilder loadKeyMaterial(final URL url, final char[] storePassword, final char[] keyPassword, final PrivateKeyStrategy aliasStrategy) throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException, CertificateException, IOException {
        Args.notNull(url, "Keystore URL");
        final KeyStore identityStore = KeyStore.getInstance(KeyStore.getDefaultType());
        final InputStream instream = url.openStream();
        try {
            identityStore.load(instream, storePassword);
        }
        finally {
            instream.close();
        }
        return this.loadKeyMaterial(identityStore, keyPassword, aliasStrategy);
    }
    
    public SSLContextBuilder loadKeyMaterial(final URL url, final char[] storePassword, final char[] keyPassword) throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException, CertificateException, IOException {
        return this.loadKeyMaterial(url, storePassword, keyPassword, null);
    }
    
    protected void initSSLContext(final SSLContext sslcontext, final Collection<KeyManager> keyManagers, final Collection<TrustManager> trustManagers, final SecureRandom secureRandom) throws KeyManagementException {
        sslcontext.init((KeyManager[])(keyManagers.isEmpty() ? null : ((KeyManager[])keyManagers.toArray(new KeyManager[keyManagers.size()]))), (TrustManager[])(trustManagers.isEmpty() ? null : ((TrustManager[])trustManagers.toArray(new TrustManager[trustManagers.size()]))), secureRandom);
    }
    
    public SSLContext build() throws NoSuchAlgorithmException, KeyManagementException {
        final SSLContext sslcontext = SSLContext.getInstance((this.protocol != null) ? this.protocol : "TLS");
        this.initSSLContext(sslcontext, this.keymanagers, this.trustmanagers, this.secureRandom);
        return sslcontext;
    }
    
    static class TrustManagerDelegate implements X509TrustManager
    {
        private final X509TrustManager trustManager;
        private final TrustStrategy trustStrategy;
        
        TrustManagerDelegate(final X509TrustManager trustManager, final TrustStrategy trustStrategy) {
            this.trustManager = trustManager;
            this.trustStrategy = trustStrategy;
        }
        
        @Override
        public void checkClientTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
            this.trustManager.checkClientTrusted(chain, authType);
        }
        
        @Override
        public void checkServerTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
            if (!this.trustStrategy.isTrusted(chain, authType)) {
                this.trustManager.checkServerTrusted(chain, authType);
            }
        }
        
        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return this.trustManager.getAcceptedIssuers();
        }
    }
    
    static class KeyManagerDelegate extends X509ExtendedKeyManager
    {
        private final X509ExtendedKeyManager keyManager;
        private final PrivateKeyStrategy aliasStrategy;
        
        KeyManagerDelegate(final X509ExtendedKeyManager keyManager, final PrivateKeyStrategy aliasStrategy) {
            this.keyManager = keyManager;
            this.aliasStrategy = aliasStrategy;
        }
        
        @Override
        public String[] getClientAliases(final String keyType, final Principal[] issuers) {
            return this.keyManager.getClientAliases(keyType, issuers);
        }
        
        public Map<String, PrivateKeyDetails> getClientAliasMap(final String[] keyTypes, final Principal[] issuers) {
            final Map<String, PrivateKeyDetails> validAliases = new HashMap<String, PrivateKeyDetails>();
            for (final String keyType : keyTypes) {
                final String[] aliases = this.keyManager.getClientAliases(keyType, issuers);
                if (aliases != null) {
                    for (final String alias : aliases) {
                        validAliases.put(alias, new PrivateKeyDetails(keyType, this.keyManager.getCertificateChain(alias)));
                    }
                }
            }
            return validAliases;
        }
        
        public Map<String, PrivateKeyDetails> getServerAliasMap(final String keyType, final Principal[] issuers) {
            final Map<String, PrivateKeyDetails> validAliases = new HashMap<String, PrivateKeyDetails>();
            final String[] aliases = this.keyManager.getServerAliases(keyType, issuers);
            if (aliases != null) {
                for (final String alias : aliases) {
                    validAliases.put(alias, new PrivateKeyDetails(keyType, this.keyManager.getCertificateChain(alias)));
                }
            }
            return validAliases;
        }
        
        @Override
        public String chooseClientAlias(final String[] keyTypes, final Principal[] issuers, final Socket socket) {
            final Map<String, PrivateKeyDetails> validAliases = this.getClientAliasMap(keyTypes, issuers);
            return this.aliasStrategy.chooseAlias(validAliases, socket);
        }
        
        @Override
        public String[] getServerAliases(final String keyType, final Principal[] issuers) {
            return this.keyManager.getServerAliases(keyType, issuers);
        }
        
        @Override
        public String chooseServerAlias(final String keyType, final Principal[] issuers, final Socket socket) {
            final Map<String, PrivateKeyDetails> validAliases = this.getServerAliasMap(keyType, issuers);
            return this.aliasStrategy.chooseAlias(validAliases, socket);
        }
        
        @Override
        public X509Certificate[] getCertificateChain(final String alias) {
            return this.keyManager.getCertificateChain(alias);
        }
        
        @Override
        public PrivateKey getPrivateKey(final String alias) {
            return this.keyManager.getPrivateKey(alias);
        }
        
        @Override
        public String chooseEngineClientAlias(final String[] keyTypes, final Principal[] issuers, final SSLEngine sslEngine) {
            final Map<String, PrivateKeyDetails> validAliases = this.getClientAliasMap(keyTypes, issuers);
            return this.aliasStrategy.chooseAlias(validAliases, null);
        }
        
        @Override
        public String chooseEngineServerAlias(final String keyType, final Principal[] issuers, final SSLEngine sslEngine) {
            final Map<String, PrivateKeyDetails> validAliases = this.getServerAliasMap(keyType, issuers);
            return this.aliasStrategy.chooseAlias(validAliases, null);
        }
    }
}
