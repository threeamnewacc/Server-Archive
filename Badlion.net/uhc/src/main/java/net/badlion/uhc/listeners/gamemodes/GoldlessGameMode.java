package net.badlion.uhc.listeners.gamemodes;

import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.events.GoldenHeadDroppedEvent;
import net.badlion.uhc.events.PlayerDeathItemEvent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GoldlessGameMode implements GameMode {

    private Random random = new Random();

    public ItemStack getExplanationItem() {
        ItemStack item = new ItemStack(Material.GOLD_INGOT);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Goldless");

        List<String> lore = new ArrayList<>();

        lore.add(ChatColor.AQUA + "- You cannot mine gold");
        lore.add(ChatColor.AQUA + "- You cannot blast mine gold");
        lore.add(ChatColor.AQUA + "- Everytime you kill someone they drop 1 golden head");
        lore.add(ChatColor.AQUA + "- Everytime you kill someone they drop 8 gold ingots.");
        lore.add(ChatColor.AQUA + "- Suggestion: Only get a few apples.");

        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);

        return item;
    }

    public String getAuthor() {
        return "Badlion";
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onGoldBroken(BlockBreakEvent event) {
        if (event.getBlock().getType() == Material.GOLD_ORE) {
            event.setCancelled(true);

            event.getBlock().setType(Material.AIR);

            // Give EXP still
            BadlionUHC.getInstance().createExpOrb(event.getBlock().getLocation(), 1);
        }
    }

    @EventHandler
    public void onGoldBlastMined(EntityExplodeEvent event) {
        Iterator<Block> it = event.blockList().iterator();
        while (it.hasNext()) {
            Block block = it.next();
            if (block.getType() == Material.GOLD_ORE) {
                block.setType(Material.AIR);
                it.remove();
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
	    if (BadlionUHC.getInstance().getState() != BadlionUHC.BadlionUHCState.STARTED) return;

        ItemStack[] itemStacks = new ItemStack[] {new ItemStack(Material.GOLD_INGOT, 8), ItemStackUtil.createGoldenHead()};

        for (ItemStack itemStack : itemStacks) {
            PlayerDeathItemEvent playerDeathItemEvent = new PlayerDeathItemEvent(itemStack);
            BadlionUHC.getInstance().getServer().getPluginManager().callEvent(playerDeathItemEvent);

            if (!playerDeathItemEvent.isCancelled()) {
                event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), playerDeathItemEvent.getItemStack());
            }
        }
    }

    @EventHandler
    public void onGoldenHeadMade(GoldenHeadDroppedEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onCombatLoggerDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Zombie) {
            if (event.getEntity().hasMetadata("CombatLoggerNPC")) {
                event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), ItemStackUtil.createGoldenHead());
                event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), new ItemStack(Material.GOLD_INGOT, 8));
            }
        }
    }

    @Override
    public void unregister() {
        PlayerDeathEvent.getHandlerList().unregister(this);
        BlockBreakEvent.getHandlerList().unregister(this);
        EntityExplodeEvent.getHandlerList().unregister(this);
        EntityDeathEvent.getHandlerList().unregister(this);
        GoldenHeadDroppedEvent.getHandlerList().unregister(this);
    }

}
