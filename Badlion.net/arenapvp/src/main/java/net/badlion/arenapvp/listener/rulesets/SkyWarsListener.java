package net.badlion.arenapvp.listener.rulesets;

import org.bukkit.event.Listener;

public class SkyWarsListener implements Listener {

    /*
    @EventHandler
	public void onKitLoadEvent(KitLoadEvent event) {
		if (this == event.getKitRuleSet()) {
			for (ItemStack itemStack : event.getPlayer().getInventory().getContents()) {
				if (itemStack == null) continue;

				if (itemStack.getType() == Material.FISHING_ROD) {
					itemStack.setDurability((short) 0);
				}
			}
		}
	}

    @EventHandler(priority = EventPriority.LAST)
    public void onBlockBreakEvent(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Group group = PotPvP.getInstance().getPlayerGroup(player);

        if (GameState.groupIsInMatchmakingAndUsingRuleSet(group, this)) {
	        if (!this.canBreakBlock(event.getBlock())) return;

            if (this.blacklistedBlocks.contains(event.getBlock().getType())) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You're not allowed to break this block!");
            } else {
                event.setCancelled(false);
            }
        }
    }

    @EventHandler(priority = EventPriority.LAST)
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Group group = PotPvP.getInstance().getPlayerGroup(player);

        if (GameState.groupIsInMatchmakingAndUsingRuleSet(group, this)) {
	        if (!this.canBreakBlock(event.getBlock())) return;

            event.setCancelled(false);

            Game game = GameState.getGroupGame(group);
            if (game.getArena() instanceof SkyWarsArena) {
	            // Don't let them place blocks on the walls (red wool)
	            if (event.getBlockAgainst().getType() == Material.WOOL && event.getBlockAgainst().getData() == 14) {
		            player.sendMessage(ChatColor.RED + "You can't place a block here!");
		            event.setCancelled(true);
		            return;
	            }

                ((SkyWarsArena) game.getArena()).addToDestroy(event.getBlock().getLocation());
            }
        }
    }
    */
}
