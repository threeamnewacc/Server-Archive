// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.core.command.punish;

import club.mineman.core.util.finalutil.CC;
import club.mineman.core.rank.Rank;

public class UnbanCommand extends PunishCommand
{
    public UnbanCommand() {
        super(Rank.ADMIN, "unban", "Unban a player.", CC.RED + "Usage: /unban <player>", PunishType.UNBAN);
    }
}
