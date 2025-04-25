package us.zonix.practice.ffa;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import us.zonix.practice.CustomLocation;
import us.zonix.practice.Practice;
import us.zonix.practice.ffa.killstreak.KillStreak;
import us.zonix.practice.ffa.killstreak.impl.DebuffKillStreak;
import us.zonix.practice.ffa.killstreak.impl.GappleKillStreak;
import us.zonix.practice.ffa.killstreak.impl.GodAppleKillStreak;
import us.zonix.practice.kit.Kit;
import us.zonix.practice.player.PlayerData;
import us.zonix.practice.player.PlayerState;
import us.zonix.practice.util.PlayerUtil;

public class FFAManager {
    private final Map<Item, Long> itemTracker = new HashMap<>();
    private final Map<UUID, Integer> killStreakTracker = new HashMap<>();
    private final Set<KillStreak> killStreaks = new HashSet<>();
    private final Practice plugin = Practice.getInstance();
    private final CustomLocation spawnPoint;
    private final Kit kit;

    public void addPlayer(Player player) {
        if (this.killStreaks.isEmpty()) {
            this.killStreaks.add(new GappleKillStreak());
            this.killStreaks.add(new DebuffKillStreak());
            this.killStreaks.add(new GodAppleKillStreak());
        }

        PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        playerData.setPlayerState(PlayerState.FFA);
        player.getInventory().setHeldItemSlot(0);
        player.teleport(this.spawnPoint.toBukkitLocation());
        player.setFlying(false);
        PlayerUtil.clearPlayer(player);
        player.sendMessage(ChatColor.GREEN + "You have been sent to the FFA arena.");
        this.kit.applyToPlayer(player);
        player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));

        for (PlayerData data : this.plugin.getPlayerManager().getAllData()) {
            Player player1 = this.plugin.getServer().getPlayer(data.getUniqueId());
            if (data.getPlayerState() == PlayerState.FFA) {
                player.showPlayer(player1);
                player1.showPlayer(player);
            } else {
                player.hidePlayer(player1);
                player1.hidePlayer(player);
            }
        }
    }

    public void removePlayer(Player player) {
        for (PlayerData data : this.plugin.getPlayerManager().getAllData()) {
            Player player1 = this.plugin.getServer().getPlayer(data.getUniqueId());
            if (data.getPlayerState() == PlayerState.FFA) {
                player.hidePlayer(player1);
                player1.hidePlayer(player);
            }
        }

        this.killStreakTracker.remove(player.getUniqueId());
        this.plugin.getPlayerManager().sendToSpawnAndReset(player);
    }

    public int getTotalPlaying() {
        int count = 0;

        for (Player online : this.plugin.getServer().getOnlinePlayers()) {
            if (this.plugin.getPlayerManager().getPlayerData(online.getUniqueId()).getPlayerState() == PlayerState.FFA) {
                count++;
            }
        }

        return count;
    }

    public FFAManager(CustomLocation spawnPoint, Kit kit) {
        this.spawnPoint = spawnPoint;
        this.kit = kit;
    }

    public Map<Item, Long> getItemTracker() {
        return this.itemTracker;
    }

    public Map<UUID, Integer> getKillStreakTracker() {
        return this.killStreakTracker;
    }

    public Set<KillStreak> getKillStreaks() {
        return this.killStreaks;
    }
}
