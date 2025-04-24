// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.core.command.punish;

import club.mineman.core.util.finalutil.CC;
import club.mineman.core.rank.Rank;

public class BanCommand extends PunishCommand
{
    public BanCommand() {
        super(Rank.MOD, "ban", "Ban a player.", CC.RED + "Usage: /ban <player> [time] [reason]", PunishType.BAN);
    }
}
