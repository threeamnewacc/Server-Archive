package com.massivecraft.factions.listeners;

import club.minemen.hcfactions.utils.ItemStackUtil;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.massivecraft.factions.struct.Permission;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Sign;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class KitSignListener implements Listener {

	private final long cooldown = 10 * 60 * 1000; // 10 min

	private final Cache<UUID, Long> cooldowns = CacheBuilder.newBuilder().expireAfterWrite(cooldown + 2000, TimeUnit.MILLISECONDS).build();


	@EventHandler
	public void onSignUpdate(SignChangeEvent event) {
		if (event.getLine(0).equalsIgnoreCase("[kitfish]")) {
			if (event.getPlayer().hasPermission(Permission.BYPASS.node)) {
				Sign sign = new Sign(event.getBlock().getType(), event.getBlock().getData());
				if (!sign.isWallSign()) {
					clearLines(event);
					event.getPlayer().sendFormattedMessage("{0}You must place this kit sign on a wall.", ChatColor.RED);
					return;
				}
				event.setLine(0, "");
				event.setLine(1, ChatColor.GREEN + ChatColor.BOLD.toString() + "Fishing Kit");
				event.setLine(2, "[Right Click]");
				event.setLine(3, "");
			} else {
				clearLines(event);
				event.getPlayer().sendFormattedMessage("{0}You can not make kit signs.", ChatColor.RED);
			}
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Block block = event.getClickedBlock();
			if (block.getType().equals(Material.WALL_SIGN)) {
				if (isFishingKitSign((org.bukkit.block.Sign) block.getState())) {
					long playerCooldown = getCooldown(player);
					if (playerCooldown > 0) {
						player.sendFormattedMessage("{0}You can not use the fishing kit for {1}", ChatColor.RED, DurationFormatUtils.formatDurationWords(getCooldown(player), true, true));
						return;
					}
					setCooldown(player);
					ItemStack rod = ItemStackUtil.createItem(Material.FISHING_ROD, 1);
					rod.addEnchantment(Enchantment.LURE, 2);
					player.getInventory().addItem(rod);
					player.updateInventory();
				}

			}
		}
	}

	public long getCooldown(Player player) {
		Long c = cooldowns.getIfPresent(player.getUniqueId());
		return c != null ? c - System.currentTimeMillis() : 0;
	}

	public long getCooldownTime(Player player) {
		Long c = cooldowns.getIfPresent(player);
		return c != null ? c : 0;
	}

	public void setCooldown(Player player) {
		final long end = System.currentTimeMillis() + cooldown;
		cooldowns.put(player.getUniqueId(), end);
	}

	public boolean isFishingKitSign(org.bukkit.block.Sign sign) {
		return sign.getLine(1).equals(ChatColor.GREEN + ChatColor.BOLD.toString() + "Fishing Kit") && sign.getLine(2).equals("[Right Click]");
	}

	private void clearLines(SignChangeEvent event) {
		for (int i = 0; i < 4; i++) {
			event.setLine(i, "");
		}
	}
}
