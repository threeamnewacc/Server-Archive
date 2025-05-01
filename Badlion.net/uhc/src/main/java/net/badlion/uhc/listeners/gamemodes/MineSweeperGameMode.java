package net.badlion.uhc.listeners.gamemodes;

import net.badlion.gberry.utils.MessageUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MineSweeperGameMode implements GameMode {

	private Set<UUID> uuids = new HashSet<>();

    public ItemStack getExplanationItem() {
        ItemStack item = new ItemStack(Material.COAL_ORE);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "MineSweeper");

        List<String> lore = new ArrayList<>();

        lore.add(ChatColor.AQUA + "- If you mine an ore you have to get rid of all");
        lore.add(ChatColor.AQUA + "   the blocks touching it first. If not it will explode");
        lore.add(ChatColor.AQUA + " More information: www.badlion.net/wiki/minesweeper");

        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);

        return item;
    }

    public String getAuthor() {
        return "Badlion";
    }

    private static Set<Material> safeMaterials = new HashSet<>();

    public MineSweeperGameMode() {
        MineSweeperGameMode.safeMaterials.add(Material.COAL_ORE);
        MineSweeperGameMode.safeMaterials.add(Material.IRON_ORE);
        MineSweeperGameMode.safeMaterials.add(Material.GOLD_ORE);
        MineSweeperGameMode.safeMaterials.add(Material.DIAMOND_ORE);
        MineSweeperGameMode.safeMaterials.add(Material.EMERALD_ORE);
        MineSweeperGameMode.safeMaterials.add(Material.LAPIS_ORE);
        MineSweeperGameMode.safeMaterials.add(Material.REDSTONE_ORE);
        MineSweeperGameMode.safeMaterials.add(Material.GLOWING_REDSTONE_ORE);
        MineSweeperGameMode.safeMaterials.add(Material.AIR);
        MineSweeperGameMode.safeMaterials.add(Material.WATER);
        MineSweeperGameMode.safeMaterials.add(Material.LAVA);
        MineSweeperGameMode.safeMaterials.add(Material.STATIONARY_LAVA);
        MineSweeperGameMode.safeMaterials.add(Material.STATIONARY_WATER);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (MineSweeperGameMode.safeMaterials.contains(event.getBlock().getType())) {
            if (!MineSweeperGameMode.safeMaterials.contains(event.getBlock().getLocation().add(0, 1, 0).getBlock().getType())) {
	            this.explodeBlock(event.getPlayer(), event.getBlock().getLocation());
            } else if (!MineSweeperGameMode.safeMaterials.contains(event.getBlock().getLocation().add(0, -1, 0).getBlock().getType())) {
	            this.explodeBlock(event.getPlayer(), event.getBlock().getLocation());
            } else if (!MineSweeperGameMode.safeMaterials.contains(event.getBlock().getLocation().add(1, 0, 0).getBlock().getType())) {
	            this.explodeBlock(event.getPlayer(), event.getBlock().getLocation());
            } else if (!MineSweeperGameMode.safeMaterials.contains(event.getBlock().getLocation().add(-1, 0, 0).getBlock().getType())) {
	            this.explodeBlock(event.getPlayer(), event.getBlock().getLocation());
            } else if (!MineSweeperGameMode.safeMaterials.contains(event.getBlock().getLocation().add(0, 0, 1).getBlock().getType())) {
	            this.explodeBlock(event.getPlayer(), event.getBlock().getLocation());
            } else if (!MineSweeperGameMode.safeMaterials.contains(event.getBlock().getLocation().add(0, 0, -1).getBlock().getType())) {
	            this.explodeBlock(event.getPlayer(), event.getBlock().getLocation());
            }
        }
    }

	private void explodeBlock(Player player, Location location) {
		location.getWorld().createExplosion(location, 10);

		this.uuids.add(player.getUniqueId());
		player.damage(10000);
		this.uuids.remove(player.getUniqueId());
	}

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerFuckedUpEvent(PlayerDeathEvent event) {
	    if (this.uuids.contains(event.getEntity().getUniqueId())) {
		    event.getEntity().setLastDamageCause(MessageUtil.CUSTOM_ENTITY_DAMAGE_EVENT);
		    event.setDeathMessage(" blew up in a mine");
        }
    }

// TODO: ???
    /*@EventHandler(priority=EventPriority.FIRST)
    public void onPlayerBlownUpPre(EntityDamageEvent event) {
	    // Blocks damage from the actual explosion we create
        if (event.getEntity() instanceof Player && event.getCause().equals(EntityDamageEvent.DamageCause.CUSTOM)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.LAST)
    public void onPlayerBlownUpPost(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && event.getCause().equals(EntityDamageEvent.DamageCause.CUSTOM)) {
            event.setCancelled(false);
        }
    }*/

    @Override
    public void unregister() {
        BlockBreakEvent.getHandlerList().unregister(this);
        PlayerDeathEvent.getHandlerList().unregister(this);
        EntityDamageEvent.getHandlerList().unregister(this);
    }

}
