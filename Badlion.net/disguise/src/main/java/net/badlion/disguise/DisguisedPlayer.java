package net.badlion.disguise;

import java.util.UUID;

public class DisguisedPlayer {

    private UUID uuid;

    private String username;

    private String disguisedName;

    public DisguisedPlayer(UUID uuid, String username, String disguisedName) {
        this.uuid = uuid;

        this.username = username;

        this.disguisedName = disguisedName;
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public String getUsername() {
        return this.username;
    }

    public String getDisguisedName() {
        return this.disguisedName;
    }
}
