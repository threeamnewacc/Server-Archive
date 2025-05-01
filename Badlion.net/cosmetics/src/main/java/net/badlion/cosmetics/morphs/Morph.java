package net.badlion.cosmetics.morphs;

import net.badlion.cosmetics.CosmeticItem;
import net.badlion.cosmetics.Cosmetics;
import net.badlion.cosmetics.utils.MorphUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;

public abstract class Morph extends CosmeticItem {

    protected MorphUtil.MorphType morphType;

    public Morph(String name, ItemRarity itemRarity, ItemStack itemStack) {
        super(Cosmetics.CosmeticType.MORPH, name, itemRarity, itemStack);
    }

    public abstract void setMorph(Player player);

    public void removeMorph(Player player) {
        // Reset walk speed for wither skeleton morph
        if (this.morphType == MorphUtil.MorphType.WITHER_SKELETON) {
            player.setWalkSpeed(0.2F);
        }

        new MorphUtil(this.morphType, player).sendServerRemoveMorph();
    }

    public final void handlePlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            this.handleLeftClick(event);
        } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
            this.handleRightClick(event);
        }
    }

    public void handleSneak(PlayerToggleSneakEvent event) {

    }

    protected void handleLeftClick(PlayerInteractEvent event) {

    }

    protected void handleRightClick(PlayerInteractEvent event) {

    }

    public MorphUtil.MorphType getMorphType() {
        return this.morphType;
    }

}