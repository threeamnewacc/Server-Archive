package net.badlion.survivalgames;

import net.badlion.common.libraries.HTTPCommon;
import net.badlion.common.libraries.exceptions.HTTPRequestFailException;
import net.badlion.cosmetics.Cosmetics;
import net.badlion.gberry.Gberry;
import net.badlion.ministats.Game;
import net.badlion.ministats.MiniStats;
import net.badlion.ministats.PlayerData;
import net.badlion.ministats.managers.DatabaseManager;
import net.badlion.survivalgames.listeners.MiniStatsListener;
import net.badlion.survivalgames.managers.RatingManager;
import net.badlion.survivalgames.managers.SGPlayerManager;
import net.badlion.survivalgames.tasks.DeathMatchNoMovingTask;
import net.badlion.survivalgames.tasks.LoadMapTask;
import net.badlion.survivalgames.tasks.PreGameStartTask;
import net.badlion.survivalgames.bukkitevents.PlayerRatingChangeEvent;
import net.badlion.survivalgames.bukkitevents.SGGameStartEvent;
import net.badlion.survivalgames.gamemodes.GameMode;
import net.badlion.survivalgames.inventories.SkullPlayerInventory;
import net.badlion.survivalgames.util.BukkitUtil;
import net.badlion.gberry.utils.FireWorkUtil;
import net.badlion.survivalgames.util.RatingUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.*;

public class SGGame extends Game {

    public static int NUM_OF_PLAYERS_FOR_DEATH_MATCH = 4;

    private GameMode gameMode;
    private SGWorld sgWorld;
    private Set<Inventory> tier1Chests = new HashSet<>();
    private Set<Inventory> tier2Chests = new HashSet<>();

    private boolean deathMatch = false;
    private Random random = new Random();
    private Set<UUID> uuids = new HashSet<>();
    private List<Integer> ratings;
    private int position = 0;
    private boolean chestsRefilled = false;

    public SGGame(GameMode gameMode, SGWorld sgWorld) {
        // Stub
        super(sgWorld.getgWorld());
        SurvivalGames.getInstance().setGame(this);

        this.region.setAllowBrokenBlocks(true);

        this.sgWorld = sgWorld;
        this.gameMode = gameMode;

        this.sgWorld.load();

        Set<SGPlayer> sgPlayers = SGPlayerManager.getPlayersByState(SGPlayer.State.ALIVE);
        for (SGPlayer sgPlayer : sgPlayers) {
            this.uuids.add(sgPlayer.getUuid());
        }

        new LoadMapTask(this).runTaskTimer(SurvivalGames.getInstance(), 14, 14);

        this.startGameCountdown();

        SurvivalGames.getInstance().setState(SurvivalGames.SGState.START_COUNTDOWN);

        // Register special listener for game mode
        SurvivalGames.getInstance().getServer().getPluginManager().registerEvents((Listener) this.gameMode, SurvivalGames.getInstance());

	    // Disable pets/particles
	    //SmellyParticles.getInstance().disallowParticles();
	    //SmellyPets.getInstance().disallowPets();
    }

    public Location getSpawnLocation(int i) {
        return this.sgWorld.getSpawnLocations().get(i);
    }

    public void handleDeath(final SGPlayer sgPlayer) {
        SurvivalGames.getInstance().getGame().getUuids().remove(sgPlayer.getUuid());

	    // Remove skull for spectate inventory
	    SkullPlayerInventory.removeSkullForPlayer(sgPlayer);

        SGPlayerManager.updateState(sgPlayer.getUuid(), SGPlayer.State.SPECTATOR);

        // Possible they quit before the game starts
        if (sgPlayer.getStartTime() != 0) {
            SurvivalGames.getInstance().getMiniStats().getPlayerDataListener().getPlayerData(sgPlayer.getUuid()).addTotalTimePlayed((System.currentTimeMillis() - sgPlayer.getStartTime()) / 1000);
        }

        this.handleRating(sgPlayer, 0.0);
    }

    private void handleRating(final SGPlayer sgPlayer, final Double winOrLoss) {
        final Ladder ladder = Ladder.getLadder(SGGame.this.gameMode.name(), Ladder.LadderType.FFA);
        int oldRating = RatingManager.getPlayerRating(sgPlayer.getUuid(), ladder);

        // Account for the rating changes based on our algorithm
        Bukkit.getLogger().info("rating diff " + sgPlayer.calculateRatingDiff());
        sgPlayer.setPosition(this.position);
        int ratingDiff = RatingUtil.calculateRatingDiff(this.ratings, oldRating + sgPlayer.calculateRatingDiff(), this.position--);
        final int newRating = oldRating + ratingDiff;

        MiniStatsListener.oldRatings.put(sgPlayer.getUuid(), oldRating);
        MiniStatsListener.newRatings.put(sgPlayer.getUuid(), newRating);

        SurvivalGames.getInstance().getServer().getScheduler().runTaskAsynchronously(SurvivalGames.getInstance(), new Runnable() {
            @Override
            public void run() {
                RatingManager.setGroupRating(sgPlayer.getUuid(), ladder, newRating, winOrLoss);

                // Fire off event for smelly
                BukkitUtil.runTask(new Runnable() {
                    @Override
                    public void run() {
                        Player p = SurvivalGames.getInstance().getServer().getPlayer(sgPlayer.getUuid());
                        if (p == null) {
                            return;
                        }

                        PlayerRatingChangeEvent event = new PlayerRatingChangeEvent(p, newRating, RatingManager.getPlayerGlobalRating(sgPlayer.getUuid()), ladder.getName());
                        SurvivalGames.getInstance().getServer().getPluginManager().callEvent(event);
                    }
                });

                if (SurvivalGames.getInstance().getApi()) {
                    try {
                        HTTPCommon.executeDELETERequest(SurvivalGames.getInstance().getApiURL() + "PlayerDead/" + sgPlayer.getUuid() + "/" + SurvivalGames.getInstance().getApiKey());
                    } catch (HTTPRequestFailException e) {
                        Bukkit.getLogger().info("Failed to save DB information for " + sgPlayer.getUuid() + " " + sgPlayer.getUsername());
                    }
                }
            }
        });
    }

    public void startGameCountdown() {
        // Move everyone to spawns
        Map<UUID, Location> locationMap = new HashMap<>();
        Set<SGPlayer> sgPlayers = SGPlayerManager.getPlayersByState(SGPlayer.State.ALIVE);
        LinkedList<SGPlayer> playingSGPlayers = new LinkedList<>();
	    LinkedList<String> playingPlayers = new LinkedList<>();
        int i = 0;
        for (Iterator it = sgPlayers.iterator(); it.hasNext(); ) {
            SGPlayer sgPlayer = (SGPlayer) it.next();
            Player player = SurvivalGames.getInstance().getServer().getPlayer(sgPlayer.getUuid());
            if (player != null) {
	            player.getInventory().clear();
	            for (PotionEffect effect : player.getActivePotionEffects()) {
		            player.removePotionEffect(effect.getType());
	            }

                locationMap.put(player.getUniqueId(), this.getSpawnLocation(i));
                player.teleport(this.getSpawnLocation(i));

	            // Add to our list for skulls for spectate inventory
	            playingPlayers.add(player.getPlayerListName());
                playingSGPlayers.add(sgPlayer);

                PlayerData playerData = new PlayerData(player.getUniqueId(), player.getName(), this.getGWorld().getNiceWorldName());
                SurvivalGames.getInstance().getMiniStats().getPlayerDataListener().getPlayerDataMap().put(player.getUniqueId(), playerData);
            } else {
	            Bukkit.getLogger().severe("SG PLAYER WAS NULL, SG OBJECT PUT INTO SPECTATOR");
                it.remove();
                SGPlayerManager.updateState(sgPlayer.getUuid(), SGPlayer.State.SPECTATOR);
            }

            ++i;
        }

	    // Sort our list
	    Collections.sort(playingPlayers);

        // Add their ratings to a list for calculations
        List<Integer> ratings = new ArrayList<>();
        for (SGPlayer sgPlayer : playingSGPlayers) {
            ratings.add(RatingManager.getPlayerRating(sgPlayer.getUuid(), Ladder.getLadder("Classic", Ladder.LadderType.FFA)));
        }
        SurvivalGames.getInstance().setRatings(ratings);
        this.setRatings(ratings);

	    // Add skulls for spectate inventory
	    SkullPlayerInventory.addSkullForPlayer(playingPlayers);

	    SurvivalGames.getInstance().getServer().getPluginManager().callEvent(new SGGameStartEvent());

        new PreGameStartTask(locationMap).runTaskTimer(SurvivalGames.getInstance(), 0, 1);

        // Disable cosmetics
        Cosmetics.getInstance().setCosmeticEnabled(Cosmetics.CosmeticType.GADGET, false);
        Cosmetics.getInstance().setCosmeticEnabled(Cosmetics.CosmeticType.MORPH, false);
        Cosmetics.getInstance().setCosmeticEnabled(Cosmetics.CosmeticType.PET, false);
        Cosmetics.getInstance().setCosmeticEnabled(Cosmetics.CosmeticType.PARTICLE, false);
    }

    public void fillAllChests() {
	    this.fillTier1Chests();
        this.fillTier2Chests();
    }

    public void fillTier1Chests() {
	    for (Inventory inventory : this.tier1Chests) {
            this.fillChestCommon(inventory, 1);
        }
    }

    public void fillTier2Chests() {
        for (Inventory inventory : this.tier2Chests) {
            this.fillChestCommon(inventory, 2);
        }
    }

    public void fillChestCommon(Inventory inventory, int tier) {
	    for (ItemStack item : inventory.getContents()) {
            if (item != null && item.getType() != Material.AIR) {
	            return;
            }
        }

        int numOfItems = tier == 1 ? random.nextInt(3) + 4 : random.nextInt(3) + 3;
        Set<Integer> items = new HashSet<>();
        Set<Material> materials = new HashSet<>();

        for (int i = 0; i < numOfItems; i++) {
            int slot;
            do {
                slot = random.nextInt(27);
            } while (items.contains(slot));

            items.add(slot);

            // Get a unique item for the chest
            ItemStack itemStack;
            if (tier == 1) {
                itemStack = this.gameMode.getTier1Item();
            } else {
                itemStack = this.gameMode.getTier2Item();
            }

            while (materials.contains(itemStack.getType())) {
                if (tier == 1) {
                    itemStack = this.gameMode.getTier1Item();
                } else {
                    itemStack = this.gameMode.getTier2Item();
                }
            }

            materials.add(itemStack.getType());
            inventory.setItem(slot, itemStack);
        }
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public boolean checkForEndGame() {
        Set<SGPlayer> sgPlayers = SGPlayerManager.getPlayersByState(SGPlayer.State.ALIVE);

        if (sgPlayers.size() == 1) {
            SGPlayer sgPlayer = sgPlayers.iterator().next();
            Player player = SurvivalGames.getInstance().getServer().getPlayer(sgPlayer.getUuid());

            SurvivalGames.getInstance().getMiniStats().getPlayerDataListener().getPlayerData(sgPlayer.getUuid()).addTotalTimePlayed((System.currentTimeMillis() - sgPlayer.getStartTime()) / 1000);

            if (player != null) {
                this.setEndTime(System.currentTimeMillis());
                this.addWinner(player.getUniqueId());

                this.handleRating(sgPlayer, 1.0);

                for (Location location : this.sgWorld.getSpawnLocations()) {
                    FireWorkUtil.shootFirework(location);
                }

                for (Location location : this.sgWorld.getDeathMatchLocations()) {
                    FireWorkUtil.shootFirework(location);
                }

                Gberry.broadcastMessageNoBalance(SurvivalGames.SG_PREFIX + ChatColor.GREEN + player.getPlayerListName() + ChatColor.GOLD + " has won the match!");
                Gberry.broadcastMessageNoBalance(SurvivalGames.SG_PREFIX + ChatColor.DARK_GREEN + "Server will reboot in 10 seconds.");
                SurvivalGames.getInstance().setState(SurvivalGames.SGState.END);

                SurvivalGames.getInstance().getServer().getScheduler().runTaskLater(SurvivalGames.getInstance(), new Runnable() {
                                  public void run() {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
                    }
                }, 20 * 10);

                // Stop tracking data
                SurvivalGames.getInstance().getMiniStats().stopListening();

                SurvivalGames.getInstance().getServer().getScheduler().runTaskAsynchronously(SurvivalGames.getInstance(), new Runnable() {
                        @Override
                        public void run() {
                        DatabaseManager.saveMatchData(SurvivalGames.getInstance().getGame());
                    }
                });

                return true;
            }
        }

        return false;
    }

    public void startDeathMatch() {
        // Go through, teleport them to death match arena and start next timer
        Map<UUID, Location> locationMap = new HashMap<>();
        Set<SGPlayer> aliveSGPlayers = SGPlayerManager.getPlayersByState(SGPlayer.State.ALIVE);
        int i = 0;
        for (Iterator it = aliveSGPlayers.iterator(); it.hasNext();) {
            SGPlayer sgPlayer = (SGPlayer) it.next();
            Player player = SurvivalGames.getInstance().getServer().getPlayer(sgPlayer.getUuid());
            if (player != null) {
                // Just restart the locations if we have a deathmatch arena and we have too many people
                if (this.sgWorld.getgWorld().getYml().getBoolean("deathmatch_arena") &&
                            i == SurvivalGames.getInstance().getGame().getSgWorld().getDeathMatchLocations().size()) {
                    i = 0;
                }

                locationMap.put(player.getUniqueId(), SurvivalGames.getInstance().getGame().getDeathMatchLocation(i));
                player.teleport(SurvivalGames.getInstance().getGame().getDeathMatchLocation(i));
            } else {
                // They are offline, they lose
                it.remove();
                SurvivalGames.getInstance().getGame().handleDeath(sgPlayer);
            }

            ++i;
        }

        // TP spectators
        String[] locValues = this.getGWorld().getYml().getString("deathmatch_spectator_location").split(" ");
        Location spectatorLocation = new Location(Bukkit.getWorld(this.getGWorld().getInternalName()), Double.parseDouble(locValues[0]), Double.parseDouble(locValues[1]), Double.parseDouble(locValues[2]), Float.parseFloat(locValues[3]), Float.parseFloat(locValues[4]));

        Set<SGPlayer> sgPlayers = SGPlayerManager.getPlayersByState(SGPlayer.State.SPECTATOR);
        for (SGPlayer sgPlayer : sgPlayers) {
            Player player = SurvivalGames.getInstance().getServer().getPlayer(sgPlayer.getUuid());
            if (player != null) {
                player.teleport(spectatorLocation);
            }
        }

        // Go to alive players and punish those who haven't contributed
        for (SGPlayer sgPlayer : aliveSGPlayers) {
            PlayerData playerData = MiniStats.getInstance().getPlayerDataListener().getPlayerDataMap().get(sgPlayer.getUuid());
            if (playerData.getDamageDealt() >= 20) { // 10 hearts
                sgPlayer.setDid10HeartsOfDmgBeforeDM(true);
            }
        }


        SurvivalGames.getInstance().setState(SurvivalGames.SGState.DEATH_MATCH_COUNTDOWN);

        new DeathMatchNoMovingTask(locationMap).runTaskTimer(SurvivalGames.getInstance(), 0, 1);
    }

    public boolean isDeathMatch() {
        return deathMatch;
    }

    public void setDeathMatch(boolean deathMatch) {
        this.deathMatch = deathMatch;
    }

    public Location getDeathMatchLocation(int i) {
        if (!this.sgWorld.getgWorld().getYml().getBoolean("deathmatch_arena")) {
            return this.sgWorld.getSpawnLocations().get(i);
        }

        return this.sgWorld.getDeathMatchLocations().get(i);
    }

    public void saveStatsAndEndGame() {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
    }

    public SGWorld getSgWorld() {
        return sgWorld;
    }

    public Set<UUID> getUuids() {
        return uuids;
    }

    public void setRatings(List<Integer> ratings) {
        this.ratings = ratings;
        Gberry.log("RATING", "Ratings: " + this.ratings.toString());
        this.position = ratings.size() - 1;
    }

    public Set<Inventory> getTier1Chests() {
        return tier1Chests;
    }

    public Set<Inventory> getTier2Chests() {
        return tier2Chests;
    }

    public boolean isChestsRefilled() {
        return chestsRefilled;
    }

    public void setChestsRefilled(boolean chestsRefilled) {
        this.chestsRefilled = chestsRefilled;
    }

    public List<Integer> getRatings() {
        return ratings;
    }
}
