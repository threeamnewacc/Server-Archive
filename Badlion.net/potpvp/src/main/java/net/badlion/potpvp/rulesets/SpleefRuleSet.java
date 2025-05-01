package net.badlion.potpvp.rulesets;

import net.badlion.common.libraries.EnumCommon;
import net.badlion.gberry.Gberry;
import net.badlion.potpvp.Group;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.managers.ArenaManager;
import net.badlion.potpvp.states.matchmaking.GameState;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ArrowCollideWithBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class SpleefRuleSet extends KitRuleSet {

    public SpleefRuleSet(int id, String name) {
        super(id, name, new ItemStack(Material.DIAMOND_SPADE), ArenaManager.ArenaType.SPLEEF, false, false);

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

	@EventHandler
	public void onPlayerTakeDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			Group group = PotPvP.getInstance().getPlayerGroup(player);

			if (GameState.groupIsInMatchmakingAndUsingRuleSet(group, this)) {
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
				Group group = PotPvP.getInstance().getPlayerGroup(player);

				if (GameState.groupIsInMatchmakingAndUsingRuleSet(group, this)) {
					if (this.canBreakBlock(event.getBlock())) {
						if (event.getBlock().getType() != Material.WOOL && event.getBlock().getType() != Material.GLASS) {
							// Record removal
							GameState.getGroupGame(group).getArena().addBlockRemoved(event.getBlock(), player);

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
			Group group = PotPvP.getInstance().getPlayerGroup(event.getPlayer());

			if (GameState.groupIsInMatchmakingAndUsingRuleSet(group, this)) {
				if (this.canBreakBlock(event.getClickedBlock())) {
					if (event.getItem() != null && event.getItem().getType() == Material.DIAMOND_SPADE) {
						if (event.getClickedBlock().getType() != Material.WOOL && event.getClickedBlock().getType() != Material.GLASS) {
							event.setCancelled(true);

							// Record removal
							GameState.getGroupGame(group).getArena().addBlockRemoved(event.getClickedBlock(), event.getPlayer());

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
