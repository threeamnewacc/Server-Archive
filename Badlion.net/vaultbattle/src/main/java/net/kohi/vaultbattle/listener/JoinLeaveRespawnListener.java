package net.kohi.vaultbattle.listener;

import net.badlion.common.libraries.EnumCommon;
import net.badlion.gpermissions.GPermissions;
import net.kohi.sidebar.SidebarAPI;
import net.kohi.vaultbattle.VaultBattlePlugin;
import net.kohi.vaultbattle.type.Phase;
import net.kohi.vaultbattle.type.PlayerData;
import net.kohi.vaultbattle.type.Team;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;

public class JoinLeaveRespawnListener implements Listener {

    private final VaultBattlePlugin plugin;
    private final Scoreboard workaroundScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

    public JoinLeaveRespawnListener(VaultBattlePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        // deny worldedit compass
        event.getPlayer().addAttachment(plugin, "worldedit.navigation.jumpto.tool", false);
        event.getPlayer().addAttachment(plugin, "worldedit.navigation.thru.tool", false);
    
        event.setJoinMessage(null);
        Player player = event.getPlayer();
        SidebarAPI.addSidebarItem(event.getPlayer(), plugin.getGameManager().getSpaceSidebar());
        SidebarAPI.addSidebarItem(event.getPlayer(), plugin.getGameManager().getServerSidebar());
        // leave this one out for now
        // SidebarAPI.addSidebarItem(event.getPlayer(), plugin.getGameManager().getIpSidebar());
        if (plugin.getGameManager().getBreakAmount() != 3) {
            SidebarAPI.addSidebarItem(event.getPlayer(), plugin.getGameManager().getPhaseSidebar());
        }

        if (event.getPlayer().hasPermission("badlion.sgmod")) {
            GPermissions.giveModPermissions(event.getPlayer());
        } else if (event.getPlayer().hasPermission("badlion.sgtrial")) {
            GPermissions.giveTrialPermissions(event.getPlayer());
        }

        // TODO: unfuck scoreboards in gspigot
        player.setScoreboard(workaroundScoreboard);
        player.setScoreboard(plugin.getScoreboard());
        if (plugin.getGameManager().getPhase().equals(Phase.PRE_START)) {
            for (PotionEffect effect : player.getActivePotionEffects()) {
                player.removePotionEffect(effect.getType());
            }
            player.teleport(new Location(Bukkit.getWorld("world"), 0, 65, 0));
            player.setGameMode(GameMode.CREATIVE);
            player.getInventory().clear();
            player.getInventory().setHelmet(null);
            player.getInventory().setChestplate(null);
            player.getInventory().setLeggings(null);
            player.getInventory().setBoots(null);
            player.getInventory().setItem(2, plugin.getTeamManager().getRandomTeamItem());
            int i = 3;
            for (Team team : plugin.getTeamManager().getTeams()) {
                player.getInventory().setItem(i, team.getJoinItem());
                i++;
            }
            player.updateInventory();
            for (Team team : plugin.getTeamManager().getTeams()) {
                SidebarAPI.addSidebarItem(event.getPlayer(), team.getPlayerCountSidebar());
            }
        } else {
            PlayerData playerData = plugin.getPlayerDataManager().get(player);
            Team t = playerData.getTeam();
            if (t != null) {
                for (Team team : plugin.getTeamManager().getTeams()) {
                    SidebarAPI.addSidebarItem(player, team.getBankSidebar());
                }
                if (t.isEliminated()) {
                    player.getInventory().clear();
                    player.getInventory().setHelmet(null);
                    player.getInventory().setChestplate(null);
                    player.getInventory().setLeggings(null);
                    player.getInventory().setBoots(null);
                    plugin.getPlayerDataManager().teleportToSpawn(player);
                    plugin.getPlayerDataManager().makeSpectator(player);
                    return;
                }
                //Dont think this tp is needed, and is probably being abused by some people
                //player.teleport(plugin.getGameManager().getMap().getTeamSpawn(t.getColor()));
            } else {
                if (!plugin.getGameManager().isWallsDropped()) {
                    for (PotionEffect effect : player.getActivePotionEffects()) {
                        player.removePotionEffect(effect.getType());
                    }
                    playerData.setPickingTeam(true);
                    player.teleport(new Location(Bukkit.getWorld("world"), 0, 65, 0));
                    player.setGameMode(GameMode.CREATIVE);
                    player.getInventory().clear();
                    player.getInventory().setHelmet(null);
                    player.getInventory().setChestplate(null);
                    player.getInventory().setLeggings(null);
                    player.getInventory().setBoots(null);
                    player.getInventory().setItem(2, plugin.getTeamManager().getRandomTeamItem());
                    int i = 3;
                    for (Team team : plugin.getTeamManager().getTeams()) {
                        player.getInventory().setItem(i, team.getJoinItem());
                        i++;
                    }
                    player.updateInventory();
                    for (Team team : plugin.getTeamManager().getTeams()) {
                        SidebarAPI.addSidebarItem(event.getPlayer(), team.getPlayerCountSidebar());
                    }
                } else {
                    player.getInventory().clear();
                    player.getInventory().setHelmet(null);
                    player.getInventory().setChestplate(null);
                    player.getInventory().setLeggings(null);
                    player.getInventory().setBoots(null);

                    plugin.getPlayerDataManager().teleportToSpawn(player);
                    plugin.getPlayerDataManager().makeSpectator(player);
                    for (Team team : plugin.getTeamManager().getTeams()) {
                        SidebarAPI.addSidebarItem(event.getPlayer(), team.getBankSidebar());
                    }
                }
            }
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        Player player = event.getPlayer();
        PlayerData playerData = plugin.getPlayerDataManager().get(player);
        if (playerData.isSpectating()) {
            playerData.setSpectating(false);
            plugin.getGameManager().getSpectatePlayersMenu().update();
        }
        if (playerData.isPickingTeam()) {
            playerData.setPickingTeam(false);
        }
        if (plugin.getGameManager().getPhase().equals(Phase.PRE_START)) {
            Team team = playerData.getTeam();
            if (team != null) {
                plugin.getTeamManager().removeFromAllTeams(player);
            }
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = plugin.getPlayerDataManager().get(player);
        Team team = playerData.getTeam();
        if (team != null) {
            if (team.isEliminated()) {
                event.setRespawnLocation(new Location(Bukkit.getWorld("world"), 0, 65, 0));
                return;
            }
            event.setRespawnLocation(plugin.getGameManager().getMap().getTeamSpawn(team.getColor()));
            new BukkitRunnable() {
                @Override
                public void run() {
                    plugin.getTeamManager().giveKit(player);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 5, 6, true)); // resistance to avoid any nasty tp damage
                }
            }.runTask(plugin);
            if (plugin.getGameManager().isWallsDropped()) {
                team.broadcastMessage(ChatColor.RED + "Lost 5 emeralds due to " + player.getDisplayName() + ChatColor.RED + " respawning.");
                if (team.canRespawn()) {
                    for (int i = 0; i < 5; i++) {
                        Location bankDestroy = plugin.getGameManager().getMap().getBanks().get(team.getColor()).getBlock(team.getBankRounded());
                        bankDestroy.getBlock().setType(Material.AIR);
                        bankDestroy.getWorld().playEffect(bankDestroy, Effect.STEP_SOUND, 133);
                        team.destroyBankBlock();
                        team.broadcastSound(EnumCommon.getEnumValueOf(Sound.class, "ITEM_BREAK", "ENTITY_ITEM_BREAK"), 1.0F, 1.0F);
                    }
                } else {
                    //put them into a mode where they can click a item to respawn. for now just let them respawn but take no emeralds away.
                }
            }
        }
    }

}
