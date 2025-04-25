package cc.patrone.practice.manager.impl;

import cc.patrone.core.util.UUIDFetcher;
import cc.patrone.practice.manager.Manager;
import cc.patrone.practice.manager.ManagerHandler;
import cc.patrone.practice.player.PracticeProfile;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProfileManager extends Manager {

    private Map<UUID, PracticeProfile> profileMap;

    public ProfileManager(ManagerHandler managerHandler) {
        super(managerHandler);
        profileMap = new HashMap<>();
    }

    public void addPlayer(Player player) {
        profileMap.put(player.getUniqueId(), new PracticeProfile(UUIDFetcher.getUUID(player.getName())));
    }

    public void addUUID(UUID uuid, String name) {
        profileMap.put(uuid, new PracticeProfile(UUIDFetcher.getUUID(name)));
    }

    public void removePlayer(Player player) {
        profileMap.remove(player.getUniqueId());
    }

    public PracticeProfile getProfile(Player player) {
        return profileMap.get(player.getUniqueId());
    }

    public PracticeProfile getProfile(UUID uuid) {
        return profileMap.get(uuid);
    }

    public boolean hasProfile(Player player) {
        return profileMap.containsKey(player.getUniqueId());
    }
}
