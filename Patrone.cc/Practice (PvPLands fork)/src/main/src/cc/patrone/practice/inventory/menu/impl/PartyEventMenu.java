package cc.patrone.practice.inventory.menu.impl;

import zone.potion.inventory.menu.Menu;
import zone.potion.inventory.menu.action.Action;
import zone.potion.utils.item.ItemBuilder;
import zone.potion.utils.message.CC;
import cc.patrone.practice.PracticePlugin;
import org.bukkit.Material;

public class PartyEventMenu extends Menu {
	private final PracticePlugin plugin;

	public PartyEventMenu(PracticePlugin plugin) {
		super(1, "Select a Party Event");
		this.plugin = plugin;
	}

	private Action getAction(Class<? extends Menu> clazz) {
		return player -> {
			player.closeInventory();
			plugin.getMenuManager().getMenu(clazz).open(player);
		};
	}

	@Override
	public void setup() {
		setActionableItem(3, new ItemBuilder(Material.LEASH)
				.name(CC.PRIMARY + "Team Split Fight")
				.lore(CC.SECONDARY + "Start a team fight with party members split randomly.")
				.build(), getAction(PartySplitMenu.class));

		setActionableItem(5, new ItemBuilder(Material.NETHER_STAR)
				.name(CC.PRIMARY + "FFA Fight")
				.lore(CC.SECONDARY + "Start a free-for-all fight with your party members.")
				.build(), getAction(PartyFfaMenu.class));
	}

	@Override
	public void update() {
	}
}
