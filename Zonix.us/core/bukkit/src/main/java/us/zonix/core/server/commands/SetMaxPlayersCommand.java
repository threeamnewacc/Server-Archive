package us.zonix.core.server.commands;

import net.minecraft.server.v1_8_R3.PlayerList;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.entity.Player;
import us.zonix.core.rank.Rank;
import us.zonix.core.util.command.BaseCommand;
import us.zonix.core.util.command.Command;
import us.zonix.core.util.command.CommandArgs;

import java.lang.reflect.Field;

public class SetMaxPlayersCommand extends BaseCommand {

    private static final int defaultTo = -157345;
    private static Field maxPlayersField;

    static {
        try {
            maxPlayersField = PlayerList.class.getDeclaredField("maxPlayers");
            maxPlayersField.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Command(name = "setmaxplayers", rank = Rank.OWNER, requiresPlayer = true)
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "Usage: /setmaxplayers [amount]");
            player.sendMessage(ChatColor.YELLOW + "Current Max Players: " + this.main.getServer().getMaxPlayers());
            return;
        }

        if (!NumberUtils.isNumber(args[0])) {
            player.sendMessage(ChatColor.RED + "Invalid amount.");
            return;
        }

        int amount = Integer.parseInt(args[0]);

        if (amount == SetMaxPlayersCommand.defaultTo) {
            player.sendMessage(ChatColor.YELLOW + "Current Max Players: " + this.main.getServer().getMaxPlayers());
            return;
        }

        if (amount <= 0) {
            player.sendMessage(ChatColor.RED + "Invalid amount.");
            return;
        }


        int oldPlayers = this.main.getServer().getMaxPlayers();
        try {
            maxPlayersField.set(((CraftServer) this.main.getServer()).getHandle(), amount);
            player.sendMessage(ChatColor.YELLOW + "Set Max Players: " + amount);
        } catch (Exception e) {
            e.printStackTrace();
            player.sendMessage(ChatColor.RED + "Error while setting players amount.");
        }
    }

}
