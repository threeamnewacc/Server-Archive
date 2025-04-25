package us.zonix.practice.commands.time;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.zonix.practice.Practice;
import us.zonix.practice.player.PlayerData;
import us.zonix.practice.settings.item.ProfileOptionsItemState;

public class SunsetCommand extends Command {
    public SunsetCommand() {
        super("sunset");
        this.setDescription("Set player time to sunset.");
        this.setUsage(ChatColor.RED + "Usage: /sunset");
    }

    public boolean execute(CommandSender sender, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        } else {
            ((Player)sender).setPlayerTime(12000L, false);
            PlayerData playerData = Practice.getInstance().getPlayerManager().getPlayerData(((Player)sender).getUniqueId());
            playerData.getOptions().setTime(ProfileOptionsItemState.SUNSET);
            return true;
        }
    }
}
