package net.badlion.potpvp.listeners;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.potpvp.Group;
import net.badlion.potpvp.GroupStateMachine;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.helpers.PartyHelper;
import net.badlion.smellyinventory.SmellyInventory;
import net.badlion.potpvp.inventories.duel.RedRoverChooseFighterInventory;
import net.badlion.potpvp.matchmaking.Match;
import net.badlion.potpvp.matchmaking.RedRoverMatch;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PartyDeadListener extends BukkitUtil.Listener {

    @EventHandler(ignoreCancelled=true)
    public void playerInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Group group = PotPvP.getInstance().getPlayerGroup(player);
        if (!player.spigot().getCollidesWithEntities() && GroupStateMachine.matchMakingState.contains(group)) {
            ItemStack item = event.getItem();

            event.setCancelled(true);

            if (item == null || item.getType().equals(Material.AIR)) return;

            if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                switch (player.getInventory().getHeldItemSlot()) {
                   /* case 2: // Show player inventories
                        PartyPlayerInventoriesInventory.openViewPartyPlayerInventoriesInventory(player, group);
                        break;*/
                    case 8: // Leave party
	                    Group newGroup = new Group(player, true);

	                    // Does all the work
	                    Gberry.log("PARTY", "Player left party: " + player.getName());
	                    PartyHelper.handleLeave(player, group.getParty(), true);

	                    // Call this after because it calls party.removePlayer()
	                    PotPvP.getInstance().updatePlayerGroup(player, newGroup);
                        break;
                }
            }
        }
    }

	@EventHandler
	public void redRoverPlayerInteractEvent(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Group group = PotPvP.getInstance().getPlayerGroup(player);
		if (GroupStateMachine.regularMatchState.contains(group)) {
			Match match = GroupStateMachine.regularMatchState.getMatchFromGroup(group);
			if (match instanceof RedRoverMatch) {
				if (((RedRoverMatch) match).isSelectingFighter(player)) {
					ItemStack item = event.getItem();

					event.setCancelled(true);

					if (item == null || item.getType().equals(Material.AIR)) return;

					if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
						switch (player.getInventory().getHeldItemSlot()) {
							case 0: // Open choose fighter inventory
								// Figure out which one we need to open
								if (item.equals(RedRoverChooseFighterInventory.getChooseFirstFighterItem())) {
									RedRoverChooseFighterInventory.openSelectFirstFighterInventory(player, group);
								} else {
									RedRoverChooseFighterInventory.openSelectEnemyFighterInventory(player, group);
								}
								break;
						}
					}
				}
			}
		}
	}

    @EventHandler
    public void playerInteractEntityEvent(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Group group = PotPvP.getInstance().getPlayerGroup(player);
        if (!player.spigot().getCollidesWithEntities() &&
		        (GroupStateMachine.matchMakingState.contains(group) || GroupStateMachine.spectatorState.contains(group))) {
            if (event.getRightClicked() instanceof Player) {
                BukkitUtil.openInventory(player, PartyHelper.createPlayerInventory(null, (Player) event.getRightClicked()));
            }
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void inventoryClickEvent(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        Group group = PotPvP.getInstance().getPlayerGroup(player);
        if (!player.spigot().getCollidesWithEntities() && GroupStateMachine.matchMakingState.contains(group)) {
            final ItemStack item = event.getCurrentItem();

            if (item == null || item.getType().equals(Material.AIR)) return;

            event.setCancelled(true);

            if (PartyHelper.isPlayerInventory(event.getView().getTopInventory())) {
                if (SmellyInventory.isCloseInventoryItem(item)) {
                    BukkitUtil.closeInventory(player);
                }
            }

            // Update inventory just because edge cases
            player.updateInventory();
        }
    }

    @EventHandler
    public void inventoryDragEvent(InventoryDragEvent event) {
        Player player = (Player) event.getWhoClicked();
        Group group = PotPvP.getInstance().getPlayerGroup(player);
        if (!player.spigot().getCollidesWithEntities() && GroupStateMachine.matchMakingState.contains(group)) {
            if (PartyHelper.isPlayerInventory(event.getView().getTopInventory())) { // TODO: ADD ALL PARTY DEAD STATE INVENTORIES TO THIS, CRUCIAL
                for (Integer rawSlot : event.getRawSlots()) {
                    if (rawSlot < 54) {
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }
    }

}
