package net.badlion.potpvp.managers;

import net.badlion.gberry.utils.BukkitUtil;

import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.potpvp.Group;
import net.badlion.potpvp.GroupStateMachine;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.helpers.KitHelper;
import net.badlion.potpvp.matchmaking.MatchMakingService;
import net.badlion.potpvp.tasks.MatchInventoryRemovalTask;
import net.badlion.smellyinventory.SmellyInventory;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class MatchMakingManager extends BukkitUtil.Listener {

	private static Map<Player, ItemStack[]> opponentArmor = new HashMap<>();
	private static Map<Player, ItemStack[]> opponentExtraItems = new HashMap<>();
	private static Map<Player, ItemStack[]> opponentContents = new HashMap<>();

	private static Map<Group, MatchMakingService> playerMatchMakingServiceHashMap = new HashMap<>();

    public static void addToMatchMaking(Group group, MatchMakingService service) {
        MatchMakingManager.playerMatchMakingServiceHashMap.put(group, service);
    }

    public static MatchMakingService getMatchMakingService(Group group) {
        return MatchMakingManager.playerMatchMakingServiceHashMap.get(group);
    }

    public static boolean removeFromMatchMaking(Group group) {
        MatchMakingService service = MatchMakingManager.playerMatchMakingServiceHashMap.remove(group);
        return service != null && service.removeGroup(group);
    }

	public static void openOpponentInventory(Player player) {
		ItemStack[] opponentArmor = MatchMakingManager.opponentArmor.get(player);
		if (opponentArmor != null) {
			// Can they open the opponent inventory in their current state?
			Group group = PotPvP.getInstance().getPlayerGroup(player);
			if (!GroupStateMachine.regularMatchState.contains(group)) {
				Inventory inventory = PotPvP.getInstance().getServer().createInventory(null, 54,
						ChatColor.BOLD + ChatColor.AQUA.toString() + "Opponent's Inventory");

				// Fill inventory
				KitHelper.fillInventoryWithContents(inventory, opponentArmor, MatchMakingManager.opponentContents.get(player), MatchMakingManager.opponentExtraItems.get(player));

				inventory.setItem(53, SmellyInventory.getCloseInventoryItem());

				// Open inventory
				BukkitUtil.openInventory(player, inventory);
			} else {
				player.sendMessage(ChatColor.RED + "Cannot open inventory while in match");
			}
		} else {
			player.sendMessage(ChatColor.RED + "Opponent's inventory expired. (1 minute elapsed)");
		}
	}

	public static void saveOpponentInventory(Player player, ItemStack[] opponentArmor, ItemStack[] opponentContents, ItemStack[] extraItems) {
		// Save contents instead of making the inventory and saving the inventory
		// to save a memory and processing power
		MatchMakingManager.opponentArmor.put(player, opponentArmor);
		MatchMakingManager.opponentContents.put(player, opponentContents);
		MatchMakingManager.opponentExtraItems.put(player, extraItems);

		// Remove the inventory after a minute
		BukkitUtil.runTaskLater(new MatchInventoryRemovalTask(player), 1200L);
	}

	public static void removeOpponentInventory(Player player) {
		MatchMakingManager.opponentArmor.remove(player);
		MatchMakingManager.opponentContents.remove(player);
		MatchMakingManager.opponentExtraItems.remove(player);
	}

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Group group = PotPvP.getInstance().getPlayerGroup(event.getPlayer());
        MatchMakingManager.removeFromMatchMaking(group);
    }

}
