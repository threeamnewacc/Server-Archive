package me.devkevin.practice.leaderboard;

import com.google.common.collect.Lists;
import com.mongodb.client.MongoCursor;
import lombok.Getter;
import me.devkevin.practice.Practice;
import me.devkevin.practice.kit.Kit;
import me.devkevin.practice.profile.Profile;
import org.bson.Document;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Copyright 13/05/2022 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class LeaderboardManager {

    private final Practice plugin;
    @Getter private final Set<Leaderboard> leaderboards = ConcurrentHashMap.newKeySet();

    public LeaderboardManager(Practice plugin) {
        this.plugin = plugin;
        updateLeaderboards();
    }

    public void createLeaderboards() {
        for (Kit kit : this.plugin.getKitManager().getKits()) {
            if (kit.getName().equalsIgnoreCase("HCF")) continue;

            try (MongoCursor<Document> iterator = this.plugin.getProfileManager().getPlayersSortByLadderElo(kit)) {
                while (iterator.hasNext()) {
                    try {
                        Document document = iterator.next();
                        UUID uuid = UUID.fromString(document.getString("uuid"));
                        String name = document.getString("username");

                        if (!document.containsKey("stats")) {
                            continue;
                        }

                        Document statics = (Document)document.get("stats");
                        int elo = Profile.DEFAULT_ELO;
                        int winStreak = 0;

                        if (statics.containsKey(kit.getName())) {
                            Document ladder = (Document)statics.get(kit.getName());
                            if (kit.isRanked()) {
                                elo = ladder.getInteger("elo");
                            }
                            if (ladder.containsKey("currentStreak")) {
                                winStreak = ladder.getInteger("currentStreak");
                            }
                        }

                        Leaderboard leaderboard = new Leaderboard(elo, winStreak, uuid, name, kit);
                        leaderboards.add(leaderboard);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void updateLeaderboards() {
        this.leaderboards.clear();
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, this::createLeaderboards);
    }

    public List<Leaderboard> getKitLeaderboards(Kit kit) {
        List<Leaderboard> leaderboardsKits = Lists.newArrayList();
        this.leaderboards.stream().filter(leaderboard -> leaderboard.getKit() == kit).forEach(leaderboardsKits::add);

        return leaderboardsKits;
    }
    public List<Leaderboard> getSortedKitLeaderboards(Kit kit, String type) {
        List<Leaderboard> leaderboardsKit = Lists.newArrayList();

        try {
            for (Leaderboard leaderboard : this.leaderboards) {
                if (leaderboard.getKit() == kit) {
                    leaderboardsKit.add(leaderboard);
                }
            }

            switch (type.toLowerCase()) {
                case "elo":
                    leaderboardsKit.sort((leaderboard1, leaderboard2) -> leaderboard2.getElo() - leaderboard1.getElo());
                    break;
                case "winstreak":
                    leaderboardsKit.sort((leaderboard1, leaderboard2) -> leaderboard2.getWinStreak() - leaderboard1.getWinStreak());
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return leaderboardsKit;
    }
}
