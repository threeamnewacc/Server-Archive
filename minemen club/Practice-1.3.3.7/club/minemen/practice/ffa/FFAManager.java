// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.ffa;

import java.beans.ConstructorProperties;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Iterator;
import club.minemen.practice.player.PlayerData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import club.minemen.core.util.finalutil.CC;
import club.minemen.practice.player.PlayerState;
import club.minemen.practice.ffa.killstreak.impl.GodAppleKillStreak;
import club.minemen.practice.ffa.killstreak.impl.DebuffKillStreak;
import club.minemen.practice.ffa.killstreak.impl.GappleKillStreak;
import org.bukkit.entity.Player;
import club.minemen.practice.kit.Kit;
import club.minemen.core.util.CustomLocation;
import club.minemen.practice.Practice;
import club.minemen.practice.ffa.killstreak.KillStreak;
import java.util.Set;
import java.util.UUID;
import org.bukkit.entity.Item;
import java.util.Map;

public class FFAManager
{
    private final Map<Item, Long> itemTracker;
    private final Map<UUID, Integer> killStreakTracker;
    private final Set<KillStreak> killStreaks;
    private final Practice plugin;
    private final CustomLocation spawnPoint;
    private final Kit kit;
    
    public void addPlayer(final Player player) {
        if (this.killStreaks.isEmpty()) {
            this.killStreaks.add(new GappleKillStreak());
            this.killStreaks.add(new DebuffKillStreak());
            this.killStreaks.add(new GodAppleKillStreak());
        }
        final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        playerData.setPlayerState(PlayerState.FFA);
        player.teleport(this.spawnPoint.toBukkitLocation());
        player.sendMessage(CC.SECONDARY + "Welcome to the FFA arena!");
        this.kit.applyToPlayer(player);
        for (int i = 0; i < player.getInventory().getContents().length; ++i) {
            final ItemStack itemStack = player.getInventory().getContents()[i];
            if (itemStack != null && itemStack.getType() == Material.POTION) {
                player.getInventory().setItem(i, new ItemStack(Material.MUSHROOM_SOUP));
            }
        }
        player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
        for (final PlayerData data : this.plugin.getPlayerManager().getAllData()) {
            final Player player2 = this.plugin.getServer().getPlayer(data.getUniqueId());
            if (data.getPlayerState() == PlayerState.FFA) {
                player.showPlayer(player2);
                player2.showPlayer(player);
            }
            else {
                player.hidePlayer(player2);
                player2.hidePlayer(player);
            }
        }
    }
    
    public void removePlayer(final Player player) {
        for (final PlayerData data : this.plugin.getPlayerManager().getAllData()) {
            final Player player2 = this.plugin.getServer().getPlayer(data.getUniqueId());
            if (data.getPlayerState() == PlayerState.FFA) {
                player.hidePlayer(player2);
                player2.hidePlayer(player);
            }
        }
        this.plugin.getPlayerManager().sendToSpawnAndReset(player);
    }
    
    @ConstructorProperties({ "spawnPoint", "kit" })
    public FFAManager(final CustomLocation spawnPoint, final Kit kit) {
        this.itemTracker = new HashMap<Item, Long>();
        this.killStreakTracker = new HashMap<UUID, Integer>();
        this.killStreaks = new HashSet<KillStreak>();
        this.plugin = Practice.getInstance();
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
