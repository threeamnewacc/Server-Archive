package cc.patrone.practice.inventory.menu.impl;

import zone.potion.inventory.menu.Menu;
import zone.potion.utils.item.ItemBuilder;
import zone.potion.utils.message.CC;
import cc.patrone.practice.PracticePlugin;
import cc.patrone.practice.party.Party;
import cc.patrone.practice.player.PracticeProfile;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class PartyListMenu extends Menu {
	private final PracticePlugin plugin;

	public PartyListMenu(PracticePlugin plugin) {
		super(4, "Select a Party to Duel");
		this.plugin = plugin;
	}

	@Override
	public void setup() {
	}

	@Override
	public void update() {
		if (getInventory().getContents().length != 0) {
			clear();
		}

		int count = 0;

		for (Party party : plugin.getPartyManager().getParties()) {
			List<String> members = new ArrayList<>();

			for (PracticeProfile member : party.getMembers()) {
				members.add(CC.PRIMARY + member.getName());
			}

			ItemStack skull = new ItemBuilder(Material.SKULL_ITEM)
					.name(CC.PRIMARY + party.getLeader().getName() + CC.SECONDARY + "'s Party "
							+ CC.ACCENT + "(" + party.getMembers().size() + ")")
					.lore(members.toArray(new String[0]))
					.build();

			setActionableItem(count++, skull, player -> {
				player.closeInventory();
				player.performCommand("duel " + party.getLeader().getName());
			});
		}
	}
}
