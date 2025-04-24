package me.devkevin.landcore.utils;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@UtilityClass
public final class ProfileUtil {
    private static final String API_URL = "https://api.mojang.com/users/profiles/minecraft/";
    private static final String SESSION_SERVER_URL = "https://sessionserver.mojang.com/session/minecraft/profile/";
    private static final Cache<String, MojangProfile> NAME_CACHE = CacheBuilder
            .newBuilder()
            .expireAfterAccess(15L, TimeUnit.MINUTES)
            .build();
    private static final Cache<UUID, MojangProfile> ID_CACHE = CacheBuilder
            .newBuilder()
            .expireAfterAccess(15L, TimeUnit.MINUTES)
            .build();

    public static MojangProfile lookupProfile(UUID id) {
        MojangProfile cachedProfile = ID_CACHE.getIfPresent(id);

        if (cachedProfile != null) {
            return cachedProfile;
        }

        Player player = Bukkit.getPlayer(id);

        if (player != null) {
            return cacheProfile(player.getName(), player.getUniqueId());
        }

        String name = lookupNameFromId(id);

        if (name != null) {
            return cacheProfile(name, id);
        }

        return null;
    }

    public static MojangProfile lookupProfile(String name) {
        MojangProfile cachedProfile = ID_CACHE.getIfPresent(name.toLowerCase());

        if (cachedProfile != null) {
            return cachedProfile;
        }

        Player player = Bukkit.getPlayerExact(name);

        if (player != null) {
            return cacheProfile(player.getName(), player.getUniqueId());
        }

        UUID id = lookupIdFromName(name);

        if (id != null) {
            MojangProfile profile = lookupProfile(id);

            if (profile != null) {
                return profile;
            }
        }

        return null;
    }

    private static MojangProfile cacheProfile(String name, UUID id) {
        MojangProfile profile = new MojangProfile(name, id);

        NAME_CACHE.put(name.toLowerCase(), profile);
        ID_CACHE.put(id, profile);

        return profile;
    }

    private static UUID parseId(String idString) {
        return UUID.fromString(idString.substring(0, 8) + "-" + idString.substring(8, 12) + "-"
                + idString.substring(12, 16) + "-" + idString.substring(16, 20) + "-" + idString.substring(20, 32));
    }

    private static UUID lookupIdFromName(String name) {
        try {
            URL url = new URL(API_URL + name);

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
                String id = reader.readLine();

                if (id != null) {
                    return parseId(id.split("\"")[3]);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    private static String lookupNameFromId(UUID id) {
        try {
            URL url = new URL(SESSION_SERVER_URL + id.toString().replace("-", ""));

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
                String name = reader.readLine();

                if (name != null) {
                    return name.split("\"")[7];
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    public static class MojangProfile {
        private final String name;
        private final UUID id;
    }
}
