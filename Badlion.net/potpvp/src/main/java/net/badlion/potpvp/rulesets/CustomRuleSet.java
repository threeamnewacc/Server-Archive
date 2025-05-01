package net.badlion.potpvp.rulesets;

import net.badlion.potpvp.Group;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.managers.ArenaManager;
import net.badlion.potpvp.states.matchmaking.GameState;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class CustomRuleSet extends KitRuleSet {

	public CustomRuleSet(int id, String name) {
		super(id, name, ArenaManager.ArenaType.PEARL, true, true);

		this.is1_9Compatible = true;

		// Enable in duels
		this.enabledInDuels = true;

		// Initialize valid enchants
		this.validEnchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
		this.validEnchantments.put(Enchantment.PROTECTION_FALL, 4);
		this.validEnchantments.put(Enchantment.DAMAGE_ALL, 5);
		this.validEnchantments.put(Enchantment.KNOCKBACK, 2);
		this.validEnchantments.put(Enchantment.FIRE_ASPECT, 2);
		this.validEnchantments.put(Enchantment.DURABILITY, 3);
		this.validEnchantments.put(Enchantment.ARROW_DAMAGE, 5);
		this.validEnchantments.put(Enchantment.ARROW_KNOCKBACK, 2);
		this.validEnchantments.put(Enchantment.ARROW_FIRE, 1);
		this.validEnchantments.put(Enchantment.ARROW_INFINITE, 1);
	}

	// Not putting this into matchmaking listener to be safe,
	// not sure if soup is available in other kits
	@EventHandler
	public void onPlayerDrinkSoupEvent(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Group group = PotPvP.getInstance().getPlayerGroup(player);

		if (GameState.groupIsInMatchmakingAndUsingRuleSet(group, this)) {
			if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if (event.getItem() != null && event.getItem().getType() == Material.MUSHROOM_SOUP) {
					if (!player.isDead()) {
						if (player.getHealth() < 20) {
							if (player.getHealth() + 7 < 20) {
								player.setHealth(player.getHealth() + 7);
								player.getItemInHand().setType(Material.BOWL);
								player.getItemInHand().setItemMeta(null);
							} else {
								player.setHealth(20);
								player.getItemInHand().setType(Material.BOWL);
								player.getItemInHand().setItemMeta(null);
							}
						}
					}
				}
			}
		}
	}

}
