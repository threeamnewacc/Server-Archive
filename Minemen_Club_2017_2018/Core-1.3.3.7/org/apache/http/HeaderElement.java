// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http;

public interface HeaderElement
{
    String getName();
    
    String getValue();
    
    NameValuePair[] getParameters();
    
    NameValuePair getParameterByName(final String p0);
    
    int getParameterCount();
    
    NameValuePair getParameter(final int p0);
}
