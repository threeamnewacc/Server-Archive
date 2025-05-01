package net.badlion.arenapvp.listener.rulesets;

import net.badlion.arenacommon.rulesets.KitRuleSet;
import net.badlion.arenacommon.util.ItemStackUtil;
import net.badlion.arenapvp.state.MatchState;
import net.badlion.gberry.utils.BukkitUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class IronOCNListener implements Listener {


	@EventHandler
	public void onRegenDrinkEvent(PlayerItemConsumeEvent event) {
		if (MatchState.playerIsInMatchAndUsingRuleSet(event.getPlayer(), KitRuleSet.ironOCNRuleSet)) {
			if (event.getItem().equals(ItemStackUtil.REGENERATION_POTION_II)) {
				final Player player = event.getPlayer();
				BukkitUtil.runTaskNextTick(new Runnable() {
					@Override
					public void run() {
						// Add potion effect
						player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 80, 1), true);
					}
				});
			}
		}
	}

}
