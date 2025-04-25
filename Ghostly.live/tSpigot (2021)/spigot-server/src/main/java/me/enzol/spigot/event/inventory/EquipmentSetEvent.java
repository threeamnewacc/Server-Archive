package me.enzol.spigot.event.inventory;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class EquipmentSetEvent extends Event {
  private static final HandlerList handlers = new HandlerList();

  private final HumanEntity humanEntity;
  private final int slot;
  private final ItemStack previousItem;
  private final ItemStack newItem;

  public EquipmentSetEvent(HumanEntity humanEntity, int slot, ItemStack previousItem, ItemStack newItem) {
    this.humanEntity = humanEntity;
    this.slot = slot;
    this.previousItem = previousItem;
    this.newItem = newItem;
  }

  public HandlerList getHandlers() { return handlers; }

  public static HandlerList getHandlerList() { return handlers; }
}
