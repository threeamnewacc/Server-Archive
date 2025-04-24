// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.impl.client;

import org.apache.http.config.Lookup;
import org.apache.http.conn.util.PublicSuffixMatcherLoader;
import org.apache.http.impl.cookie.IgnoreSpecProvider;
import org.apache.http.impl.cookie.NetscapeDraftSpecProvider;
import org.apache.http.impl.cookie.RFC6265CookieSpecProvider;
import org.apache.http.impl.cookie.DefaultCookieSpecProvider;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.util.PublicSuffixMatcher;

public final class CookieSpecRegistries
{
    public static RegistryBuilder<CookieSpecProvider> createDefaultBuilder(final PublicSuffixMatcher publicSuffixMatcher) {
        final CookieSpecProvider defaultProvider = new DefaultCookieSpecProvider(publicSuffixMatcher);
        final CookieSpecProvider laxStandardProvider = new RFC6265CookieSpecProvider(RFC6265CookieSpecProvider.CompatibilityLevel.RELAXED, publicSuffixMatcher);
        final CookieSpecProvider strictStandardProvider = new RFC6265CookieSpecProvider(RFC6265CookieSpecProvider.CompatibilityLevel.STRICT, publicSuffixMatcher);
        return (RegistryBuilder<CookieSpecProvider>)RegistryBuilder.create().register("default", (NetscapeDraftSpecProvider)defaultProvider).register("best-match", (NetscapeDraftSpecProvider)defaultProvider).register("compatibility", (NetscapeDraftSpecProvider)defaultProvider).register("standard", (NetscapeDraftSpecProvider)laxStandardProvider).register("standard-strict", (NetscapeDraftSpecProvider)strictStandardProvider).register("netscape", new NetscapeDraftSpecProvider()).register("ignoreCookies", (NetscapeDraftSpecProvider)new IgnoreSpecProvider());
    }
    
    public static RegistryBuilder<CookieSpecProvider> createDefaultBuilder() {
        return createDefaultBuilder(PublicSuffixMatcherLoader.getDefault());
    }
    
    public static Lookup<CookieSpecProvider> createDefault() {
        return createDefault(PublicSuffixMatcherLoader.getDefault());
    }
    
    public static Lookup<CookieSpecProvider> createDefault(final PublicSuffixMatcher publicSuffixMatcher) {
        return createDefaultBuilder(publicSuffixMatcher).build();
    }
    
    private CookieSpecRegistries() {
    }
}
