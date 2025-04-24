package club.minemen.practice.util;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public final class PlayerUtil {

	private PlayerUtil() {
	}

	public static void setFirstSlotOfType(Player player, Material type, ItemStack itemStack) {
		for (int i = 0; i < player.getInventory().getContents().length; i++) {
			ItemStack itemStack1 = player.getInventory().getContents()[i];
			if (itemStack1 == null || itemStack1.getType() == type || itemStack1.getType() == Material.AIR) {
				player.getInventory().setItem(i, itemStack);
				break;
			}
		}
	}

	public static void clearPlayer(Player player) {
		player.setHealth(20.0D);
		player.setFoodLevel(20);
		player.setSaturation(12.8F);
		player.setMaximumNoDamageTicks(20);
		player.setFireTicks(0);
		player.setFallDistance(0.0F);
		player.setLevel(0);
		player.setExp(0.0F);
		player.setWalkSpeed(0.2F);
		player.getInventory().setHeldItemSlot(0);
		player.setAllowFlight(false);
		player.getInventory().clear();
		player.getInventory().setArmorContents(null);
		player.closeInventory();
		player.setGameMode(GameMode.SURVIVAL);
		player.getActivePotionEffects().stream().map(PotionEffect::getType).forEach(player::removePotionEffect);
		((CraftPlayer) player).getHandle().getDataWatcher().watch(9, (byte) 0);
		player.updateInventory();
	}

	public static void sendMessage(String message, Player... players) {
		for (Player player : players) {
			player.sendMessage(message);
		}
	}
}
