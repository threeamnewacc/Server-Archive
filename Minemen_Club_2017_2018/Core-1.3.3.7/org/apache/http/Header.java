// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http;

public interface Header
{
    String getName();
    
    String getValue();
    
    HeaderElement[] getElements() throws ParseException;
}
