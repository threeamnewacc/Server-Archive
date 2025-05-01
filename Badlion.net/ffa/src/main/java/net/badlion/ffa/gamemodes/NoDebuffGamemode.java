package net.badlion.ffa.gamemodes;

import net.badlion.arenacommon.event.KitLoadEvent;
import net.badlion.arenacommon.kits.Kit;
import net.badlion.arenacommon.kits.KitCommon;
import net.badlion.arenacommon.kits.KitType;
import net.badlion.arenacommon.rulesets.KitRuleSet;
import net.badlion.arenacommon.util.ItemStackUtil;
import net.badlion.combattag.events.CombatTagDropInventoryEvent;
import net.badlion.ffa.FFA;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.gamemodes.Gamemode;
import net.badlion.mpg.kits.MPGKit;
import net.badlion.mpg.managers.MPGPlayerManager;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PlayerItemsDroppedFromDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NoDebuffGamemode extends Gamemode implements Listener {

	private static final int ITEM_DROP_CLEAR_TIME = 30; // Seconds

	@EventHandler
	public void onAsyncPlayerPreLoginEvent(AsyncPlayerPreLoginEvent event) {
		try {
			List<String> uuids = new ArrayList<>();
			uuids.add(event.getUniqueId().toString());

			// Load kits for this ruleset
			List<Kit> kits = KitCommon.getAllKitContentsForPlayersAndRuleset(Gberry.getConnection(), uuids, KitRuleSet.noDebuffRuleSet).get(event.getUniqueId());

			KitType kitType = new KitType(event.getUniqueId().toString(), KitRuleSet.noDebuffRuleSet.getName());

			Map<KitType, List<Kit>> kitMap = new HashMap<>();
			kitMap.put(kitType, kits);

			KitCommon.inventories.put(event.getUniqueId(), kitMap);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@EventHandler
	public void onKitLoadEvent(KitLoadEvent event) {
		ItemStackUtil.addUnbreakingToWeapons(event.getPlayer());
		ItemStackUtil.addUnbreakingToArmor(event.getPlayer());
	}

	@EventHandler
	public void onPotionSplashEvent(PotionSplashEvent event) {
		Player player = ((Player) event.getEntity().getShooter());

		// Are they on the spawn platform?
		if (player.getLocation().getY() >= FFA.getInstance().getFFAGame().getWorld().getSpawnPlatformYLimit()) {
			// Don't let them throw the splash potion
			event.setCancelled(true);

			// Add potion back
			player.getInventory().addItem(event.getPotion().getItem());
			player.updateInventory();
		}
	}

	@EventHandler
	public void onPlayerItemConsumeEvent(PlayerItemConsumeEvent event) {
		final Player player = event.getPlayer();

		if (event.getItem().getType() == Material.POTION) {
			// Remove this item after they drink it
			BukkitUtil.runTaskNextTick(new Runnable() {
				@Override
				public void run() {
					player.setItemInHand(null);
				}
			});
		}
	}

	@EventHandler
	public void onPlayerDeathEvent(PlayerDeathEvent event) {
		Player player = event.getEntity();
		Player killer = player.getKiller();

		if (killer != null) {
			// Reset fire resistance 8 minute potion effect
			killer.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 9600, 0), true);
		}
	}

	@EventHandler
	public void onPlayerItemsDroppedFromDeathEvent(PlayerItemsDroppedFromDeathEvent event) {
		// Don't drop weapons/armor
		for (Item item : event.getItemsDroppedOnDeath()) {
			if (this.isWeaponsOrArmor(item.getItemStack())) {
				item.remove();
			} else {
				// Set age to clear item quicker
				item.setAge(6000 - (NoDebuffGamemode.ITEM_DROP_CLEAR_TIME * 20));
			}
		}
	}

	@EventHandler
	public void onCombatTagDropInventoryEvent(CombatTagDropInventoryEvent event) {
		// Set age to clear items quicker
		event.setDroppedItemAge(6000 - (NoDebuffGamemode.ITEM_DROP_CLEAR_TIME * 20));

		for (int i = 0; i < event.getArmor().length; i++) {
			if (this.isWeaponsOrArmor(event.getArmor()[i])) {
				event.getArmor()[i] = null;
			}
		}

		for (int i = 0; i < event.getInventory().length; i++) {
			if (this.isWeaponsOrArmor(event.getInventory()[i])) {
				event.getInventory()[i] = null;
			}
		}
	}

	@EventHandler
	public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
		if (MPGPlayerManager.getMPGPlayer(event.getPlayer()).getState() != MPGPlayer.PlayerState.PLAYER) return;

		// Set age to clear item quicker
		event.getItemDrop().setAge(6000 - (NoDebuffGamemode.ITEM_DROP_CLEAR_TIME * 20));
	}

	private boolean isWeaponsOrArmor(ItemStack itemStack) {
		if (itemStack == null) return false;

		Material type = itemStack.getType();

		if (type == Material.WOOD_SWORD || type == Material.STONE_SWORD
				|| type == Material.GOLD_SWORD || type == Material.IRON_SWORD
				|| type == Material.DIAMOND_SWORD || type == Material.BOW
				|| type == Material.FISHING_ROD || type == Material.WOOD_AXE
				|| type == Material.STONE_AXE || type == Material.GOLD_AXE
				|| type == Material.IRON_AXE || type == Material.DIAMOND_AXE
				|| type == Material.FISHING_ROD || type == Material.LEATHER_HELMET
				|| type == Material.LEATHER_CHESTPLATE || type == Material.LEATHER_LEGGINGS
				|| type == Material.LEATHER_BOOTS || type == Material.CHAINMAIL_HELMET
				|| type == Material.CHAINMAIL_CHESTPLATE || type == Material.CHAINMAIL_LEGGINGS
				|| type == Material.CHAINMAIL_BOOTS || type == Material.GOLD_HELMET
				|| type == Material.GOLD_CHESTPLATE || type == Material.GOLD_LEGGINGS
				|| type == Material.GOLD_BOOTS || type == Material.IRON_HELMET
				|| type == Material.IRON_CHESTPLATE || type == Material.IRON_LEGGINGS
				|| type == Material.IRON_BOOTS || type == Material.DIAMOND_HELMET
				|| type == Material.DIAMOND_CHESTPLATE || type == Material.DIAMOND_LEGGINGS
				|| type == Material.DIAMOND_BOOTS) {
			return true;
		}

		return false;
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

    }

	public String getName() {
		return "NoDebuff";
	}

	@Override
	public MPGKit getDefaultKit() {
		return null;
	}

}
