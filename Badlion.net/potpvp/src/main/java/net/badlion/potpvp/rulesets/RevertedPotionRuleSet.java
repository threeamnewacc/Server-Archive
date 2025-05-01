package net.badlion.potpvp.rulesets;

import net.badlion.potpvp.Group;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.helpers.PotionFixHelper;
import net.badlion.potpvp.managers.ArenaManager;
import net.badlion.potpvp.states.matchmaking.GameState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.inventory.ItemStack;

public class RevertedPotionRuleSet extends KitRuleSet {

    public RevertedPotionRuleSet(int id, String name, ItemStack itemStack, ArenaManager.ArenaType arenaType, boolean usesCustomChests, boolean allowsExtraArmorSets) {
        super(id, name, itemStack, arenaType, usesCustomChests, allowsExtraArmorSets);
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if ((event.getDamager() instanceof Player)) {
            Player player = (Player) event.getDamager();
            Group group = PotPvP.getInstance().getPlayerGroup(player);

            if (GameState.groupIsInMatchmakingAndUsingRuleSet(group, this)) {
                PotionFixHelper.modifyDamage(player, event, 6);
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
