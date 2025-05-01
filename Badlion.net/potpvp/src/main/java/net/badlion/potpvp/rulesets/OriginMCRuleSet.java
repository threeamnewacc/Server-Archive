package net.badlion.potpvp.rulesets;

import net.badlion.potpvp.Group;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.helpers.PotionFixHelper;
import net.badlion.potpvp.managers.ArenaManager;
import net.badlion.potpvp.states.matchmaking.GameState;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.inventory.ItemStack;

public class OriginMCRuleSet extends FourMinutePotionRuleSet {

    public OriginMCRuleSet(int id, String name) {
        super(id, name, new ItemStack(Material.BOOK), ArenaManager.ArenaType.PEARL, true, false);

	    // Create default armor kit
	    this.defaultArmorKit[3] = new ItemStack(Material.IRON_HELMET);
	    this.defaultArmorKit[2] = new ItemStack(Material.IRON_CHESTPLATE);
	    this.defaultArmorKit[1] = new ItemStack(Material.IRON_LEGGINGS);
	    this.defaultArmorKit[0] = new ItemStack(Material.IRON_BOOTS);

	    // Create default inventory kit
	    this.defaultInventoryKit[0] = new ItemStack(Material.IRON_SWORD);
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if ((event.getDamager() instanceof Player)) {
            Player player = (Player) event.getDamager();
            Group group = PotPvP.getInstance().getPlayerGroup(player);

            if (GameState.groupIsInMatchmakingAndUsingRuleSet(group, this)) {
                PotionFixHelper.modifyDamage(player, event, 9);
            }
        }
    }

    @EventHandler
    public void onPlayerHeal(EntityRegainHealthEvent event) {
        if ((event.getEntity() instanceof Player))
        {
            Player player = (Player) event.getEntity();
            Group group = PotPvP.getInstance().getPlayerGroup(player);

            if (GameState.groupIsInMatchmakingAndUsingRuleSet(group, this)) {
                PotionFixHelper.modifyHealPotion(event, 6);
                PotionFixHelper.modifyRegenPotion(event);
            }
        }
    }
}
