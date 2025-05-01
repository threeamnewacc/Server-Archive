package net.badlion.arenapvp.listener.rulesets;

import net.badlion.arenacommon.rulesets.KitRuleSet;
import net.badlion.arenapvp.helper.PotionFixHelper;
import net.badlion.arenapvp.state.MatchState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;

public class LegacyListener implements Listener {


	@EventHandler
	public void onPlayerDamage(EntityDamageByEntityEvent event) {
		if ((event.getDamager() instanceof Player)) {
			Player player = (Player) event.getDamager();

			if (MatchState.playerIsInMatchAndUsingRuleSet(player, KitRuleSet.legacyRuleSet)) {
				PotionFixHelper.modifyDamage(player, event, 6);
			}
		}
	}

	@EventHandler
	public void onPlayerHeal(EntityRegainHealthEvent event) {
		if ((event.getEntity() instanceof Player)) {
			Player player = (Player) event.getEntity();

			if (MatchState.playerIsInMatchAndUsingRuleSet(player, KitRuleSet.legacyRuleSet)) {
				PotionFixHelper.modifyHealPotion(event, 6);
				PotionFixHelper.modifyRegenPotion(event);
			}
		}
	}

}
