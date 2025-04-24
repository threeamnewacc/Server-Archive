package me.devkevin.practice.profile.manager;

import club.inverted.chatcolor.CC;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.player.CoreProfile;
import me.devkevin.practice.Practice;
import me.devkevin.practice.data.PracticeDatabase;
import me.devkevin.practice.file.Config;
import me.devkevin.practice.kit.Kit;
import me.devkevin.practice.kit.PlayerKit;
import me.devkevin.practice.match.timer.impl.EnderpearlTimer;
import me.devkevin.practice.party.Party;
import me.devkevin.practice.profile.Profile;
import me.devkevin.practice.profile.state.ProfileState;
import me.devkevin.practice.profile.task.SpawnPlayerVisibilityRunnable;
import me.devkevin.practice.queue.QueueEntry;
import me.devkevin.practice.util.InventoryUtil;
import me.devkevin.practice.util.ItemBuilder;
import me.devkevin.practice.util.PlayerUtil;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Copyright 10/04/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class ProfileManager {

    private final Practice plugin = Practice.getInstance();
    @Getter private final Map<UUID, Profile> profileData = new ConcurrentHashMap<>();

    public void createPlayerData(Player player) {
        Profile data = new Profile(player.getUniqueId());

        for (Kit ladder : plugin.getKitManager().getKits()) {
            data.getKits().put(ladder.getName(), new PlayerKit[4]);
        }

        data.setName(player.getName());
        data.setFollowing(false);

        this.profileData.put(data.getUuid(), data);
    }

    public void loadData(Profile profile) {


        /*
         * Loading player kits
         */
        Config config = new Config("/players/" + profile.getUuid().toString(), this.plugin);
        ConfigurationSection playerKitsSection = config.getConfig().getConfigurationSection("playerkits");
        if (playerKitsSection != null) {
            this.plugin.getKitManager().getKits().forEach((kit) -> {
                ConfigurationSection kitSection = playerKitsSection.getConfigurationSection(kit.getName());
                if (kitSection != null) {
                    kitSection.getKeys(false).forEach((kitKey) -> {
                        Integer kitIndex = Integer.parseInt(kitKey);
                        String displayName = kitSection.getString(kitKey + ".displayName");
                        ItemStack[] contents = (ItemStack[]) ((List) kitSection.get(kitKey + ".contents")).toArray(new ItemStack[0]);
                        PlayerKit playerKit = new PlayerKit(kit.getName(), kitIndex, contents, displayName);
                        profile.addPlayerKit(kitIndex, playerKit);
                    });
                }

            });
        }

        profile.setState(ProfileState.SPAWN);

        /*
         * Loading player stats.
         */
        Document document = Practice.getInstance().getPracticeDatabase().getProfiles().find(Filters.eq("uuid", profile.getUuid().toString())).first();

        if (document == null) {
            for (Kit kit : this.plugin.getKitManager().getKits()) {
                profile.setElo(kit.getName(), 1000);
                profile.setWins(kit.getName(), 0);
                profile.setLosses(kit.getName(), 0);
                profile.setCurrentWinstreak(kit.getName(), 0);
                profile.setHighestWinStreak(kit.getName(), 0);
            }

            this.saveData(profile);
            return;
        }

        profile.setName(document.getString("username"));

        Document statsDocument = (Document) document.get("stats");
        Document globalDocument = (Document) document.get("global");
        Document loadoutsDocument = (Document) document.get("loadouts");

        if (globalDocument == null) {
            profile.setGlobalElo(1000);
            profile.setMatchesPlayed(0);
            profile.setGlobalWinStreak(0);
            profile.setGlobalHighestWinStreak(0);
            return;
        }

        for (String key : loadoutsDocument.keySet()) {
            Kit ladder = Practice.getInstance().getKitManager().getKit(key);
            if (ladder == null) {
                continue;
            }

            JsonArray kitsArray = plugin.getJsonParser().parse(loadoutsDocument.getString(key)).getAsJsonArray();
            PlayerKit[] kits = new PlayerKit[4];
            for (JsonElement kitElement : kitsArray) {
                JsonObject kitObject = kitElement.getAsJsonObject();
                PlayerKit kit = new PlayerKit(kitObject.get("name").getAsString(), kitObject.get("index").getAsInt(), InventoryUtil.deserializeInventory(kitObject.get("contents").getAsString()), kitObject.get("name").getAsString());
                kit.setContents(InventoryUtil.deserializeInventory(kitObject.get("contents").getAsString()));
                kits[kitObject.get("index").getAsInt()] = kit;
            }

            profile.getKits().put(ladder.getName(), kits);
        }

        profile.setGlobalElo(globalDocument.getInteger("globalElo"));
        profile.setMatchesPlayed(globalDocument.getInteger("matchesPlayed"));
        profile.setGlobalWinStreak(globalDocument.getInteger("globalWinStreak"));
        profile.setGlobalHighestWinStreak(globalDocument.getInteger("highestGlobalWinStreak"));

        statsDocument.keySet().forEach(key -> {
            Document ladderDocument = (Document) statsDocument.get(key);
            if (ladderDocument == null) {
                System.out.println("Ladder document is null for ladder " + key);
                return;
            }

            if (ladderDocument.containsKey("elo")) {
                profile.getRankedElo().put(key, ladderDocument.getInteger("elo"));
            }
            if (ladderDocument.containsKey("wins")) {
                profile.getRankedWins().put(key, ladderDocument.getInteger("wins"));
            }
            if (ladderDocument.containsKey("losses")) {
                profile.getRankedLosses().put(key, ladderDocument.getInteger("losses"));
            }
            if (ladderDocument.containsKey("currentStreak")) {
                profile.getCurrentWinstreak().put(key, ladderDocument.getInteger("currentStreak"));
            }
            if (ladderDocument.containsKey("highestStreak")) {
                profile.getHighestWinStreak().put(key, ladderDocument.getInteger("highestStreak"));
            }
        });

        profile.setDataLoaded(true);
    }

    public void saveData(Profile profile) {
        if (profile == null) return;
        if (!profile.isDataLoaded()) return;

        Config config = new Config("/players/" + profile.getUuid().toString(), this.plugin);

        /*
         * Saving player kits
         */
        this.plugin.getKitManager().getKits().forEach(kit -> {
            Map<Integer, PlayerKit> playerKits = profile.getPlayerKits(kit.getName());

            if (playerKits != null) {
                playerKits.forEach((key, value) -> {
                    config.getConfig().set("playerkits." + kit.getName() + "." + key + ".displayName", value.getDisplayName());
                    config.getConfig().set("playerkits." + kit.getName() + "." + key + ".contents", value.getContents());
                });

                config.getConfig().set("profile.settings.duelRequests", profile.getOptions().isDuelRequests());
                config.getConfig().set("profile.settings.scoreboard", profile.getOptions().isScoreboard());
                config.getConfig().set("profile.settings.partyInvites", profile.getOptions().isPartyInvites());
                config.getConfig().set("profile.settings.spectators", profile.getOptions().isSpectators());
                config.getConfig().set("profile.settings.time", profile.getOptions().getTime().name());
            }
        });

        config.save();

        /*
         * Saving player stats.
         */
        Document document = new Document();
        Document statsDocument = new Document();
        Document globalDocument = new Document();
        Document loadoutsDocument = new Document();

        profile.getCurrentWinstreak().forEach((key, value) -> {
            if (key.equalsIgnoreCase("HCF")) return;

            Document ladderDocument;
            if (statsDocument.containsKey(key)) {
                ladderDocument = (Document) statsDocument.get(key);
            } else {
                ladderDocument = new Document();
            }

            ladderDocument.put("currentStreak", value);
            statsDocument.put(key, ladderDocument);
        });

        profile.getHighestWinStreak().forEach((key, value) -> {
            if (key.equalsIgnoreCase("HCF")) return;

            Document ladderDocument;
            if (statsDocument.containsKey(key)) {
                ladderDocument = (Document) statsDocument.get(key);
            } else {
                ladderDocument = new Document();
            }

            ladderDocument.put("highestStreak", value);
            statsDocument.put(key, ladderDocument);
        });

        profile.getRankedWins().forEach((key, value) -> {
            if (key.equalsIgnoreCase("HCF")) return;

            Document ladderDocument;
            if (statsDocument.containsKey(key)) {
                ladderDocument = (Document) statsDocument.get(key);
            } else {
                ladderDocument = new Document();
            }

            ladderDocument.put("wins", value);
            statsDocument.put(key, ladderDocument);
        });

        profile.getRankedLosses().forEach((key, value) -> {
            if (key.equalsIgnoreCase("HCF")) return;

            Document ladderDocument;
            if (statsDocument.containsKey(key)) {
                ladderDocument = (Document) statsDocument.get(key);
            } else {
                ladderDocument = new Document();
            }

            ladderDocument.put("losses", value);
            statsDocument.put(key, ladderDocument);
        });

        profile.getRankedElo().forEach((key, value) -> {
            if (key.equalsIgnoreCase("HCF")) return;

            Document ladderDocument;
            if (statsDocument.containsKey(key)) {
                ladderDocument = (Document) statsDocument.get(key);
            } else {
                ladderDocument = new Document();
            }

            ladderDocument.put("elo", value);
            statsDocument.put(key, ladderDocument);
        });

        int kits = 0, count = 0;
        for (Kit kit : this.plugin.getKitManager().getRankedKits()) {
            if (profile.getRankedElo().get(kit.getName()) == null) continue;
            kits += profile.getRankedElo().get(kit.getName());
            count++;
        }
        if (kits == 1) kits = 0;
        if (count == 0) count = 1;

        profile.getKits().forEach((key, value) -> {
            JsonArray kitsArray = new JsonArray();
            for (int i = 0; i < 4; i++) {
                PlayerKit kit = value[i];
                if (kit != null) {
                    JsonObject kitObject = new JsonObject();
                    kitObject.addProperty("index", i);
                    kitObject.addProperty("name", kit.getName());
                    kitObject.addProperty("contents", InventoryUtil.serializeInventory(kit.getContents()));

                    kitsArray.add(kitObject);
                }
            }

            loadoutsDocument.put(key, kitsArray.toString());
        });

        globalDocument.put("globalElo", (kits / count));
        globalDocument.put("matchesPlayed", profile.getMatchesPlayed());

        globalDocument.put("globalWinStreak", profile.getGlobalWinStreak());
        globalDocument.put("highestGlobalWinStreak", profile.getGlobalHighestWinStreak());

        document.put("uuid", profile.getUuid().toString());
        document.put("username", profile.getName());
        document.put("stats", statsDocument);
        document.put("global", globalDocument);
        document.put("loadouts", loadoutsDocument);

        JSONObject data = new JSONObject();

        for (Kit kit : plugin.getKitManager().getRankedKits()) {
            JSONObject kitData = new JSONObject();

            kitData.put("elo", profile.getElo(kit.getName()));
            kitData.put("party-elo", profile.getPartyElo(kit.getName()));

            data.put(kit, kitData);
        }

        Practice.getInstance().getPracticeDatabase().getProfiles().replaceOne(Filters.eq("uuid", profile.getUuid().toString()), document, new ReplaceOptions().upsert(true));
    }


    public void removePlayerData(UUID uuid) {
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, ()
                -> {
            ProfileManager.this.saveData(ProfileManager.this.profileData.get(uuid));
            ProfileManager.this.profileData.remove(uuid);
        });
    }

    public Collection<Profile> getAllData() {
        return profileData.values();
    }

    public Profile getProfileData(UUID uuid) {

        if (Bukkit.getPlayer(uuid) != null) {
            if (!this.profileData.containsKey(uuid)) {
                createPlayerData(Bukkit.getPlayer(uuid));
            }
        }

        return this.profileData.get(uuid);
    }

    public void giveSpawnItems(Player player) {
        boolean inParty = this.plugin.getPartyManager().getParty(player.getUniqueId()) != null;
        boolean inEvent = this.plugin.getEventManager().getEventPlaying(player) != null;
        boolean isRematching = this.plugin.getMatchManager().isRematching(player.getUniqueId());
        ItemStack[] items = this.plugin.getHotbarItem().getSpawnItems();

        boolean inQueue = plugin.getQueue().getQueueEntry(player.getUniqueId()) != null;

        if (inParty) {
            items = this.plugin.getHotbarItem().getPartyItems();
        } else if (inEvent) {
            items = this.plugin.getHotbarItem().getEventItems();
        } else if (inQueue) {
            items = this.plugin.getHotbarItem().getQueueItems();
        }

        player.getInventory().setContents(items);
        if (inParty) {
            Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
            player.getInventory().setItem(3, new ItemBuilder(Material.NETHER_STAR)
                    .name(LandCore.getInstance().getProfileManager().getProfile(party.getLeader()).getGrant().getRank().getColor() + Bukkit.getPlayer(party.getLeader()).getName() + CC.GRAY + "'s Party")
                    .build());
        }

        if (plugin.getMatchManager().hasPlayAgainRequest(player.getUniqueId())) {
            player.getInventory().addItem(plugin.getHotbarItem().getPlayAgain());
        }

        player.updateInventory();
    }

    public Kit kit;

    public void sendToSpawn(Player player) {
        Profile profile = ProfileManager.this.getProfileData(player.getUniqueId());
        Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
        QueueEntry queue = this.plugin.getQueue().getQueueEntry(player.getUniqueId());

        if (queue != null) {
            profile.setState(ProfileState.QUEUE);
        } else if (party != null) {
            profile.setState(ProfileState.PARTY);
        } else {
            profile.setState(ProfileState.SPAWN);
        }

        if (!player.isOnline()) {
            return;
        }

        this.plugin.getTimerManager().getTimer(EnderpearlTimer.class).clearCooldown(player.getUniqueId());

        PlayerUtil.reset(player);

        giveSpawnItems(player);

        player.teleport(this.plugin.getCustomLocationManager().getSpawn().toBukkitLocation());

        // tp player to random spawns
        /*Random random = new Random();
        int randomIndex = random.nextInt(getSpawnsLocations().length);
        player.teleport(getSpawnsLocations()[randomIndex].toBukkitLocation());*/

        if (party != null) {
            party.getMembers().stream().map(Bukkit::getPlayer).filter(Objects::nonNull).forEach(member -> {
                member.showPlayer(player);
                player.showPlayer(member);
            });
        }

        CoreProfile coreProfile = LandCore.getInstance().getProfileManager().getProfile(player.getUniqueId());

        if (coreProfile.hasDonor()) {
            player.setAllowFlight(true);
            player.setFlying(true);
        }

        updatePlayerView();
    }

    public void sendToSpawnAndResetButNotTP(Player player) {
        Profile profile = plugin.getProfileManager().getProfileData(player.getUniqueId());
        Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
        QueueEntry queue = this.plugin.getQueue().getQueueEntry(player.getUniqueId());

        if (queue != null) {
            profile.setState(ProfileState.QUEUE);
        } else if (party != null) {
            profile.setState(ProfileState.PARTY);
        } else {
            profile.setState(ProfileState.SPAWN);
        }

        if (!player.isOnline()) {
            return;
        }

        this.plugin.getTimerManager().getTimer(EnderpearlTimer.class).clearCooldown(player.getUniqueId());

        PlayerUtil.reset(player);

        giveSpawnItems(player);

        // we won't that when players left to queue or something don't get teleported again to spawn
        if (!player.getLocation().getWorld().getName()
                .equals(plugin.getCustomLocationManager().getSpawn().toBukkitLocation().getWorld().getName())) {
            player.teleport(plugin.getCustomLocationManager().getSpawn().toBukkitLocation());
        }

        if (party != null) {
            party.getMembers().stream().map(Bukkit::getPlayer).filter(Objects::nonNull).forEach(member -> {
                member.showPlayer(player);
                player.showPlayer(member);
            });
        }

        CoreProfile coreProfile = LandCore.getInstance().getProfileManager().getProfile(player.getUniqueId());

        if (coreProfile.hasDonor()) {
            player.setAllowFlight(true);
            player.setFlying(true);
        }

        updatePlayerView();
    }

    /*public CustomLocation[] getSpawnsLocations() {
        CustomLocation[] array = new CustomLocation[4];
        array[0] = Practice.getInstance().getCustomLocationManager().getSpawn();
        array[1] = Practice.getInstance().getCustomLocationManager().getSpawn2();
        array[2] = Practice.getInstance().getCustomLocationManager().getSpawn3();
        array[3] = Practice.getInstance().getCustomLocationManager().getSpawn4();
        return array;
    }*/

    public MongoCursor<Document> getPlayersSortByLadderElo(Kit ladder) {
        final Document sort = new Document();
        sort.put("stats." + ladder.getName() + ".elo", -1);

        return PracticeDatabase.getInstance().getProfiles().find().sort(sort).limit(10).iterator();
    }

    public MongoCursor<Document> getPlayersSortedByDocumentElo(String document) {
        final Document sort = new Document();
        sort.put("global." + document, -1);

        return PracticeDatabase.getInstance().getProfiles().find().sort(sort).limit(10).iterator();
    }

    public void updatePlayerView() {
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new SpawnPlayerVisibilityRunnable());
    }
}
