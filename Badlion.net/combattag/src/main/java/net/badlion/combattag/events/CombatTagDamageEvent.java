package net.badlion.combattag.events;

import net.badlion.combattag.LoggerNPC;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CombatTagDamageEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private boolean cancelled;
    private LoggerNPC loggerNPC;
    private Player damager;
    private double damage;
    private double finalDamage;

    public CombatTagDamageEvent(LoggerNPC loggerNPC, Player damager, double damage, double finalDamage) {
        this.loggerNPC = loggerNPC;
        this.damager = damager;
        this.damage = damage;
        this.finalDamage = finalDamage;
    }

    public LoggerNPC getLoggerNPC() {
        return loggerNPC;
    }

    public Player getDamager() {
        return damager;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public double getDamage() {
        return damage;
    }

    public double getFinalDamage() {
        return finalDamage;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

}
