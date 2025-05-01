package net.badlion.uhc.tasks;

import net.badlion.uhc.BadlionUHC;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;

public class AutoButcherTask extends BukkitRunnable {

    @Override
    public void run() {
        double recentTps = BadlionUHC.getInstance().getServer().getRecentTps()[0];

        /*if (recentTps < 12) {
            for (World world : BadlionUHC.getInstance().getServer().getWorlds()) {
                world.setNerfAnimals(true);
                for (Entity entity : world.getEntities()) {
                    if (entity instanceof LivingEntity && !(entity instanceof Player)) {
	                    // Is it a combat log npc?
	                    if (entity instanceof Zombie) {
		                    if (entity.hasMetadata("CombatLoggerNPC")) {
			                    continue;
		                    }
	                    } else if (entity instanceof EnderDragon) {
                            continue;
                        }

                        entity.remove();
                    }
                }
            }

            Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "uhcmc Butchered everything!");
        } else */
	    if (recentTps < 14) {
            for (World world : BadlionUHC.getInstance().getServer().getWorlds()) {
	            // Disable AI
                world.setNerfAnimals(true);

                for (Entity entity : world.getEntities()) {
                    if (entity instanceof Monster && !(entity instanceof Player)) {
	                    // Is it a combat log npc?
	                    if (entity instanceof Zombie) {
		                    if (entity.hasMetadata("CombatLoggerNPC")) {
			                    continue;
		                    }
	                    } else if (entity instanceof EnderDragon || entity instanceof Cow
			                    || entity instanceof Pig || entity instanceof Chicken) {
                            continue;
                        }

                        entity.remove();
                    }
                }
            }

            Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "uhcmc Butchered monsters!");
        } else if (recentTps < 16) {
            for (World world : BadlionUHC.getInstance().getServer().getWorlds()) {
	            // Disable AI
                world.setNerfAnimals(true);
                for (Entity entity : world.getEntities()) {
                    if (entity instanceof Sheep || entity instanceof Squid || entity instanceof Bat) {
                        entity.remove();
                    }
                }
            }

            Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "uhcmc Butchered minor animals!");
        } else if (recentTps > 19) {
            for (World world : BadlionUHC.getInstance().getServer().getWorlds()) {
	            // Enable AI
                world.setNerfAnimals(false);
            }
        }
    }

}
