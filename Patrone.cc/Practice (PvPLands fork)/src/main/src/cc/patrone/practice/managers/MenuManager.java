package cc.patrone.practice.managers;

import zone.potion.inventory.menu.Menu;
import cc.patrone.practice.PracticePlugin;
import cc.patrone.practice.inventory.menu.impl.ArenaMenu;
import cc.patrone.practice.inventory.menu.impl.DuelMenu;
import cc.patrone.practice.inventory.menu.impl.KitEditorMenu;
import cc.patrone.practice.inventory.menu.impl.PartyEventMenu;
import cc.patrone.practice.inventory.menu.impl.PartyFfaMenu;
import cc.patrone.practice.inventory.menu.impl.PartyListMenu;
import cc.patrone.practice.inventory.menu.impl.PartySplitMenu;
import cc.patrone.practice.inventory.menu.impl.RankedMenu;
import cc.patrone.practice.inventory.menu.impl.SettingsMenu;
import cc.patrone.practice.inventory.menu.impl.SumoArenaMenu;
import cc.patrone.practice.inventory.menu.impl.UnrankedMenu;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.inventory.Inventory;

public class MenuManager {
	private final Map<Class<? extends Menu>, Menu> menus = new HashMap<>();

	public MenuManager(PracticePlugin plugin) {
		registerMenus(
				new RankedMenu(plugin),
				new UnrankedMenu(plugin),
				new DuelMenu(plugin),
				new ArenaMenu(plugin),
				new SumoArenaMenu(plugin),
				new KitEditorMenu(plugin),
				new PartyListMenu(plugin),
				new PartyEventMenu(plugin),
				new PartySplitMenu(plugin),
				new PartyFfaMenu(plugin),
				new SettingsMenu(plugin)
				//  new RedroverMenu()
		);

		plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
			getMenu(RankedMenu.class).update();
			getMenu(UnrankedMenu.class).update();
			getMenu(PartyListMenu.class).update();
		}, 20L, 40L);
	}

	public Menu getMenu(Class<? extends Menu> clazz) {
		return menus.get(clazz);
	}

	public Menu getMatchingMenu(Inventory other) {
		for (Menu menu : menus.values()) {
			if (menu.getInventory().equals(other)) {
				return menu;
			}
		}

		return null;
	}

	public void registerMenus(Menu... menus) {
		for (Menu menu : menus) {
			menu.setup();
			menu.update();
			this.menus.put(menu.getClass(), menu);
		}
	}

	public void unregisterMenu(Menu menu) {
		menus.remove(menu.getClass());
	}

	public void updateAllMenus() {
		for (Menu menu : menus.values()) {
			menu.update();
		}
	}
}
