package net.badlion.survivalgames.tasks;

import net.badlion.gberry.Gberry;
import net.badlion.survivalgames.SGPlayer;
import net.badlion.survivalgames.SurvivalGames;
import net.badlion.survivalgames.managers.SGPlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class PreGameStartTask extends BukkitRunnable {

    private Map<UUID, Location> locationMap;
    private int count = 20 * 20;
    private boolean handledDCPlayers = false;

    public PreGameStartTask(Map<UUID, Location> locationMap) {
        this.locationMap = locationMap;
    }

    @Override
    public void run() {
        if (count == 0) {
            // Store start time for stat tracking here of everyone
            long time = System.currentTimeMillis();
            for (SGPlayer sgPlayer : SGPlayerManager.getAllSGPlayers()) {
                sgPlayer.setStartTime(time);
            }

            Gberry.broadcastMessageNoBalance(SurvivalGames.SG_PREFIX + ChatColor.DARK_GREEN + "[" + ChatColor.GOLD + "Match" + ChatColor.DARK_GREEN + "] " + ChatColor.GOLD + " GO!");
            SurvivalGames.getInstance().setState(SurvivalGames.SGState.STARTED);
            SurvivalGames.getInstance().getGame().setStartTime(time);

            // Start off tasks
            new GameTimeTask().runTaskTimer(SurvivalGames.getInstance(), 20, 20);

            this.cancel();

            // Remove any lingering entities
            for (Entity entity : SurvivalGames.getInstance().getServer().getWorld(SurvivalGames.getInstance().getGame().getGWorld().getInternalName()).getEntities()) {
                if (entity instanceof LivingEntity && !(entity instanceof Player)) {
                    entity.remove();
                }
            }

            // Listen to stuff now for stats system
            SurvivalGames.getInstance().getMiniStats().startListening();

            SurvivalGames.getInstance().getGame().checkForEndGame();

            return;
        } else if (SGPlayerManager.getPlayersByState(SGPlayer.State.ALIVE).size() == 1) {
            this.cancel();
            this.handleDCdPlayers();

            return;
        } else if (count > 200 && count % 100 == 0) {
            Gberry.broadcastMessageNoBalance(SurvivalGames.SG_PREFIX + ChatColor.DARK_GREEN + "[" + ChatColor.GOLD + "Match" + ChatColor.DARK_GREEN + "] " + ChatColor.GOLD + " Will start in " + ChatColor.YELLOW + (this.count / 20) + ChatColor.GOLD + " seconds");
        } else if (count == 100) {
	        this.handleDCdPlayers();

            Gberry.broadcastMessageNoBalance(SurvivalGames.SG_PREFIX + ChatColor.DARK_GREEN + "[" + ChatColor.GOLD + "Match" + ChatColor.DARK_GREEN + "] " + ChatColor.GOLD + " Will start in " + ChatColor.YELLOW + (this.count / 20) + ChatColor.GOLD + " seconds");
        } else if (count <= 200 && count % 20 == 0) {
            Gberry.broadcastMessageNoBalance(SurvivalGames.SG_PREFIX + ChatColor.DARK_GREEN + "[" + ChatColor.GOLD + "Match" + ChatColor.DARK_GREEN + "] " + ChatColor.GOLD + " Will start in " + ChatColor.YELLOW + (this.count / 20) + ChatColor.GOLD + " seconds");
        }

        Set<SGPlayer> sgPlayers = SGPlayerManager.getPlayersByState(SGPlayer.State.ALIVE);
        for (SGPlayer sgPlayer : sgPlayers) {
            Player player = SurvivalGames.getInstance().getServer().getPlayer(sgPlayer.getUuid());
            if (player != null) {
                Location prevLocation = this.locationMap.get(player.getUniqueId());

                // Could be null somehow?
                if (prevLocation != null) {
                    if (player.getLocation().getBlockX() != prevLocation.getBlockX() || player.getLocation().getBlockZ() != prevLocation.getBlockZ()) {
                        Location newLocation = player.getLocation();
                        newLocation.setX(prevLocation.getX());
                        newLocation.setZ(prevLocation.getZ());

                        player.teleport(newLocation);
                    }
                } else {
                    this.locationMap.put(player.getUniqueId(), player.getLocation());
                }
            }
        }

        count--;
    }

    private void handleDCdPlayers() {
        if (this.handledDCPlayers) {
            return;
        }

        this.handledDCPlayers = true;

        //System.out.println("ok");
        final List<SGPlayer> offlinePlayers = new ArrayList<>();
        for (UUID uuid : SurvivalGames.getInstance().getGame().getUuids()) {
            if (SurvivalGames.getInstance().getServer().getPlayer(uuid) == null) {
                offlinePlayers.add(SGPlayerManager.getSGPlayer(uuid));
                System.out.println(uuid);
            }
        }

        // Now go through and handle their deaths
        for (SGPlayer sgDodgeFaggot : offlinePlayers) {
            SurvivalGames.getInstance().getGame().handleDeath(sgDodgeFaggot);
        }

        SurvivalGames.getInstance().getGame().checkForEndGame();

        //System.out.println("wat");

        // Force update everyone's rating for calculations later on
        /*SurvivalGames.getInstance().getServer().getScheduler().runTaskAsynchronously(SurvivalGames.getInstance(), new Runnable() {
            @Override
            public void run() {
                System.out.println("derp");
                Ladder ladder = Ladder.getLadder(SurvivalGames.getInstance().getGame().getGameMode().name(), Ladder.LadderType.FFA);
                System.out.println("derp2");

                Gberry.log("RATING", "test " + SurvivalGames.getInstance().getGame().getRatings());

                if (SurvivalGames.getInstance().getGame().getRatings() == null) {
                    Gberry.log("RATING", "Getting ratings");
                    Map<UUID, Integer> ratingsMap = RatingManager.fetchAndStoreRatings(SurvivalGames.getInstance().getGame().getUuids(), ladder);

                    List<Integer> ratings = new ArrayList<>();
                    for (UUID uuid : SurvivalGames.getInstance().getGame().getUuids()) {
                        ratings.add(ratingsMap.get(uuid));

                        SGPlayer sgPlayer = SGPlayerManager.getSGPlayer(uuid);
                        sgPlayer.setStartTime(System.currentTimeMillis());
                        PlayerData playerData = new PlayerData(Bukkit.getPlayer(uuid), uuid, sgPlayer.getUsername(), SurvivalGames.getInstance().getGame().getgWorld().getNiceWorldName());
                        SurvivalGames.getInstance().getMiniStats().getPlayerDataListener().getPlayerDataMap().put(uuid, playerData);
                    }

                    Gberry.log("RATING", "Setting ratings");
                    SurvivalGames.getInstance().getGame().setRatings(ratings);
                }

                Gberry.log("RATING", "Apparently ratings is set already somehow?");

                SurvivalGames.getInstance().getServer().getScheduler().runTask(SurvivalGames.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        // Insert a loss for all them bitches who thought they could queue dodge
                        for (SGPlayer sgDodgeFaggot : offlinePlayers) {
                            SurvivalGames.getInstance().getGame().handleDeath(sgDodgeFaggot);
                        }

                        SurvivalGames.getInstance().getGame().checkForEndGame();
                    }
                });
            }
        });*/
    }

}
