package me.devkevin.landcore.utils.menu.menus;

import me.devkevin.landcore.utils.item.ItemBuilder;
import me.devkevin.landcore.utils.menu.Button;
import me.devkevin.landcore.utils.menu.Menu;
import me.devkevin.landcore.utils.menu.TypeCallback;
import me.devkevin.landcore.utils.menu.buttons.ConfirmationButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class ConfirmMenu extends Menu {

	private String title;
	private TypeCallback<Boolean> response;
	private boolean closeAfterResponse;
	private Button[] centerButtons;

	public ConfirmMenu(String title, TypeCallback<Boolean> response, boolean closeAfter, Button... centerButtons) {
		this.title = title;
		this.response = response;
		this.closeAfterResponse = closeAfter;
		this.centerButtons = centerButtons;
	}

	@Override
	public Map<Integer, Button> getButtons(Player player) {
		HashMap<Integer, Button> buttons = new HashMap<>();

		Integer[] cPanes = new Integer[] { 0,1,2,9,11,18,19,20};
		for (int i : cPanes) {
			buttons.put(i, new Button() {
				@Override
				public ItemStack getButtonItem(Player player) {
					return new ItemBuilder(Material.STAINED_GLASS_PANE).name(" ").durability((byte) 5).build();
				}
			});
			buttons.put(10, new ConfirmationButton(true, response, closeAfterResponse));
		}

		Integer[] dPanes = new Integer[] { 6,7,8,15,17,24,25,26};
		for (int i : dPanes) {
			buttons.put(i, new Button() {
				@Override
				public ItemStack getButtonItem(Player player) {
					return new ItemBuilder(Material.STAINED_GLASS_PANE).name(" ").durability((byte) 14).build();
				}
			});
			buttons.put(16, new ConfirmationButton(false, response, closeAfterResponse));
		}

		if (centerButtons != null) {
			for (int i = 0; i < centerButtons.length; i++) {
				if (centerButtons[i] != null) {
					buttons.put(getSlot(4, i), centerButtons[i]);
				}
			}
		}

		return buttons;
	}

	@Override
	public String getTitle(Player player) {
		return title;
	}

}
