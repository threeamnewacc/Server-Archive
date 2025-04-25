package cc.patrone.practice.task;

import cc.patrone.core.util.NameFetcher;
import cc.patrone.practice.PracticePlugin;
import cc.patrone.practice.kit.Kit;
import cc.patrone.practice.player.LeaderboardsPlayer;
import cc.patrone.practice.util.ItemBuilder;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.*;

@RequiredArgsConstructor
public class LeaderboardTask extends BukkitRunnable {

    private final PracticePlugin plugin;
    private List<LeaderboardsPlayer> leaderboardsPlayerList = new ArrayList<>();


    public void run() {
        Arrays.stream(new File(this.plugin.getDataFolder().getAbsolutePath() + File.separator + "players").listFiles()).forEach(f -> {
            YamlConfiguration file = YamlConfiguration.loadConfiguration(f);
            UUID uuid = (UUID.fromString(f.getName().split("\\.")[0]));
            Arrays.stream(Kit.values()).forEach(k -> {
                int elo = file.getInt("kit." + k.getName() + ".elo");
                Player player = this.plugin.getServer().getPlayer(uuid);
                leaderboardsPlayerList.add(new LeaderboardsPlayer(uuid, k, (player != null ? this.plugin.getManagerHandler().getProfileManager().getProfile(player).getElo(k) : (elo == 0 ? 1000 : elo))));
            });
        });

        Arrays.stream(Kit.values()).forEach(k -> {

            List<LeaderboardsPlayer> leaderboard = new ArrayList<>();
            leaderboardsPlayerList.stream().filter(l -> l.getKit() == k).forEach(l -> {
                leaderboard.add(l);
            });
            leaderboard.sort(Comparator.comparing(LeaderboardsPlayer::getElo));
            Collections.reverse(leaderboard);


            List<String> lore = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                if (leaderboard.size() > i) {
                    LeaderboardsPlayer leaderboardsPlayer = leaderboard.get(i);
                    String name = NameFetcher.getName(leaderboardsPlayer.getUuid());
                    lore.add(ChatColor.LIGHT_PURPLE + "" + (i + 1) + ". " + ChatColor.DARK_PURPLE + name + ChatColor.LIGHT_PURPLE + " (" + leaderboardsPlayer.getElo() + ")");
                }
            }
            ItemStack display = new ItemBuilder(k.getDisplay()).setName(ChatColor.LIGHT_PURPLE + k.getName()).setLore(lore).toItemStack();
            this.plugin.getManagerHandler().getInventoryManager().getLeaderboardsInventory().setItem(k.getId(), display);
        });

        leaderboardsPlayerList.clear();
    }


}
