package us.zonix.practice.util.timer.event;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import us.zonix.practice.Practice;
import us.zonix.practice.util.timer.Timer;

public class TimerExpireEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Optional<UUID> userUUID;
    private final Timer timer;
    private Optional<Player> player;

    public TimerExpireEvent(Timer timer) {
        this.userUUID = Optional.empty();
        this.timer = timer;
    }

    public TimerExpireEvent(UUID userUUID, Timer timer) {
        this.userUUID = Optional.ofNullable(userUUID);
        this.timer = timer;
    }

    public TimerExpireEvent(Player player, Timer timer) {
        Objects.requireNonNull(player);
        this.player = Optional.of(player);
        this.userUUID = Optional.of(player.getUniqueId());
        this.timer = timer;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public Optional<Player> getPlayer() {
        if (this.player == null) {
            this.player = this.userUUID.isPresent() ? Optional.of(Practice.getInstance().getServer().getPlayer(this.userUUID.get())) : Optional.empty();
        }

        return this.player;
    }

    public Optional<UUID> getUserUUID() {
        return this.userUUID;
    }

    public Timer getTimer() {
        return this.timer;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
