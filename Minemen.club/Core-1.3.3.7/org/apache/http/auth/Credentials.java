// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.auth;

import java.security.Principal;

public interface Credentials
{
    Principal getUserPrincipal();
    
    String getPassword();
}
