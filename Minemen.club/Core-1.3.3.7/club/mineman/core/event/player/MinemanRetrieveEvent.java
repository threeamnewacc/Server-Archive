// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.core.event.player;

import club.mineman.core.mineman.Mineman;
import club.mineman.core.util.BanWrapper;
import club.mineman.core.event.MinemanEvent;

public class MinemanRetrieveEvent extends MinemanEvent
{
    private final BanWrapper banWrapper;
    
    public MinemanRetrieveEvent(final Mineman mineman, final BanWrapper banWrapper) {
        super(mineman);
        this.banWrapper = banWrapper;
    }
    
    public BanWrapper getBanWrapper() {
        return this.banWrapper;
    }
}
