package net.badlion.cosmetics.gadgets;

import net.badlion.cosmetics.Cosmetics;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class PaintballGadget extends Gadget {

    public PaintballGadget() {
        super("paintball_gun", ItemRarity.SUPER_COMMON, ItemStackUtil.createItem(Material.DIAMOND_BARDING, ChatColor.GREEN + "Paintball Gun", ChatColor.GRAY + "These guns are so deadly,", ChatColor.GRAY + "they are banned down under!"));
    }

    @Override
    public void handlePlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getItem().getType() == Material.DIAMOND_BARDING) {
            Player player = event.getPlayer();
            Egg egg = (Egg) player.getWorld().spawnEntity(player.getEyeLocation().add(player.getLocation().getDirection().multiply(1.0D)), EntityType.EGG);
            egg.setShooter(player);
            egg.setVelocity(player.getLocation().getDirection().multiply(2.0));
            egg.setMetadata("paintballegg", new FixedMetadataValue(Cosmetics.getInstance(), "paintballegg"));
        }
    }

    @Override
    public void giveGadget(Player player) {
        player.getInventory().setItem(5, ItemStackUtil.createItem(Material.DIAMOND_BARDING, ChatColor.DARK_PURPLE + ChatColor.BOLD.toString() + "Paintball Gun"));
    }

}
