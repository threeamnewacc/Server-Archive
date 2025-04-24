package me.devkevin.practice.hcf;

import lombok.Getter;
import me.devkevin.practice.Practice;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 25/02/2023 @ 0:01
 * HCFClass / me.devkevin.practice.hcf / Practice
 */
@Getter
public abstract class HCFClass implements Listener {

    public static long DEFAULT_MAX_DURATION = TimeUnit.MINUTES.toMillis(8L);

    private final String name;
    private final List<PotionEffect> effects = new ArrayList<>();

    public HCFClass(String name) {
        this.name = name;
        Practice.getInstance().getHcfManager().getClasses().add(this);
    }

    public abstract boolean canApply(Player player);

    public abstract boolean onEquip(Player player);

    public abstract void onUnEquip(Player player);

    public abstract void onQuit(Player player);

    public void registerHCFListeners() {
        Bukkit.getPluginManager().registerEvents(this, Practice.getInstance());
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        onQuit(event.getPlayer());
    }
}
