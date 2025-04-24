package me.devkevin.landcore.event;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 18/01/2023 @ 15:14
 * PlayerFakeEvent / land.pvp.core.event / LandCore
 */
@Getter
public class PlayerFakeEvent extends BaseEvent {
    private Player player;

    public PlayerFakeEvent(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public UUID getUniqueId() {
        return player.getUniqueId();
    }
}
