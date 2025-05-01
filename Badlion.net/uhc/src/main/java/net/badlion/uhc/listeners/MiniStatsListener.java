package net.badlion.uhc.listeners;

import net.badlion.ministats.MiniStats;
import net.badlion.ministats.events.MiniMatchEvent;
import net.badlion.ministats.events.MiniPlayerMatchEvent;
import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.UHCPlayer;
import net.badlion.uhc.commands.handlers.GameModeHandler;
import net.badlion.uhc.managers.UHCPlayerManager;
import net.badlion.uhc.tasks.BorderShrinkTask;
import net.badlion.uhc.tasks.GameTimeTask;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MiniStatsListener implements Listener {

    public static Set<Location> ignoreBlockLocations = new HashSet<>();

    @EventHandler(priority=EventPriority.LAST, ignoreCancelled=true)
    public void onBlockBroken(BlockBreakEvent event) {
        MiniStatsListener.handleBlockBreak(event);
    }

    public static void handleBlockBreak(BlockBreakEvent event) {
        UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer(event.getPlayer().getUniqueId());
        if (MiniStats.getInstance().getPlayerDataListener().isTrackStats() && uhcPlayer != null && uhcPlayer.isAliveAndPlaying()) {
            if (!MiniStatsListener.ignoreBlockLocations.contains(event.getBlock().getLocation())) {
                uhcPlayer.addBlock(event.getBlock().getType());
                MiniStatsListener.ignoreBlockLocations.add(event.getBlock().getLocation());
            }
        }
    }

    @EventHandler(priority=EventPriority.LAST, ignoreCancelled=true)
    public void onBlockPlace(BlockPlaceEvent event) {
        UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer(event.getPlayer().getUniqueId());
        if (MiniStats.getInstance().getPlayerDataListener().isTrackStats() && uhcPlayer != null && uhcPlayer.isAliveAndPlaying()) {
            if (UHCPlayer.BLOCKS.contains(event.getBlock().getType())) {
                MiniStatsListener.ignoreBlockLocations.add(event.getBlock().getLocation());
            }
        }
    }

    @EventHandler
    public void onAnimalMobDied(EntityDeathEvent event) {
        if (MiniStats.getInstance().getPlayerDataListener().isTrackStats() && event.getEntity().getKiller() != null) {
            UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer(event.getEntity().getKiller().getUniqueId());
            if (uhcPlayer != null && uhcPlayer.isAliveAndPlaying()) {
                uhcPlayer.addAnimalMob(event.getEntity().getType());
            }
        }
    }

    @EventHandler(priority= EventPriority.LAST, ignoreCancelled=true)
    public void onPlayerTakeFallDamage(EntityDamageEvent event) {
        if (MiniStats.getInstance().getPlayerDataListener().isTrackStats() && event.getEntity().getType() == EntityType.PLAYER && event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer(event.getEntity().getUniqueId());
            if (uhcPlayer != null && uhcPlayer.isAliveAndPlaying()) {
                uhcPlayer.addFallDamage(event.getFinalDamage());
            }
        }
    }

    @EventHandler(priority= EventPriority.LAST)
    public void onPlayerLevelChange(PlayerLevelChangeEvent event) {
        if (MiniStats.getInstance().getPlayerDataListener().isTrackStats() && event.getNewLevel() > event.getOldLevel()) {
            UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer(event.getPlayer().getUniqueId());
            if (uhcPlayer != null && uhcPlayer.isAliveAndPlaying()) {
                uhcPlayer.addLevels(event.getNewLevel() - event.getOldLevel());
            }
        }
    }

    @EventHandler(priority= EventPriority.LAST, ignoreCancelled=true)
    public void onHorseTamed(EntityTameEvent event) {
        if (MiniStats.getInstance().getPlayerDataListener().isTrackStats() && event.getEntity().getType() == EntityType.HORSE) {
            UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer(event.getOwner().getUniqueId());
            if (uhcPlayer != null && uhcPlayer.isAliveAndPlaying()) {
                uhcPlayer.addHorsesTamed();
            }
        }
    }

    @EventHandler
    public void onPlayerDrinkPotion(PlayerItemConsumeEvent event) {
        UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer(event.getPlayer().getUniqueId());
        if (MiniStats.getInstance().getPlayerDataListener().isTrackStats() && uhcPlayer != null && uhcPlayer.isAliveAndPlaying()) {
            if (event.getItem().getType() == Material.POTION) {
                uhcPlayer.addPotion(event.getItem().getDurability());
            }
        }
    }

    @EventHandler
    public void onPlayerSplashPotion(PotionSplashEvent event) {
        if (MiniStats.getInstance().getPlayerDataListener().isTrackStats() && event.getEntity().getShooter() instanceof Player) {
            Player player = (Player) event.getEntity().getShooter();
            UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer(player.getUniqueId());
            if (uhcPlayer != null && uhcPlayer.isAliveAndPlaying()) {
                uhcPlayer.addPotion(event.getPotion().getItem().getDurability());
            }
        }
    }

    @EventHandler(priority= EventPriority.LAST, ignoreCancelled=true)
    public void onPlayerEatGApple(PlayerItemConsumeEvent event) {
        if (MiniStats.getInstance().getPlayerDataListener().isTrackStats() && event.getItem() != null && event.getItem().getType() == Material.GOLDEN_APPLE) {
            UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer(event.getPlayer().getUniqueId());
            if (uhcPlayer != null && uhcPlayer.isAliveAndPlaying()) {
                if ((boolean) BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.ABSORPTION.name()).getValue()) {
                    uhcPlayer.addAbsorptionHearts();
                }

                ItemMeta meta = event.getItem().getItemMeta();
                if (meta != null && meta.hasDisplayName() && meta.getDisplayName().equals(ChatColor.GOLD + "Golden Head")) {
                    uhcPlayer.addGoldenHead();
                } else {
                    uhcPlayer.addGoldenApple();
                }
            }
        }
    }

    @EventHandler(priority= EventPriority.LAST, ignoreCancelled=true)
    public void onNetherPortalEnter(PlayerPortalEvent event) {
        UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer(event.getPlayer().getUniqueId());
        if (MiniStats.getInstance().getPlayerDataListener().isTrackStats() && uhcPlayer != null && uhcPlayer.isAliveAndPlaying()) {
            if (event.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL) {
                uhcPlayer.addEndPortal();
            } else if (event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
                uhcPlayer.addNetherPortal();
            }
        }
    }

    /*@EventHandler
    public void onPlayerKills(MiniPlayerKillEvent event) {
        PlayerData.PlayerKill playerKill = event.getPlayerKill();
        playerKill.setOldPlayerELO(CustomEventListener.oldRatings.get(playerKill.getPlayer().getUniqueId()));
        playerKill.setNewPlayerELO(CustomEventListener.newRatings.get(playerKill.getPlayer().getUniqueId()));
        playerKill.setOldKilledPlayerELO(CustomEventListener.oldRatings.get(playerKill.getKilledPlayer().getUniqueId()));
        playerKill.setNewKilledPlayerELO(CustomEventListener.newRatings.get(playerKill.getKilledPlayer().getUniqueId()));
    }*/

    @EventHandler
    public void onPlayerKills(MiniPlayerMatchEvent event) {
        UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer(event.getPlayerData().getUniqueId());
        if (uhcPlayer != null && uhcPlayer.trackStats()) {
            event.getProfileJSON().put("team_id", uhcPlayer.getTeam().getTeamNumber());
            event.getProfileJSON().put("game_death_time_in_seconds", uhcPlayer.getGameDeathTimeInSeconds());
            event.getProfileJSON().put("num_of_teams_alive", uhcPlayer.getNumOfTeamsAliveOnDeath());
            event.getProfileJSON().put("num_of_players_alive", uhcPlayer.getNumOfPlayersAliveOnDeath());
        }
    }

    @EventHandler
    public void onMatchSave(MiniMatchEvent event) {
        List<String> gamemodes = new ArrayList<>();
        for (String gamemode : GameModeHandler.GAME_MODES) {
            gamemodes.add(gamemode);
        }

        event.getMatchJSON().put("gamemodes", gamemodes);
        List<String> configs = new ArrayList<>();
        for (String config : BadlionUHC.getInstance().getConfigurator().getOptionValues()) {
            configs.add(ChatColor.stripColor(config));
        }

        event.getMatchJSON().put("match_length", GameTimeTask.secondsInGame);
        event.getMatchJSON().put("config", configs);

        if (BadlionUHC.getInstance().getBorderShrink() != null && BadlionUHC.getInstance().getBorderShrink() && BadlionUHC.getInstance().getBorderShrinkTask() != null) {
            BorderShrinkTask ezTask = BadlionUHC.getInstance().getBorderShrinkTask();
            event.getMatchJSON().put("bs_start_time", ezTask.startTime);
            event.getMatchJSON().put("bs_shrink_interval", ezTask.shrinkInterval);
            event.getMatchJSON().put("bs_shrink_amount", ezTask.shrinkAmount);
            event.getMatchJSON().put("bs_minimum_radius", ezTask.minimumRadius);
        }
    }

}
