package net.badlion.sgrankedmatchmaker.services;

import net.badlion.gberry.Gberry;
import net.badlion.sgrankedmatchmaker.SGRankedMatchMaker;
import net.badlion.sgrankedmatchmaker.managers.MatchMakingManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class DefaultMatchMakingService implements MatchMakingService {

    private Map<Integer, List<UUID>> priorities = new HashMap<>();
    private Map<UUID, Integer> playerPriority = new HashMap<>();

    public DefaultMatchMakingService() {
        for (int i = 0; i < 5; i++) {
            this.priorities.put(i, new ArrayList<UUID>());
        }

        new DefaultMatchMakingTask().runTaskTimer(SGRankedMatchMaker.getInstance(), 30 * 20, 30 * 20);
    }

    public void add(UUID uuid) {
        Gberry.log("SGRMM2", "Putting priority " + uuid);
        this.priorities.get(0).add(uuid);
        this.playerPriority.put(uuid, 0);
    }

    public void addToTopPriority(UUID uuid) {
        Gberry.log("SGRMM2", "Putting top priority " + uuid);
        this.priorities.get(4).add(uuid);
        this.playerPriority.put(uuid, 4);
    }

    public void increasePriority(UUID uuid) {
        Gberry.log("SGRMM2", "Increasing priority for " + uuid);

        Integer priority = this.remove(uuid);
        if (priority == null) {
            throw new RuntimeException("Null priority for " + uuid + " when trying to increase");
        }

        if (priority < 4) {
            priority += 1;
        }

        this.priorities.get(priority).add(uuid);
        this.playerPriority.put(uuid, priority);
    }

    public Integer remove(UUID uuid) {
        Gberry.log("SGRMM2", "Removing priority " + uuid);
        Integer priority = this.playerPriority.remove(uuid);
        if (priority != null) {
            this.priorities.get(priority).remove(uuid);
        }

        return priority;
    }

    public class DefaultMatchMakingTask extends BukkitRunnable {

        @Override
        public void run() {
            Gberry.log("SGRMM", "Running matchmaking");

            List<UUID> uuids = new ArrayList<>();
            for (Player p : SGRankedMatchMaker.getInstance().getServer().getOnlinePlayers()) {
                // Don't grab people who want to watch
                if (p.isOp()) {
                    continue;
                }

                // If they are already in a match skip them too (race condition within bukkit)
                if (MatchMakingManager.isInMatch(p.getUniqueId())) {
                    continue;
                }

                uuids.add(p.getUniqueId());
                Gberry.log("SGRMM", "Checking " + p.getUniqueId());
            }

            List<UUID> newPlayers = new ArrayList<>();
            Collections.shuffle(uuids);
            int i = 0;
            if (uuids.size() >= SGRankedMatchMaker.getInstance().getNumOfRequiredPlayers()) {
                for (i = 0; i < uuids.size() && i < SGRankedMatchMaker.PLAYERS_PER_MATCH; i++) {
                    newPlayers.add(uuids.get(i));
                    Gberry.log("SGRMM", "Adding to match1 " + uuids.get(i));
                    DefaultMatchMakingService.this.remove(uuids.get(i));
                }

                // Force newPlayers into a match
                Iterator<UUID> it = newPlayers.iterator();
                while (it.hasNext()) {
                    UUID uuid = it.next();

                    uuids.remove(uuid);
                    Player p = SGRankedMatchMaker.getInstance().getServer().getPlayer(uuid);
                    if (p != null) {
                        p.sendMessage(ChatColor.GREEN + "Matchmaking has found you a match. You will be moved within 5 seconds.");
                    } else {
                        // They logged off or something
                        it.remove();
                    }
                }
            }

            // Increase priority of everyone else
            i = 0;
            while (i < uuids.size()) {
                DefaultMatchMakingService.this.increasePriority(uuids.get(i++));
            }

            // Force ppl in priority 4 (max)
            List<UUID> newPlayers2 = new ArrayList<>();
            if (uuids.size() >= SGRankedMatchMaker.getInstance().getNumOfRequiredPlayers()) {
                Iterator<UUID> priority4UUIDs = DefaultMatchMakingService.this.priorities.get(4).iterator();
                while (priority4UUIDs.hasNext() && newPlayers2.size() < SGRankedMatchMaker.PLAYERS_PER_MATCH) {
                    UUID uuid = priority4UUIDs.next();
                    newPlayers2.add(uuid);
                    Gberry.log("SGRMM", "Adding to match2 " + uuid);
                    priority4UUIDs.remove();
                    uuids.remove(uuid); // Remove from global list
                    DefaultMatchMakingService.this.remove(uuid);
                }

                // Randomly pick some more ppl
                for (i = 0; i < uuids.size() && newPlayers2.size() < SGRankedMatchMaker.PLAYERS_PER_MATCH; i++) {
                    newPlayers2.add(uuids.get(i));
                    DefaultMatchMakingService.this.remove(uuids.get(i));
                }
            }

            // Ok we have our 2 groups of people...lets try to put them into matches
            if (newPlayers.size() >= SGRankedMatchMaker.getInstance().getNumOfRequiredPlayers()) {
                SGRankedMatchMaker.getInstance().createNewMatch(newPlayers);
            } else {
                // Put back into queue
                for (UUID uuid : newPlayers) {
                    DefaultMatchMakingService.this.addToTopPriority(uuid);
                }
            }

            if (newPlayers2.size() >= SGRankedMatchMaker.getInstance().getNumOfRequiredPlayers()) {
                SGRankedMatchMaker.getInstance().createNewMatch(newPlayers2);
            } else {
                // Put back into queue
                for (UUID uuid : newPlayers2) {
                    DefaultMatchMakingService.this.addToTopPriority(uuid);
                }
            }
        }

    }

}
