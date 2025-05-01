package net.badlion.cosmetics.gadgets;

import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PaintTrailGadget extends Gadget {

    public static Set<UUID> togglePaint = new HashSet<>();

    public PaintTrailGadget() {
        super("paint_trail", ItemRarity.RARE, ItemStackUtil.createItem(Material.INK_SACK, ChatColor.GREEN + "Paint Trail", ChatColor.GRAY + "A trail of paint, that ", ChatColor.GRAY + "follows you forever."));
    }

    @Override
    public void handlePlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getItem().getType() == Material.INK_SACK) {
            Player player = event.getPlayer();
            if (togglePaint.contains(player.getUniqueId())) {
                togglePaint.remove(player.getUniqueId());
                player.sendMessage(ChatColor.RED + "You have disabled Paint Trail.");
            } else {
                togglePaint.add(player.getUniqueId());
                player.sendMessage(ChatColor.GREEN + "You have enabled Paint Trail.");
            }
        }
    }

    @Override
    public void giveGadget(Player player) {
        player.getInventory().setItem(5, ItemStackUtil.createItem(Material.INK_SACK, ChatColor.DARK_PURPLE + ChatColor.BOLD.toString() + "Paint Trail"));
    }
}
