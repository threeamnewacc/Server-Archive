package net.badlion.arenapvp.listener.rulesets;

import net.badlion.arenacommon.event.KitLoadEvent;
import net.badlion.arenacommon.rulesets.KitRuleSet;
import net.badlion.arenacommon.util.ItemStackUtil;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class GodAppleListener implements Listener {


	@EventHandler
	public void onKitLoadEvent(KitLoadEvent event) {
		if (KitRuleSet.godAppleRuleSet == event.getKitRuleSet()) {
			for (ItemStack itemStack : event.getPlayer().getInventory().getContents()) {
				if (itemStack == null) continue;

				// Remove potions from their kit since we have permanent potion effects now
				if (itemStack.getType() == Material.POTION) {
					event.getPlayer().getInventory().remove(itemStack);
				}
			}

			ItemStackUtil.removeUnbreakingFromArmor(event.getPlayer());
		}
	}
}
