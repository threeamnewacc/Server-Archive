package club.minemen.events.event.impl;

import club.minemen.core.util.finalutil.CC;
import club.minemen.core.util.finalutil.ItemUtil;
import club.minemen.events.event.events.CaptureEvent;
import com.massivecraft.factions.FactionPlayer;
import java.util.concurrent.TimeUnit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class KothEvent extends CaptureEvent {

	private static transient final ItemStack KOTH_KEY = ItemUtil.createItem(Material.TRIPWIRE_HOOK, CC.PINK + "KOTH Key");

	public KothEvent() {
		super(null, null, TimeUnit.SECONDS, 15);
	}

	public KothEvent(String name) {
		super(name, null, TimeUnit.SECONDS, 15);
	}

	@Override
	public void onEnd(FactionPlayer winner) {
		super.onEnd(winner);

		Player player = winner.getPlayer();
		if (player == null || !player.isOnline()) {
			return;
		}

		if (player.getInventory().firstEmpty() == -1) {
			if (player.getEnderChest().firstEmpty() == -1) {
				player.sendMessage(CC.RED + "You didn't receive your KOTH key due to your inventory and " +
				                   "ender chest being empty.");
			} else {
				player.sendMessage(CC.GOLD + "A KOTH key has been added into your ender chest.");
				player.getEnderChest().addItem(KOTH_KEY.clone());
			}
		} else {
			player.sendMessage(CC.GOLD + "A KOTH key has been added into your inventory.");
			player.getInventory().addItem(KOTH_KEY.clone());
		}
	}

}
