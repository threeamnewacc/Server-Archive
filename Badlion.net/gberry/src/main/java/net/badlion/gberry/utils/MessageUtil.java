package net.badlion.gberry.utils;

import net.badlion.gberry.Gberry;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class MessageUtil {

	public static final char HEART = '\u2764';
	public static final String HEART_WITH_COLOR = ChatColor.DARK_RED + "\u2764";

	public static final EntityDamageEvent CUSTOM_ENTITY_DAMAGE_EVENT = new EntityDamageEvent(null, EntityDamageEvent.DamageCause.CUSTOM, 0);
	public static final EntityDamageEvent VOID_DAMAGE_EVENT = new EntityDamageEvent(null, EntityDamageEvent.DamageCause.VOID, 0);

	/**
	 * Broadcasts custom death messages depending on the kill reason
	 *
	 * @param event - PlayerDeathEvent or CombatTagKilledEvent
	 * @param livingEntity - Combat logger entity or Player object
	 * @param killer - The killer of the LivingEntity, if there is one
	 */
    public static void handleDeathMessage(Event event, LivingEntity livingEntity, Player killer) {
	    String name;
	    if (livingEntity instanceof Player) {
		    name = ((Player) livingEntity).getDisguisedName();
	    } else {
		    name = livingEntity.getCustomName();
	    }

	    if (livingEntity.getLastDamageCause() != null) {
            switch (livingEntity.getLastDamageCause().getCause()) {
                case BLOCK_EXPLOSION:
                    Gberry.broadcastMessageNoBalance(ChatColor.YELLOW + name + ChatColor.RED + " just got blown the hell up");
                    break;
                case CONTACT:
                    if (killer != null) {
                        Gberry.broadcastMessageNoBalance(ChatColor.YELLOW + name + ChatColor.RED + " walked into a cactus whilst trying to escape " +
                                                                 ChatColor.YELLOW + killer.getDisguisedName());
                    } else {
                        Gberry.broadcastMessageNoBalance(ChatColor.YELLOW + name + ChatColor.RED + " was pricked to death");
                    }
                    break;
                case CUSTOM:
	                // Use the death message from the event if applicable
	                if (event instanceof PlayerDeathEvent) {
		                Gberry.broadcastMessageNoBalance(ChatColor.YELLOW + name + ChatColor.RED + ((PlayerDeathEvent) event).getDeathMessage());
	                } else {
		                Gberry.broadcastMessageNoBalance(ChatColor.YELLOW + name + ChatColor.RED + " died for some reason");
	                }
                    break;
                case DROWNING:
                    if (killer != null) {
                        Gberry.broadcastMessageNoBalance(ChatColor.YELLOW + name + ChatColor.RED + " drowned whilst trying to escape " +
                                                                 ChatColor.YELLOW + killer.getDisguisedName());
                    } else {
                        Gberry.broadcastMessageNoBalance(ChatColor.YELLOW + name + ChatColor.RED + " drowned");
                    }
                    break;
                case ENTITY_ATTACK:
                    if (killer != null) {
                        Gberry.broadcastMessageNoBalance(ChatColor.YELLOW + name + ChatColor.RED + " was slain by " +
                                                                 ChatColor.YELLOW + killer.getDisguisedName());
                    } else {
                        Gberry.broadcastMessageNoBalance(ChatColor.YELLOW + name + ChatColor.RED + " was slain");
                    }
                    break;
                case ENTITY_EXPLOSION:
                    if (killer != null) {
                        Gberry.broadcastMessageNoBalance(ChatColor.YELLOW + name + ChatColor.RED + " got blown the hell up by " +
                                                                 ChatColor.YELLOW + killer.getDisguisedName());
                    } else {
                        Gberry.broadcastMessageNoBalance(ChatColor.YELLOW + name + ChatColor.RED + " got blown the hell up");
                    }
                    break;
                case FALL:
                    if (killer != null) {
                        Gberry.broadcastMessageNoBalance(ChatColor.YELLOW + name + ChatColor.RED + " was doomed to fall by " +
                                                                 ChatColor.YELLOW + killer.getDisguisedName());
                    } else {
                        if (livingEntity.getFallDistance() > 5) {
                            Gberry.broadcastMessageNoBalance(ChatColor.YELLOW + name + ChatColor.RED + " fell from a high place");
                        } else {
                            Gberry.broadcastMessageNoBalance(ChatColor.YELLOW + name + ChatColor.RED + " hit the ground too hard");
                        }
                    }
                    break;
                case FALLING_BLOCK:
                    Gberry.broadcastMessageNoBalance(ChatColor.YELLOW + name + ChatColor.RED + " got freaking squashed by a block");
                    break;
                case FIRE:
                    if (killer != null) {
                        Gberry.broadcastMessageNoBalance(ChatColor.YELLOW + name + ChatColor.RED + " walked into a fire whilst fighting " +
                                                                 ChatColor.YELLOW + killer.getDisguisedName());
                    } else {
                        Gberry.broadcastMessageNoBalance(ChatColor.YELLOW + name + ChatColor.RED + " went up in flames");
                    }
                    break;
                case FIRE_TICK:
                    if (killer != null) {
                        Gberry.broadcastMessageNoBalance(ChatColor.YELLOW + name + ChatColor.RED + " was burnt to a crisp whilst fighting " +
                                                                 ChatColor.YELLOW + killer.getDisguisedName());
                    } else {
                        Gberry.broadcastMessageNoBalance(ChatColor.YELLOW + name + ChatColor.RED + " burned to death");
                    }
                    break;
                case LAVA:
                    if (killer != null) {
                        Gberry.broadcastMessageNoBalance(ChatColor.YELLOW + name + ChatColor.RED +
                                                                 " tried to swim in lava while trying to escape " + ChatColor.YELLOW + killer.getDisguisedName());
                    } else {
                        Gberry.broadcastMessageNoBalance(ChatColor.YELLOW + name + ChatColor.RED + " tried to swim in lava");
                    }
                    break;
                case LIGHTNING:
                    Gberry.broadcastMessageNoBalance(ChatColor.YELLOW + name + ChatColor.RED + " got lit the hell up by lightnin'");
                    break;
                case MAGIC:
                    if (killer != null) {
                        Gberry.broadcastMessageNoBalance(ChatColor.YELLOW + name + ChatColor.RED +
                                                                 " was killed by " + ChatColor.YELLOW + killer.getDisguisedName() + ChatColor.RED + " using magic");
                    } else {
                        Gberry.broadcastMessageNoBalance(ChatColor.YELLOW + name + ChatColor.RED + " was killed by magic");
                    }
                    break;
                case POISON:
                    Gberry.broadcastMessageNoBalance(ChatColor.YELLOW + name + ChatColor.RED + " was poisoned");
                    break;
                case PROJECTILE:
                    if (killer != null) {
                        Gberry.broadcastMessageNoBalance(ChatColor.YELLOW + name + ChatColor.RED + " was shot by " +
                                                                 ChatColor.YELLOW + killer.getDisguisedName());
                    } else {
                        Gberry.broadcastMessageNoBalance(ChatColor.YELLOW + name + ChatColor.RED + " was shot");
                    }
                    break;
                case STARVATION:
                    Gberry.broadcastMessageNoBalance(ChatColor.YELLOW + name + ChatColor.RED + " starved to death");
                    break;
                case SUFFOCATION:
                    Gberry.broadcastMessageNoBalance(ChatColor.YELLOW + name + ChatColor.RED + " suffocated in a wall");
                    break;
                case SUICIDE:
                    Gberry.broadcastMessageNoBalance(ChatColor.YELLOW + name + ChatColor.RED + " took his own life like a peasant");
                    break;
                case THORNS:
                    Gberry.broadcastMessageNoBalance(ChatColor.YELLOW + name + ChatColor.RED + " killed themself by trying to kill someone LOL");
                    break;
                case VOID:
                    if (killer != null) {
                        Gberry.broadcastMessageNoBalance(ChatColor.YELLOW + name + ChatColor.RED +
                                                                 " was knocked into the void by " + ChatColor.YELLOW + killer.getDisguisedName());
                    } else {
                        Gberry.broadcastMessageNoBalance(ChatColor.YELLOW + name + ChatColor.RED + " fell out of the world");
                    }
                    break;
                case WITHER:
                    Gberry.broadcastMessageNoBalance(ChatColor.YELLOW + name + ChatColor.RED + " withered away");
                    break;
                default:
                    Gberry.broadcastMessageNoBalance(ChatColor.YELLOW + name + ChatColor.RED + " died");
            }
        } else {
            Gberry.broadcastMessageNoBalance(ChatColor.YELLOW + name + ChatColor.RED + " died");
        }

	    // Don't send the default death message
	    if (event instanceof PlayerDeathEvent) {
		    ((PlayerDeathEvent) event).setDeathMessage(null);
	    }
    }

}
