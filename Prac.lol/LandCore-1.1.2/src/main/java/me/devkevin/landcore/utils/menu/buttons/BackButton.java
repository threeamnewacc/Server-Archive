package me.devkevin.landcore.utils.menu.buttons;

import lombok.AllArgsConstructor;
import me.devkevin.landcore.utils.item.ItemBuilder;
import me.devkevin.landcore.utils.menu.Button;
import me.devkevin.landcore.utils.menu.Menu;
import me.devkevin.landcore.utils.message.CC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public class BackButton extends Button {

	private Menu back;

	@Override
	public ItemStack getButtonItem(Player player) {
		ItemBuilder item =  new ItemBuilder(Material.ARROW).name(CC.translate("&cGo back"));
		return item.build();
	}

	@Override
	public void clicked(Player player, int i, ClickType clickType, int hb) {
		Button.playNeutral(player);

		this.back.openMenu(player);
	}

}
