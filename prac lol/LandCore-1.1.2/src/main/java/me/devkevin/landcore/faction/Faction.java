package me.devkevin.landcore.faction;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 21/03/2023 @ 0:38
 * Faction / me.devkevin.landcore.faction / LandCore
 */
@Getter
@Setter
@RequiredArgsConstructor
public class Faction {
    private final String name;
    private final UUID leader;

    private int limit = 12;

    private List<UUID> members = new ArrayList<>();
    private List<UUID> captains = new ArrayList<>();

    private String description;
    private String password;
    private String dateCreated;
    private int elo = 1000;
    private int wins;
    private int losses;
    private int winStreak;

    private boolean factionChat;

    public void broadcast(String message) {
        members.forEach(users -> {
            Player player = (Player) Bukkit.getOfflinePlayer(users);

            if (player != null) {
                player.sendMessage(message);
            }
        });
    }
}
