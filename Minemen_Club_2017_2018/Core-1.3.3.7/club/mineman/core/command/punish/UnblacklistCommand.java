// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.core.command.punish;

import club.mineman.core.util.finalutil.CC;
import club.mineman.core.rank.Rank;

public class UnblacklistCommand extends PunishCommand
{
    public UnblacklistCommand() {
        super(Rank.OWNER, "unblacklist", "Un-blacklist a player.", CC.RED + "Usage: /unblacklist <player>", PunishType.UNBLACKLIST);
    }
}
