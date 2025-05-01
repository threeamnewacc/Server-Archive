package net.badlion.gedit.history;

import net.badlion.gedit.sessions.Session;
import net.badlion.gedit.sessions.SessionManager;
import org.bukkit.entity.Player;

import java.util.*;

public class HistoryManager {

    private static final Map<UUID,List<PasteHistory>> history = new HashMap<>();

    public static List<PasteHistory> getHistory(Player player) {
        List<PasteHistory> pastes = HistoryManager.history.get(player.getUniqueId());
        if (pastes == null) {
            return null;
        }

        return pastes;
    }

    public static PasteHistory savePaste(Player player) {
        PasteHistory history = null;

        try {
            history = new PasteHistory((Session) SessionManager.getSession(player).clone());
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        if (HistoryManager.getHistory(player) == null) {
            List<PasteHistory> pastes = new ArrayList<>();
            pastes.add(history);

            HistoryManager.history.put(player.getUniqueId(),pastes);
        } else {
            HistoryManager.getHistory(player).add(history);
        }

        SessionManager.getSession(player).removeBlockHistory();
        return history;
    }

}
