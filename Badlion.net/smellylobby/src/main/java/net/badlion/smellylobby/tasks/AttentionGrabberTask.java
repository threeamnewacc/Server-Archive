package net.badlion.smellylobby.tasks;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class AttentionGrabberTask extends BukkitRunnable {

	public static final String MAXIMUM_ATTENTION = "§6=§b=§6=§b=§6=§b=§6=§b=§6=§b=§6=§b=§6=§b=§6=§b=§6=§b=§6=§b=§6=§b=§6=§b=§6=§b=";
	public static final String REGAIN_ATTENTION = "§b=§6=§b=§6=§b=§6=§b=§6=§b=§6=§b=§6=§b=§6=§b=§6=§b=§6=§b=§6=§b=§6=§b=§6=§b=§6=";

	private Inventory inventory;

	private ItemStack itemStack;

	private boolean attentionCaptured = true;

	public AttentionGrabberTask(Inventory inventory, int slot) {
		this.inventory = inventory;

		this.itemStack = inventory.getItem(slot);
	}

	@Override
	public void run() {
		if (this.itemStack == null) {
			this.cancel();

			throw new RuntimeException("ItemStack null for attention grabber task: " + this.inventory.getName());
		}

		ItemMeta itemMeta = this.itemStack.getItemMeta();
		List<String> lore = itemMeta.getLore();

		if (this.attentionCaptured) {
			lore.set(0, AttentionGrabberTask.MAXIMUM_ATTENTION);
			lore.set(lore.size() - 1, AttentionGrabberTask.MAXIMUM_ATTENTION);
		} else {
			lore.set(0, AttentionGrabberTask.REGAIN_ATTENTION);
			lore.set(lore.size() - 1, AttentionGrabberTask.REGAIN_ATTENTION);
		}

		itemMeta.setLore(lore);
		this.itemStack.setItemMeta(itemMeta);

		for (HumanEntity humanEntity : this.inventory.getViewers()) {
			((Player) humanEntity).updateInventory();
		}

		this.attentionCaptured = !this.attentionCaptured;
	}

}
