// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.core.command.punish;

import club.mineman.core.util.finalutil.CC;
import club.mineman.core.rank.Rank;

public class KickCommand extends PunishCommand
{
    public KickCommand() {
        super(Rank.TRAINEE, "kick", "Kick a player.", CC.RED + "Usage: /kick <player> [reason]", PunishType.KICK);
    }
}
