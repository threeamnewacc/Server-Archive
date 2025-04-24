// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.antigamingchair.check;

import org.bukkit.entity.Player;

public interface ICheck<T>
{
    void handleCheck(final Player p0, final T p1);
    
    Class<? extends T> getType();
}
