package net.badlion.arenapvp.listener.rulesets;

import org.bukkit.event.Listener;

public class BuffSoupListener implements Listener {

	/*
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
	*/

}
