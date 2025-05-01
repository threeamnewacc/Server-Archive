package net.badlion.cosmetics.gadgets;

import net.badlion.cosmetics.CosmeticItem;
import net.badlion.cosmetics.Cosmetics;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public abstract class Gadget extends CosmeticItem {

    protected String permission;

    public Gadget(String name, ItemRarity rarity, ItemStack itemStack) {
        super(Cosmetics.CosmeticType.GADGET, name, rarity, itemStack);
    }

    public abstract void giveGadget(Player player);

    public void handlePlayerInteractEvent(PlayerInteractEvent event) {

    }

    public String getPermission() {
        return permission == null ? "badlion.gadgets." + getName().toLowerCase().replace("_", "") : permission;
    }

}
