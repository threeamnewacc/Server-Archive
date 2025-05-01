package net.badlion.gcheat.listeners;

import net.badlion.gcheat.GCheat;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.GCheatEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReducedKnockbackListener implements Listener {

    private final Pattern reducesTheirKB = Pattern.compile("reduces their KB \\((\\d+)");
    private static Map<UUID, List<Long>> lastTinyKB = new HashMap<>();

    @EventHandler
    public void onGCheat(GCheatEvent event) {
        if (event.getType() == GCheatEvent.Type.ANTI_KB) {
            Matcher matcher = reducesTheirKB.matcher(event.getMsg());
            if (matcher.find()) {
                int amount = Integer.parseInt(matcher.group(1));
                // note this is initial velocity - taking 60% initial velocity makes you travel much less further and is obvious to anyone watching
                if (amount < 60) {
                    GCheat.handleTimeDetection(ReducedKnockbackListener.lastTinyKB, event.getPlayer().getUniqueId(), event.getPlayer().getName(), 60000, 10);
                } else {
                    // TODO: ban these in a way more low-key manner
                }
            }
        }
    }
}
