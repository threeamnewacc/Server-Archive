// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.impl.cookie;

import java.io.IOException;
import org.apache.http.conn.util.PublicSuffixList;
import java.util.Collection;
import java.io.Reader;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.annotation.Contract;

@Deprecated
@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class PublicSuffixListParser
{
    private final PublicSuffixFilter filter;
    private final org.apache.http.conn.util.PublicSuffixListParser parser;
    
    PublicSuffixListParser(final PublicSuffixFilter filter) {
        this.filter = filter;
        this.parser = new org.apache.http.conn.util.PublicSuffixListParser();
    }
    
    public void parse(final Reader reader) throws IOException {
        final PublicSuffixList suffixList = this.parser.parse(reader);
        this.filter.setPublicSuffixes(suffixList.getRules());
        this.filter.setExceptions(suffixList.getExceptions());
    }
}
