package net.badlion.gfactions.events;

import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import net.badlion.gfactions.GFactions;
import net.badlion.gfactions.bukkitevents.EventStateChangeEvent;
import net.badlion.gfactions.managers.FactionManager;
import net.badlion.gberry.Gberry;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

public class DragonEvent {

    private GFactions plugin;

    private EnderDragon dragon;
    private World theEnd;

    public DragonEvent(GFactions plugin) {
        this.plugin = plugin;

        this.theEnd = this.plugin.getServer().getWorld("world_the_end");
    }

    public void startEvent() {
        // Recreate the healing beacons
        for (Location loc : this.plugin.getEndCrystalLocations()) {
            this.theEnd.spawn(loc, EnderCrystal.class);
        }

        Location spawnLocation = new Location(this.theEnd, -25, 90, 10);
        EnderDragon dragon = this.theEnd.spawn(spawnLocation, EnderDragon.class);
        dragon.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1), false); // SPEED 2 BITCHES
	    dragon.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 3), false); // STR 4 BITCHES
        dragon.setMaxHealth(dragon.getHealth() * 0.5); // 1/2 THE HEALTH, 1/2 THE MUSCLE, 1/2 THE DRAGON
        dragon.setHealth(dragon.getHealth() * 0.5); // 1/2 THE HEALTH, 1/2 THE MUSCLE, 1/2 THE DRAGON
        this.dragon = dragon;

        Gberry.broadcastMessage(ChatColor.GOLD + "The Ender Dragon has spawned in the End, kill the dragon within an hour for valuable loot!");

        // Call tab list event
        EventStateChangeEvent event = new EventStateChangeEvent("Dragon", true);
        this.plugin.getServer().getPluginManager().callEvent(event);
    }

    public void endEvent(boolean dragonKilled, Location locationKilled, Player killer) {
        if (!dragonKilled) {
            // Kill dragon
            this.dragon.remove();

            Gberry.broadcastMessage(ChatColor.GOLD + "The Ender Dragon has despawned after not being killed for an hour!");
        } else {
            if (this.plugin.getKillDragonTask() != null) {
                this.plugin.getKillDragonTask().cancel();
            }

            System.out.println(locationKilled);

            // Drop the loot yo
            ArrayList<ItemStack> items = this.plugin.getItemGenerator().generateRandomRareItem(3);
            for (ItemStack item : items) {
                this.theEnd.dropItem(locationKilled, item);
            }

			if (killer != null) {
				final Faction faction = FPlayers.i.get(killer).getFaction();
                if (!faction.getId().equals("0")) {
                    this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
                        @Override
                        public void run() {
                            FactionManager.addStatToFaction("dragons", faction);
                        }
                    });
                }

				Bukkit.getLogger().info("~Faction " + faction.getId() + faction.getTag() + " won Dragon!");
			} else {
				Bukkit.getLogger().info("null");
			}

            // Drop the egg!
            this.theEnd.dropItem(locationKilled, new ItemStack(Material.DRAGON_EGG));

            Gberry.broadcastMessage(ChatColor.GOLD + "The Ender Dragon has been slain by a heroic group of peasants!");
        }

        // Remove ender crystals
        for (Entity e : this.theEnd.getEntities()) {
            if (e.getType() == EntityType.ENDER_CRYSTAL) {
                e.remove();
            }
        }

        this.plugin.setDragonEvent(null);
        this.plugin.setKillDragonTask(null);

        // Call tab list event
        EventStateChangeEvent event = new EventStateChangeEvent("Dragon", false);
        this.plugin.getServer().getPluginManager().callEvent(event);
    }

}
