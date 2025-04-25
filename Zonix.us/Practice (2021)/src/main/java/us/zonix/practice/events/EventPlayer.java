package us.zonix.practice.events;

import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class EventPlayer {
    private final UUID uuid;
    private final PracticeEvent event;

    public Player getPlayer() {
        return Bukkit.getPlayer(this.uuid);
    }

    public boolean playerExists() {
        return this.getPlayer() != null;
    }

    public void sendMessage(String... strings) {
        Player player = this.getPlayer();
        if (player != null) {
            for (String string : strings) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', string));
            }
        }
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public PracticeEvent getEvent() {
        return this.event;
    }

    public EventPlayer(UUID uuid, PracticeEvent event) {
        this.uuid = uuid;
        this.event = event;
    }
}
