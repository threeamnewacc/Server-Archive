package net.badlion.uhc.listeners.gamemodes;

import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.UHCPlayer;
import net.badlion.uhc.events.GameStartEvent;
import net.badlion.uhc.managers.UHCPlayerManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.ChatColor;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class TrumpGameMode implements GameMode {

    public TrumpGameMode() {
        BadlionUHC.getInstance().getConfigurator().getBooleanOption(BadlionUHC.CONFIG_OPTIONS.NETHER.name()).setValue("false");
    }

    @EventHandler
    public void onGameStart(GameStartEvent event) {
        BadlionUHC.getInstance().getConfigurator().getBooleanOption(BadlionUHC.CONFIG_OPTIONS.NETHER.name()).setValue("false");

	    BadlionUHC.getInstance().getServer().getScheduler().runTaskLater(BadlionUHC.getInstance(), new Runnable() {
		    @Override
		    public void run() {
			    for (UHCPlayer uhcPlayer : UHCPlayerManager.getAllUHCPlayers()) {
				    Player player = uhcPlayer.getPlayer();
				    Location location = player.getLocation();
				    // If they aren't in 100x100
				    if (location.getX() > 100 || location.getZ() > 100) {
					    player.damage(1000.0D); // KILL THEM!
				    }
			    }
		    }
	    }, 20L * 60 * 30);
    }

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.getBlock().getLocation().getY() >= 150) {
			return;
		}
		if ((event.getBlock().getLocation().getX() > 100.0D && event.getBlock().getLocation().getX() < 500.0D)
				|| (event.getBlock().getLocation().getZ() > 100.0D && event.getBlock().getLocation().getZ() < 500.0D)) {
			event.setCancelled(true);
			event.getPlayer().sendMessage(ChatColor.RED + "You can't break blocks here!");
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (event.getBlock().getLocation().getY() >= 150 && event.getBlock().getType() != Material.WATER && event.getBlock().getType() != Material.LAVA) {
			return;
		}
		if ((event.getBlock().getLocation().getX() > 100.0D && event.getBlock().getLocation().getX() < 500.0D)
				|| (event.getBlock().getLocation().getZ() > 100.0D && event.getBlock().getLocation().getZ() < 500.0D)) {
			event.setCancelled(true);
			event.getPlayer().sendMessage(ChatColor.RED + "You can't place blocks here!");
		}
	}

    public ItemStack getExplanationItem() {
        ItemStack item = new ItemStack(Material.BONE);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Barebones");

        List<String> lore = new ArrayList<>();

        lore.add(ChatColor.AQUA + "- Nether is disabled");
        lore.add(ChatColor.AQUA + "- All ores except iron/coal drop an iron ingot");
        lore.add(ChatColor.AQUA + "- On player death they drop 1 diamond, 1 golden apple, 32 arrows, & 2 string");
        lore.add(ChatColor.AQUA + "- You cannot craft Enchanting Tables, Anvils, or Golden Apples");

        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);

        return item;
    }

    public String getAuthor() {
        return "http://reddit.com/u/Audicyy";
    }

    @Override
    public void unregister() {
	    GameStartEvent.getHandlerList().unregister(this);
	    BlockBreakEvent.getHandlerList().unregister(this);
	    BlockPlaceEvent.getHandlerList().unregister(this);
    }

}
