package me.devkevin.practice.hcf.listener;

import me.devkevin.practice.Practice;
import me.devkevin.practice.hcf.HCFClass;
import me.devkevin.practice.hcf.manager.HCFManager;
import me.devkevin.practice.match.Match;
import me.devkevin.practice.profile.Profile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 25/02/2023 @ 0:18
 * HCFClassListener / me.devkevin.practice.hcf.listener / Practice
 */
public class HCFClassListener implements Listener {
    private final Practice plugin = Practice.getInstance();
    private final HCFManager hcfManager;

    public static List<UUID> preWarmups = new ArrayList<>();
    public static Map<UUID, Long> cooldown = new HashMap<>();
    public static Map<UUID, HCFClass> classWarmups = new HashMap<>();

    public HCFClassListener(HCFManager hcfManager) {
        this.hcfManager = hcfManager;

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Profile practicePlayerData : Practice.getInstance().getProfileManager().getAllData()) {
                    if (practicePlayerData.isFighting()) {
                        Player player = Bukkit.getPlayer(practicePlayerData.getUuid());
                        Match match = Practice.getInstance().getMatchManager().getMatch(player.getUniqueId());

                        if (match.isPartyMatch()) {
                            if (match.getKit().getName().equalsIgnoreCase("HCF")) {
                                attemptEquip(player);
                            }
                        }
                    }
                }
            }
        }.runTaskTimerAsynchronously(this.plugin, 20L, 20L);

        Practice.getInstance().getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    public static boolean isActive(Player player) {
        return cooldown.containsKey(player.getUniqueId()) && System.currentTimeMillis() < cooldown.get(player.getUniqueId());
    }

    public static void applyCooldown(Player player, int seconds) {
        cooldown.put(player.getUniqueId(), System.currentTimeMillis() + (seconds * 1000));
    }

    public static void clearCooldown(Player player) {
        cooldown.remove(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        hcfManager.setEquippedClass(event.getEntity(), null);
    }

    private void attemptEquip(Player player) {
        HCFClass current = hcfManager.getHCFClass(player);
        if (current != null) {
            if (current.canApply(player)) {
                return;
            }

            hcfManager.setEquippedClass(player, null);
            preWarmups.remove(player.getUniqueId());
        } else if ((current = classWarmups.get(player.getUniqueId())) != null) {
            if (current.canApply(player)) {
                return;
            }

            clearCooldown(player);
            preWarmups.remove(player.getUniqueId());
        }

        hcfManager.getClasses().forEach(hcfClass -> {
            if (hcfClass.canApply(player)) {
                classWarmups.put(player.getUniqueId(), hcfClass);
                preWarmups.add(player.getUniqueId());

                applyCooldown(player, 2);

                new BukkitRunnable() {
                    public void run() {
                        HCFClass pvpClass = classWarmups.remove(player.getUniqueId());

                        if (preWarmups.contains(player.getUniqueId())) {
                            hcfManager.setEquippedClass(player, pvpClass);
                            preWarmups.remove(player.getUniqueId());
                        }
                    }
                }.runTaskLater(this.plugin, 40L);
            }
        });
    }
}
