// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.core.command.punish;

import club.mineman.core.util.finalutil.CC;
import club.mineman.core.rank.Rank;

public class MuteCommand extends PunishCommand
{
    public MuteCommand() {
        super(Rank.TRAINEE, "mute", "Mute a player.", CC.RED + "Usage: /mute <player> [time] [reason]", PunishType.MUTE);
    }
}
