package cc.patrone.practice.inventory.menu.impl;

import zone.potion.inventory.menu.Menu;
import zone.potion.utils.item.ItemBuilder;
import zone.potion.utils.message.CC;
import cc.patrone.practice.kit.Kit;
import cc.patrone.practice.kit.PlayerKit;
import cc.patrone.practice.player.PracticeProfile;
import cc.patrone.practice.wrapper.CustomKitWrapper;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class KitLayoutMenu extends Menu {
	private final PracticeProfile profile;

	public KitLayoutMenu(PracticeProfile profile) {
		super(4, "Select a Kit Management Action");
		this.profile = profile;
	}

	@Override
	public void setup() {
	}

	@Override
	public void update() {
		clear();

		Kit editingKit = profile.getEditorData().getKit();
		String editingKitName = editingKit.getName();
		CustomKitWrapper customKits = profile.getKitWrapper(editingKit);

		for (int i = 0; i < 8; i++) {
			final int index = i + 1;
			ItemStack kitSaver = new ItemBuilder(Material.INK_SACK)
					.durability(10)
					.name(CC.PRIMARY + "Save kit " + CC.SECONDARY + "#" + index)
					.build();

			setActionableItem(i, kitSaver, player -> {
				PlayerKit kit = new PlayerKit(editingKitName);
				String customKitName = CC.SECONDARY + "Custom " + editingKit.getName() + " Kit #" + index;

				kit.setArmor(player.getInventory().getArmorContents());
				kit.setContents(player.getInventory().getContents());
				kit.setCustomName(customKitName);

				customKits.setKit(index, kit);

				player.closeInventory();

				update();

				player.sendMessage(CC.GREEN + "Saved kit " + index + ".");
			});
		}

		if (profile.hasCustomKits(editingKit)) {
			for (Map.Entry<Integer, PlayerKit> entry : customKits.getKits().entrySet()) {
				int index = entry.getKey();
				PlayerKit kit = entry.getValue();

				ItemStack kitSaver = new ItemBuilder(Material.INK_SACK)
						.durability(10)
						.name(CC.PRIMARY + "Save kit " + CC.SECONDARY + "#" + index)
						.build();

				ItemStack kitLoader = new ItemBuilder(Material.INK_SACK)
						.durability(8)
						.name(CC.PRIMARY + "Load kit " + CC.SECONDARY + "#" + index)
						.build();

				ItemStack kitRenamer = new ItemBuilder(Material.NAME_TAG)
						.name(CC.PRIMARY + "Rename kit " + CC.SECONDARY + "#" + index)
						.build();

				ItemStack kitDeleter = new ItemBuilder(Material.INK_SACK)
						.durability(1)
						.name(CC.PRIMARY + "Delete kit " + CC.SECONDARY + "#" + index)
						.build();

				setActionableItem(index - 1, kitSaver, player -> {
					String customKitName = kit.getCustomName() == null
							? CC.SECONDARY + "Custom " + editingKit.getName() + " Kit #" + index
							: kit.getCustomName();

					kit.setArmor(player.getInventory().getArmorContents());
					kit.setContents(player.getInventory().getContents());
					kit.setCustomName(customKitName);

					customKits.setKit(index, kit);

					player.closeInventory();

					update();

					player.sendMessage(CC.GREEN + "Saved kit " + index + ".");
				});

				setActionableItem(index + 8, kitLoader, player -> {
					player.closeInventory();
					kit.apply(player);
				});

				setActionableItem(index + 17, kitRenamer, player -> {
					profile.getEditorData().setCustomKit(kit);
					profile.getEditorData().setRenaming(true);
					player.closeInventory();
					player.sendMessage(CC.PRIMARY + "Type a name for the kit.\n" +
							"You can use chat colors, too; for example, &6Cool Kit turns into " + CC.GOLD + "Cool Kit" + CC.PRIMARY + ".");
				});

				setActionableItem(index + 26, kitDeleter, player -> {
					customKits.removeKit(index);

					update();

					player.sendMessage(CC.RED + "Removed kit " + index + ".");
				});
			}
		}
	}
}
