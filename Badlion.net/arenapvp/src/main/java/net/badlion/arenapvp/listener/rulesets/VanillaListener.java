package net.badlion.arenapvp.listener.rulesets;

import net.badlion.arenacommon.event.KitLoadEvent;
import net.badlion.arenacommon.rulesets.KitRuleSet;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class VanillaListener implements Listener {


	@EventHandler
	public void onKitLoadEvent(KitLoadEvent event) {
		if (KitRuleSet.vanillaRuleSet == event.getKitRuleSet()) {
			for (ItemStack itemStack : event.getPlayer().getInventory().getContents()) {
				if (itemStack == null) continue;

				if (itemStack.getType() == Material.FISHING_ROD) {
					itemStack.setDurability((short) 0);
				}
			}
		}
	}


}
