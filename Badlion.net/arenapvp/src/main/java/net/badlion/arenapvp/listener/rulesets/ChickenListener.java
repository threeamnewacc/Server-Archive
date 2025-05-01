package net.badlion.arenapvp.listener.rulesets;

import org.bukkit.event.Listener;

public class ChickenListener implements Listener {

    /*
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
    */
}
