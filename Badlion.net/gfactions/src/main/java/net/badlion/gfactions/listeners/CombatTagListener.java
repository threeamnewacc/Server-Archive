package net.badlion.gfactions.listeners;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import net.badlion.banmanager.BanManager;
import net.badlion.banmanager.events.PunishedPlayerEvent;
import net.badlion.combattag.events.CombatTagDamageEvent;
import net.badlion.combattag.events.CombatTagKilledEvent;
import net.badlion.gberry.Gberry;
import net.badlion.gfactions.FightParticipant;
import net.badlion.gfactions.GFactions;
import net.badlion.gfactions.managers.BattleManager;
import net.badlion.gfactions.managers.DeathBanManager;
import net.badlion.gfactions.managers.FactionManager;
import net.badlion.gfactions.tasks.CombatLoggerDisappearTask;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftZombie;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CombatTagListener implements Listener {

    @EventHandler
    public void onPlayerHurtCombatLoggerEvent(CombatTagDamageEvent event) {
		Player damager = event.getDamager();

        // Is it a combat log npc?
		if (Board.getFactionAt(damager.getLocation()).getId().equalsIgnoreCase("-1")) {
			damager.sendMessage(ChatColor.RED + "This is a PVP Protected Zone.");
			event.setCancelled(true);
			return;
		}

		// Don't let them use splash potions on the NPC
		if (event.getDamager() instanceof ThrownPotion) {
			event.setCancelled(true);
			return;
		}

		// Make sure the pvp protected person can't attack
		String uuidString = damager.getUniqueId().toString();
		if (GFactions.plugin.getMapNameToPvPTimeRemaining().containsKey(uuidString) &&
				GFactions.plugin.getMapNameToJoinTime().containsKey(uuidString)) {
			int timeRemaining = GFactions.plugin.getMapNameToPvPTimeRemaining().get(uuidString);
			long timeJoined = GFactions.plugin.getMapNameToJoinTime().get(uuidString);
			long currentTime = System.currentTimeMillis();

			// Ok they are still protected...don't allow them to attack others
			if ((timeJoined + timeRemaining) > currentTime) {
				event.setCancelled(true);
				if (event.getDamager() instanceof Projectile) {
					event.getDamager().remove();
				}

				damager.sendMessage(ChatColor.RED + "Cannot attack others with pvp protection on.");
			} else {
				// Their PVP protection is over, time to remove from the system
				GFactions.plugin.getMapNameToPvPTimeRemaining().remove(uuidString);
				GFactions.plugin.getMapNameToJoinTime().remove(uuidString);

				final Player tmp = ((Player) event.getDamager());
				GFactions.plugin.getServer().getScheduler().runTaskAsynchronously(GFactions.plugin, new Runnable() {

					@Override
					public void run() {
						// Purge from DB
						GFactions.plugin.removeProtection(tmp);
					}
				});
			}
		}
    }

	@EventHandler
	public void entityDeathEvent(CombatTagKilledEvent event) {
		// Send death message
		Player player = event.getLoggerNPC().getPlayer();
		Player killer = event.getLoggerNPC().getEntity().getKiller();

		/* STATS START */

		// Stats stuff
		final Faction deathFaction = FPlayers.i.get(player).getFaction();
		final Faction killFaction;

		if (event.getLoggerNPC().getEntity().getKiller() != null) {
			killFaction = FPlayers.i.get(event.getLoggerNPC().getEntity().getKiller()).getFaction();
		} else {
			killFaction = null;
		}

		GFactions.plugin.getServer().getScheduler().runTaskAsynchronously(GFactions.plugin, new Runnable() {
			@Override
			public void run() {
				if (!deathFaction.getId().equals("0")) {
					FactionManager.addKillDeathToFaction("deaths", deathFaction);
				}

				if (killFaction != null && !killFaction.getId().equals("0")) {
					FactionManager.addKillDeathToFaction("kills", killFaction);
				}
			}
		});

		// More stats stuff
		long currentTime = System.currentTimeMillis();

		FightParticipant fightParticipant = BattleManager.playerToFightParticipantMap.get(player.getUniqueId().toString());
		if (fightParticipant != null) {
			fightParticipant.setDeaths(fightParticipant.getDeaths() + 1);
			fightParticipant.setCurrentKillStreak(0);

			// Killer
			if (killer != null) {
				FightParticipant fightParticipantKiller = BattleManager.playerToFightParticipantMap.get(killer.getUniqueId().toString());
				if (fightParticipantKiller != null) {
					fightParticipantKiller.setKills(fightParticipantKiller.getKills() + 1);
					fightParticipantKiller.setCurrentKillStreak(fightParticipantKiller.getCurrentKillStreak() + 1);

					// Handle kill streak
					if (fightParticipantKiller.getCurrentKillStreak() > fightParticipantKiller.getMaxKillStreak()) {
						fightParticipantKiller.setMaxKillStreak(fightParticipantKiller.getCurrentKillStreak());
					}
				}
			}

			// Assists, within 60 seconds of player's life
			for (Map.Entry<String, Long> entry : fightParticipant.getMapOfLastHitTimeByPlayer().entrySet()) {
				if (entry.getValue() + 60000 > currentTime) {
					FightParticipant fightParticipantAssist = BattleManager.playerToFightParticipantMap.get(entry.getKey());
					if (fightParticipantAssist != null) {
						fightParticipantAssist.setAssists(fightParticipantAssist.getAssists());
					}
				}
			}

			// Handle final stuff in Life
			ItemStack killerItem = killer == null ? null : killer.getItemInHand();
			FightParticipant.Life life = new FightParticipant.Life(fightParticipant, currentTime, killerItem, event.getDrops());
			fightParticipant.getDeathInfo().add(life);

			// Update the last entry of the FightTime
			FightParticipant.FightTime fightTime = fightParticipant.getFightTimes().get(fightParticipant.getFightTimes().size() - 1);
			fightTime.setExitTime(currentTime);
		}

		// WTF more stats stuff?
		// Add a death to the player
		GFactions.plugin.getKdaManager().getKDAPlayer(player).addDeath();

		// Add a kill to the killer if killer exists
		if (killer != null) {
			GFactions.plugin.getKdaManager().getKDAPlayer(killer).addKill();
		}

		/* STATS END */

		// Deathban player
		DeathBanManager.deathbanPlayer(player);

		// Clear player's inventory, heal, teleport
		player.loadData();
		player.getInventory().clear();
		player.getInventory().setArmorContents(new ItemStack[4]);
		ItemStack itemStack = new ItemStack(Material.FISHING_ROD);
		itemStack.addEnchantment(Enchantment.LURE, 2);
		ItemStack data = new ItemStack(Material.STRING);
		data.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
		player.getInventory().addItem(new ItemStack(Material.COMPASS), itemStack, data);
		player.saveData();

		// PVP timer
		GFactions.plugin.getMapNameToJoinTime().put(player.getUniqueId().toString(), System.currentTimeMillis());
		GFactions.plugin.getMapNameToPvPTimeRemaining().put(player.getUniqueId().toString(), GFactions.PVP_PROTECTION_TIME * 1000);

		// Sync to database
		GFactions.plugin.getServer().getScheduler().runTaskAsynchronously(GFactions.plugin, new Runnable() {
			public void run() {
				GFactions.plugin.updateProtection(player, GFactions.PVP_PROTECTION_TIME * 1000);
			}
		});
	}

}
