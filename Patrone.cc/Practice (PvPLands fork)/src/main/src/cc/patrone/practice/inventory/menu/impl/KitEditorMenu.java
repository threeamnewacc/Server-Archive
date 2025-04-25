package cc.patrone.practice.inventory.menu.impl;

import zone.potion.inventory.menu.Menu;
import cc.patrone.practice.PracticePlugin;
import cc.patrone.practice.kit.Kit;
import cc.patrone.practice.player.PracticeProfile;

public class KitEditorMenu extends Menu {
	private final PracticePlugin plugin;

	public KitEditorMenu(PracticePlugin plugin) {
		super(1, "Select a Kit to Edit");
		this.plugin = plugin;
	}

	@Override
	public void setup() {
	}

	@Override
	public void update() {
		int count = 0;

		for (Kit kit : Kit.values()) {
			setActionableItem(count++, kit.getIcon(), player -> {
				PracticeProfile profile = plugin.getPlayerManager().getProfile(player.getUniqueId());
				plugin.getEditorManager().startEditing(player, profile, kit);
			});
		}
	}
}
