package net.badlion.gfactions.tasks.manhunt;

import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import net.badlion.gberry.Gberry;
import net.badlion.gfactions.GFactions;
import net.badlion.gfactions.bukkitevents.EventStateChangeEvent;
import net.badlion.gfactions.managers.FactionManager;
import net.badlion.smellyloot.managers.LootManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ManHuntTrackerTask extends BukkitRunnable {

    private GFactions plugin;

    private Player player;
    private int secondsAlive = -1; // Start at -1, initial takes to 0, then 10 minutes later it's 10

    public ManHuntTrackerTask(GFactions plugin, Player player) {
        this.plugin = plugin;

        this.player = player;
    }

    @Override
    public void run() {
        if (this.plugin.getManHuntPP() == null || !this.player.equals(this.plugin.getManHuntTagged())) {
            this.cancel();
            return;
        }

		this.secondsAlive++;

        if (this.secondsAlive == 600) { // THEY WIN
            /*final ArrayList<ItemStack> items = new ArrayList<>();
            items.addAll(plugin.getItemGenerator().generateRandomSuperRareItem(1));
            items.addAll(plugin.getItemGenerator().generateRandomRareItem(3));

	        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
		        @Override
		        public void run() {
			        // Give them one super rare item
                    plugin.getAuction().insertHeldAuctionItems(player.getUniqueId().toString(), items);
		        }
	        });

	        this.player.sendMessage(ChatColor.YELLOW + "You have won the Man Hunt, do \"/claim\" to claim your prize!");*/

	        final Faction faction = FPlayers.i.get(player).getFaction();
            if (!faction.getId().equals("0")) {
                this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
                    @Override
                    public void run() {
                        FactionManager.addStatToFaction("manhunts", faction);
                    }
                });
            }

	        Bukkit.getLogger().info("~Faction " + faction.getId() + faction.getTag() + " won Man Hunt!");

	        Gberry.broadcastMessage(ChatColor.GREEN + "[ManHunt] " + this.player.getName() + " has survived for 10 continuous minutes and has won the Man Hunt!");

	        this.plugin.getManHuntPP().setType(Material.AIR);
	        this.plugin.setManHuntPP(null);
	        this.plugin.setManHuntTagged(null);
	        this.cancel();

            // Drop Loot
	        LootManager.dropEventLootPlayer("manhunt", this.player);

	        // Call TabList event
	        EventStateChangeEvent event = new EventStateChangeEvent("Man Hunt", false);
	        this.plugin.getServer().getPluginManager().callEvent(event);
        } else if (this.secondsAlive == 0) {
	        this.plugin.addCombatTagged(this.player);
	        Gberry.broadcastMessage(ChatColor.GREEN + "[ManHunt] " + this.player.getName() + " has been tagged at "
			        + (int) this.player.getLocation().getX() + ", " + (int) this.player.getLocation().getY() + ", " + (int) this.player.getLocation().getZ() + "!");
        } else if (this.secondsAlive % 15 == 0) {
            this.plugin.addCombatTagged(this.player);

            Gberry.broadcastMessage(ChatColor.GREEN + "[ManHunt] " + this.player.getName() + " is at "
                    + (int) this.player.getLocation().getX() + ", " + (int) this.player.getLocation().getY() + ", " + (int) this.player.getLocation().getZ() +
                    " and has been alive for " + this.secondsAlive / 60 + " minutes and " + this.secondsAlive % 60 + " seconds!");

            this.updateCompasses();
        } else if (this.secondsAlive % 5 == 0) {
            this.updateCompasses();
        }
    }

    private void updateCompasses() {
        for (Player p : this.plugin.getServer().getOnlinePlayers()) {
            p.setCompassTarget(this.player.getLocation());
        }
    }

}
