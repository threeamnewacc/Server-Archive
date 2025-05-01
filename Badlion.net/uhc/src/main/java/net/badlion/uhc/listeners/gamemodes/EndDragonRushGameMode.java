package net.badlion.uhc.listeners.gamemodes;

import net.badlion.gberry.Gberry;
import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.UHCPlayer;
import net.badlion.uhc.UHCTeam;
import net.badlion.uhc.events.BorderShrinkSetEvent;
import net.badlion.uhc.events.GameStartEvent;
import net.badlion.uhc.events.PlayerDeathItemEvent;
import net.badlion.uhc.managers.UHCPlayerManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerPostPortalEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EndDragonRushGameMode implements GameMode {

    public ItemStack getExplanationItem() {
        ItemStack item = new ItemStack(Material.DRAGON_EGG);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Ender Dragon Rush");

        List<String> lore = new ArrayList<>();

        lore.add(ChatColor.AQUA + "- There are 4 portals located at +/- 1500");
        lore.add(ChatColor.AQUA + "- There is no border shrink or perma day");
        //lore.add(ChatColor.AQUA + "- There is an announcement when a portal is lit");
        lore.add(ChatColor.AQUA + "- Everytime you kill someone they drop 1 enderpearl");
        lore.add(ChatColor.AQUA + "- First team to kill the dragon or last team standing wins");
        lore.add(ChatColor.AQUA + "- Number of Ender Eyes required to activate a portal is ");
        lore.add(ChatColor.AQUA + "   # of players per team + 1 ");
        lore.add(ChatColor.AQUA + "   e.g. ffa = 2 ender eyes, to3 = 4 ender eyes ");

        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);

        return item;
    }

    public String getAuthor() {
        return "https://reddit.com/u/GuudeBoulderfist";
    }

    @EventHandler
    public void onEnderDragonDie(EntityDeathEvent event) {
        if (event.getEntityType() == EntityType.ENDER_DRAGON && BadlionUHC.getInstance().getEndTime() == -1) {
            Player killer = event.getEntity().getKiller();
            if (killer != null) {
                UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer(killer.getUniqueId());
                BadlionUHC.getInstance().declareWinningTeam(uhcPlayer.getTeam());
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getBlock().getType() == Material.ENDER_PORTAL_FRAME || event.getBlock().getType() == Material.ENDER_PORTAL) {
            event.setCancelled(true);
        } else if (event.getBlock().getType() == Material.MOB_SPAWNER && event.getBlock().getWorld().getName().equals(BadlionUHC.UHCWORLD_NETHER_NAME)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
	    if (BadlionUHC.getInstance().getState() != BadlionUHC.BadlionUHCState.STARTED) return;

        ItemStack[] itemStacks = new ItemStack[] {new ItemStack(Material.ENDER_PEARL)};

        for (ItemStack itemStack : itemStacks) {
            PlayerDeathItemEvent playerDeathItemEvent = new PlayerDeathItemEvent(itemStack);
            BadlionUHC.getInstance().getServer().getPluginManager().callEvent(playerDeathItemEvent);

            if (!playerDeathItemEvent.isCancelled()) {
                event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), playerDeathItemEvent.getItemStack());
            }
        }
    }

    @EventHandler
    public void onBorderShrinkingTurnedOn(BorderShrinkSetEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerPlaceBed(PlayerInteractEvent event) {
        if (event.getItem() != null && event.getItem().getType() == Material.BED && event.getPlayer().getLocation().getWorld().getName().equalsIgnoreCase(BadlionUHC.UHCWORLD_END_NAME)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onGameStarts(GameStartEvent event) {
        EndDragonRushGameMode.createPortal(-1500, -1500);
	    EndDragonRushGameMode.createPortal(-1500, 1500);
	    EndDragonRushGameMode.createPortal(1500, -1500);
	    EndDragonRushGameMode.createPortal(1500, 1500);

        BadlionUHC.getInstance().getServer().dispatchCommand(Bukkit.getConsoleSender(), "wb uhcworld_nether set 1500 0 0");
    }

    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent event) {
        if (event.getFrom().getWorld().getName().equals(BadlionUHC.UHCWORLD_NAME) && event.getTo().getWorld().getName().equals(BadlionUHC.UHCWORLD_NETHER_NAME)) {
            event.setTo(new Location(event.getTo().getWorld(), 4 * event.getTo().getBlockX(), event.getTo().getBlockY(), 4 * event.getTo().getBlockZ()));
        } else if (event.getFrom().getWorld().getName().equals(BadlionUHC.UHCWORLD_NETHER_NAME) && event.getTo().getWorld().getName().equals(BadlionUHC.UHCWORLD_NAME)) {
            event.setTo(new Location(event.getTo().getWorld(), event.getTo().getBlockX(), event.getTo().getBlockY(), event.getTo().getBlockZ()));
        }
    }

    @EventHandler
    public void onPortalCreated(PortalCreateEvent event) {
        //if (event.getReason() == PortalCreateEvent.CreateReason.END) {
        //    Gberry.broadcastMessage(ChatColor.AQUA + "An End Portal has been lit.");
        //}
    }

    @EventHandler
    public void onPlayerTeleportIntoEnd(PlayerPostPortalEvent event) {
        if (event.getFrom() != null && event.getFrom().getWorld().getName().equals(BadlionUHC.UHCWORLD_NAME)) {
            if (event.getTo() != null && event.getTo().getWorld().getName().equals(BadlionUHC.UHCWORLD_END_NAME)) {
                // Find any teammates in the end first and use their position
                UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer(event.getPlayer().getUniqueId());
                for (UUID uuid : uhcPlayer.getTeam().getUuids()) {
                    Player p = BadlionUHC.getInstance().getServer().getPlayer(uuid);
                    if (p != null && p.getLocation().getWorld().getName().equals(BadlionUHC.UHCWORLD_END_NAME)) {
                        if (p == event.getPlayer()) {
                            continue;
                        }

                        event.setTo(p.getLocation());
                        return;
                    }
                }

                // Randomly pick between -100, -100 and 100, 100 and find a nice place for them to properly spawn
                while (true) {
                    int x = Gberry.generateRandomInt(-100, 100);
                    int z = Gberry.generateRandomInt(-100, 100);

                    Block block = Bukkit.getWorld(BadlionUHC.UHCWORLD_END_NAME).getHighestBlockAt(x, z);
                    if (block == null) {
                        continue;
                    }

                    // Get block underneath
                    Block block1 = Bukkit.getWorld(BadlionUHC.UHCWORLD_END_NAME).getBlockAt(block.getLocation().add(0, -1, 0));
                    Block block2 = Bukkit.getWorld(BadlionUHC.UHCWORLD_END_NAME).getBlockAt(block.getLocation().add(0, -2, 0));

                    // Check to see if it's something we don't want to spawn under
                    if (block1 == null || block2 == null) {
                        continue;
                    }

                    if (block1.getType() != Material.ENDER_STONE || block2.getType() != Material.ENDER_STONE) {
                        continue;
                    }

                    // Found a nice place for them to spawn
                    if (block1.getLocation().getY() >= 55) {
                        Bukkit.getLogger().severe(block1.getLocation().add(0, 2, 0).toString());
                        event.setTo(block1.getLocation().add(0, 2, 0));
                        return;
                    }
                }
            }
        }
    }

    public static void createPortal(int x, int z) {
        World world = BadlionUHC.getInstance().getServer().getWorld(BadlionUHC.UHCWORLD_NAME);
        Location location = world.getHighestBlockAt(x, z).getLocation();

        location.clone().add(-2, 0, -1).getBlock().setType(Material.ENDER_PORTAL_FRAME);
        location.clone().add(-2, 0, -1).getBlock().setData((byte) 7);
        location.clone().add(-2, 0, 0).getBlock().setType(Material.ENDER_PORTAL_FRAME);
        location.clone().add(-2, 0, 0).getBlock().setData((byte) 7);
        location.clone().add(-2, 0, 1).getBlock().setType(Material.ENDER_PORTAL_FRAME);
        location.clone().add(-2, 0, 1).getBlock().setData((byte) 7);
        location.clone().add(2, 0, -1).getBlock().setType(Material.ENDER_PORTAL_FRAME);
        location.clone().add(2, 0, -1).getBlock().setData((byte) 5);
        location.clone().add(2, 0, 0).getBlock().setType(Material.ENDER_PORTAL_FRAME);
        location.clone().add(2, 0, 0).getBlock().setData((byte) 5);
        location.clone().add(2, 0, 1).getBlock().setType(Material.ENDER_PORTAL_FRAME);
        location.clone().add(2, 0, 1).getBlock().setData((byte) 5);
        location.clone().add(-1, 0, 2).getBlock().setType(Material.ENDER_PORTAL_FRAME);
        location.clone().add(-1, 0, 2).getBlock().setData((byte) 2);
        location.clone().add(0, 0, 2).getBlock().setType(Material.ENDER_PORTAL_FRAME);
        location.clone().add(0, 0, 2).getBlock().setData((byte) 2);
        location.clone().add(1, 0, 2).getBlock().setType(Material.ENDER_PORTAL_FRAME);
        location.clone().add(1, 0, 2).getBlock().setData((byte) 2);
        location.clone().add(-1, 0, -2).getBlock().setType(Material.ENDER_PORTAL_FRAME);
        location.clone().add(-1, 0, -2).getBlock().setData((byte) 0);
        location.clone().add(0, 0, -2).getBlock().setType(Material.ENDER_PORTAL_FRAME);
        location.clone().add(0, 0, -2).getBlock().setData((byte) 0);
        location.clone().add(1, 0, -2).getBlock().setType(Material.ENDER_PORTAL_FRAME);
        location.clone().add(1, 0, -2).getBlock().setData((byte) 0);

        int numOfEyesMissing = 2;
        if (BadlionUHC.getInstance().getGameType() == UHCTeam.GameType.TEAM) {
            numOfEyesMissing = 1 + (int) BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.TEAMSIZE.name()).getValue();
        }

        if (numOfEyesMissing < 6) {
            location.clone().add(-1, 0, 2).getBlock().setData((byte) 6);
        }

        if (numOfEyesMissing < 5) {
            location.clone().add(0, 0, 2).getBlock().setData((byte) 6);
        }

        if (numOfEyesMissing < 4) {
            location.clone().add(1, 0, 2).getBlock().setData((byte) 6);
        }

        if (numOfEyesMissing < 3) {
            location.clone().add(-1, 0, -2).getBlock().setData((byte) 4);
        }

        if (numOfEyesMissing < 2) {
            location.clone().add(0, 0, -2).getBlock().setData((byte) 4);
        }

        if (numOfEyesMissing < 1) {
            location.clone().add(1, 0, -2).getBlock().setData((byte) 4);
        }

        /*location.clone().add(-1, 0, -1).getBlock().setType(Material.ENDER_PORTAL);
        location.clone().add(-1, 0, 0).getBlock().setType(Material.ENDER_PORTAL);
        location.clone().add(-1, 0, 1).getBlock().setType(Material.ENDER_PORTAL);
        location.clone().add(0, 0, -1).getBlock().setType(Material.ENDER_PORTAL);
        location.clone().add(0, 0, 0).getBlock().setType(Material.ENDER_PORTAL);
        location.clone().add(0, 0, 1).getBlock().setType(Material.ENDER_PORTAL);
        location.clone().add(1, 0, -1).getBlock().setType(Material.ENDER_PORTAL);
        location.clone().add(1, 0, 0).getBlock().setType(Material.ENDER_PORTAL);
        location.clone().add(1, 0, 1).getBlock().setType(Material.ENDER_PORTAL);*/

        location.clone().add(-2, -1, -1).getBlock().setType(Material.BEDROCK);
        location.clone().add(-2, -1, 0).getBlock().setType(Material.BEDROCK);
        location.clone().add(-2, -1, 1).getBlock().setType(Material.BEDROCK);
        location.clone().add(2, -1, -1).getBlock().setType(Material.BEDROCK);
        location.clone().add(2, -1, 0).getBlock().setType(Material.BEDROCK);
        location.clone().add(2, -1, 1).getBlock().setType(Material.BEDROCK);
        location.clone().add(-1, -1, 2).getBlock().setType(Material.BEDROCK);
        location.clone().add(0, -1, 2).getBlock().setType(Material.BEDROCK);
        location.clone().add(1, -1, 2).getBlock().setType(Material.BEDROCK);
        location.clone().add(-1, -1, -2).getBlock().setType(Material.BEDROCK);
        location.clone().add(0, -1, -2).getBlock().setType(Material.BEDROCK);
        location.clone().add(1, -1, -2).getBlock().setType(Material.BEDROCK);

        location.clone().add(-1, -1, -1).getBlock().setType(Material.BEDROCK);
        location.clone().add(-1, -1, 0).getBlock().setType(Material.BEDROCK);
        location.clone().add(-1, -1, 1).getBlock().setType(Material.BEDROCK);
        location.clone().add(0, -1, -1).getBlock().setType(Material.BEDROCK);
        location.clone().add(0, -1, 0).getBlock().setType(Material.BEDROCK);
        location.clone().add(0, -1, 1).getBlock().setType(Material.BEDROCK);
        location.clone().add(1, -1, -1).getBlock().setType(Material.BEDROCK);
        location.clone().add(1, -1, 0).getBlock().setType(Material.BEDROCK);
        location.clone().add(1, -1, 1).getBlock().setType(Material.BEDROCK);
    }

	@EventHandler
	public void onEndChunkUnloadEvent(ChunkUnloadEvent event) {
		if (event.getWorld().getEnvironment() == World.Environment.THE_END) {
			event.setCancelled(true);
		}
	}

    @Override
    public void unregister() {
        EntityDeathEvent.getHandlerList().unregister(this);
        BlockBreakEvent.getHandlerList().unregister(this);
        BorderShrinkSetEvent.getHandlerList().unregister(this);
        GameStartEvent.getHandlerList().unregister(this);
        PlayerDeathEvent.getHandlerList().unregister(this);
        PlayerPortalEvent.getHandlerList().unregister(this);
        PlayerPostPortalEvent.getHandlerList().unregister(this);
        PlayerInteractEvent.getHandlerList().unregister(this);
        PortalCreateEvent.getHandlerList().unregister(this);
    }

}
