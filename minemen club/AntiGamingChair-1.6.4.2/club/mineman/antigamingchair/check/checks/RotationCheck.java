// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.antigamingchair.check.checks;

import club.mineman.antigamingchair.data.PlayerData;
import club.mineman.antigamingchair.AntiGamingChair;
import club.mineman.paper.event.PlayerUpdateRotationEvent;
import club.mineman.antigamingchair.check.AbstractCheck;

public abstract class RotationCheck extends AbstractCheck<PlayerUpdateRotationEvent>
{
    public RotationCheck(final AntiGamingChair plugin, final PlayerData playerData) {
        super(plugin, playerData, PlayerUpdateRotationEvent.class);
    }
}
