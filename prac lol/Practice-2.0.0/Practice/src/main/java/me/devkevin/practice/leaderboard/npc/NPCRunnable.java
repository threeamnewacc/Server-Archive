package me.devkevin.practice.leaderboard.npc;

import me.devkevin.npc.npcs.NPC;
import me.devkevin.practice.Practice;
import me.devkevin.practice.kit.Kit;
import me.devkevin.practice.leaderboard.Leaderboard;
import me.devkevin.practice.location.CustomLocation;
import me.devkevin.practice.util.NPCUtil;
import me.devkevin.practice.util.TaskUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 07/03/2023 @ 21:18
 * NPCRunnable / me.devkevin.practice.leaderboard.npc / Practice
 */
public class NPCRunnable implements Runnable {
    private final Map<Integer, CustomLocation> npcLocations = new HashMap<>();
    private final Practice plugin;
    private int currentKitIndex = 0;

    public NPCRunnable(Practice plugin) {
        this.plugin = plugin;

        npcLocations.put(0, plugin.getCustomLocationManager().getNpc1());
        npcLocations.put(1, plugin.getCustomLocationManager().getNpc2());
        npcLocations.put(2, plugin.getCustomLocationManager().getNpc3());
    }

    @Override
    public void run() {
        List<Kit> kits = this.plugin.getKitManager().getRankedKits();
        Kit kit = kits.get(currentKitIndex);

        // increment the kit index and wrap around to the start of the list if necessary
        currentKitIndex = (currentKitIndex + 1) % kits.size();

        List<Leaderboard> leaderboards = this.plugin.getLeaderboardManager()
                .getSortedKitLeaderboards(kit, "elo")
                .stream().limit(3).collect(Collectors.toList());

        if (leaderboards.isEmpty()) {
            return;
        }

        plugin.getHologramManager().updateHolograms(leaderboards, kit);

        List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());

        for (int i = 0; i < leaderboards.size(); i++) {
            Leaderboard leaderboard = leaderboards.get(i);
            NPC npc = NPC.getByName("top" + (i + 1));
            if (npc == null) {
                continue;
            }

            CustomLocation npcLocation = npcLocations.get(i);

            if (npcLocation == null) {
                continue;
            }

            Location location = npcLocation.toBukkitLocation();
            npc.setLocation(location);
            npc.setYaw(location.getYaw());
            npc.setPitch(location.getPitch());
            npc.setHeadYaw(location.getYaw());

            Optional<OfflinePlayer> player = Optional.ofNullable(plugin.getServer().getOfflinePlayer(leaderboard.getUuid()));
            player.ifPresent(p -> {
                String[] skin = NPCUtil.getSkinFromMojang(p.getUniqueId());

                npc.setDisplayName(p.getName());

                if (skin != null && skin.length >= 2) {
                    npc.setTexture(skin[0]);
                    npc.setSignature(skin[1]);
                }
            });

            npc.setHand(kit.getIcon());

            if (npc.getDisplayName() != null) {
                onlinePlayers.forEach(npc::destroy);
                onlinePlayers.forEach(npc::spawn);
            }

            TaskUtil.runAsync(npc::save);
        }
    }
}
