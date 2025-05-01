package net.badlion.gedit.sessions;

import net.badlion.gedit.wands.SelectionManager;
import net.badlion.gedit.wands.WandSelection;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SessionManager {

    private static final Map<UUID, Session> sessions = new HashMap<>();

    public static Session getSession(Player p) {
        Session session = SessionManager.sessions.get(p.getUniqueId());

        if (session == null) {
            session = createSession(p, SelectionManager.getSelection(p));
        }

        return session;
    }

    public static Session createSession(Player p, WandSelection selection) {
        Session session = new Session(p.getUniqueId(), selection);
        SessionManager.sessions.put(p.getUniqueId(), session);
        return session;
    }

    public static Map<UUID, Session> getSessions() {
        return sessions;
    }

}
