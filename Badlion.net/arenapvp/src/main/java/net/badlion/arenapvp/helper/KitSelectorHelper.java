package net.badlion.arenapvp.helper;

import net.badlion.arenacommon.kits.Kit;
import net.badlion.arenacommon.kits.KitCommon;
import net.badlion.arenacommon.kits.KitType;
import net.badlion.arenacommon.rulesets.KitRuleSet;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class KitSelectorHelper {

	public static void giveSelectorItems(Player player, KitRuleSet kitRuleSet) {
		PlayerHelper.clearInventory(player);
		player.getInventory().setItem(8, KitSelectorHelper.getDefaultKitSelector(kitRuleSet));
		Map<KitType, List<Kit>> kitTypeListMap = KitCommon.inventories.get(player.getUniqueId());
		if (kitTypeListMap != null) {
			KitType kitType = new KitType(player.getUniqueId().toString(), kitRuleSet.getName());
			List<Kit> kits = kitTypeListMap.get(kitType);
			if (kits != null) {
				for (Kit kit : kits) {
					player.getInventory().setItem(kit.getId(), KitSelectorHelper.getKitSelector(kitRuleSet, kit));
				}
			}
		}
	}

	public static ItemStack getDefaultKitSelector(KitRuleSet kitRuleSet) {
		ItemStack itemStack = ItemStackUtil.createItem(Material.ENCHANTED_BOOK, 1, (short) 0, ChatColor.GREEN + "Default " + kitRuleSet.getName() + " kit.");
		return itemStack;
	}

	public static ItemStack getKitSelector(KitRuleSet kitRuleSet, Kit kit) {
		ItemStack itemStack = ItemStackUtil.createItem(Material.ENCHANTED_BOOK, 1, (short) 0, ChatColor.GOLD + "Load " + kitRuleSet.getName() + " kit: " + (kit.getId() + 1));
		return itemStack;
	}


}
