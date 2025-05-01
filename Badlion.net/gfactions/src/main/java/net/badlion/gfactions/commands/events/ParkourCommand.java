package net.badlion.gfactions.commands.events;

import net.badlion.gfactions.GFactions;
import net.badlion.gfactions.bukkitevents.EventStateChangeEvent;
import net.badlion.gfactions.tasks.parkour.ParkourRemoveChestTask;
import net.badlion.gfactions.tasks.parkour.ParkourTask;
import net.badlion.gberry.Gberry;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ParkourCommand implements CommandExecutor {
	
	private GFactions plugin;
	
	public ParkourCommand(GFactions plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, final String[] args) {
        List<String> chests = (List<String>) this.plugin.getConfig().getList("gfactions.parkour.chest_names");
        String chestName = chests.get(this.plugin.generateRandomInt(0, chests.size() - 1));

        int x = this.plugin.getConfig().getInt("gfactions.parkour.chest_locations." + chestName + ".chest_x");
        int y = this.plugin.getConfig().getInt("gfactions.parkour.chest_locations." + chestName + ".chest_y");
        int z = this.plugin.getConfig().getInt("gfactions.parkour.chest_locations." + chestName + ".chest_z");
		
		// Cast to proper format to edit inventory
		Block block = this.plugin.getServer().getWorld("world").getBlockAt(x, y, z);
        block.setType(Material.CHEST);
		Chest chest = (Chest) block.getState();

        // Pick 3 random items
        for (int i = 0; i < 3; i++) {
            int r = this.plugin.generateRandomInt(1, 100);
            ArrayList<ItemStack> items = null;
            if (1 <= r && r < 3) {
                items = this.plugin.getItemGenerator().generateRandomSuperRareItem(1);
            } else if (3 <= r && r < 20) {
                items = this.plugin.getItemGenerator().generateRandomRareItem(1);
            } else if (20 <= r && r < 85) {
                items = this.plugin.getItemGenerator().generateRandomCommonItem(1);
            } else if (85 <= r && r <= 100) {
                items = this.plugin.getItemGenerator().generateRandomTrashItem(1);
            }

            chest.getBlockInventory().addItem(items.get(0));
        }

        // Keep track of the chest
        this.plugin.setParkourChest(chest);
        new ParkourTask(this.plugin).runTaskTimer(this.plugin, 20, 20);

		this.plugin.getLogger().info("Parkour chest spawned at " + x + ", " + y + ", " + z);

		Gberry.broadcastMessage(ChatColor.GREEN + "A new parkour chest has spawned at " + x + ", " + y + ", " + z);
		
		// Run a task to remove the chest later either way
        new ParkourRemoveChestTask(this.plugin, false).runTaskLater(this.plugin, 15 * 60 * 20);

        // Call TabList event
        EventStateChangeEvent event = new EventStateChangeEvent("Parkour", true);
        this.plugin.getServer().getPluginManager().callEvent(event);

		// All done
		return true;
	}

}
