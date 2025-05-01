package net.badlion.gedit.wands;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SelectionManager {

    private static final Map<UUID,WandSelection> wandSelections = new HashMap<>();

    public static WandSelection getSelection(Player player) {
        WandSelection selection = SelectionManager.wandSelections.get(player.getUniqueId());
        if (selection == null) {
	        SelectionManager.wandSelections.put(player.getUniqueId(), new WandSelection(player.getUniqueId()));
            selection = SelectionManager.wandSelections.get(player.getUniqueId());
        }

        return selection;
    }

    public static String toStringLocation(Location loc) {
        return "X: " + loc.getBlockX() + " Y: " + loc.getBlockY() + " Z: " + loc.getBlockZ();
    }

}
