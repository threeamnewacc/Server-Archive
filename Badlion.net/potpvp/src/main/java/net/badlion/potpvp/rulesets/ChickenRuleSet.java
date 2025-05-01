package net.badlion.potpvp.rulesets;

import net.badlion.potpvp.Group;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.managers.ArenaManager;
import net.badlion.potpvp.states.matchmaking.GameState;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ChickenRuleSet extends KitRuleSet {

    public ChickenRuleSet(int id, String name) {
	    super(id, name, new ItemStack(Material.RAW_CHICKEN), ArenaManager.ArenaType.NON_PEARL, false, false);

	    // Create default inventory kit
	    this.defaultInventoryKit[0] = new ItemStack(Material.RAW_CHICKEN);
	    this.defaultInventoryKit[0].addUnsafeEnchantment(Enchantment.KNOCKBACK, 10);
    }

    @EventHandler(priority= EventPriority.MONITOR)
    public void onPlayerHitPlayer(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Group group = PotPvP.getInstance().getPlayerGroup(player);

            if (GameState.groupIsInMatchmakingAndUsingRuleSet(group, this)) {
                if (event.getDamager() instanceof Player) {
                    Player attacker = (Player) event.getDamager();
                    if (attacker.getItemInHand().getType() != Material.RAW_CHICKEN) {
                        attacker.sendMessage(ChatColor.RED + "You can only attack with your meat.");
                        event.setCancelled(true);
                    } else {
                        event.setDamage(2.0);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerStarve(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Group group = PotPvP.getInstance().getPlayerGroup(player);

            if (GameState.groupIsInMatchmakingAndUsingRuleSet(group, this)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerDropChicken(PlayerDropItemEvent event) {
        Group group = PotPvP.getInstance().getPlayerGroup(event.getPlayer());

        if (GameState.groupIsInMatchmakingAndUsingRuleSet(group, this)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerEatHisOwnMeat(PlayerInteractEvent event) {
        Group group = PotPvP.getInstance().getPlayerGroup(event.getPlayer());

        if (GameState.groupIsInMatchmakingAndUsingRuleSet(group, this)) {
            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (event.getItem() != null && event.getItem().getType() == Material.RAW_CHICKEN) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(ChatColor.RED + "You can't eat your own meat boy.");
                }
            }
        }
    }

    @EventHandler
    public void onRegen(EntityRegainHealthEvent event) {
        if (event.getEntity() instanceof Player) {
            Group group = PotPvP.getInstance().getPlayerGroup(event.getEntity());

            if (GameState.groupIsInMatchmakingAndUsingRuleSet(group, this)) {
                if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
