package cc.patrone.practice.editor;

import cc.patrone.practice.PracticePlugin;
import cc.patrone.practice.inventory.menu.impl.KitLayoutMenu;
import cc.patrone.practice.kit.Kit;
import cc.patrone.practice.kit.PlayerKit;
import cc.patrone.practice.player.PracticeProfile;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditorData {
	private final Kit kit;
	private PlayerKit customKit;
	private KitLayoutMenu menu;
	private boolean renaming;

	public EditorData(Kit kit, PlayerKit customKit) {
		this.kit = kit;
		this.customKit = customKit;
	}

	public void unregisterMenu(PracticePlugin plugin) {
		if (menu != null) {
			plugin.getMenuManager().unregisterMenu(menu);
		}
	}

	public KitLayoutMenu registerMenu(PracticeProfile profile, PracticePlugin plugin) {
		KitLayoutMenu menu = new KitLayoutMenu(profile);
		this.menu = menu;
		plugin.getMenuManager().registerMenus(menu);
		return menu;
	}
}
