package net.badlion.uhc.listeners.gamemodes;

import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.events.PlayerDeathItemEvent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
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

public class DiamondlessGameMode implements GameMode {

    private Random random = new Random();

    public ItemStack getExplanationItem() {
        ItemStack item = new ItemStack(Material.DIAMOND);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Diamondless");

        List<String> lore = new ArrayList<>();

        lore.add(ChatColor.AQUA + "- You cannot mine diamonds");
        lore.add(ChatColor.AQUA + "- You cannot blast mine diamonds");
        lore.add(ChatColor.AQUA + "- Everytime you kill someone they drop 1 diamond");

        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);

        return item;
    }

    public String getAuthor() {
        return "http://reddit.com/u/climbing";
    }

    @EventHandler
    public void onDiamondBroken(BlockBreakEvent event) {
        if (event.getBlock().getType() == Material.DIAMOND_ORE) {
            event.setCancelled(true);

	        event.getBlock().setType(Material.AIR);

            // Give EXP still
            BadlionUHC.getInstance().createExpOrb(event.getBlock().getLocation(), this.random.nextInt(5) + 3);
        }
    }

    @EventHandler
    public void onDiamondBlastMined(EntityExplodeEvent event) {
        Iterator<Block> it = event.blockList().iterator();
        while (it.hasNext()) {
            Block block = it.next();
            if (block.getType() == Material.DIAMOND_ORE) {
                block.setType(Material.AIR);
                it.remove();
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
	    if (BadlionUHC.getInstance().getState() != BadlionUHC.BadlionUHCState.STARTED) return;

        ItemStack[] itemStacks = new ItemStack[] {new ItemStack(Material.DIAMOND)};

        for (ItemStack itemStack : itemStacks) {
            PlayerDeathItemEvent playerDeathItemEvent = new PlayerDeathItemEvent(itemStack);
            BadlionUHC.getInstance().getServer().getPluginManager().callEvent(playerDeathItemEvent);

            if (!playerDeathItemEvent.isCancelled()) {
                event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), playerDeathItemEvent.getItemStack());
            }
        }
    }

    @Override
    public void unregister() {
        PlayerDeathEvent.getHandlerList().unregister(this);
        BlockBreakEvent.getHandlerList().unregister(this);
        EntityExplodeEvent.getHandlerList().unregister(this);
        EntityDeathEvent.getHandlerList().unregister(this);
    }

    @EventHandler
    public void onCombatLoggerDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Zombie) {
            if (event.getEntity().hasMetadata("CombatLoggerNPC")) {
                event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), new ItemStack(Material.DIAMOND));
            }
        }
    }

}
