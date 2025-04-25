package us.zonix.hcfactions.profile;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import us.zonix.hcfactions.combatlogger.CombatLogger;
import us.zonix.hcfactions.crate.Crate;
import us.zonix.hcfactions.event.Event;
import us.zonix.hcfactions.event.EventManager;
import us.zonix.hcfactions.event.glowstone.GlowstoneEvent;
import us.zonix.hcfactions.event.koth.KothEvent;
import us.zonix.hcfactions.factions.Faction;
import us.zonix.hcfactions.mode.Mode;
import us.zonix.hcfactions.util.player.SimpleOfflinePlayer;
import us.zonix.hcfactions.combatlogger.CombatLogger;
import us.zonix.hcfactions.crate.Crate;
import us.zonix.hcfactions.event.Event;
import us.zonix.hcfactions.event.EventManager;
import us.zonix.hcfactions.event.glowstone.GlowstoneEvent;
import us.zonix.hcfactions.event.koth.KothEvent;
import us.zonix.hcfactions.mode.Mode;
import us.zonix.hcfactions.util.player.SimpleOfflinePlayer;

import java.io.IOException;


public class ProfileAutoSaver extends BukkitRunnable {

    private JavaPlugin plugin;

    public ProfileAutoSaver(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {

        Faction.save();

        try {
            SimpleOfflinePlayer.save(plugin);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        for (Profile profile : Profile.getProfiles()) {
            profile.save();
        }

        for (Mode mode : Mode.getModes()) {
            mode.save();
        }

        for (CombatLogger logger : CombatLogger.getLoggers()) {
            logger.getEntity().remove();
        }

        for (Event event : EventManager.getInstance().getEvents()) {
            if (event instanceof KothEvent) {
                ((KothEvent) event).save();
            }
            else if (event instanceof GlowstoneEvent) {
                ((GlowstoneEvent) event).save();
            }
        }

        for (Crate crate : Crate.getCrates()) {
            crate.save();
        }

    }

}
