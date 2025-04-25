package us.zonix.practice.managers;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.Sorts;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import us.zonix.practice.Practice;
import us.zonix.practice.events.sumo.SumoEvent;
import us.zonix.practice.file.Config;
import us.zonix.practice.kit.Kit;
import us.zonix.practice.kit.PlayerKit;
import us.zonix.practice.mongo.PracticeMongo;
import us.zonix.practice.player.EloRank;
import us.zonix.practice.player.PlayerData;
import us.zonix.practice.player.PlayerState;
import us.zonix.practice.util.ItemUtil;
import us.zonix.practice.util.PlayerUtil;
import us.zonix.practice.util.timer.impl.EnderpearlTimer;

public class PlayerManager {
    private final Practice plugin = Practice.getInstance();
    private final Map<UUID, PlayerData> playerData = new ConcurrentHashMap<>();

    public void createPlayerData(Player player) {
        PlayerData data = new PlayerData(player.getUniqueId());
        this.playerData.put(data.getUniqueId(), data);
        this.loadData(data);
    }

    private void loadData(PlayerData playerData) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            playerData.setPlayerState(PlayerState.SPAWN);
            Document document = (Document)PracticeMongo.getInstance().getPlayers().find(Filters.eq("uuid", playerData.getUniqueId().toString())).first();
            if (document == null) {
                this.saveData(playerData);
            } else {
                Document statisticsDocument = (Document)document.get("statistics");
                if (statisticsDocument != null) {
                    try {
                        statisticsDocument.keySet().forEach(key -> {
                            Document ladderDocument = (Document)statisticsDocument.get(key);
                            if (ladderDocument.containsKey("rankedElo")) {
                                playerData.getRankedElo().put(key, ladderDocument.getInteger("rankedElo"));
                            }

                            if (ladderDocument.containsKey("rankedWins")) {
                                playerData.getRankedWins().put(key, ladderDocument.getInteger("rankedWins"));
                            }

                            if (ladderDocument.containsKey("rankedLosses")) {
                                playerData.getRankedLosses().put(key, ladderDocument.getInteger("rankedLosses"));
                            }
                        });
                    } catch (Exception var5) {
                        var5.printStackTrace();
                    }
                }

                if (document.containsKey("totalRankedMatches")) {
                    playerData.setPlayedRanked(document.getInteger("totalRankedMatches"));
                }

                if (document.containsKey("totalUnrankedMatches")) {
                    playerData.setPlayedUnranked(document.getInteger("totalUnrankedMatches"));
                }

                if (document.containsKey("totalUnrankedWins")) {
                    playerData.setUnrankedWins(document.getInteger("totalUnrankedWins"));
                }

                if (document.containsKey("partyStatistics")) {
                    Document partyStats = (Document)document.get("partyStatistics", Document.class);
                    playerData.setPlayedBard(partyStats.getInteger("playedBard"));
                    playerData.setPlayedArcher(partyStats.getInteger("playedArcher"));
                }

                this.saveConfigPlayerData(playerData);
            }
        });
    }

    public void removePlayerData(UUID uuid) {
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
            this.saveData(this.playerData.get(uuid));
            this.playerData.remove(uuid);
        });
    }

    public void saveData(PlayerData playerData) {
        if (playerData != null) {
            Document document = new Document();
            Document statisticsDocument = new Document();
            playerData.getRankedWins().forEach((key, value) -> {
                Document ladderDocument;
                if (statisticsDocument.containsKey(key)) {
                    ladderDocument = (Document)statisticsDocument.get(key);
                } else {
                    ladderDocument = new Document();
                }

                ladderDocument.put("rankedWins", value);
                statisticsDocument.put(key, ladderDocument);
            });
            playerData.getRankedLosses().forEach((key, value) -> {
                Document ladderDocument;
                if (statisticsDocument.containsKey(key)) {
                    ladderDocument = (Document)statisticsDocument.get(key);
                } else {
                    ladderDocument = new Document();
                }

                ladderDocument.put("rankedLosses", value);
                statisticsDocument.put(key, ladderDocument);
            });
            playerData.getRankedElo().forEach((key, value) -> {
                Document ladderDocument;
                if (statisticsDocument.containsKey(key)) {
                    ladderDocument = (Document)statisticsDocument.get(key);
                } else {
                    ladderDocument = new Document();
                }

                ladderDocument.put("rankedElo", value);
                statisticsDocument.put(key, ladderDocument);
            });
            document.put("uuid", playerData.getUniqueId().toString());
            document.put("statistics", statisticsDocument);
            document.put("totalRankedMatches", playerData.getPlayedRanked());
            document.put("totalUnrankedMatches", playerData.getPlayedUnranked());
            document.put("totalUnrankedWins", playerData.getUnrankedWins());
            document.put(
                "partyStatistics", new Document().append("playedBard", playerData.getPlayedBard()).append("playedArcher", playerData.getPlayedArcher())
            );
            document.put("eloRank", EloRank.getRankByElo(playerData.getGlobalStats("ELO")).name());
            PracticeMongo.getInstance()
                .getPlayers()
                .replaceOne(Filters.eq("uuid", playerData.getUniqueId().toString()), document, new ReplaceOptions().upsert(true));
            Config config = new Config("/players/" + playerData.getUniqueId().toString(), this.plugin);
            this.plugin.getKitManager().getKits().forEach(kit -> {
                Map<Integer, PlayerKit> playerKits = playerData.getPlayerKits(kit.getName());
                if (playerKits != null) {
                    playerKits.forEach((key, value) -> {
                        config.getConfig().set("playerkits." + kit.getName() + "." + key + ".displayName", value.getDisplayName());
                        config.getConfig().set("playerkits." + kit.getName() + "." + key + ".contents", value.getContents());
                    });
                }
            });
            config.save();
        }
    }

    public Map<String, Integer> getEloByKit(Kit kit) {
        Map<String, Integer> eloHash = new HashMap<>();
        FindIterable<Document> documents = PracticeMongo.getInstance().getPlayers().find();
        if (documents == null) {
            return null;
        } else {
            MongoCursor var4 = documents.iterator();

            while (var4.hasNext()) {
                Document document = (Document)var4.next();
                Document statisticsDocument = (Document)document.get("statistics");
                if (statisticsDocument != null) {
                    Document ladderDocument = (Document)statisticsDocument.get(kit.getName());
                    if (ladderDocument.containsKey("rankedElo")) {
                        eloHash.put(document.getString("uuid"), ladderDocument.getInteger("rankedElo"));
                    }
                }
            }

            return eloHash;
        }
    }

    public Collection<PlayerData> getAllData() {
        return this.playerData.values();
    }

    public PlayerData getPlayerData(UUID uuid) {
        PlayerData playerData = this.playerData.get(uuid);
        if (playerData == null) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                this.createPlayerData(player);
                playerData = this.playerData.get(uuid);
            }
        }

        return playerData;
    }

    public List<Player> getPlayersByState(PlayerState state) {
        return Bukkit.getOnlinePlayers()
            .parallelStream()
            .filter(player -> this.getPlayerData(player.getUniqueId()).getPlayerState().equals(state))
            .collect(Collectors.toList());
    }

    public void giveLobbyItems(Player player) {
        boolean inParty = this.plugin.getPartyManager().getParty(player.getUniqueId()) != null;
        boolean inTournament = this.plugin.getTournamentManager().getTournament(player.getUniqueId()) != null;
        boolean inEvent = this.plugin.getEventManager().getEventPlaying(player) != null;
        boolean inSumo = this.plugin.getEventManager().getEventPlaying(player) != null
            && this.plugin.getEventManager().getEventPlaying(player) instanceof SumoEvent;
        boolean isRematching = this.plugin.getMatchManager().isRematching(player.getUniqueId());
        ItemStack[] items = this.plugin.getItemManager().getSpawnItems();
        if (inTournament) {
            items = this.plugin.getItemManager().getTournamentItems();
        } else if (inSumo) {
            items = this.plugin.getItemManager().getSumoItems();
        } else if (inEvent) {
            items = this.plugin.getItemManager().getEventItems();
        } else if (inParty) {
            items = this.plugin.getItemManager().getPartyItems();
        }

        player.getInventory().setContents(items);
        if (isRematching && !inParty && !inTournament && !inEvent) {
            player.getInventory().setItem(3, ItemUtil.createItem(Material.INK_SACK, ChatColor.GREEN + "Rematch", 1, (short)10));
            player.getInventory().setItem(5, ItemUtil.createItem(Material.PAPER, ChatColor.YELLOW + "Inventories", 1, (short)0));
        }

        player.updateInventory();
    }

    private void saveConfigPlayerData(PlayerData playerData) {
        Config config = new Config("/players/" + playerData.getUniqueId().toString(), this.plugin);
        ConfigurationSection playerKitsSection = config.getConfig().getConfigurationSection("playerkits");
        if (playerKitsSection != null) {
            this.plugin.getKitManager().getKits().forEach(kit -> {
                ConfigurationSection kitSection = playerKitsSection.getConfigurationSection(kit.getName());
                if (kitSection != null) {
                    kitSection.getKeys(false).forEach(kitKey -> {
                        Integer kitIndex = Integer.parseInt(kitKey);
                        String displayName = kitSection.getString(kitKey + ".displayName");
                        ItemStack[] contents = (kitSection.getList(kitKey + ".contents")).toArray(new ItemStack[0]);
                        PlayerKit playerKit = new PlayerKit(kit.getName(), kitIndex, contents, displayName);
                        playerData.addPlayerKit(kitIndex, playerKit);
                    });
                }
            });
        }
    }

    public HashMap<String, Integer> findTopEloByKit(String kitName, int limit) {
        HashMap<String, Integer> eloMap = new HashMap<>();
        FindIterable<Document> documents = PracticeMongo.getInstance()
            .getPlayers()
            .find()
            .sort(Sorts.descending(new String[]{"statistics." + kitName + ".rankedElo"}))
            .limit(limit);
        MongoCursor var5 = documents.iterator();

        while (var5.hasNext()) {
            Document document = (Document)var5.next();
            Document statisticsDocument = (Document)document.get("statistics");
            if (statisticsDocument != null) {
                Document ladderDocument = (Document)statisticsDocument.get(kitName);
                if (ladderDocument != null) {
                    eloMap.put(document.getString("uuid"), ladderDocument.getInteger("rankedElo"));
                }
            }
        }

        return eloMap.entrySet()
                .stream()
                .sorted(Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Entry::getKey,
                        Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    public void sendToSpawnAndReset(Player player) {
        PlayerData playerData = this.getPlayerData(player.getUniqueId());
        playerData.setPlayerState(PlayerState.SPAWN);
        playerData.setBestOfThreeDuel(false);
        playerData.setBestOfThreeKit(null);
        playerData.setBestOfThreeArena(null);
        playerData.setBestOfThreeQueueType(null);
        PlayerUtil.clearPlayer(player);
        this.plugin.getTimerManager().getTimer(EnderpearlTimer.class).clearCooldown(player.getUniqueId());
        this.giveLobbyItems(player);
        if (player.isOnline()) {
            if (this.plugin.getSpawnManager().getSpawnLocation() == null) {
                player.teleport(player.getWorld().getSpawnLocation());
            } else {
                player.teleport(this.plugin.getSpawnManager().getSpawnLocation().toBukkitLocation());
            }

            this.plugin
                .getServer()
                .getOnlinePlayers()
                .forEach(
                    p -> {
                        boolean playerSeen = playerData.getOptions().isVisibility()
                            && player.hasPermission("practice.visibility")
                            && Practice.getInstance().getPlayerManager().getPlayerData(player.getUniqueId()).getPlayerState() == PlayerState.SPAWN;
                        boolean pSeen = playerData.getOptions().isVisibility()
                            && player.hasPermission("practice.visibility")
                            && Practice.getInstance().getPlayerManager().getPlayerData(p.getUniqueId()).getPlayerState() == PlayerState.SPAWN;
                        if (playerSeen) {
                            p.showPlayer(player);
                        } else {
                            p.hidePlayer(player);
                        }

                        if (pSeen) {
                            player.showPlayer(p);
                        } else {
                            player.hidePlayer(p);
                        }
                    }
                );
            if (player.hasPermission("practice.fly")) {
                player.setAllowFlight(true);
                player.setFlying(true);
            }
        }
    }
}
