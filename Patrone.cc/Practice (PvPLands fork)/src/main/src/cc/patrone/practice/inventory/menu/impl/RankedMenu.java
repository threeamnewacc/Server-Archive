package cc.patrone.practice.inventory.menu.impl;

import zone.potion.inventory.menu.Menu;
import zone.potion.utils.item.ItemBuilder;
import zone.potion.utils.message.CC;
import cc.patrone.practice.PracticePlugin;
import cc.patrone.practice.kit.Kit;
import org.bukkit.inventory.ItemStack;

public class RankedMenu extends Menu {
	private final PracticePlugin plugin;

	public RankedMenu(PracticePlugin plugin) {
		super(1, "Select a Ranked Kit");
		this.plugin = plugin;
	}

	@Override
	public void setup() {
	}

	@Override
	public void update() {
		int count = 0;

		for (Kit kit : Kit.values()) {
			if (!kit.isRanked()) {
				continue;
			}

			int queueCount = plugin.getQueueManager().playersQueued(kit.getName(), true, false);
			int fighting = plugin.getMatchManager().playersFighting(kit.getName(), true);

			ItemStack icon = ItemBuilder
					.from(kit.getIcon())
					.lore(CC.PRIMARY + "Playing: " + CC.SECONDARY + fighting,
							CC.PRIMARY + "Queued: " + CC.SECONDARY + queueCount)
					.build();

			setActionableItem(count++, icon, player -> plugin.getQueueManager().enqueueTeam(player, plugin.getPlayerManager().getProfile(player.getUniqueId()), kit, true));
		}
	}
}
