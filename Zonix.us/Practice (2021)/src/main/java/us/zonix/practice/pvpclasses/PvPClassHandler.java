package us.zonix.practice.pvpclasses;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import us.zonix.practice.Practice;
import us.zonix.practice.pvpclasses.event.BardRestoreEvent;
import us.zonix.practice.pvpclasses.pvpclasses.ArcherClass;
import us.zonix.practice.pvpclasses.pvpclasses.BardClass;

public class PvPClassHandler extends BukkitRunnable implements Listener {
    private static Map<String, PvPClass> equippedKits = new HashMap<>();
    private static Map<UUID, PvPClass.SavedPotion> savedPotions = new HashMap<>();
    List<PvPClass> pvpClasses = new ArrayList<>();

    public static Map<String, PvPClass> getEquippedKits() {
        return equippedKits;
    }

    public static Map<UUID, PvPClass.SavedPotion> getSavedPotions() {
        return savedPotions;
    }

    public List<PvPClass> getPvpClasses() {
        return this.pvpClasses;
    }

    public PvPClassHandler() {
        this.pvpClasses.add(new BardClass());
        this.pvpClasses.add(new ArcherClass());

        for (PvPClass pvpClass : this.pvpClasses) {
            Practice.getInstance().getServer().getPluginManager().registerEvents(pvpClass, Practice.getInstance());
        }

        Practice.getInstance().getServer().getScheduler().runTaskTimer(Practice.getInstance(), this, 2L, 2L);
        Practice.getInstance().getServer().getPluginManager().registerEvents(this, Practice.getInstance());
    }

    public void run() {
        for (Player player : Practice.getInstance().getServer().getOnlinePlayers()) {
            if (equippedKits.containsKey(player.getName())) {
                PvPClass equippedPvPClass = equippedKits.get(player.getName());
                if (!equippedPvPClass.qualifies(player.getInventory())) {
                    equippedKits.remove(player.getName());
                    player.sendMessage(ChatColor.RED + equippedPvPClass.getName() + " class has been disabled.");
                    equippedPvPClass.remove(player);
                    PvPClass.removeInfiniteEffects(player);
                } else if (!player.hasMetadata("frozen")) {
                    equippedPvPClass.tick(player);
                }
            } else {
                for (PvPClass pvpClass : this.pvpClasses) {
                    if (pvpClass.qualifies(player.getInventory()) && pvpClass.canApply(player) && !player.hasMetadata("frozen")) {
                        pvpClass.apply(player);
                        getEquippedKits().put(player.getName(), pvpClass);
                        player.sendMessage(ChatColor.GREEN + pvpClass.getName() + " class has been enabled.");
                    }
                }
            }
        }

        this.checkSavedPotions();
    }

    public void checkSavedPotions() {
        Iterator<Entry<UUID, PvPClass.SavedPotion>> idIterator = savedPotions.entrySet().iterator();

        while (idIterator.hasNext()) {
            Entry<UUID, PvPClass.SavedPotion> id = idIterator.next();
            Player player = Bukkit.getPlayer(id.getKey());
            if (player != null && player.isOnline()) {
                Bukkit.getPluginManager().callEvent(new BardRestoreEvent(player, id.getValue()));
                if (id.getValue().getTime() < System.currentTimeMillis() && !id.getValue().isPerm()) {
                    if (player.hasPotionEffect(id.getValue().getPotionEffect().getType())) {
                        player.getActivePotionEffects()
                            .forEach(
                                potion -> {
                                    PotionEffect restore = id.getValue().getPotionEffect();
                                    if (potion.getType() == restore.getType()
                                        && potion.getDuration() < restore.getDuration()
                                        && potion.getAmplifier() <= restore.getAmplifier()) {
                                        player.removePotionEffect(restore.getType());
                                    }
                                }
                            );
                    }

                    if (player.addPotionEffect(id.getValue().getPotionEffect(), true)) {
                        idIterator.remove();
                    }
                }
            } else {
                idIterator.remove();
            }
        }
    }

    @EventHandler(
        priority = EventPriority.MONITOR
    )
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getPlayer().getItemInHand() != null && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            for (PvPClass pvPClass : this.pvpClasses) {
                if (hasKitOn(event.getPlayer(), pvPClass)
                    && pvPClass.getConsumables() != null
                    && pvPClass.getConsumables().contains(event.getPlayer().getItemInHand().getType())
                    && pvPClass.itemConsumed(event.getPlayer(), event.getItem().getType())) {
                    if (event.getPlayer().getItemInHand().getAmount() > 1) {
                        event.getPlayer().getItemInHand().setAmount(event.getPlayer().getItemInHand().getAmount() - 1);
                    } else {
                        event.getPlayer().getInventory().remove(event.getPlayer().getItemInHand());
                    }
                }
            }
        }
    }

    public static PvPClass getPvPClass(Player player) {
        return equippedKits.getOrDefault(player.getName(), null);
    }

    public static boolean hasKitOn(Player player, PvPClass pvpClass) {
        return equippedKits.containsKey(player.getName()) && equippedKits.get(player.getName()) == pvpClass;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (equippedKits.containsKey(event.getPlayer().getName())) {
            equippedKits.get(event.getPlayer().getName()).remove(event.getPlayer());
            equippedKits.remove(event.getPlayer().getName());
        }
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        if (equippedKits.containsKey(event.getPlayer().getName())) {
            equippedKits.get(event.getPlayer().getName()).remove(event.getPlayer());
            equippedKits.remove(event.getPlayer().getName());
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (equippedKits.containsKey(event.getPlayer().getName())) {
            equippedKits.get(event.getPlayer().getName()).remove(event.getPlayer());
            equippedKits.remove(event.getPlayer().getName());
        }

        for (PotionEffect potionEffect : event.getPlayer().getActivePotionEffects()) {
            if (potionEffect.getDuration() > 1000000) {
                event.getPlayer().removePotionEffect(potionEffect.getType());
            }
        }
    }
}
