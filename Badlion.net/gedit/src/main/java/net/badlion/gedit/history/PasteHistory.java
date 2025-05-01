package net.badlion.gedit.history;

import net.badlion.gedit.sessions.Session;

public class PasteHistory {

    private Session session;

    public Session getSession() {
        return session;
    }

	public PasteHistory(Session session) {
		this.session = session;
	}

}
