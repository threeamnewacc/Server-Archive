// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.core.command.punish;

import club.mineman.core.util.finalutil.CC;
import club.mineman.core.rank.Rank;

public class BlacklistCommand extends PunishCommand
{
    public BlacklistCommand() {
        super(Rank.ADMIN, "blacklist", "Blacklist a player.", CC.RED + "Usage: /blacklist <player> [reason]", PunishType.BLACKLIST);
    }
}
