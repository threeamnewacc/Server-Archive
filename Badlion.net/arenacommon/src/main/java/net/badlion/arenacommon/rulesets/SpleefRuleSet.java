package net.badlion.arenacommon.rulesets;

import net.badlion.arenacommon.ArenaCommon;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SpleefRuleSet extends KitRuleSet {

    public SpleefRuleSet(int id, String name) {
        super(id, name, new ItemStack(Material.DIAMOND_SPADE), ArenaCommon.ArenaType.SPLEEF, KnockbackType.NON_SPEED, false, false);

	    this.enabledInEvents = false;

	    // Create default inventory kit
	    this.defaultInventoryKit[0] = new ItemStack(Material.DIAMOND_SPADE);
	    this.defaultInventoryKit[0].addUnsafeEnchantment(Enchantment.DIG_SPEED, 10);

	    this.defaultInventoryKit[1] = new ItemStack(Material.BOW);
	    this.defaultInventoryKit[1].addEnchantment(Enchantment.ARROW_INFINITE, 1);

	    this.defaultInventoryKit[9] = new ItemStack(Material.ARROW);
    }

	@Override
	public void sendMessages(Player player) {
		player.sendMessage(ChatColor.DARK_AQUA + "Dig blocks with the shovel to make your opponents fall into the void!");
	}

}
