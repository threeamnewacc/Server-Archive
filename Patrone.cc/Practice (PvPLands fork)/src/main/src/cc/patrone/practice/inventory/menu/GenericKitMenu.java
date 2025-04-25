package cc.patrone.practice.inventory.menu;

import zone.potion.inventory.menu.Menu;
import zone.potion.inventory.menu.action.Action;
import cc.patrone.practice.PracticePlugin;
import cc.patrone.practice.kit.Kit;

public abstract class GenericKitMenu extends Menu {
	protected final PracticePlugin plugin;

	protected GenericKitMenu(String name, PracticePlugin plugin) {
		super(1, name);
		this.plugin = plugin;
	}

	@Override
	public void update() {
		int count = 0;

		for (Kit kit : Kit.values()) {
			setActionableItem(count++, kit.getIcon(), getAction(kit));
		}
	}

	protected abstract Action getAction(Kit kit);
}
