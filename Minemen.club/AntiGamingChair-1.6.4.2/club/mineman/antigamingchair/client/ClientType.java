// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.antigamingchair.client;

import java.beans.ConstructorProperties;

public enum ClientType
{
    COSMIC_CLIENT(false, "CosmicClient"), 
    CHEAT_BREAKER(false, "https://discord.gg/pbAHNC4"), 
    VANILLA(false, "Vanilla"), 
    FORGE(false, "Forge"), 
    OC_MC(false, "OCMC"), 
    HACKED_CLIENT_A(true, "Hacked Client A"), 
    HACKED_CLIENT_B(true, "Hacked Client B"), 
    HACKED_CLIENT_C(true, "Hacked Client C"), 
    HACKED_CLIENT_D(true, "Hacked Client D"), 
    HACKED_CLIENT_E(true, "Hacked Client E"), 
    HACKED_CLIENT_F(true, "Hacked Client F");
    
    private final boolean hacked;
    private final String name;
    
    public boolean isHacked() {
        return this.hacked;
    }
    
    public String getName() {
        return this.name;
    }
    
    @ConstructorProperties({ "hacked", "name" })
    private ClientType(final boolean hacked, final String name) {
        this.hacked = hacked;
        this.name = name;
    }
}
