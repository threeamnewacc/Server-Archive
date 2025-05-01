package net.badlion.arenasetup.command;

import net.badlion.arenasetup.ArenaSetup;
import net.badlion.arenasetup.SetupSession;
import net.badlion.gedit.wands.SelectionManager;
import net.badlion.gedit.wands.WandSelection;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetupCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if (ArenaSetup.getInstance().getSetupSessionMap().containsKey(player.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "ERROR: You are already setting up an arena!");
                return false;
            }
            if (args.length != 1) {
                player.sendMessage("/arenasetup <name>");
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
            SetupSession setupSession = new SetupSession(args[0], selection);
            ArenaSetup.getInstance().getSetupSessionMap().put(player.getUniqueId(), setupSession);
            player.sendMessage(ChatColor.GOLD + "Started Arena Setup...");
            player.sendMessage(ChatColor.GREEN + "Step 1:");
            player.sendMessage(ChatColor.GOLD + "Set the two warps for this arena, stand at the warp locations and use" + ChatColor.YELLOW + "/setwarp1 and /setwarp2");
            player.sendMessage("");
            player.sendMessage(ChatColor.GREEN + "Step 2:");
            player.sendMessage(ChatColor.GOLD + "Set the kits that will use this arena, you can select 1 or more kit types. Use " + ChatColor.YELLOW + "/setarenatypes");
            player.sendMessage("");
            player.sendMessage(ChatColor.GREEN + "The arena you made has " + air + " air blocks, and " + blocks + " solid blocks.");
            player.sendMessage(ChatColor.GOLD + "Use " + ChatColor.YELLOW + "/setarenaselection" + ChatColor.GOLD + " to update the arenas gedit selection before you save it.");
            player.sendMessage("");
            player.sendMessage(ChatColor.GREEN + "Once you are done use " + ChatColor.YELLOW + "/finishsetup " + ChatColor.GREEN + "to save the arena.");
            player.sendMessage(ChatColor.GOLD + "You can use /arenastatus to see setup status.");
        }
        return false;
    }
}
