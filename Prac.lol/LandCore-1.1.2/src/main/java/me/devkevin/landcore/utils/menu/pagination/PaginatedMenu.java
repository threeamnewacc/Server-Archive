package me.devkevin.landcore.utils.menu.pagination;

import lombok.Getter;
import me.devkevin.landcore.utils.menu.Button;
import me.devkevin.landcore.utils.menu.Menu;
import me.devkevin.landcore.utils.message.CC;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

public abstract class PaginatedMenu extends Menu {

	@Getter private int page = 1;

	{
		setUpdateAfterClick(false);
	}

	@Override
	public String getTitle(Player player) {
		return CC.translate(getPrePaginatedTitle(player) + "  &8[" + page + "/" + getPages(player)+ "]");
	}

	public final void modPage(Player player, int mod) {
		page += mod;
		getButtons().clear();
		openMenu(player);
	}

	public final int getPages(Player player) {
		int buttonAmount = getAllPagesButtons(player).size();

		if (buttonAmount == 0) {
			return 1;
		}

		return (int) Math.ceil(buttonAmount / (double) getMaxItemsPerPage(player));
	}

	@Override
	public final Map<Integer, Button> getButtons(Player player) {
		int minIndex = (int) ((double) (page - 1) * getMaxItemsPerPage(player));
		int maxIndex = (int) ((double) (page) * getMaxItemsPerPage(player));

		HashMap<Integer, Button> buttons = new HashMap<>();

		buttons.put(0, new PageButton(-1, this));
		buttons.put(8, new PageButton(1, this));
		/*buttons.put(1, new GlassFill(this));
		buttons.put(2, new GlassFill(this));
		buttons.put(3, new GlassFill(this));
		buttons.put(4, new GlassFill(this));
		buttons.put(5, new GlassFill(this));
		buttons.put(6, new GlassFill(this));
		buttons.put(7, new GlassFill(this));*/


		for (Map.Entry<Integer, Button> entry : getAllPagesButtons(player).entrySet()) {
			int ind = entry.getKey();

			if (ind >= minIndex && ind < maxIndex) {
				ind -= (int) ((double) (getMaxItemsPerPage(player)) * (page - 1)) - 9;
				buttons.put(ind, entry.getValue());
			}
		}

		Map<Integer, Button> global = getGlobalButtons(player);

		if (global != null) {
			for (Map.Entry<Integer, Button> gent : global.entrySet()) {
				buttons.put(gent.getKey(), gent.getValue());
			}
		}

		return buttons;
	}

	public int getMaxItemsPerPage(Player player) {
		return 18;
	}

	public Map<Integer, Button> getGlobalButtons(Player player) {
		return null;
	}

	public abstract String getPrePaginatedTitle(Player player);

	public abstract Map<Integer, Button> getAllPagesButtons(Player player);

	protected void surroundButtons(boolean full, Map buttons, ItemStack itemStack) {
		IntStream.range(0, getSize()).filter(slot -> buttons.get(slot) == null).forEach(slot -> {
			if (slot < 9 || slot > getSize() - 10 || full && (slot % 9 == 0 || (slot+1) % 9 == 0)) {
				buttons.put(slot, new Button() {
					@Override
					public ItemStack getButtonItem(Player player) {
						return itemStack;
					}
				});
			}
		});
	}

}
