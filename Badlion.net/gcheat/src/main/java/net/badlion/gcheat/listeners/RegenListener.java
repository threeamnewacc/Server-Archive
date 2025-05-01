package net.badlion.gcheat.listeners;

import net.badlion.gcheat.GCheat;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.GCheatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegenListener implements Listener {

    private static Map<UUID, List<Long>> lastTimerHacks = new HashMap<>();
    private static Map<UUID, List<Long>> lastHighTimerHacks = new HashMap<>();
    private static Deque<FalseRecord> lastFalseTimerHackers = new LinkedList<>();
    private static Pattern p = Pattern.compile("mass packet hacking Type B VL(\\d+)"); // Stolen from CraftChatMessage

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        RegenListener.lastTimerHacks.put(event.getPlayer().getUniqueId(), new ArrayList<Long>());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        RegenListener.lastTimerHacks.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerMassPacketHack(GCheatEvent event) {
        if (event.getType() == GCheatEvent.Type.TIMER) {
            String msg = event.getMsg();

            Matcher matcher = p.matcher(msg);
            if (matcher.find()) {
                Integer lvl;
                try {
                    lvl = Integer.parseInt(matcher.group(1));
                } catch (NumberFormatException e) {
                    Bukkit.getLogger().info("Wrong Timer int " + matcher.group(1));
                    return;
                }

                FalseRecord record = new FalseRecord(event.getPlayer().getUniqueId(), System.currentTimeMillis());
                Set<UUID> lastUUIDS = new HashSet<>();
                for (FalseRecord r : RegenListener.lastFalseTimerHackers) {
                    lastUUIDS.add(r.getUuid());
                }

                // Server is lagging
                if (lastUUIDS.size() > 5) {
                    RegenListener.lastFalseTimerHackers.clear();
                    return;
                }

                // Don't fill too many
                if (RegenListener.lastFalseTimerHackers.size() == 10) {
                    RegenListener.lastFalseTimerHackers.removeFirst();
                }

                RegenListener.lastFalseTimerHackers.add(record);

                if (lvl > 250) {
                    GCheat.handleTimeDetection(RegenListener.lastHighTimerHacks, event.getPlayer().getUniqueId(), event.getPlayer().getName(), 30000, 3);
                } else {
                    GCheat.handleTimeDetection(RegenListener.lastTimerHacks, event.getPlayer().getUniqueId(), event.getPlayer().getName(), 300000, 15);
                }
            }
        }
    }

    public class FalseRecord {

        private UUID uuid;
        private Long ts;

        public FalseRecord(UUID uuid, Long ts) {
            this.uuid = uuid;
            this.ts = ts;
        }

        public UUID getUuid() {
            return uuid;
        }

        public Long getTs() {
            return ts;
        }

    }

}
