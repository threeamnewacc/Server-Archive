package net.badlion.gfactions.listeners;

import io.github.andrepl.chatlib.Text;
import net.badlion.gfactions.GFactions;
import org.bukkit.ChatColor;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

public class DeathListener implements Listener {

	private GFactions plugin;

	public DeathListener(GFactions plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();

        /*if (player.hasPermission("GFactions.iron")) {
            // Don't lose XP and don't drop it
            event.setKeepLevel(true);
            event.setDroppedExp(0);

            // Remove from combat tag list
            this.plugin.getCombatTag().removeTagged(p.getUniqueId());
        }*/

        // Drop ALL THE XP
        event.setDroppedExp(event.getEntity().getTotalExperience());

		// Store last location for /back
		//this.plugin.getTpManager().getLastKnownLocation().put(p.getUniqueId().toString(), p.getLocation());

        // Custom death messages woohoo

        // Get player stuff
        String pName = player.getName();

        // Get killer stuff
        Player killer = player.getKiller();
        String killerName = killer != null ? killer.getName() : "";

        // Get mob stuff
        LivingEntity mob = this.getLastEntityDamager(player);
        String mobName = "";
        if (mob instanceof Player) {
            mobName = ((Player) mob).getName();
        } else {
            mobName = mob != null ? mob.getType().toString().substring(0, 1) + mob.getType().toString().substring(1).toLowerCase() : "";
        }

        if (event.getEntity().getLastDamageCause() != null) {
            switch (event.getEntity().getLastDamageCause().getCause()) {
                case BLOCK_EXPLOSION:
                    event.setDeathMessage(ChatColor.YELLOW + pName + ChatColor.RED + " just got blown the hell up");
                    break;
                case CONTACT:
                    if (killer != null) {
                        event.setDeathMessage(ChatColor.YELLOW + pName + ChatColor.RED + " walked into a cactus whilst trying to escape " + ChatColor.YELLOW + killerName);
                    } else {
                        event.setDeathMessage(ChatColor.YELLOW + pName + ChatColor.RED + " was pricked to death");
                    }
                    break;
                case CUSTOM:
                    event.setDeathMessage(ChatColor.YELLOW + pName + ChatColor.RED + " was killed by an unknown cause");
                    break;
                case DROWNING:
                    if (killer != null) {
                        event.setDeathMessage(ChatColor.YELLOW + pName + ChatColor.RED + " drowned whilst trying to escape " + ChatColor.YELLOW + killerName);
                    } else {
                        event.setDeathMessage(ChatColor.YELLOW + pName + ChatColor.RED + " drowned");
                    }
                    break;
                case ENTITY_ATTACK:
                    if (killer != null) {
                        if (this.plugin.generateRandomInt(0, 1) == 1) {
                            ItemStack weapon = killer.getItemInHand();
                            if (weapon != null && weapon.hasItemMeta() && weapon.getItemMeta().hasDisplayName()) {
                                String deathMessage = ChatColor.YELLOW + pName + ChatColor.RED + " was slain by by " + ChatColor.YELLOW + killerName
                                        + ChatColor.RED + " using " + ChatColor.AQUA + "[";
                                Text text = new Text(deathMessage);
                                text.appendItem(weapon);
                                text.append("§b]"); // Hardcoded ChatColor.AQUA to work
                                for (Player pl : this.plugin.getServer().getOnlinePlayers()) {
                                    text.send(pl);
                                }
                                event.setDeathMessage(null);
                            } else {
                                event.setDeathMessage(ChatColor.YELLOW + pName + ChatColor.RED + " was slain by " + ChatColor.YELLOW + killerName);
                            }
                        } else {
                            ItemStack weapon = killer.getItemInHand();
                            if (weapon != null && weapon.hasItemMeta() && weapon.getItemMeta().hasDisplayName()) {
                                String deathMessage = ChatColor.YELLOW + pName + ChatColor.RED + " was finished off by " + ChatColor.YELLOW + killerName
                                        + ChatColor.RED + " using " + ChatColor.AQUA + "[";
                                Text text = new Text(deathMessage);
                                text.appendItem(weapon);
                                text.append("§b]"); // Hardcoded ChatColor.AQUA to work
                                for (Player pl : this.plugin.getServer().getOnlinePlayers()) {
                                    text.send(pl);
                                }
                                event.setDeathMessage(null);
                            } else {
                                event.setDeathMessage(ChatColor.YELLOW + pName + ChatColor.RED + " was finished off by " + ChatColor.YELLOW + killerName);
                            }
                        }
                    } else if (mob != null) {
                        event.setDeathMessage(ChatColor.YELLOW + pName + ChatColor.RED + " was slain by a " + ChatColor.YELLOW + mobName + ChatColor.RED + " using magic");
                    }
                    break;
                case ENTITY_EXPLOSION:
                    if (killer != null) {
                        event.setDeathMessage(ChatColor.YELLOW + pName + ChatColor.RED + " got blown the hell up by " + ChatColor.YELLOW + killerName);
                    } else if (mob != null) {
                        event.setDeathMessage(ChatColor.YELLOW + pName + ChatColor.RED + " got blown the hell up by a " + ChatColor.YELLOW + killerName);
                    }
                    break;
                case FALL:
                    if (killer != null) {
                        event.setDeathMessage(ChatColor.YELLOW + pName + ChatColor.RED + " was doomed to fall by " + ChatColor.YELLOW + killerName);
                    } else {
                        if (player.getFallDistance() > 5) {
                            event.setDeathMessage(ChatColor.YELLOW + pName + ChatColor.RED + " fell from a high place");
                        } else {
                            event.setDeathMessage(ChatColor.YELLOW + pName + ChatColor.RED + " hit the ground too hard");
                        }
                    }
                    break;
                case FALLING_BLOCK:
                    event.setDeathMessage(ChatColor.YELLOW + pName + ChatColor.RED + " got freaking squashed by a block");
                    break;
                case FIRE:
                    if (killer != null) {
                        event.setDeathMessage(ChatColor.YELLOW + pName + ChatColor.RED + " walked into a fire whilst fighting " + ChatColor.YELLOW + killerName);
                    } else {
                        event.setDeathMessage(ChatColor.YELLOW + pName + ChatColor.RED + " went up in flames");
                    }
                    break;
                case FIRE_TICK:
                    if (killer != null) {
                        event.setDeathMessage(ChatColor.YELLOW + pName + ChatColor.RED + " was burnt to a crisp whilst fighting " + ChatColor.YELLOW + killerName);
                    } else {
                        event.setDeathMessage(ChatColor.YELLOW + pName + ChatColor.RED + " burned to death");
                    }
                    break;
                case LAVA:
                    if (killer != null) {
                        event.setDeathMessage(ChatColor.YELLOW + pName + ChatColor.RED + " tried to swim in lava while trying to escape " + ChatColor.YELLOW + killerName);
                    } else {
                        event.setDeathMessage(ChatColor.YELLOW + pName + ChatColor.RED + " tried to swim in lava");
                    }
                    break;
                case LIGHTNING:
                    event.setDeathMessage(ChatColor.YELLOW + pName + ChatColor.RED + " got lit the hell up by lightnin'");
                    break;
                case MAGIC:
                    if (killer != null) {
                        event.setDeathMessage(ChatColor.YELLOW + pName + ChatColor.RED + " was killed by " + ChatColor.YELLOW + killerName + ChatColor.RED + " using magic");
                    } else if (mob != null) {
                        event.setDeathMessage(ChatColor.YELLOW + pName + ChatColor.RED + " was killed by a " + ChatColor.YELLOW + mobName + ChatColor.RED + " using magic");
                    } else {
                        event.setDeathMessage(ChatColor.YELLOW + pName + ChatColor.RED + " was killed by magic");
                    }
                    break;
                case POISON:
                    event.setDeathMessage(ChatColor.YELLOW + pName + ChatColor.RED + " was poisoned");
                    break;
                case PROJECTILE:
                    if (killer != null) {
                        event.setDeathMessage(ChatColor.YELLOW + pName + ChatColor.RED + " was shot by " + ChatColor.YELLOW + killerName);
                    } else if (mob != null) {
                        if (mob.getType().equals(EntityType.BLAZE) || mob.getType().equals(EntityType.GHAST)) {
                            event.setDeathMessage(ChatColor.YELLOW + pName + ChatColor.RED + " was fireballed by a " + ChatColor.YELLOW + mobName);
                        } else {
                            event.setDeathMessage(ChatColor.YELLOW + pName + ChatColor.RED + " was shot by a " + ChatColor.YELLOW + mobName);
                        }
                    } else {
                        event.setDeathMessage(ChatColor.YELLOW + pName + ChatColor.RED + " was shot");
                    }
                    break;
                case STARVATION:
                    event.setDeathMessage(ChatColor.YELLOW + pName + ChatColor.RED + " starved to death");
                    break;
                case SUFFOCATION:
                    event.setDeathMessage(ChatColor.YELLOW + pName + ChatColor.RED + " suffocated in a wall");
                    break;
                case SUICIDE:
                    event.setDeathMessage(ChatColor.YELLOW + pName + ChatColor.RED + " took his own life like a peasant");
                    break;
                case THORNS:
                    event.setDeathMessage(ChatColor.YELLOW + pName + ChatColor.RED + " killed themself by trying to kill someone LOL");
                    break;
                case VOID:
                    if (killer != null) {
                        event.setDeathMessage(ChatColor.YELLOW + pName + ChatColor.RED + " was knocked into the void by " + ChatColor.YELLOW + killerName);
                    } else {
                        event.setDeathMessage(ChatColor.YELLOW + pName + ChatColor.RED + " fell out of the world");
                    }
                    break;
                case WITHER:
                    event.setDeathMessage(ChatColor.YELLOW + pName + ChatColor.RED + " withered away");
                    break;
            }
        } else {
            event.setDeathMessage(ChatColor.YELLOW + pName + ChatColor.RED + " died.");
        }
    }

    public LivingEntity getLastEntityDamager(Player player) {
        if (player.getKiller() == null && player.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent entityEvent = (EntityDamageByEntityEvent) player.getLastDamageCause();
            Entity damager = entityEvent.getDamager();
            if (damager instanceof Projectile) {
                Object shooter = ((Projectile) damager).getShooter();
                if (shooter instanceof LivingEntity) {
                    return (LivingEntity) shooter;
                }
            }
        }
        return null;
    }

}
