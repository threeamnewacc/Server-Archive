package me.devkevin.practice.hcf.event;

import lombok.Getter;
import me.devkevin.practice.hcf.HCFClass;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 25/02/2023 @ 0:15
 * ArmorClassEquipEvent / me.devkevin.practice.hcf.event / Practice
 */
@Getter
public class ArmorClassEquipEvent extends PlayerEvent {

    private static final HandlerList HANDLERS = new HandlerList();
    private final HCFClass hcfClass;

    public ArmorClassEquipEvent(Player player, HCFClass hcfClass) {
        super(player);
        this.hcfClass = hcfClass;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
