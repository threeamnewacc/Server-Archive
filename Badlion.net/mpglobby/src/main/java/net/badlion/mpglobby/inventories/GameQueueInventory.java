package net.badlion.mpglobby.inventories;

import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.mpglobby.MPGLobby;
import net.badlion.mpglobby.QueueType;
import net.badlion.smellyinventory.SmellyInventory;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

public class GameQueueInventory {

    private static SmellyInventory smellyInventory;

    public static void initialize() {
        GameQueueInventory.smellyInventory = new SmellyInventory(new GameQueueInventoryScreenHandler(), 18,
                                                                   ChatColor.BOLD + ChatColor.AQUA.toString() + MPGLobby.QUEUE_INVENTORY_NAME);

	    // Create items for all the queue types
	    int slot = 0;
	    for (QueueType queueType : QueueType.values()) {
		    ItemStack item = ItemStackUtil.createItem(queueType.getItemMaterial(), ChatColor.GREEN + queueType.getName());

		    GameQueueInventory.smellyInventory.getMainInventory().addItem(item);

		    // Save reference to inventory item in queue type enum
		    queueType.setItem(GameQueueInventory.smellyInventory.getMainInventory().getItem(slot));

		    // Increment slot pointer
		    slot++;
	    }
    }

    public static void openGameQueueInventory(Player player) {
        BukkitUtil.openInventory(player, GameQueueInventory.smellyInventory.getMainInventory());
    }

    private static class GameQueueInventoryScreenHandler implements SmellyInventory.SmellyInventoryHandler {

        @Override
        public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {
            // Find the queue this item corresponds to
	        for (QueueType queueType : QueueType.values()) {
		        if (item.getItemMeta().getDisplayName().equals(queueType.getItem().getItemMeta().getDisplayName())) {
			        MPGLobby.getInstance().joinQueue(player, queueType);
			        BukkitUtil.closeInventory(player);
			        return;
		        }
	        }

	        player.sendMessage(ChatColor.RED + "Cannot join this queue due to an error, please report this as a bug!");
	        BukkitUtil.closeInventory(player);

	        // Throw exception if the item didn't have a corresponding queue
	        throw new RuntimeException("Queue for item " + item.getItemMeta().getDisplayName() + " not found!");
        }

        @Override
        public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {

        }

    }

}
