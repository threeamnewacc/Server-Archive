package us.zonix.hcfactions.event.koth;

import us.zonix.hcfactions.FactionsPlugin;
import us.zonix.hcfactions.event.EventManager;
import us.zonix.hcfactions.event.Event;
import us.zonix.hcfactions.profile.Profile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import us.zonix.hcfactions.util.player.PlayerUtility;
import us.zonix.hcfactions.FactionsPlugin;
import us.zonix.hcfactions.event.EventManager;
import us.zonix.hcfactions.profile.Profile;
import us.zonix.hcfactions.util.player.PlayerUtility;

public class KothEventListeners implements Listener {

    public KothEventListeners() {
        new BukkitRunnable() {
            @Override
            public void run() {

                for (Event possibleEvent : EventManager.getInstance().getEvents()) {
                    if (possibleEvent instanceof KothEvent && possibleEvent.isActive()) {
                        KothEvent koth = (KothEvent) possibleEvent;

                        if (koth.isFinished()) {
                            koth.stop(false);
                            continue;
                        }

                        if (koth.getCappingPlayer() != null) {
                            Player player = koth.getCappingPlayer();

                            if (player.isDead() || !player.isValid() || !player.isOnline() || !koth.getZone().isInside(player)) {
                                koth.setCappingPlayer(null);
                            } else {
                                if (koth.getDecisecondsLeft() % 600 == 0 && koth.getDecisecondsLeft() != koth.getCapTime() / 100 && koth.getDecisecondsLeft() != 0) {
                                    Bukkit.broadcastMessage(FactionsPlugin.getInstance().getLanguageConfig().getString("KOTH.CONTESTED").replace("%KOTH%", koth.getName()).replace("%TIME%", koth.getTimeLeft()).replace("%PLAYER%", player.getName()));
                                }
                            }
                        } else {
                            if (!(koth.isGrace())) {
                                for (Player player : PlayerUtility.getOnlinePlayers() ) {
                                    Profile profile = Profile.getByPlayer(player);

                                    if (player.isDead()) {
                                        continue;
                                    }

                                    if (profile.getProtection() != null) {
                                        continue;
                                    }

                                    if (koth.getZone().isInside(player)) {
                                        koth.setCappingPlayer(player);
                                        break;
                                    }
                                }
                            }
                        }

                    }
                }

            }
        }.runTaskTimerAsynchronously(FactionsPlugin.getInstance(), 2L, 2L);
    }

}
