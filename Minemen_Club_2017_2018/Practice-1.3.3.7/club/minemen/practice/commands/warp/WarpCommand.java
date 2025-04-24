// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.commands.warp;

import club.minemen.practice.player.PlayerData;
import club.minemen.core.util.finalutil.CC;
import club.minemen.practice.player.PlayerState;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import java.util.List;
import java.util.Collections;
import club.minemen.practice.Practice;
import club.minemen.core.command.BaseCommand;

public class WarpCommand extends BaseCommand
{
    private final Practice plugin;
    
    public WarpCommand() {
        super("spawn");
        this.plugin = Practice.getInstance();
        this.setPlayerOnly(true);
        this.setAliases((List)Collections.singletonList("ffa"));
    }
    
    public boolean onExecute(final CommandSender commandSender, final String label, final String[] args) {
        final Player player = (Player)commandSender;
        final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        if (playerData.getPlayerState() != PlayerState.SPAWN && playerData.getPlayerState() != PlayerState.FFA) {
            player.sendMessage(CC.RED + "You can't do this in your current state.");
            return true;
        }
        final String lowerCase = label.toLowerCase();
        switch (lowerCase) {
            case "spawn": {
                this.plugin.getPlayerManager().sendToSpawnAndReset(player);
                break;
            }
        }
        return true;
    }
}
