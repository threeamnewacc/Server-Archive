package net.badlion.arenapvp.listener.rulesets;

import net.badlion.arenacommon.rulesets.KitRuleSet;
import net.badlion.arenapvp.state.MatchState;
import net.badlion.common.libraries.EnumCommon;
import net.badlion.gberry.Gberry;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ArrowCollideWithBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class SpleefListener implements Listener {


	@EventHandler
	public void onPlayerTakeDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();

			if (MatchState.playerIsInMatchAndUsingRuleSet(player, KitRuleSet.spleefRuleSet)) {
				if (event.getCause() != EntityDamageEvent.DamageCause.VOID) {
					event.setCancelled(true);
				}
			}
		}
	}

	public void playSound(Block block) {
		Location location = block.getLocation();

		Sound sound = EnumCommon.getEnumValueOf(Sound.class, "GLASS", "BLOCK_GLASS_BREAK");
		Material type = block.getType();

		switch (type) {
			case GRASS:
				sound = EnumCommon.getEnumValueOf(Sound.class, "DIG_GRASS", "BLOCK_GRASS_BREAK");
				break;
			case SNOW:
				sound = EnumCommon.getEnumValueOf(Sound.class, "DIG_SNOW", "BLOCK_SNOW_BREAK");
				break;
			case STONE:
				sound = EnumCommon.getEnumValueOf(Sound.class, "DIG_STONE", "BLOCK_STONE_BREAK");
				break;
			case SAND:
				sound = EnumCommon.getEnumValueOf(Sound.class, "DIG_SAND", "BLOCK_SAND_BREAK");
				break;
		}

		block.getWorld().playSound(location, sound, 1F, 1F);
	}

	@EventHandler
	public void onArrowCollideWithBlockEvent(ArrowCollideWithBlockEvent event) {
		if (event.getArrow().getShooter() instanceof Player) {
			Player player = (Player) event.getArrow().getShooter();

			// Is the player online?
			if (Gberry.isPlayerOnline(player)) {

				if (MatchState.playerIsInMatchAndUsingRuleSet(player, KitRuleSet.spleefRuleSet)) {
					if (KitRuleSet.spleefRuleSet.canBreakBlock(event.getBlock())) {
						if (event.getBlock().getType() != Material.WOOL && event.getBlock().getType() != Material.GLASS) {
							// Record removal
							MatchState.getPlayerMatch(player).getArena().addBlockRemoved(event.getBlock(), player);

							// Play break sound
							this.playSound(event.getBlock());

							// Set block to air
							event.getBlock().setType(Material.AIR);
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		if (event.getAction() == Action.LEFT_CLICK_BLOCK) {

			if (MatchState.playerIsInMatchAndUsingRuleSet(event.getPlayer(), KitRuleSet.spleefRuleSet)) {
				if (KitRuleSet.spleefRuleSet.canBreakBlock(event.getClickedBlock())) {
					if (event.getItem() != null && event.getItem().getType() == Material.DIAMOND_SPADE) {
						if (event.getClickedBlock().getType() != Material.WOOL && event.getClickedBlock().getType() != Material.GLASS) {
							event.setCancelled(true);

							// Record removal
							MatchState.getPlayerMatch(event.getPlayer()).getArena().addBlockRemoved(event.getClickedBlock(), event.getPlayer());

							// Play break sound
							this.playSound(event.getClickedBlock());

							// Set block to air
							event.getClickedBlock().setType(Material.AIR);
						}
					}
				}
			}
		}
	}

}
