package net.badlion.uhcmeetup.listeners;

import net.badlion.arenacommon.kits.Kit;
import net.badlion.arenacommon.kits.KitCommon;
import net.badlion.arenacommon.kits.KitType;
import net.badlion.arenacommon.rulesets.KitRuleSet;
import net.badlion.gberry.Gberry;
import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.managers.MPGPlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GlobalListener implements Listener {

	@EventHandler
	public void onAsyncPlayerPreLoginEvent(AsyncPlayerPreLoginEvent event) {
		MPGPlayer mpgPlayer = MPGPlayerManager.getMPGPlayer(event.getUniqueId());

		// Only load kits if this player is in the DC state
		if (mpgPlayer.getState() != MPGPlayer.PlayerState.DC) return;

		try {
			List<String> uuids = new ArrayList<>();
			uuids.add(event.getUniqueId().toString());

			// Load kits for this ruleset
			List<Kit> kits = KitCommon.getAllKitContentsForPlayersAndRuleset(Gberry.getConnection(), uuids, KitRuleSet.buildUHCRuleSet).get(event.getUniqueId());

			KitType kitType = new KitType(event.getUniqueId().toString(), KitRuleSet.buildUHCRuleSet.getName());

			Map<KitType, List<Kit>> kitMap = new HashMap<>();
			kitMap.put(kitType, kits);

			KitCommon.inventories.put(event.getUniqueId(), kitMap);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
	    Player player = event.getPlayer();

	    player.sendMessage(Gberry.getLineSeparator(ChatColor.GOLD));
	    player.sendMessage(ChatColor.GOLD + "Welcome to " + ChatColor.AQUA + "Badlion ArenaPvP UHCMeetup" + ChatColor.GOLD + "!");
	    player.sendMessage(Gberry.getLineSeparator(ChatColor.GOLD));

	    // Apply BuildUHC knockback everytime a player logs in (in case we loaded a kit for a player's combat logger)
	    KitRuleSet.buildUHCRuleSet.applyKnockbackToPlayer(player);
    }

    @EventHandler
    public void onChunkLoadEvent(ChunkLoadEvent event) {
        for (Entity entity : event.getChunk().getEntities()) {
            if (!(entity instanceof Player) && entity instanceof LivingEntity) {
                entity.remove();
            }
        }
    }

	@EventHandler
	public void onChunkUnload(ChunkUnloadEvent event) {
		event.setCancelled(true);
	}

}
