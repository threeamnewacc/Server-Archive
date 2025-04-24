package me.devkevin.practice.leaderboard.hologram;

import club.inverted.chatcolor.CC;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import me.devkevin.practice.Practice;
import me.devkevin.practice.kit.Kit;
import me.devkevin.practice.leaderboard.Leaderboard;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 13/03/2023 @ 23:04
 * HologramManager / me.devkevin.practice.leaderboard.hologram / Practice
 */
public class HologramManager {
    private final Practice plugin;
    private final List<Hologram> npcHolograms;

    public HologramManager(Practice plugin) {
        this.plugin = plugin;

        npcHolograms = new ArrayList<>();
        npcHolograms.add(HologramsAPI.createHologram(plugin, plugin.getCustomLocationManager().getNpc1().toBukkitLocation().add(0, 3.5, 0)));
        npcHolograms.add(HologramsAPI.createHologram(plugin, plugin.getCustomLocationManager().getNpc2().toBukkitLocation().add(0, 3.5, 0)));
        npcHolograms.add(HologramsAPI.createHologram(plugin, plugin.getCustomLocationManager().getNpc3().toBukkitLocation().add(0, 3.5, 0)));
    }

    public void updateHolograms(List<Leaderboard> leaderboards, Kit kit) {
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            npcHolograms.forEach(Hologram::clearLines);

            int npcHologramCount = npcHolograms.size();
            int leaderboardCount = Math.min(npcHologramCount, leaderboards.size());
            for (int i = 0; i < leaderboardCount; i++) {
                Leaderboard leaderboard = leaderboards.get(i);

                Set<UUID> onlinePlayers = plugin.getServer().getOnlinePlayers().stream().map(Player::getUniqueId).collect(Collectors.toSet());
                UUID uuid = leaderboard.getUuid();
                OfflinePlayer offlinePlayer = plugin.getServer().getOfflinePlayer(uuid);
                String playerName = onlinePlayers.contains(uuid) ? plugin.getServer().getPlayer(uuid).getName() : offlinePlayer.getName();

                Hologram npcHologram = npcHolograms.get(i);
                updateHologram(npcHologram, playerName, leaderboard.getElo(), kit, i);
            }
        });
    }

    private void updateHologram(Hologram npcHologram, String playerName, int elo, Kit kit, int position) {
        npcHologram.appendTextLine("");
        npcHologram.appendTextLine("");
        npcHologram.appendTextLine(CC.AQUA + CC.BOLD + "Top #" + (position + 1));
        npcHologram.appendTextLine(CC.GOLD + CC.BOLD + kit.getName());
        npcHologram.appendTextLine(CC.translate(""));
        npcHologram.appendTextLine(CC.YELLOW + playerName + CC.GRAY + " (" + elo + ")");
    }
}
