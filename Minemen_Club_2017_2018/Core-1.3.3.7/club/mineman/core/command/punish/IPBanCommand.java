// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.core.command.punish;

import club.mineman.core.util.finalutil.CC;
import club.mineman.core.rank.Rank;

public class IPBanCommand extends PunishCommand
{
    public IPBanCommand() {
        super(Rank.ADMIN, "ipban", "IP-Ban a player.", CC.RED + "Usage: /ipban <player> [reason]", PunishType.IPBAN);
    }
}
