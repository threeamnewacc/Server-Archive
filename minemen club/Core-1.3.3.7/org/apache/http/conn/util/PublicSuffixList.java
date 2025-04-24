// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.conn.util;

import java.util.Collections;
import org.apache.http.util.Args;
import java.util.List;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.annotation.Contract;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public final class PublicSuffixList
{
    private final DomainType type;
    private final List<String> rules;
    private final List<String> exceptions;
    
    public PublicSuffixList(final DomainType type, final List<String> rules, final List<String> exceptions) {
        this.type = Args.notNull(type, "Domain type");
        this.rules = Collections.unmodifiableList((List<? extends String>)Args.notNull((List<? extends T>)rules, "Domain suffix rules"));
        this.exceptions = Collections.unmodifiableList((List<? extends String>)((exceptions != null) ? exceptions : Collections.emptyList()));
    }
    
    public PublicSuffixList(final List<String> rules, final List<String> exceptions) {
        this(DomainType.UNKNOWN, rules, exceptions);
    }
    
    public DomainType getType() {
        return this.type;
    }
    
    public List<String> getRules() {
        return this.rules;
    }
    
    public List<String> getExceptions() {
        return this.exceptions;
    }
}
