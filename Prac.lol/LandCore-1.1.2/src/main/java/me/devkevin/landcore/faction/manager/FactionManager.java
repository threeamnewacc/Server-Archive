package me.devkevin.landcore.faction.manager;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.faction.Faction;
import me.devkevin.landcore.faction.profile.FactionProfile;
import me.devkevin.landcore.utils.message.CC;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 21/03/2023 @ 0:48
 * FactionManager / me.devkevin.landcore.faction.manager / LandCore
 */
@Getter
public class FactionManager {
    private final LandCore plugin = LandCore.getInstance();

    private final List<Faction> factions = new ArrayList<>();
    private final Map<UUID, FactionProfile> factionProfileMap = new HashMap<>();

    public FactionManager() {
        load();
    }

    private void load() {
        MongoCollection<Document> collection = this.plugin.getMongoStorage().getFactionsCollection();

        for (Document document : collection.find()) {
            Faction faction = new Faction(document.getString("name"), (UUID) document.get("leader"));
            faction.setMembers((List<UUID>) document.get("members"));
            faction.getMembers().add((UUID) document.get("leader"));
            faction.setCaptains((List<UUID>) document.get("captains"));
            faction.setDescription(document.getString("description"));
            faction.setDateCreated(document.getString("date-created"));
            faction.setElo(document.getInteger("elo"));
            faction.setWins(document.getInteger("wins"));
            faction.setLosses(document.getInteger("losses"));
            faction.setWinStreak(document.getInteger("winStreak"));
            if (document.containsKey("description")) {
                faction.setDescription(document.getString("description"));
            }
            if (document.containsKey("password")) {
                faction.setPassword(document.getString("password"));
            }

            faction.setFactionChat(document.getBoolean("faction_chat_enabled"));

            factions.add(faction);
        }
    }

    public void save() {
        MongoCollection<Document> collection = this.plugin.getMongoStorage().getFactionsCollection();
        collection.deleteMany(new Document());

        for (Faction faction : factions) {
            Document document = new Document()
                    .append("name", faction.getName())
                    .append("leader", faction.getLeader())
                    .append("members", faction.getMembers())
                    .append("captains", faction.getCaptains())
                    .append("date-created", faction.getDateCreated())
                    .append("elo", faction.getElo())
                    .append("wins", faction.getWins())
                    .append("losses", faction.getLosses())
                    .append("winStreak", faction.getWinStreak())
                    .append("faction_chat_enabled", faction.isFactionChat());

            if (faction.getDescription() != null) {
                document.append("description", faction.getDescription());
            }
            if (faction.getPassword() != null) {
                document.append("password", faction.getPassword());
            }

            collection.insertOne(document);
        }
    }

    public void loadPlayerFaction(Player player) {
        MongoCollection<Document> collection = this.plugin.getMongoStorage().getFactionsPlayersCollection();
        Document document =  collection.find(Filters.eq("uuid", player.getUniqueId().toString())).first();

        if (document != null) {
            FactionProfile factionProfile = this.getProfile(player);
            Faction faction = this.getFaction(document.getString("faction"));

            factionProfile.setFaction(faction);
        }
    }

    public void savePlayerFaction(Player player) {
        MongoCollection<Document> collection = this.plugin.getMongoStorage().getFactionsPlayersCollection();
        String uuid = player.getUniqueId().toString();

        FactionProfile factionProfile = this.getProfile(player);

        if (factionProfile == null) {
            collection.deleteOne(Filters.eq("uuid", uuid));
        } else {
            Document document = new Document("uuid", uuid)
                    .append("faction", factionProfile.getFaction().getName());
            collection.replaceOne(Filters.eq("uuid", uuid), document, new ReplaceOptions().upsert(true));
        }
    }

    public Faction getFaction(String name) {
        return factions.stream().filter(faction -> faction.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public void addPlayer(Player player) {
        factionProfileMap.put(player.getUniqueId(), new FactionProfile());
    }

    public void removePlayer(Player player) {
        factionProfileMap.remove(player.getUniqueId());
    }

    public FactionProfile getProfile(Player player) {
        return factionProfileMap.get(player.getUniqueId());
    }
    public boolean hasProfile(Player player) {
        return factionProfileMap.containsKey(player.getUniqueId());
    }

    public void broadcastToFactions(String message, String server) {
        for (Faction faction : factions) {
            faction.broadcast(CC.DARK_GREEN + "(Faction) " + message + " " + server);
        }
    }

    public void broadcastToFactionsChat(String displayName, String message, String server) {
        for (Faction faction : factions) {
            faction.broadcast(CC.DARK_GREEN + "(Faction) " + CC.GOLD + " [" + server + "] " + displayName + CC.GRAY + " : " + CC.R + message);
        }
    }
}
