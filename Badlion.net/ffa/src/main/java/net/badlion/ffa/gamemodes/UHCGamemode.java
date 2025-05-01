package net.badlion.ffa.gamemodes;

import net.badlion.arenacommon.event.KitLoadEvent;
import net.badlion.arenacommon.kits.Kit;
import net.badlion.arenacommon.kits.KitCommon;
import net.badlion.arenacommon.kits.KitType;
import net.badlion.arenacommon.rulesets.KitRuleSet;
import net.badlion.combattag.events.CombatTagDropInventoryEvent;
import net.badlion.ffa.FFA;
import net.badlion.ffa.listeners.EnderPearlListener;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.gberry.utils.MessageUtil;
import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.gamemodes.Gamemode;
import net.badlion.mpg.kits.MPGKit;
import net.badlion.mpg.managers.MPGPlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerItemsDroppedFromDeathEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UHCGamemode extends Gamemode implements Listener {

	public UHCGamemode() {
		// Register enderpearl listener
		FFA.getInstance().getServer().getPluginManager().registerEvents(new EnderPearlListener(), FFA.getInstance());
	}

	@EventHandler
	public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
		if (MPGPlayerManager.getMPGPlayer(event.getPlayer()).getState() != MPGPlayer.PlayerState.PLAYER) return;

		// Don't let players drop items
		event.setCancelled(true);
	}

	@EventHandler
	public void onCombatTagDropInventoryEvent(CombatTagDropInventoryEvent event) {
		Entity entity = event.getLoggerNPC().getEntity();

		// Don't drop items on death
		event.setCancelled(true);

		// Drop a Golden Head since we disable crafting benches
		entity.getWorld().dropItemNaturally(entity.getLocation(), ItemStackUtil.createGoldenHead());
	}

	@EventHandler
	public void onPlayerItemsDroppedFromDeathEvent(PlayerItemsDroppedFromDeathEvent event) {
		// Don't drop items on death
		for (Item item : event.getItemsDroppedOnDeath()) {
			item.remove();
		}
	}

	@EventHandler
	public void onAsyncPlayerPreLoginEvent(AsyncPlayerPreLoginEvent event) {
		try {
			List<String> uuids = new ArrayList<>();
			uuids.add(event.getUniqueId().toString());

			// Load kits for this ruleset
			List<Kit> kits = KitCommon.getAllKitContentsForPlayersAndRuleset(Gberry.getConnection(), uuids, KitRuleSet.uhcRuleSet).get(event.getUniqueId());

			KitType kitType = new KitType(event.getUniqueId().toString(), KitRuleSet.uhcRuleSet.getName());

			Map<KitType, List<Kit>> kitMap = new HashMap<>();
			kitMap.put(kitType, kits);

			KitCommon.inventories.put(event.getUniqueId(), kitMap);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@EventHandler
	public void onKitLoadEvent(KitLoadEvent event) {
		Player player = event.getPlayer();

		// Find steak and set to full stack
		ItemStack steak = player.getInventory().getItem(player.getInventory().first(Material.COOKED_BEEF));

		// Avoid weird edge cases
		if (steak != null) {
			steak.setAmount(64);
		}

		boolean foundArrows = false;
		ItemStack[] inventoryContents = player.getInventory().getContents();
		for (int i = 0; i < inventoryContents.length; i++) {
			ItemStack itemStack = inventoryContents[i];

			if (itemStack == null) continue;

			// Only give them a single arrow
			if (itemStack.getType() == Material.ARROW) {
				if (foundArrows) {
					player.getInventory().setItem(i, null);
				} else {
					foundArrows = true;

					itemStack.setAmount(1);
				}
			} else if (itemStack.getType() == Material.BOW) {
				// Put infinity on the bow
				itemStack.addEnchantment(Enchantment.ARROW_INFINITE, 1);
			}
		}

		// Are they still on the spawn platform? Avoid exploits
		if (player.getLocation().getY() > FFA.getInstance().getFFAGame().getWorld().getSpawnPlatformYLimit()) {
			// Add an enderpearl to their inventory
			player.getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
		}

		ItemStackUtil.addUnbreakingToWeapons(player);
		ItemStackUtil.addUnbreakingToArmor(player);
	}

	@EventHandler(priority = EventPriority.LAST, ignoreCancelled = true)
	public void onArrowHit(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player && event.getDamager() instanceof Arrow) {
			Player damaged = (Player) event.getEntity();

			if (((Arrow) event.getDamager()).getShooter() instanceof Player) {
				Player damager = (Player) ((Arrow) event.getDamager()).getShooter();

				if (damaged != damager && damaged.getHealth() - event.getFinalDamage() > 0) {
					damager.sendMessage(ChatColor.GOLD + damaged.getDisguisedName() + ChatColor.DARK_AQUA + " is now at " + ChatColor.GOLD +
							Math.ceil(damaged.getHealth() - event.getFinalDamage()) / 2D + " " + MessageUtil.HEART_WITH_COLOR);
				}
			}
		}
	}

	@EventHandler
	public void onPlayerItemConsumeEvent(PlayerItemConsumeEvent event) {
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

	@EventHandler
	public void onHealthRegenEvent(EntityRegainHealthEvent event) {
		if (event.getEntity() instanceof Player) {
			if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) {
				event.setCancelled(true);
			}
		}
	}

	@Override
	public ItemStack getTierItem(int tier) {
		return null;
	}

	@Override
	public List<ItemStack> getCommonTierItems(int tier) {
		return null;
	}

	@Override
	public int getNumOfTierRandom(int tier) {
		return -1;
	}

	@Override
	public int getNumOfTierGuaranteed(int tier) {
		return -1;
	}

    @Override
    public void handleDeath(LivingEntity died) {
	    // Drop a Golden Head since we disable crafting benches
	    died.getWorld().dropItemNaturally(died.getLocation(), ItemStackUtil.createGoldenHead());
    }

	public String getName() {
		return "UHC";
	}

	@Override
	public MPGKit getDefaultKit() {
		return null;
	}

}
