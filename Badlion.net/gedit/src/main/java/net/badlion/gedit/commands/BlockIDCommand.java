package net.badlion.gedit.commands;

import net.badlion.gedit.GEdit;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashSet;
import java.util.UUID;

public class BlockIDCommand implements CommandExecutor, Listener {

	private java.util.Set<UUID> uuids = new HashSet<>();

	public BlockIDCommand() {
		Bukkit.getPluginManager().registerEvents(this, GEdit.getInstance());
	}

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
	    if (sender instanceof Player) {
		    this.uuids.add(((Player) sender).getUniqueId());

		    sender.sendMessage(ChatColor.YELLOW + "Punch a block to identify it.");
	    }
        return true;
    }

	@EventHandler
	public void onPlayerClickBlockEvent(PlayerInteractEvent event) {
		if (this.uuids.remove(event.getPlayer().getUniqueId())) {
			event.setCancelled(true);

			Block block = event.getClickedBlock();

			if (block != null) {
				event.getPlayer().sendMessage(ChatColor.YELLOW + "That is " + ChatColor.GREEN + block.getType()
						+ " (" + block.getTypeId() + "):" + block.getData());
			} else {
				event.getPlayer().sendMessage(ChatColor.YELLOW + "That's not a block you idiot. Cancelled.");
			}
		}
	}

}
