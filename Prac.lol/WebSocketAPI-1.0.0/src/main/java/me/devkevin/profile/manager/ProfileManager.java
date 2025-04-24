package me.devkevin.profile.manager;

import lombok.Getter;
import me.devkevin.profile.Profile;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by DevKevin
 * Project: WebSocketAPI
 * Date: 23/02/2022 @ 19:47
 */
@Getter
public class ProfileManager {
    private final Map<UUID, Profile> players = new HashMap<>();

    public Profile addPlayer(UUID uuid, String name, InetAddress ipAddress) {
        Profile profile = new Profile(uuid, name, ipAddress);

        this.players.put(uuid, profile);

        return profile;
    }

    public Profile getPlayer(UUID uuid) {
        return this.players.get(uuid);
    }

    public Profile removePlayer(UUID uuid) {
        return this.players.remove(uuid);
    }
}
