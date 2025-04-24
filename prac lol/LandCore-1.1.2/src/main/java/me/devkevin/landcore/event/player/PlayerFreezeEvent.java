package me.devkevin.landcore.event.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.devkevin.landcore.event.CoreEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 21/01/2023 @ 18:27
 * PlayerFreezeEvent / me.devkevin.landcore.event.player / LandCore
 */
@Getter
@Setter
public class PlayerFreezeEvent extends PlayerEvent {

    public PlayerFreezeEvent(Player who) {
        super(who);
    }

    @Override
    public HandlerList getHandlers() {
        return null;
    }
}
