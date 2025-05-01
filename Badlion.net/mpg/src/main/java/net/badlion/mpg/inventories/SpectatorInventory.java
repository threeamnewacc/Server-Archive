package net.badlion.mpg.inventories;

import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.mpg.MPG;
import net.badlion.smellyinventory.SmellyInventory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SpectatorInventory {

    private static SmellyInventory smellyInventory;

    public static void initialize() {
        SpectatorInventory.smellyInventory = new SmellyInventory(new SpectatePlayerInventoryScreenHandler(), 18,
                                                                   ChatColor.BOLD + ChatColor.AQUA.toString() + "Fly Speed");

        for (int i = 1; i <= 4; i++) {
	        SpectatorInventory.smellyInventory.getMainInventory().addItem(
			        ItemStackUtil.createItem(Material.POTION, 1, (short) 8194, ChatColor.GREEN + "Fly Speed " + i));
        }

	    SpectatorInventory.smellyInventory.getMainInventory().setItem(8,
			    ItemStackUtil.createItem(Material.MILK_BUCKET, ChatColor.GREEN + "Normal Fly Speed"));
    }

	public static void givePlayerSpectatorItems(Player player) {
		player.getInventory().addItem(new ItemStack(Material.COMPASS));

		if (MPG.getInstance().getBooleanOption(MPG.ConfigFlag.USE_SKULL_SPECTATOR_INVENTORY)) {
			player.getInventory().addItem(ItemStackUtil.createItem(Material.WATCH, ChatColor.AQUA + "Alive Players"));
		}

		ItemStack speedItem = ItemStackUtil.SWIFTNESS_SPLASH;
		ItemMeta speedItemMeta = speedItem.getItemMeta();
		speedItemMeta.setDisplayName(ChatColor.AQUA + "Fly Speed");
		speedItem.setItemMeta(speedItemMeta);

		player.getInventory().setItem(4, speedItem);

		if (MPG.USES_MATCHMAKING) {
			player.getInventory().setItem(8, ItemStackUtil.createItem(Material.NAME_TAG, ChatColor.AQUA + "Return to Lobby"));
		} else {
			player.getInventory().setItem(8, ItemStackUtil.createItem(Material.REDSTONE, ChatColor.AQUA + "Leave Spectate Mode"));
		}
	}

    public static void openSpeedPlayerInventory(Player player) {
        BukkitUtil.openInventory(player, SpectatorInventory.smellyInventory.getMainInventory());
    }

    private static class SpectatePlayerInventoryScreenHandler implements SmellyInventory.SmellyInventoryHandler {

        @Override
        public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {
            if (item.getType() == Material.MILK_BUCKET) {
                player.removePotionEffect(PotionEffectType.SPEED);
	            player.setFlySpeed(0.1F);

                player.sendMessage(ChatColor.GREEN + "Normal fly speed activated.");
            } else if (slot >= 0 && slot <= 3) {
                player.removePotionEffect(PotionEffectType.SPEED);
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, slot));
	            player.setFlySpeed(0.1F + (slot * 0.05F));

                player.sendMessage(ChatColor.GREEN + "Fly speed " + (slot + 1) + " activated.");
            }

            BukkitUtil.closeInventory(player);
        }

        @Override
        public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {

        }

    }

}
