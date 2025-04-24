package me.devkevin.landcore.event.player;

import me.devkevin.landcore.player.CoreProfile;
import me.devkevin.landcore.player.grant.Grant;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

@Getter
public class PlayerRankChangeEvent extends PlayerEvent {
    private static final HandlerList HANDLERS = new HandlerList();
    private final CoreProfile profile;
    private final Grant newRank;
    private final long duration;

    public PlayerRankChangeEvent(Player who, CoreProfile profile, Grant newRank, long duration) {
        super(who);
        this.profile = profile;
        this.newRank = newRank;
        this.duration = duration;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
