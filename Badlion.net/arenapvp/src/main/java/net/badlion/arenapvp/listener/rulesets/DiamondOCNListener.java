package net.badlion.arenapvp.listener.rulesets;

import net.badlion.arenacommon.rulesets.KitRuleSet;
import net.badlion.arenacommon.util.ItemStackUtil;
import net.badlion.arenapvp.state.MatchState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class DiamondOCNListener implements Listener {

	@EventHandler
	public void onRegenDrinkEvent(PlayerItemConsumeEvent event) {
		if (MatchState.playerIsInMatchAndUsingRuleSet(event.getPlayer(), KitRuleSet.diamondOCNRuleSet)) {
			if (event.getItem().getType() == ItemStackUtil.REGENERATION_POTION_II.getType()
					&& event.getItem().getDurability() == ItemStackUtil.REGENERATION_POTION_II.getDurability()) {
				Player player = event.getPlayer();

				// Remove the regen potion effect and add ours
				for (PotionEffect potionEffect : player.getActivePotionEffects()) {
					if (potionEffect.getType().equals(PotionEffectType.REGENERATION)) {
						player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 80, 1, true));
					}
				}
			}
		}
	}

}
