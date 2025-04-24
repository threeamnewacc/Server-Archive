package me.devkevin.landcore.utils.menu.pagination;

import lombok.AllArgsConstructor;
import me.devkevin.landcore.utils.item.ItemBuilder;
import me.devkevin.landcore.utils.menu.Button;
import me.devkevin.landcore.utils.message.CC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public class PageButton extends Button {

	private int mod;
	private PaginatedMenu menu;

	@Override
	public ItemStack getButtonItem(Player player) {
		ItemBuilder item = new ItemBuilder(Material.LEVER);

		if (this.hasNext(player)) {
			if (mod > 0) {
				item.durability((byte) 13);
			} else {
				item.durability((byte) 14);
			}
			item.name(CC.translate(this.mod > 0 ? "&aNext page" : "&cPrevious page"));
		} else {
			item.durability((byte) 7);
			item.name(CC.translate((this.mod > 0 ? "&7Last page" : "&7First page")));
		}

		return item.build();
	}

	@Override
	public void clicked(Player player, int i, ClickType clickType, int hb) {
		if (clickType == ClickType.RIGHT) {
			new ViewAllPagesMenu(this.menu).openMenu(player);
			playNeutral(player);
		} else {
			if (hasNext(player)) {
				this.menu.modPage(player, this.mod);
				Button.playNeutral(player);
			} else {
				Button.playFail(player);
			}
		}
	}

	private boolean hasNext(Player player) {
		int pg = this.menu.getPage() + this.mod;
		return pg > 0 && this.menu.getPages(player) >= pg;
	}

}
