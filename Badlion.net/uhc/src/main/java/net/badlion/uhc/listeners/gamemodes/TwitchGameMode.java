package net.badlion.uhc.listeners.gamemodes;

import net.badlion.gberry.UnregistrableListener;
import net.badlion.uhc.BadlionUHC;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TwitchGameMode implements GameMode {

    private Random random = new Random();

    public TwitchGameMode() {
        BadlionUHC.HEAD_HALF_HEARTS_TO_HEAL = 6;
    }

    public ItemStack getExplanationItem() {
        ItemStack item = new ItemStack(Material.WATCH);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Twitch On Air");

        List<String> lore = new ArrayList<>();

        lore.add(ChatColor.AQUA + "- Golden Heads Heal 3 hearts instead of 4");
        lore.add(ChatColor.AQUA + "- Flint and Gravel drop rates are doubled");

        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);

        return item;
    }

    public String getAuthor() {
        return "http://twitch.tv/Mentally";
    }

    @EventHandler
    public void onLeafDecay(LeavesDecayEvent event) {
        if ((event.getBlock().getType() == Material.LEAVES && event.getBlock().getData() == 0)
                || event.getBlock().getType() == Material.LEAVES_2 && event.getBlock().getData() == 1) {
            if (this.random.nextInt(200) == 0) {
                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.APPLE));
            }
        }
    }

    @EventHandler
    public void onLeafDecay(BlockBreakEvent event) {
        if (event.getBlock().getType() == Material.GRAVEL) {
            if (this.random.nextInt(10) == 0) {
                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.FLINT));
            }
        }
    }

    @Override
    public void unregister() {
        BadlionUHC.HEAD_HALF_HEARTS_TO_HEAL = 8;
        BlockBreakEvent.getHandlerList().unregister(this);
        LeavesDecayEvent.getHandlerList().unregister(this);
    }

}
