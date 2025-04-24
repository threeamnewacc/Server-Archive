// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.core.command.punish;

import club.mineman.core.util.finalutil.CC;
import club.mineman.core.rank.Rank;

public class UnmuteCommand extends PunishCommand
{
    public UnmuteCommand() {
        super(Rank.ADMIN, "unmute", "Unmute a player.", CC.RED + "Usage: /unmute <player>", PunishType.UNMUTE);
    }
}
