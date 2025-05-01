package net.badlion.survivalgames.gamemodes;

import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.gberry.utils.MessageUtil;
import net.badlion.mpg.MPGGame;
import net.badlion.mpg.tasks.GameTimeTask;
import net.badlion.survivalgames.SurvivalGames;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class UHCGamemode extends ClassicGamemode {

	@Override
	public ItemStack getTierItem(int tier) {
		ItemStack itemStack;

		// Don't return an GApple as an item since we
		// already put GApples in every chest
		do {
		  itemStack = super.getTierItem(tier);
		} while (itemStack == null || itemStack.getType() == Material.GOLDEN_APPLE);

		return itemStack;
	}

	@Override
	public List<ItemStack> getCommonTierItems(int tier) {
		List<ItemStack> commonItems = new ArrayList<>();

		int numberGApples = 1;

		// Tier 1 has 1-2 GApples, tier 2 has 2-3 so add one extra if tier 2
		if (tier == 2) numberGApples++;

		// 50/50 chance to add another
		if (this.random.nextInt(2) == 1) numberGApples++;

		// Add GApples
		commonItems.add(new ItemStack(Material.GOLDEN_APPLE, numberGApples));

		return commonItems;
	}

	@Override
	public void handleDeath(LivingEntity died) {
		super.handleDeath(died);

		// Drop a golden head on death
		died.getWorld().dropItemNaturally(died.getLocation(), ItemStackUtil.createGoldenHead());
	}

	@EventHandler
	public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
		if (SurvivalGames.getInstance().getSGGame().getGameState() != MPGGame.GameState.GAME) return;

		// Have 30 seconds elapsed since the game started?
		if (GameTimeTask.getInstance().getTotalSeconds() > 30) return;

		if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
			Player damager = ((Player) event.getDamager());

			// Block damage if they hit someone with a fist
			if (damager.getItemInHand() == null || damager.getItemInHand().getType() == Material.AIR) {
				event.setDamage(0);
			}
		}
	}

	@EventHandler
	public void onEatGoldenHeadEvent(final PlayerItemConsumeEvent event) {
		Player player = event.getPlayer();
		ItemStack item = event.getItem();
		if (item.getType().equals(Material.GOLDEN_APPLE)) {
			ItemMeta meta = item.getItemMeta();
			if (meta.hasDisplayName() && meta.getDisplayName().equals(ChatColor.GOLD + "Golden Head")) {
				// Add potion effect
				player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100 + 4/*Number of 1/2 hearts to heal*/ * 25, 1), true);
			}
		}
	}

	@EventHandler(priority= EventPriority.LAST, ignoreCancelled=true)
	public void onArrowHit(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player && event.getDamager() instanceof Arrow) {
			Player damaged = (Player) event.getEntity();

			if (((Arrow) event.getDamager()).getShooter() instanceof Player) {
				Player damager = (Player) ((Arrow) event.getDamager()).getShooter();

				if (damaged != damager && damaged.getHealth() - event.getFinalDamage() > 0) {
					damager.sendMessage(ChatColor.GOLD + damaged.getName() + ChatColor.DARK_AQUA + " is now at " + ChatColor.GOLD +
							Math.ceil(damaged.getHealth() - event.getFinalDamage()) / 2D + " " + MessageUtil.HEART_WITH_COLOR);
				}
			}
		}
	}

	@EventHandler
	public void onHealthRegenEvent(EntityRegainHealthEvent event) {
		if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) {
			event.setCancelled(true);
		}
	}

	public String getName() {
		return "UHC";
	}

}
