package net.badlion.arenasetup.command;

import net.badlion.arenasetup.ArenaSetup;
import net.badlion.gedit.wands.SelectionManager;
import net.badlion.gedit.wands.WandSelection;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UpdateArenaSelection implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!ArenaSetup.getInstance().getSetupSessionMap().containsKey(player.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "ERROR: You are not setting up an arena!");
                return false;
            }
            WandSelection selection = SelectionManager.getSelection(player);
            if (selection == null) {
                player.sendMessage("Select the arena with your gedit wand first.");
                return false;
            }
            int air = 0;
            int blocks = 0;
            for (Block block : selection.getAllBlocks()) {
                if (block.getType().equals(Material.AIR)) {
                    air++;
                } else {
                    blocks++;
                }
            }
            player.sendMessage(ChatColor.GREEN + "The arena you selected has " + air + " air blocks, and " + blocks + " solid blocks.");

        }
        return false;
    }
}
